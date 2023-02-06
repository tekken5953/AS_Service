package app.as_service.view.main.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import app.as_service.R
import app.as_service.api.WeatherApiExplorer
import app.as_service.dao.StaticDataObject.RESPONSE_FAIL
import app.as_service.databinding.AnalyticsFragmentBinding
import app.as_service.util.ConvertDataTypeUtil
import app.as_service.util.RequestPermissionsUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlin.concurrent.thread

//https://developer.android.com/training/location/retrieve-current?hl=ko    // 위치정보
// https://m.blog.naver.com/subid_01/221675032005  // 위/경도 -> 주소변환
// https://www.data.go.kr/iim/api/selectAPIAcountView.do  // 공공데이터포털 날씨데이터 API
// https://gist.github.com/fronteer-kr/14d7f779d52a21ac2f16 // 위/경도 -> x축 y축 좌표 변환
class AnalyticsFragment : Fragment() {

    private lateinit var binding: AnalyticsFragmentBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var x: String
    private lateinit var y: String

    private var intentLatitude: Double = 0.0
    private var intentLongitude: Double = 0.0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        RequestPermissionsUtil(requireActivity()).requestLocation()
    }

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.analytics_fragment, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (::binding.isInitialized) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location!!.let {
                        val geocoder = Geocoder(requireActivity())
                        val result = geocoder.getFromLocation(it.latitude, it.longitude, 1)[0]
                        // 위도 경도를 x y 좌표로 변환
                        val convertGrid = ConvertDataTypeUtil().convertGridGps(
                            0,
                            result.latitude,
                            result.longitude
                        )
                        x = convertGrid.x.toInt().toString()
                        y = convertGrid.y.toInt().toString()

                        SpannableStringBuilder("${result.adminArea} ${result.locality} ${result.thoroughfare}")
                            .apply {
                                setSpan(
                                    UnderlineSpan(), 0,
                                    this.length,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                                binding.titleLocationTv.text = this
                            }
                    }
                }

            binding.getApiButton.setOnClickListener {
                var currentTime = ConvertDataTypeUtil().getCurrentTimeMills()
                val date = ConvertDataTypeUtil().millsToString(currentTime, "yyyyMMdd")
                val time = ConvertDataTypeUtil().millsToString(currentTime, "HHmm")
                thread(start = true) {
                    val result = WeatherApiExplorer().getWeatherData(
                        WeatherApiExplorer().getUltraSrtNcst,
                        WeatherApiExplorer().numOfUltraSrtNcst,
                        x, y, date, time)
                    if (result == RESPONSE_FAIL) {
                        currentTime -= 1000 * 3600
                    }
                    requireActivity().runOnUiThread {
                        binding.getApiTextView.text = result
                    }
                }
            }
        }

        return binding.root
    }
}