package com.GlobalCinemaRelease.sdc.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.GlobalCinemaRelease.sdc.MovieDescription
import com.GlobalCinemaRelease.sdc.databinding.BannerViewPagerItemBinding
import com.GlobalCinemaRelease.sdc.msg.listener.setOnDebounceListener
import com.GlobalCinemaRelease.sdc.obj.Store
import com.GlobalCinemaRelease.sdc.response.DataXX

class BannerViewPagerAdapter(private val banner: List<DataXX?>): RecyclerView.Adapter<BannerViewPagerAdapter.ViewPagerViewHolder>() {
    private lateinit var ids: BannerViewPagerItemBinding

    inner class ViewPagerViewHolder(ids: BannerViewPagerItemBinding): RecyclerView.ViewHolder(ids.root){
        fun bind(img: DataXX){
            ids.apply {
                Glide.with(itemView.context).load(img.posterLink).into(bannerImg)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): BannerViewPagerAdapter.ViewPagerViewHolder {
        ids = BannerViewPagerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewPagerViewHolder(ids)
    }

    override fun getItemCount(): Int {
        return banner.size
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        banner[position]?.let { holder.bind(it) }
        Store.tempBanner = banner

        holder.itemView.setOnDebounceListener {
            Store.movieToken = banner[position]?.movieToken.toString()
//            Store.movieTokenFrom = "Home"
            holder.itemView.context.startActivity(
                Intent(holder.itemView.context, MovieDescription::class.java)
                    .putExtra("fromHome", "fromHome"))
        }
//        if (position == banner.size -1){
//            ids.bannerImg.post(slideRun)
//        }
    }
    override fun getItemViewType(position: Int) = position
    override fun getItemId(position: Int) = position.toLong()

//    private val slideRun = Runnable{
//        banner.addAll(banner)
//    }
}