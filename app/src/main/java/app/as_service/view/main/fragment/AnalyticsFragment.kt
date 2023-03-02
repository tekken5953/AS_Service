package app.as_service.view.main.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import app.as_service.R
import app.as_service.api.aircond.AirConditionApiExplorer
import app.as_service.api.weather.GetSkyData
import app.as_service.api.weather.WeatherApiExplorer
import app.as_service.dao.StaticDataObject.TAG_L
import app.as_service.dao.StaticDataObject.TAG_R
import app.as_service.databinding.AnalyticsFragmentBinding
import app.as_service.util.RefreshUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import java.lang.IllegalStateException
import kotlin.concurrent.thread
import kotlin.math.roundToInt

//https://developer.android.com/training/location/retrieve-current?hl=ko    // 위치정보
// https://m.blog.naver.com/subid_01/221675032005  // 위/경도 -> 주소변환
// https://www.data.go.kr/iim/api/selectAPIAcountView.do  // 공공데이터포털 날씨데이터 API
// https://gist.github.com/fronteer-kr/14d7f779d52a21ac2f16 // 위/경도 -> x축 y축 좌표 변환
class AnalyticsFragment : Fragment() {
    private lateinit var binding: AnalyticsFragmentBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG_L, "onAttach AnalyticsFragment")
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG_L, "onCreateView AnalyticsFragment")

        binding = DataBindingUtil.inflate(inflater, R.layout.analytics_fragment, container, false)
        binding.weatherCoverView.visibility = View.VISIBLE
        val locationStr = arguments?.get("location").toString().split("_")

        CoroutineScope(Dispatchers.IO).launch {
            val getAirCondResult = AirConditionApiExplorer()
            val station = locationStr[2].split(" ")
            getAirCondResult.getAirData("DAILY", "구로구", "1.0")
            val JO = getAirCondResult.dataJO
            try {
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "PM10Value : ${JO.get("pm10Value")}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }

        CoroutineScope(Dispatchers.Default).launch {
            try {

                val getResult = WeatherApiExplorer()
                val onceJO = getResult.onceJO

                getResult.getWeatherData(
                    "getVilageFcst",
                    "14",
                    locationStr[0], locationStr[1], false
                )

                getResult.getWeatherData(
                    "getVilageFcst",
                    "14",
                    locationStr[0], locationStr[1], true
                )

                if (!isDetached) {
                    try {
                        requireActivity().runOnUiThread {
                            binding.weatherLocationTv.text = locationStr[2]
                            binding.weatherCoverView.visibility = View.GONE
                            applySky(onceJO.getString("SKY"))
                            binding.weatherRainTv.text =
                                "오늘 비가 올 확률은 ${onceJO.getString("POP")}% 입니다"

                            val abs = onceJO.getString("yTMP")
                                .toFloat() - onceJO.getString("TMP")
                                .toFloat()

                            val upDown = if (abs > 0) { "낮습니다" } else { "높습니다" }

                            binding.weatherTempUnit.visibility = View.VISIBLE
                            binding.weatherTempTv.text = onceJO.getString("TMP")
                            binding.weatherCompareTv.text =
                                if (abs == 0f) { "어제와 기온이 같습니다" }
                                else {"어제보다 기온이 ${getDegreeTemp(abs)}˚ $upDown"}

                            binding.weatherCoverView.visibility = View.GONE
                        }
                    } catch (e: JSONException) {
                        Log.e("api_result", "JSONException : ${e.localizedMessage}")
                    } catch (e: IllegalStateException) {
                        Log.e(TAG_R, "부모 뷰 소멸상태")
                    }
                }
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
        }

        return binding.root
    }

    private fun applySky(sky: String) {
        when (GetSkyData().convert(sky)) {
            "맑음" -> {
                setSkyImage(R.drawable.sunny)
            }
            "구름많음" -> {
                setSkyImage(R.drawable.cloudy)
            }
            "흐림" -> {
                setSkyImage(R.drawable.foggy)
            }
            "FAIL" -> {
                setSkyImage(R.drawable.no_weather)
            }
        }
        binding.weatherSkyTv.text = GetSkyData().convert(sky)
    }

    private fun setSkyImage(id: Int) {
        binding.weatherSkyIv.setImageDrawable(
            ResourcesCompat.getDrawable(resources, id, null)
        )
    }

    private fun getDegreeTemp(f: Float) : Float {
        return ((kotlin.math.abs(f) * 100).roundToInt().toFloat() / 100)
    }
}