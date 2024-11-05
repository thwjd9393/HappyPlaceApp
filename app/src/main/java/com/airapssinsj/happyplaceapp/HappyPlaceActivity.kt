package com.airapssinsj.happyplaceapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airapssinsj.happyplaceapp.databinding.ActivityHappyPlaceBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    //OnClickListener를 class에 임플리먼트하여 한번에 쓸수 있도록 함
    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.et_date -> {
                DatePickerDialog(this,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }
        }
    }

    private fun updateDateInView() {
        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding!!.etDate.setText(sdf.format(cal.time).toString())
    }
}