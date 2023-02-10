package app.as_service.view.main.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import app.as_service.R
import app.as_service.api.WeatherApiExplorer
import app.as_service.dao.StaticDataObject.TAG_L
import app.as_service.dao.StaticDataObject.TAG_R
import app.as_service.databinding.AnalyticsFragmentBinding
import app.as_service.util.RequestPermissionsUtil
import org.json.JSONException
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
        Log.d(TAG_L,"onAttach AnalyticsFragment")
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG_L,"onCreateView AnalyticsFragment")

        binding = DataBindingUtil.inflate(inflater, R.layout.analytics_fragment, container, false)
        binding.weatherCoverView.visibility = View.VISIBLE
        val locationStr = arguments?.get("location").toString().split("_")

            thread(start = true) {
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
                        requireActivity().runOnUiThread {
                            try {
                                binding.weatherLocationTv.text = locationStr[2]
                                binding.weatherCoverView.visibility = View.GONE
                                binding.weatherSkyTv.text = convertSky(onceJO.getString("SKY"))
                                binding.weatherRainTv.text =
                                    "오늘 비가 올 확률은 ${onceJO.getString("POP")}% 입니다"

                                val abs = onceJO.getString("yTMP")
                                    .toFloat() - onceJO.getString("TMP")
                                    .toFloat()
                                Log.d(
                                    TAG_R,
                                    "yTMP : ${onceJO.getString("yTMP")} TMP : ${onceJO.getString("TMP")}"
                                )

                                val upDown = if (abs < 0)
                                { "낮습니다" }
                                else { "높습니다" }

                                binding.weatherTempUnit.visibility = View.VISIBLE
                                binding.weatherTempTv.text = onceJO.getString("TMP")
                                binding.weatherCompareTv.text =
                                    "어제보다 기온이 ${
                                        ((kotlin.math.abs(abs) * 100).roundToInt().toFloat() / 100)
                                    }˚ $upDown"

                                Log.d(TAG_R, "abs : $abs")

                                binding.weatherCoverView.visibility = View.GONE
                            } catch (e: JSONException) {
                                Log.e("api_result", "JSONException : ${e.localizedMessage}")
                            }
                        }
                    }
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }

        return binding.root
    }


    // 하늘상태 변환
    private fun convertSky(sky: String): String {
        return when (sky) {
            "1" -> {
                binding.weatherSkyIv.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.sunny,
                        null
                    )
                )
                "맑음"
            }
            "3" -> {
                binding.weatherSkyIv.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.cloudy,
                        null
                    )
                )
                "구름많음"
            }
            "4" -> {
                binding.weatherSkyIv.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.foggy,
                        null
                    )
                )
                "흐림"
            }
            else -> {
                binding.weatherSkyIv.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.no_weather,
                        null
                    )
                )
                "FAIL"
            }
        }
    }
}