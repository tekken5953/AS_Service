package app.as_service.api

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import app.as_service.R
import app.as_service.util.RequestPermissionsUtil
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MapsFragment : Fragment() {

    private var markerTitle = "Seoul"
    private var latitude: Double = 37.5666805
    private var longitude: Double = 126.9784147

    private val callback = OnMapReadyCallback { googleMap ->
        val default = LatLng(latitude, longitude)
        googleMap.addMarker(MarkerOptions().position(default).title(markerTitle))
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(default, 15f)
        googleMap.moveCamera(cameraUpdate)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        RequestPermissionsUtil(requireActivity()).requestLocation()
    }

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location!!.let {
                    val geocoder = Geocoder(requireActivity())
                    val result = geocoder.getFromLocation(it.latitude, it.longitude, 1)[0]
                    // 위도 경도를 x y 좌표로 변환
                    latitude = result.latitude
                    longitude = result.longitude
                    markerTitle = "${result.adminArea} ${result.locality} ${result.thoroughfare}"
                }
            }
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(callback)

        val getMarkerSync: AppCompatButton = view.findViewById(R.id.getMapButton)
        getMarkerSync.setOnClickListener {
            mapFragment.getMapAsync(callback)
        }
    }
}