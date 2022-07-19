package com.GlobalCinemaRelease.sdc.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.GlobalCinemaRelease.sdc.ViewPagerData
import com.GlobalCinemaRelease.sdc.databinding.ItemViewPagerBinding

class ViewPagerAdapter(private val image: ArrayList<ViewPagerData>): RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {
    private lateinit var ids : ItemViewPagerBinding
    inner class ViewPagerViewHolder(ids: ItemViewPagerBinding): RecyclerView.ViewHolder(ids.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        ids = ItemViewPagerBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return ViewPagerViewHolder(ids)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
       val curImage = image[position]
        holder.itemView.apply {
            ids.imageViewId.setImageResource(curImage.image)
            ids.title1Id.setText(curImage.title1)
            ids.title2Id.setText(curImage.title2)
        }

    }

    override fun getItemCount(): Int {
     return image.size
    }
}