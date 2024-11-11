package com.airapssinsj.happyplaceapp.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.airapssinsj.happyplaceapp.databinding.ItemHappyPlaceBinding
import com.airapssinsj.happyplaceapp.model.HappyPlaceModel

class HappyPlaceAdapter(
    private val items:List<HappyPlaceModel>)
    : RecyclerView.Adapter<HappyPlaceAdapter.ViewHolder>() {

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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.ivPlaceImage.setImageURI(Uri.parse(item.image))
        holder.tvTitle.text = item.title
        holder.tvDescription.text = item.description

    }

}