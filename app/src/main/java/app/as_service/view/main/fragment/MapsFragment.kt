package app.as_service.view.main.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import app.as_service.R
import app.as_service.util.RequestPermissionsUtil
import app.as_service.util.ToastUtils
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.orhanobut.logger.Logger

class MapsFragment : Fragment() {

    private var markerTitle = "Seoul"
    private var latitude: Double = 37.5666805
    private var longitude: Double = 126.9784147
    private var zoomDate: Float = 15f

    // 애드 마커 콜백
    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        val default = LatLng(latitude, longitude)
        googleMap.setOnMarkerClickListener(markerClickListener)
        googleMap.setOnInfoWindowClickListener(infoWindowClickListener)
        googleMap.isMyLocationEnabled = true
        googleMap.setOnMyLocationButtonClickListener{
            ToastUtils(requireActivity()).shortMessage("내 위치는 $markerTitle")
            false
        }
        googleMap.addMarker(MarkerOptions().position(default).title(markerTitle))
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(default, zoomDate)
        googleMap.moveCamera(cameraUpdate)
    }

    // 마커 클릭 리스너
    private val markerClickListener = GoogleMap.OnMarkerClickListener {
        val location: LatLng = it.position
        ToastUtils(requireActivity()).shortMessage("마커클릭 위치 : ${it.title} / $location")
        false
    }

    // 정보창 클릭 리스너
    private val infoWindowClickListener = GoogleMap.OnInfoWindowClickListener {
        val markerId = it.id
        ToastUtils(requireActivity()).shortMessage("정보창클릭 아이디 : $markerId")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        RequestPermissionsUtil(requireActivity()).requestLocation()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Logger.d("MapsFragment 진입")

        getLocale()
        return inflater.inflate(R.layout.map_fragment, container, false)
    }


    @SuppressLint("MissingPermission")
    private fun getLocale() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location!!.let {
                    val geocoder = Geocoder(requireActivity())
                    val result = geocoder.getFromLocation(it.latitude, it.longitude, 1)!![0]
                    // 위도 경도를 x y 좌표로 변환
                    latitude = result.latitude
                    longitude = result.longitude
                    markerTitle = result.getAddressLine(0)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        val getMarkerSync: AppCompatButton = view.findViewById(R.id.getMapButton)
        requireActivity().runOnUiThread {
            mapFragment.getMapAsync(callback)

            getMarkerSync.setOnClickListener { mapFragment.getMapAsync(callback) }
        }
    }
}