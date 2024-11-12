package com.airapssinsj.happyplaceapp.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.airapssinsj.happyplaceapp.databinding.ItemHappyPlaceBinding
import com.airapssinsj.happyplaceapp.model.HappyPlaceModel

class HappyPlaceAdapter(
    private val items:List<HappyPlaceModel>) : RecyclerView.Adapter<HappyPlaceAdapter.ViewHolder>() {

    //2. 클릭리스너 변수 설정
    private var onclickListener : OnclickListener? = null

    inner class ViewHolder(binding:ItemHappyPlaceBinding)
        : RecyclerView.ViewHolder(binding.root) {
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

}