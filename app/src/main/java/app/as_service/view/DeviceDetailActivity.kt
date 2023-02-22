package app.as_service.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import app.as_service.R
import app.as_service.adapter.AirConditionAdapter
import app.as_service.api.ui.DrawBarChart
import app.as_service.dao.AdapterModel
import app.as_service.dao.ApiModel
import app.as_service.dao.StaticDataObject
import app.as_service.dao.StaticDataObject.RESPONSE_DEFAULT
import app.as_service.dao.StaticDataObject.TAG_R
import app.as_service.databinding.DetailActivityBinding
import app.as_service.util.ConvertDataTypeUtil.millsToString
import app.as_service.util.MakeVibrator
import app.as_service.util.SharedPreferenceManager
import app.as_service.util.SnackBarUtils
import app.as_service.util.ToastUtils
import app.as_service.viewModel.BookMarkViewModel
import app.as_service.viewModel.GetValueViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class DeviceDetailActivity : AppCompatActivity() {

    private lateinit var binding: DetailActivityBinding
    private var isBookMark: Boolean = false
    private lateinit var adapter: AirConditionAdapter
    private val mList = ArrayList<AdapterModel.AirCondData>()
    private val mToast = ToastUtils(this)
    private val snackBarUtils = SnackBarUtils()
    private val getDataViewModel by viewModel<GetValueViewModel>()
    private val patchBookMarkViewModel by viewModel<BookMarkViewModel>()
    private val accessToken by lazy { SharedPreferenceManager.getString(this, "accessToken") }
    private val refreshToken by lazy {SharedPreferenceManager.getString(this, "refreshToken")}
    private var timer = Timer()
    private lateinit var serialNumber: String
    private lateinit var deviceName: String
    private lateinit var businessType: String

    override fun onDestroy() {
        super.onDestroy()
        Log.w(TAG_R, "공기질 데이터 타이머 종료")
        timer.cancel()
        timer.purge()
    }

    override fun onResume() {
        super.onResume()
        drawBarChart()
    }

    private fun getIntentData() {
        deviceName = intent.extras!!.getString("deviceName").toString()
        serialNumber = intent.extras!!.getString("serialNumber").toString()
        businessType = intent.extras!!.getString("businessType").toString()
        isBookMark = intent.extras!!.getBoolean("bookMark")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView<DetailActivityBinding?>(
                this, R.layout.detail_activity
            ).apply {
                lifecycleOwner = this@DeviceDetailActivity
                dataVM = getDataViewModel
                bookMarkVM = patchBookMarkViewModel
                getIntentData()

                applyDeviceListInViewModel()
                applyBookMarkViewModel()
            }

        if (::binding.isInitialized) {  // 바인딩 정상 적용
            binding.detailName.text = deviceName    // 디바이스 이름
            binding.detailSN.text = serialNumber    // 디바이스 S/N
            binding.detailDeviceBusiness.text = businessType    // 비즈니스 타입
            getStarred(binding.detailBookMark)  // 북마크 불러오기
            getDeviceImage(binding.detailDeviceType)    //디바이스 이미지 불러오기

            adapter = AirConditionAdapter(mList)
            binding.detailAirCondRecyclerView.adapter = adapter

            // 최초 뷰 애니메이션
            binding.detailAirCondRecyclerView.animation =
                AnimationUtils.loadAnimation(this, R.anim.trans_left_to_right)
            binding.detailGraphCard.animation =
                AnimationUtils.loadAnimation(this, R.anim.trans_right_to_left)


            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    getDataViewModel.loadDataResult(
                        serialNumber ,
                        accessToken
                    )
                }
            }, 0, 10 * 1000)
        }

        if (serialNumber == "SIA000000T") {
            addCategoryItem("온도", "22.5", "temp")
            addCategoryItem("습도", "47.1", "humid")
            addCategoryItem("미세먼지", "15", "pm")
            addCategoryItem("일산화탄소", "1", "co")
            addCategoryItem("이산화탄소", "1025", "co2")
            addCategoryItem("유기성 화합물", "0.1", "tvoc")
            addCategoryItem("공기질\n통합지수", "21", "cqi")
            addCategoryItem("바이러스\n위험지수", "7", "virus")
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
                patchBookMarkViewModel.loadPatchBookMarkResult(serialNumber,accessToken, ApiModel.PutBookMark(true))
                true
            } else {
                binding.detailBookMark.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.star_empty, null
                    )
                )
                snackBarUtils.makeSnack(binding.nestedScrollView, this, "즐겨찾기에서 제외되었습니다")
                patchBookMarkViewModel.loadPatchBookMarkResult(serialNumber,accessToken, ApiModel.PutBookMark(false))
                vibrate100()
                false
            }
        }

        // 팬 제어 아이콘
        binding.detailFanCard.setOnClickListener {
            mToast.shortMessage("팬 제어")
            vibrate100()
        }

        // 파워 제어 아이콘
        binding.detailPowerCard.setOnClickListener {
            mToast.shortMessage("파워 제어")
            vibrate100()
        }

        // 뒤로가기 아이콘
        binding.detailBack.setOnClickListener {
            vibrate100()
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

    private fun getDeviceImage(view: ImageView) {
        // 디바이스 종류 별 이미지 불러오기
        when (intent.extras?.getString("deviceType")) {
            "as_eye" -> {
                view.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources, R.drawable.as_eye, null
                    )
                )
            }
            "as_100" -> {
                view.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources, R.drawable.as100, null
                    )
                )
            }
            "as_m" -> {
                view.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources, R.drawable.as_m, null
                    )
                )
            }
        }
    }

    private fun vibrate100() {
        MakeVibrator(this).run(100)
    }

    private fun getStarred(view: ImageView) {
        // 북마크 현황
        view.setImageDrawable(
            if (isBookMark) {
                ResourcesCompat.getDrawable(resources, R.drawable.star_fill, null)
            } else {
                ResourcesCompat.getDrawable(resources, R.drawable.star_empty, null)
            }
        )
    }

    // 공기질 데이터 카테고리 아이템 추가
    private fun addCategoryItem(titleStr: String, dataStr: String, sort: String) {
        // 데이터가 모두 불러와졌을 경우 데이터만 교체
        if (mList.size == 8) {
            for (i: Int in 0 until (7)) {
                if (mList[i].title == titleStr) {
                    mList[i].data = dataStr
                }
            }
        }
        // 데이터가 일부만 호출되었을 경우 새로 추가된 데이터와 함께 다시등록
        else {
            val item = AdapterModel.AirCondData(titleStr, dataStr, sort)
            item.title = titleStr
            item.sort = sort
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

    private fun drawBarChart() {
        val chart = DrawBarChart(this)
        val tempAxis = listOf(-8.6f,-3f,5.5f,7.3f,3.5f,0.8f)
        val humidAxis = listOf(15.4f,16.3f,21.5f,33f,20f,35f)
        chart.getInstance(binding.detailBarChart)
        chart.add(tempAxis, humidAxis,"temp","humid", R.color.defaultMainColor, R.color.progressWorst)
    }

    @SuppressLint("NotifyDataSetChanged")
    // 뷰모델 호출 후 데이터 반환
    private fun applyDeviceListInViewModel() {
        getDataViewModel.getDataResult().observe(this) { data ->
            data?.let {
                addCategoryItem("온도", it.TEMPval, "temp")
                addCategoryItem("습도", it.HUMIDval, "humid")
                addCategoryItem("미세먼지", it.PM2P5val, "pm")
                addCategoryItem("일산화탄소", it.COval, "co")
                addCategoryItem("이산화탄소", it.CO2val, "co2")
                addCategoryItem("유기성 화합물", it.TVOCval, "tvoc")
                addCategoryItem("공기질\n통합지수", it.CAIval, "cqi")
                addCategoryItem("바이러스\n위험지수", it.Virusval, "virus")
                binding.detailAirCondTimeLine.text =
                    millsToString(it.date, "HH:mm")
            }
            adapter.notifyDataSetChanged()
        }
    }

    // 뷰모델 호출 후 데이터 반환
    private fun applyBookMarkViewModel() {
        patchBookMarkViewModel.patchBookMarkResult().observe(this) { data ->
            data?.let {

            }
        }
    }
}