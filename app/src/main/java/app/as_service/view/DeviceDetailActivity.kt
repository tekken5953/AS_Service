package app.as_service.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import app.as_service.R
import app.as_service.adapter.AirConditionAdapter
import app.as_service.dao.ApiModel
import app.as_service.databinding.ActivityDeviceDetailBinding
import app.as_service.util.MakeVibrator
import app.as_service.util.ToastUtils
import kotlin.random.Random

//https://notepad96.tistory.com/180     // 스크롤 애니메이션

class DeviceDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeviceDetailBinding
    private var isBookMark: Boolean = false
    private lateinit var adapter: AirConditionAdapter
    private val mList = ArrayList<ApiModel.AirCondData>()
    private val mToast = ToastUtils(this,)

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView<ActivityDeviceDetailBinding?>(this, R.layout.activity_device_detail).apply {
                detailName.text = intent.extras?.getString("deviceName")
                detailSN.text = intent.extras?.getString("serialNumber")
                detailDeviceBusiness.text = intent.extras?.getString("businessType")
                when(intent.extras?.getString("deviceType")) {
                    "as_100" -> { detailDeviceType.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.as100,null))}
                    "as_m" -> {detailDeviceType.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.as_m,null))}
                }
            }

        if (::binding.isInitialized) {
            adapter = AirConditionAdapter(mList)
            binding.detailAirCondRecyclerView.adapter = adapter

            addItem("온도", Random.nextInt(500).toString())
            addItem("습도", Random.nextInt(500).toString())
            addItem("미세먼지", Random.nextInt(500).toString())
            addItem("일산화탄소", Random.nextInt(500).toString())
            addItem("이산화탄소", Random.nextInt(500).toString())
            addItem("유기성 화합물", Random.nextInt(500).toString())
            adapter.notifyDataSetChanged()
            binding.detailAirCondRecyclerView.animation = AnimationUtils.loadAnimation(this,R.anim.trans_left_to_right)
            binding.detailCardView1.animation = AnimationUtils.loadAnimation(this, R.anim.trans_right_to_left)
        }

        binding.detailBookMark.setOnClickListener {
            isBookMark = if (!isBookMark) {
                binding.detailBookMark.setImageDrawable(ResourcesCompat.getDrawable(resources,
                    R.drawable.star_fill, null))
                MakeVibrator(this).run(50)
                mToast.shortMessage("즐겨찾기에 저장되었습니다")
                true
            } else {
                binding.detailBookMark.setImageDrawable(ResourcesCompat.getDrawable(resources,
                    R.drawable.star_empty, null))
                MakeVibrator(this).run(50)
                mToast.shortMessage("즐겨찾기에서 제외되었습니다")
                false
            }
        }

        binding.detailFanCard.setOnClickListener {
            mToast.shortMessage("팬 제어")
            MakeVibrator(this).run(100)
        }

        binding.detailPowerCard.setOnClickListener {
            mToast.shortMessage("파워 제어")
            MakeVibrator(this).run(100)
        }

        binding.detailBack.setOnClickListener {
            super.onBackPressed()
        }
    }

    private fun addItem(titleStr: String, dataStr: String) {
        val item = ApiModel.AirCondData(titleStr,dataStr)
        item.title = titleStr
        item.data = dataStr
        mList.add(item)
    }
}