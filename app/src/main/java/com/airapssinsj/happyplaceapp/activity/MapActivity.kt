package com.airapssinsj.happyplaceapp.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airapssinsj.happyplaceapp.R
import com.airapssinsj.happyplaceapp.databinding.ActivityMapBinding
import com.airapssinsj.happyplaceapp.model.HappyPlaceModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private var _binding: ActivityMapBinding? = null
    private val binding get() = _binding!!

    //모델 객체 저장 변수
    private var mHappyPLaceDetail : HappyPlaceModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAIL)) {
            mHappyPLaceDetail = intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAIL)

            Log.d("TAG", "mHappyPLaceDetail => ${mHappyPLaceDetail!!.longitude}")
        }

        if (mHappyPLaceDetail != null) {
            setSupportActionBar(binding.toolbarMap)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = mHappyPLaceDetail!!.title

            binding.toolbarMap.setNavigationOnClickListener { onBackPressed() }

            val supportMapFragment:SupportMapFragment =
                supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

            supportMapFragment.getMapAsync(this)
        }
    }

    //지도가 준비 되면 어떻게 되는지 정의할 수 있는 부분
    //마커 표시 등
    override fun onMapReady(googleMap: GoogleMap?) {
        Log.d("TAG", "Latitude: ${mHappyPLaceDetail?.latitude}, Longitude: ${mHappyPLaceDetail?.longitude}")
        val position = LatLng(mHappyPLaceDetail!!.latitude, mHappyPLaceDetail!!.longitude)

        if (googleMap != null) {
            googleMap.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(mHappyPLaceDetail?.location ?: "Unknown Location")
            )
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 5f)) // 맵 줌 설정 1f~15f

            //맵 머커에 대한 여러가지 설정
            //https://developers.google.com/maps/documentation/android-sdk/marker?hl=ko
        }
    }
}