package com.airapssinsj.happyplaceapp.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airapssinsj.happyplaceapp.R
import com.airapssinsj.happyplaceapp.databinding.ActivityHappyPlaceDetailBinding
import com.airapssinsj.happyplaceapp.model.HappyPlaceModel

class HappyPlaceDetailActivity : AppCompatActivity() {

    private var binding: ActivityHappyPlaceDetailBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHappyPlaceDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 모델 설정
        var happyplaceDetailModel : HappyPlaceModel? = null

//        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAIL)) {
//            happyplaceDetailModel = intent.getSerializableExtra(MainActivity.EXTRA_PLACE_DETAIL) as HappyPlaceModel //형변환 필요
//        }

        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAIL)) {
            happyplaceDetailModel = intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAIL)
        }

        if (happyplaceDetailModel != null) {
            setSupportActionBar(binding!!.toolbarPlaceDetail)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true) //뒤로가기
            supportActionBar!!.title = happyplaceDetailModel.title

            binding!!.toolbarPlaceDetail.setNavigationOnClickListener {
                onBackPressed()
            }

            setDisplay(happyplaceDetailModel)
        }
    }

    private fun setDisplay(happyplaceDetailModel: HappyPlaceModel?) {
        binding!!.ivPlaceImage.setImageURI(Uri.parse(happyplaceDetailModel!!.image))
        binding!!.tvDescription.text = happyplaceDetailModel.description
        binding!!.tvLocation.text = happyplaceDetailModel.location

        binding!!.btnMap.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra(MainActivity.EXTRA_PLACE_DETAIL, happyplaceDetailModel)
            startActivity(intent)
        }
    }
}