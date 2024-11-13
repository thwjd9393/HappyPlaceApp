package com.airapssinsj.happyplaceapp.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.airapssinsj.happyplaceapp.activity.HappyPlaceActivity
import com.airapssinsj.happyplaceapp.activity.MainActivity
import com.airapssinsj.happyplaceapp.databinding.ItemHappyPlaceBinding
import com.airapssinsj.happyplaceapp.model.HappyPlaceModel

class HappyPlaceAdapter(private val context: Context,private val items:List<HappyPlaceModel>) : RecyclerView.Adapter<HappyPlaceAdapter.ViewHolder>() {

    //2. 클릭리스너 변수 설정
    private var onclickListener : OnclickListener? = null

    inner class ViewHolder(binding:ItemHappyPlaceBinding)
        : RecyclerView.ViewHolder(binding.root) {
        val tvId = binding.tvId
        val ivPlaceImage = binding.ivPlaceImage
        val tvTitle = binding.tvTitle
        val tvDescription = binding.tvDescription
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemHappyPlaceBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int = items.size

    //5. 각항목에 클릭리스너 달아주기
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position] //item = HappyPlaceModel

        holder.tvId.text = item.id.toString()
        holder.ivPlaceImage.setImageURI(Uri.parse(item.image))
        holder.tvTitle.text = item.title
        holder.tvDescription.text = item.description

        holder.itemView.setOnClickListener{
            if (onclickListener != null) {
                onclickListener!!.onClick(position, item)
            }
        }

    }

    //3. 온클릭 리스너 하나로 묶는 함수 생성 -> adpter는 온클릭 리스너를 가질 수 없기 때문(상속 못받음) 따라서 우회하는 방법뿐
    fun setOnclickListener(onclickListener: OnclickListener) {
        this.onclickListener = onclickListener
    }

    //1.클릭 인터페이스 생성
    interface OnclickListener {
        fun onClick(position:Int, model:HappyPlaceModel)
    }


    //변경한 내용에 대해 알람이 가도록하는 함수
    fun notifyEditItem(activity:Activity, position:Int, requestCode:Int) {
        val intent = Intent(context, HappyPlaceActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_PLACE_DETAIL, items[position])
        activity.startActivityForResult(intent, requestCode)

        //변경하고자 하는 부분에 대한 정보 호출
        // 어댑터나 리사이클러뷰에 변경된 부분이 있으면 어댑터가 그 부분에 대한 알림을 받도록하기 위함
        notifyItemChanged(position)
    }

    fun selectItem (position:Int) : HappyPlaceModel {
        return items[position]
    }

}