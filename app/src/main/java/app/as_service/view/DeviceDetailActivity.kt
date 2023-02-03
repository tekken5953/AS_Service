package app.as_service.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import app.as_service.R
import app.as_service.adapter.AirConditionAdapter
import app.as_service.dao.AdapterModel
import app.as_service.dao.StaticDataObject.TAG
import app.as_service.databinding.ActivityDeviceDetailBinding
import app.as_service.util.*
import app.as_service.viewModel.GetValueDataModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

//https://notepad96.tistory.com/180     // 스크롤 애니메이션

class DeviceDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeviceDetailBinding
    private var isBookMark: Boolean = false
    private lateinit var adapter: AirConditionAdapter
    private val mList = ArrayList<AdapterModel.AirCondData>()
    private val mToast = ToastUtils(this)
    private val snackBarUtils = SnackBarUtils()
    private val getDataViewModel by viewModel<GetValueDataModel>()
    private val accessToken by lazy { SharedPreferenceManager.getString(this, "accessToken") }
    private val timer = Timer()

    override fun onDestroy() {
        super.onDestroy()
        Log.w(TAG, "공기질 데이터 타이머 종료")
        timer.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView<ActivityDeviceDetailBinding?>(
                this, R.layout.activity_device_detail
            ).apply {
                lifecycleOwner = this@DeviceDetailActivity
                dataVM = getDataViewModel

                detailName.text = intent.extras?.getString("deviceName")    // 디바이스 이름
                detailSN.text = intent.extras?.getString("serialNumber")    // 디바이스 S/N
                detailDeviceBusiness.text = intent.extras?.getString("businessType")    // 비즈니스 타입

                // 디바이스 종류 별 이미지 불러오기
                when (intent.extras?.getString("deviceType")) {
                    "as_100" -> {
                        detailDeviceType.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources, R.drawable.as100, null
                            )
                        )
                    }
                    "as_m" -> {
                        detailDeviceType.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources, R.drawable.as_m, null
                            )
                        )
                    }
                }

                applyDeviceListInViewModel()
            }

        if (::binding.isInitialized) {  // 바인딩 정상 적용
            adapter = AirConditionAdapter(mList)
            binding.detailAirCondRecyclerView.adapter = adapter

            // 최초 뷰 애니메이션
            binding.detailAirCondRecyclerView.animation =
                AnimationUtils.loadAnimation(this, R.anim.trans_left_to_right)
            binding.detailCardView1.animation =
                AnimationUtils.loadAnimation(this, R.anim.trans_right_to_left)

            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    getDataViewModel.loadDataResult(
//                        "TIA0002053",
                        intent.extras?.getString("serialNumber").toString(),
                        accessToken
                    )
                }
            }, 0, 10 * 1000)
        }

        // 즐겨찾기 아이콘 클릭 이벤트
        binding.detailBookMark.setOnClickListener {
            isBookMark = if (!isBookMark) {
                binding.detailBookMark.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.star_fill, null
                    )
                )
                MakeVibrator(this).run(50)
                snackBarUtils.makeSnack(binding.nestedScrollView, this, "즐겨찾기에 저장되었습니다")
                true
            } else {
                binding.detailBookMark.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.star_empty, null
                    )
                )
                snackBarUtils.makeSnack(binding.nestedScrollView, this, "즐겨찾기에서 제외되었습니다")
                MakeVibrator(this).run(50)
                false
            }
        }

        // 팬 제어 아이콘
        binding.detailFanCard.setOnClickListener {
            mToast.shortMessage("팬 제어")
            MakeVibrator(this).run(100)
        }

        // 파워 제어 아이콘
        binding.detailPowerCard.setOnClickListener {
            mToast.shortMessage("파워 제어")
            MakeVibrator(this).run(100)
        }

        // 뒤로가기 아이콘
        binding.detailBack.setOnClickListener {
            super.onBackPressed()
        }

        // 스크롤의 위치가 일정이상 일 때 해당 지도뷰 애니메이션 적용
        binding.nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (mList.size != 0) {
                // 스크롤을 500이상 다운했을 경우
                if (scrollY > 500) {
                    if (binding.detailCardView2.visibility == View.INVISIBLE) {
                       cardViewAnim()
                    }
                    if (binding.detailLocationContent.visibility == View.INVISIBLE) {
                        locationAnim()
                    }
                }
            }
            // 데이터 로딩에 실패하였을 경우
            else {
                cardViewAnim()
                locationAnim()
            }
        }
    }

    // 공기질 데이터 카테고리 아이템 추가
    private fun addCategoryItem(titleStr: String, dataStr: String, sort: String) {
        // 데이터가 모두 불러와졌을 경우 데이터만 교체
        if (mList.size == 8) {
            for (i: Int in 0 until (7)) {
                if (mList[i].title == titleStr) {
                    mList[i].sort = sort
                    mList[i].data = dataStr
                }
            }
        }
        // 데이터가 일부만 호출되었을 경우 새로 추가된 데이터와 함께 다시등록
        else {
            val item = AdapterModel.AirCondData(titleStr, dataStr, sort)
            item.title = titleStr
            item.data = dataStr
            mList.add(item)
        }
    }

    private fun cardViewAnim() {
        binding.detailCardView2.visibility = View.VISIBLE
        binding.detailCardView2.animation = AnimationUtils.loadAnimation(
            this@DeviceDetailActivity, R.anim.trans_bottom_to_top
        )
    }

    private fun locationAnim() {
        binding.detailLocationContent.visibility = View.VISIBLE
        binding.detailLocationContent.animation = AnimationUtils.loadAnimation(
            this@DeviceDetailActivity, R.anim.trans_bottom_to_top
        )
    }

    // 뷰모델 호출 후 데이터 반환
    @SuppressLint("NotifyDataSetChanged")
    private fun applyDeviceListInViewModel() {
        getDataViewModel.getDataResult().observe(this) { data ->
            data.let {
                addCategoryItem("온도", it.TEMPval, "temp")
                addCategoryItem("습도", it.HUMIDval, "humid")
                addCategoryItem("미세먼지", it.PM2P5val, "pm")
                addCategoryItem("일산화탄소", it.COval, "co")
                addCategoryItem("이산화탄소", it.CO2val, "co2")
                addCategoryItem("유기성 화합물", it.TVOCval, "tvoc")
                addCategoryItem("공기질\n통합지수", it.CAIval, "cqi")
                addCategoryItem("바이러스\n위험지수", it.Virusval, "virus")
                binding.detailAirCondTimeLine.text = ConvertDataTypeUtil().millsToString(it.date)
            }
            adapter.notifyDataSetChanged()
        }
    }
}