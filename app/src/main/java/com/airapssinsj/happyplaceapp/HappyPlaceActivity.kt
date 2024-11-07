package com.airapssinsj.happyplaceapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airapssinsj.happyplaceapp.databinding.ActivityHappyPlaceBinding
import com.karumi.dexter.Dexter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.Manifest;
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener


class HappyPlaceActivity : AppCompatActivity(), View.OnClickListener {

    var binding:ActivityHappyPlaceBinding? = null
    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener:DatePickerDialog.OnDateSetListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHappyPlaceBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding!!.toolbarAddPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) //뒤로가기
        binding!!.toolbarAddPlace.setNavigationOnClickListener {
            onBackPressed()
        }

        dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR,year)
            cal.set(Calendar.MONTH,month)
            cal.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateDateInView()
        }

        binding!!.etDate.setOnClickListener(this)
        binding!!.tvAddImage.setOnClickListener(this)

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }



    //OnClickListener를 class에 임플리먼트하여 한번에 쓸수 있도록 함
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.et_date -> {
                DatePickerDialog(this,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }
            R.id.tv_add_image -> {
                val pictureDialogItem = arrayOf("Select phto from gellery",
                    "Capture photo from camera")
                val pictureDialog = AlertDialog.Builder(this)
                    .setTitle("Select Action")
                    .setItems(pictureDialogItem) {
                        dialog, which ->
                        when(which){
                            0-> choosePhotoFromGallery()
                            1-> Toast.makeText(this, "카메라 선택,,", Toast.LENGTH_SHORT).show()
                        }
                    }.show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun choosePhotoFromGallery() {
        //멀티 퍼미션 도와주는 라이브러리 - 덱스터
        // https://github.com/Karumi/Dexter

        //안드로이드 13이산과 미만 버전 권한 나누기
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }


        Dexter.withContext(this).withPermissions(permissions)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?)
                {
                    //모든 퍼미션 부여됐을 경우
                    if (report != null) {
                        if (report.areAllPermissionsGranted()){
                            Toast.makeText(this@HappyPlaceActivity, "갤러리 저장,읽기 쓰기 권한", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                // 유저에게 이 권한이 왜 필요한지 알려주는 부분
                override fun onPermissionRationaleShouldBeShown(
                    permission: MutableList<PermissionRequest>?,
                    permissionToken: PermissionToken?
                ) {
                    showRationalDialogForPermission() //사용자에게 이유 알려주기 위한 메서드
                }
        }).onSameThread().check()
    }

    private fun showRationalDialogForPermission() {
        AlertDialog.Builder(this).setMessage(
            "이 기능에 필요한 권한이 거절되었습니다. 앱 세팅에서 변경할 수 있습니다"
        ).setPositiveButton("Go to settings")
        {
            _,_->
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName,null) //앱설정 화면으로 넘어간 뒤 사용자 권한을 바꿀수 있게함
                intent.data = uri
                startActivity(intent)
            } catch (e:ActivityNotFoundException) {
                e.printStackTrace()
            }
        }.setNegativeButton("Cancel"){
            dialog, which->
            dialog.dismiss()
        }.show()
    }

    private fun updateDateInView() {
        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding!!.etDate.setText(sdf.format(cal.time).toString())
    }
}