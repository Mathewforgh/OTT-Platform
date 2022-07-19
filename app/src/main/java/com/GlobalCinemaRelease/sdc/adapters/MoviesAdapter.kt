package com.GlobalCinemaRelease.sdc.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.GlobalCinemaRelease.sdc.MovieDescription
import com.GlobalCinemaRelease.sdc.databinding.HomePageMoviesLayoutsBinding
import com.GlobalCinemaRelease.sdc.msg.listener.setOnDebounceListener
import com.GlobalCinemaRelease.sdc.obj.Store
import com.GlobalCinemaRelease.sdc.response.DataXX


class MoviesAdapter(private val moviesImg: List<DataXX?>): RecyclerView.Adapter<MoviesAdapter.ViewHolder>() {
    private lateinit var ids: HomePageMoviesLayoutsBinding

    inner class ViewHolder(ids: HomePageMoviesLayoutsBinding): RecyclerView.ViewHolder(ids.root){
        fun bind(movies: DataXX){
            ids.apply {
                //ids.moviesLayoutId.setImageResource(movies.movieImg)
                Glide.with(itemView.context).load(movies.posterLink).fitCenter().into(moviesLayoutId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesAdapter.ViewHolder {
        ids = HomePageMoviesLayoutsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(ids)
    }

    override fun onBindViewHolder(holder: MoviesAdapter.ViewHolder, position: Int) {
        holder.bind(moviesImg[position]!!)

        holder.itemView.setOnDebounceListener {
            holder.itemView.startAnimation(Store.zoomOut(holder.itemView.context))
            Store.movieToken = moviesImg[position]?.movieToken.toString()
            Store.movieTokenFrom = "Home"
            holder.itemView.context.startActivity(Intent(holder.itemView.context, MovieDescription::class.java)
                .putExtra("fromHome", "fromHome"))
        }
    }

    override fun getItemCount(): Int {
        return moviesImg.size
    }
    override fun getItemViewType(position: Int) = position
    override fun getItemId(position: Int) = position.toLong()
}