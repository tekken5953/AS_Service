package app.as_service.view.main.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import app.as_service.R
import app.as_service.databinding.AnalyticsFragmentBinding
import app.as_service.util.ConvertDataTypeUtil
import app.as_service.util.RequestPermissionsUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

//https://developer.android.com/training/location/retrieve-current?hl=ko    // 위치정보
class AnalyticsFragment : Fragment() {

    private lateinit var binding: AnalyticsFragmentBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onAttach(context: Context) {
        super.onAttach(context)
        RequestPermissionsUtil().requestLocation(requireActivity())
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
            binding.userLocationText.text = "Nothing"

            binding.userLocationBtn.setOnClickListener {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        location.let {
                            binding.userLocationText.text =
                                "위도 : ${location?.longitude}\n경도 : ${location?.latitude}" +
                                        "\n마지막측정시간 : ${ConvertDataTypeUtil().millsToString(location?.time!!.toLong())}" +
                                        "\n수직 정확도(오차)(m) : ${location?.verticalAccuracyMeters}" +
                                        "\n수평 정확도(오차)(m) : ${location?.accuracy}" +
                                        "\n수평 이동방향(˚) : ${location?.bearing}" +
                                        "\n스피드(m/s) : ${location?.speed}"
                        }
                    }
            }

        }

        return binding.root
    }
}