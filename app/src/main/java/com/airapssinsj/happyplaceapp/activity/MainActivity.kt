package com.airapssinsj.happyplaceapp.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airapssinsj.happyplaceapp.R
import com.airapssinsj.happyplaceapp.adapter.HappyPlaceAdapter
import com.airapssinsj.happyplaceapp.database.DatabaseHandler
import com.airapssinsj.happyplaceapp.databinding.ActivityMainBinding
import com.airapssinsj.happyplaceapp.model.HappyPlaceModel

class MainActivity : AppCompatActivity() {

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

        val placesAdapter = HappyPlaceAdapter(happyPlaceList)
        binding!!.rvHappyPlacesList.adapter = placesAdapter
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
    }

}