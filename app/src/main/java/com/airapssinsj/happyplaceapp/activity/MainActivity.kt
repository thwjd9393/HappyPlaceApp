package com.airapssinsj.happyplaceapp.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.airapssinsj.happyplaceapp.R
import com.airapssinsj.happyplaceapp.adapter.HappyPlaceAdapter
import com.airapssinsj.happyplaceapp.database.DatabaseHandler
import com.airapssinsj.happyplaceapp.databinding.ActivityMainBinding
import com.airapssinsj.happyplaceapp.model.HappyPlaceModel
import pl.kitek.rvswipetodelete.SwipeToEditCallback

class MainActivity(context: Context) : AppCompatActivity() {

    var binding:ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding!!.fabAddHappyPlace.setOnClickListener {
//            startActivity(Intent(this, HappyPlaceActivity::class.java))
            startActivityForResult(Intent(this, HappyPlaceActivity::class.java), ADD_PLACE_ACTIVITY_REQUEST_CODE)
        }

        getHappyPlacesListFromLocalDB()
    }

    private fun getHappyPlacesListFromLocalDB() {
        val dbHandler = DatabaseHandler(this)
        val getHappyPlaceList : ArrayList<HappyPlaceModel> = dbHandler.getHappyPlaceList()

        if (getHappyPlaceList.size > 0) {
            binding!!.rvHappyPlacesList.visibility = View.VISIBLE
            binding!!.tvNoList.visibility = View.GONE
            setHappyPlaceRecyclerView(getHappyPlaceList)
        } else {
            binding!!.rvHappyPlacesList.visibility = View.GONE
            binding!!.tvNoList.visibility = View.VISIBLE
        }

    }

    private fun setHappyPlaceRecyclerView(
        happyPlaceList:ArrayList<HappyPlaceModel>) {

        binding!!.rvHappyPlacesList.setHasFixedSize(true) //size 수정

        val placesAdapter = HappyPlaceAdapter(this,happyPlaceList)
        binding!!.rvHappyPlacesList.adapter = placesAdapter

        placesAdapter.setOnclickListener(object : HappyPlaceAdapter.OnclickListener{
            override fun onClick(position: Int, model: HappyPlaceModel) {
                startActivity(Intent(this@MainActivity,
                    HappyPlaceDetailActivity::class.java).putExtra(EXTRA_PLACE_DETAIL, model))
                //putExtra는 Serializable 과 Parcelable 모두 지원
            }

        })

        //스와이프 활용
        // 리스트에 속해 있는 값중 하나를 스와이프 했을 때 실행되는 기능 오버라이드
        val editSwipeHandler = object : SwipeToEditCallback(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding!!.rvHappyPlacesList.adapter as HappyPlaceAdapter
                adapter.notifyEditItem(this@MainActivity, viewHolder.adapterPosition, ADD_PLACE_ACTIVITY_REQUEST_CODE)
            }

        }
    }


    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_PLACE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                getHappyPlacesListFromLocalDB()
            } else {
                Log.d("TAG", "Cancelled or Back press")
            }
        }
    }

    companion object {
        var ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
        var EXTRA_PLACE_DETAIL = "extra_place_detail"
    }

}