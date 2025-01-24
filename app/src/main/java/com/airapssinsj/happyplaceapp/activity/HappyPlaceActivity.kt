package com.airapssinsj.happyplaceapp.activity

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
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.airapssinsj.happyplaceapp.R
import com.airapssinsj.happyplaceapp.database.DatabaseHandler
import com.airapssinsj.happyplaceapp.model.HappyPlaceModel
import com.airapssinsj.happyplaceapp.utils.GetAddressFromLatLog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.UUID


class HappyPlaceActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private const val GALLERY = 1
        private const val CAMERA = 2
        //휴대폰 내에 이미지를 저장할 폴더 이름
        private const val IMAGE_DIRECTORY = "HappyPlaceImg"
        private const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 3
    }

    private var binding:ActivityHappyPlaceBinding? = null
    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener:DatePickerDialog.OnDateSetListener

    //uri로 인식되는 이미지 변수
    private var saveImageToInternalStorage : Uri? = null
    private var mLatitude : Double = 38.0
    private var mLongitude : Double = 127.0

    ///////////////
    //edit위한 변수 정보를 전달 해 줄수 있는 변수 생성
    private var mHappyPlaceDetails : HappyPlaceModel? = null

    //구글에서 제공하는 사용자 위치 찾기용 변수
    private lateinit var mFusedLocationClient:FusedLocationProviderClient


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

        //위치 얻기 초기화
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //구글 플레이스
        if (!Places.isInitialized()) {
            Places.initialize(this, resources.getString(R.string.google_maps_api_key))
        }
        
        //
        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAIL)) {
            mHappyPlaceDetails = intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAIL)
        }


        dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR,year)
            cal.set(Calendar.MONTH,month)
            cal.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateDateInView()
        }

        updateDateInView()

        if (mHappyPlaceDetails != null) {
            supportActionBar?.title = "Edit Happy Place"

            binding!!.etTitle.setText(mHappyPlaceDetails!!.title)
            binding!!.etDescription.setText(mHappyPlaceDetails!!.description)
            binding!!.etDate.setText(mHappyPlaceDetails!!.date)
            binding!!.etLocation.setText(mHappyPlaceDetails!!.location)
            mLongitude = mHappyPlaceDetails!!.longitude
            mLatitude = mHappyPlaceDetails!!.latitude

            saveImageToInternalStorage = Uri.parse(mHappyPlaceDetails!!.image)
            binding!!.ivPlaceImage.setImageURI(saveImageToInternalStorage)

            binding!!.btnSave.text = "UPDATE"
        }


        binding!!.etDate.setOnClickListener(this)
        binding!!.tvAddImage.setOnClickListener(this)
        binding!!.btnSave.setOnClickListener(this)
        binding!!.etLocation.setOnClickListener(this)
        binding!!.tvSelectCurrentLocation.setOnClickListener(this)

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    //사용자 위치 권한 얻어오기
    private fun isLocationEnabled() : Boolean{
        //사용자의 위치를 알고 있는지 또는 사용자의 위치를 알 수 있는지 여부를 알려줌
        val locationManager : LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }


    //requestLocationUpdates를 사용하면 퍼미션 받으라고 오류표시 남
    // -> 이미 requestNewUserLocation() 하기전에 퍼미션을 받았기때문에 어노테이션을 붙여 권한손실 오류 방지
    @SuppressLint("MissingPerMission") 
    private fun requestNewUserLocation() {
        //Priority : 우선순위
        //PRIORITY_HIGH_ACCURACY - 이 설정을 사용하여 가장 정확한 위치를 요청합니다. 이 설정을 사용하면 위치 서비스가 GPS를 사용하여 위치를 확인할 가능성이 높습니다.
        //PRIORITY_LOW_POWER - 이 설정을 사용하여 도시 수준의 정밀도를 요청합니다. 대략 10킬로미터의 정확성입니다. 이는 대략적인 수준의 정확성으로 간주되며 전력을 더 적게 소비할 수 있습니다.
        //PRIORITY_PASSIVE - 전력 소비에 별다른 영향을 미치지 않으면서 사용 가능한 경우 위치 업데이트를 수신. 앱에서 위치 업데이트를 트리거하지 않고 다른 앱에서 트리거한 위치를 수신
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 1000 //얼마나 자주 작동할지
        mLocationRequest.numUpdates = 1 //한번만 사용

        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallBack, Looper.myLooper())
    }

    //사용자 위치 정보 받아오는 콜백함수
    //받아온 위치정보를 유필 폴더에 만든 GetAddressFromLatLog에 넘겨주어 통해 주소로 받아오기
    private val mLocationCallBack = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation : Location = locationResult.lastLocation!!
            mLatitude = mLastLocation.latitude
            mLongitude = mLastLocation.longitude

            //GetAddressFromLatLog 유틸 실행
            val addressTask = GetAddressFromLatLog(this@HappyPlaceActivity, mLatitude, mLongitude)
            addressTask.setAddressListener(object : GetAddressFromLatLog.AddressListener{
                override fun onAddressFound(address: String?) {
                    binding!!.etLocation.setText(address)
                }
                override fun inError() {
                    Log.e("TAG","주소 얻어오기 실패")
                }
            })
            addressTask.getAddress() //실행
        }
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
                            1-> takePhotoFromCamera()
                        }
                    }.show()
            }
            R.id.btn_save -> {
                // sqLite에 데이터 저장
                when{
                    binding!!.etTitle.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "제목을 입력하세요", Toast.LENGTH_SHORT).show()
                    }
                    binding!!.etDescription.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "내용을 입력하세요", Toast.LENGTH_SHORT).show()
                    }
                    binding!!.etLocation.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "위치를 입력하세요", Toast.LENGTH_SHORT).show()
                    }
                    saveImageToInternalStorage == null -> {
                        Toast.makeText(this, "이미지를 선택하세요", Toast.LENGTH_SHORT).show()
                    } else -> {
                        val happyPlaceModel = HappyPlaceModel(
                            if(mHappyPlaceDetails == null) 0 else mHappyPlaceDetails!!.id,
                            binding!!.etTitle.text.toString(),
                            saveImageToInternalStorage.toString(),
                            binding!!.etDescription.text.toString(),
                            binding!!.etDate.text.toString(),
                            binding!!.etLocation.text.toString(),
                            mLatitude,
                            mLongitude
                        )
                        val dbHandler = DatabaseHandler(this)

                        var addHAppyPlaceResult = 0L
                        if (mHappyPlaceDetails == null) {
                            addHAppyPlaceResult = dbHandler.addHappyPlace(happyPlaceModel)
                        } else {
                            addHAppyPlaceResult = dbHandler.updateHappyPlace(happyPlaceModel).toLong()
                        }

                        if (addHAppyPlaceResult > 0) {
                            Toast.makeText(this, "저장", Toast.LENGTH_SHORT).show()
//                            startActivity(Intent(this,MainActivity::class.java))
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                    }
                }

            }
            R.id.et_location -> {
                try {
                    val placeFields = listOf(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)

                    val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, placeFields)
                        .build(this)
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)

                } catch (e:Exception) {
                    e.printStackTrace()
                }
            }
            R.id.tv_select_current_location -> {
                if (!isLocationEnabled()) {
                    //비활성화
                    Toast.makeText(this, "위치정보가 비활성화 되어 있습니다", Toast.LENGTH_SHORT).show()

                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                } else {
                    Dexter.withActivity(this).withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ).withListener(object : MultiplePermissionsListener{
                        override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                            //report가 모든 퍼미션이 허용이라고 하는 곳

                            requestNewUserLocation()
                        }

                        //사용자가 허가 안해줌
                        override fun onPermissionRationaleShouldBeShown(
                            p0: MutableList<PermissionRequest>?,
                            p1: PermissionToken?
                        ) {
                            //허락받기 위한 대화 상자 노출
                            showRationalDialogForPermission()
                        }

                    }).onSameThread() //Dexter 권한 요청 라이브러리에서 사용하는 메서드로,
                        // 권한 요청 결과 콜백(MultiplePermissionsListener)이
                        // **현재 스레드(UI 스레드)**에서 실행되도록 보장
                        .check() // 이 호출이 있어야 권한 요청이 실행됨
                }
            }
        }
    }


    private fun takePhotoFromCamera() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.CAMERA
            )
        } else {
            listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        }


        Dexter.withContext(this).withPermissions(permissions)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?)
                {
                    //모든 퍼미션 부여됐을 경우
                    if (report != null) {
                        if (report.areAllPermissionsGranted()){
                            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            startActivityForResult(cameraIntent, CAMERA)
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
                            val galleryIntent = Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            startActivityForResult(galleryIntent, GALLERY)
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

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                if (data != null) {
                    val contentURI = data.data
                    try {
                        //미디어스토어와 contentURI에서 이미지 꺼내기
                        //contentResolver?
                        val selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)

                        //찍은 사진 내가 지정한 폴더에 저장하기
                        saveImageToInternalStorage = saveImageToInternalStorage(selectedImageBitmap)
                        Log.d("TAG", "save image path :: $saveImageToInternalStorage")
                        ///data/user/0/com.airapssinsj.happyplaceapp/app_HappyPlaceImg/db7ec4f4-0c1a-4935-98a1-91e04c5cc66b.jpg

                        binding!!.ivPlaceImage.setImageBitmap(selectedImageBitmap)
                    } catch (e:IOException) {
                        e.printStackTrace()
                        Toast.makeText(this, "갤러리에서 사진을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else if (requestCode == CAMERA) {
                if (data != null && data.extras?.get("data") != null) {
                    val thumbnail: Bitmap = data.extras!!.get("data") as Bitmap

                    //찍은 사진 내가 지정한 폴더에 저장하기
                    saveImageToInternalStorage = saveImageToInternalStorage(thumbnail)
                    Log.d("TAG", "save image path :: $saveImageToInternalStorage")

                    binding!!.ivPlaceImage.setImageBitmap(thumbnail)
                } else {
                    // data가 null일 때의 처리를 작성합니다.
                    Toast.makeText(this, "이미지를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                val place:Place = Autocomplete.getPlaceFromIntent(data!!)

                binding!!.etLocation.setText(place.address)
                mLatitude == place.latLng!!.latitude
                mLongitude == place.latLng!!.longitude
            }
        }
    }

    //라이브러리 없이 퍼미션
//    private fun choosePhotoFromGallery() {
//        //안드로이드 13이산과 미만 버전 권한 나누기
//        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            listOf(
//                Manifest.permission.READ_MEDIA_IMAGES,
//                Manifest.permission.READ_MEDIA_VIDEO
//            )
//        } else {
//            listOf(
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//            )
//        }
//
//        // 필요한 권한 중 아직 허용되지 않은 권한 필터링
//        val deniedPermissions = permissions.filter { permission ->
//            ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
//        }
//
//        if (deniedPermissions.isEmpty()) {
//            // 모든 권한이 이미 허용된 경우
//            Toast.makeText(this, "이미 모든 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
//        } else {
//            // 허용되지 않은 권한을 요청
//            requestPermissionLauncher.launch(deniedPermissions.toTypedArray())
//        }
//    }
//
//    // 권한 요청 결과를 처리
//    // registerForActivityResult : 액티비티 결과 처리하는 함수
//    // ActivityResultContracts 계약서 들고 있는애
//    // RequestMultiplePermissions 여러 계약 한번에 처리 하는 애
//    private val requestPermissionLauncher =
//        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
//            // 모든 권한이 허용된 경우
//            if (permissions.all { it.value }) {
//                Toast.makeText(this, "갤러리 접근 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
//            } else {
//                // 하나 이상의 권한이 거부된 경우
//                showRationalDialogForPermission()
//            }
//        }

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

    //이미지 저장 리턴으로 저장하는 이미지 위치 받아옴
    private fun saveImageToInternalStorage(bitmap: Bitmap) : Uri {
        val wrapper = ContextWrapper(applicationContext) //어플리케이션 컨텍스트 담은 변수
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpg") //(file디렉터리 위치,압축해서 만든 비트맵 이름)"
        //파일 출력 스트림
        try {
            val stream : OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream) //(파일형식, 품질, 출력 스트림)
            stream.flush()
            stream.close()
        }catch (e:IOException) {
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }

    private fun updateDateInView() {
        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding!!.etDate.setText(sdf.format(cal.time).toString())
    }
}