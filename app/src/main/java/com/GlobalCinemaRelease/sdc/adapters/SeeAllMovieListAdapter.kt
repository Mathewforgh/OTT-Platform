package com.GlobalCinemaRelease.sdc.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.GlobalCinemaRelease.sdc.MovieDescription
import com.GlobalCinemaRelease.sdc.databinding.SeeAllMovieListBinding
import com.GlobalCinemaRelease.sdc.msg.listener.setOnDebounceListener
import com.GlobalCinemaRelease.sdc.obj.Store
import com.GlobalCinemaRelease.sdc.response.SeeAllMoviesDC

class SeeAllMovieListAdapter(private val movies: List<SeeAllMoviesDC>):
    RecyclerView.Adapter<SeeAllMovieListAdapter.ViewHolder>() {
        private lateinit var ids: SeeAllMovieListBinding

        inner class ViewHolder(ids: SeeAllMovieListBinding): RecyclerView.ViewHolder(ids.root){
            fun binding(data: SeeAllMoviesDC){
                ids.apply {
                    Glide.with(itemView.context).load(data.posterLink).into(moviesImg)
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        ids = SeeAllMovieListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(ids)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding(movies[position])

        holder.itemView.apply {
            ids.moviesImg.startAnimation(Store.zoomFadeIn(holder.itemView.context))
            setOnDebounceListener {
                Store.movieToken = movies[position].movieToken.toString()
                holder.itemView.context.startActivity(Intent(holder.itemView.context, MovieDescription::class.java)
                    .putExtra("fromHome", "fromHome"))
                Store.from_SeeAll = "seeAll"
            }
        }
    }

    override fun getItemCount(): Int {
        return movies.size
    }
    override fun getItemViewType(position: Int) = position
    override fun getItemId(position: Int) = position.toLong()
}