package com.GlobalCinemaRelease.sdc.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.GlobalCinemaRelease.sdc.MovieDescription
import com.GlobalCinemaRelease.sdc.databinding.AddWatchListLayoutBinding
import com.GlobalCinemaRelease.sdc.msg.listener.setOnDebounceListener
import com.GlobalCinemaRelease.sdc.obj.Store
import com.GlobalCinemaRelease.sdc.response.DataXXXXXX
import kotlinx.android.synthetic.main.add_watch_list_layout.view.*

class ShowWatchListMovieAdapter(
    private val movie: List<DataXXXXXX?>?,
    private val fromShowWatchListMovieAdapter: FromShowWatchListMovieAdapter,
    private var test: Boolean
):
    RecyclerView.Adapter<ShowWatchListMovieAdapter.ViewHolder>(){
        private lateinit var ids: AddWatchListLayoutBinding
        inner class ViewHolder(ids: AddWatchListLayoutBinding): RecyclerView.ViewHolder(ids.root){
            fun bind(data: DataXXXXXX){
                ids.apply {
                    Glide.with(itemView.context).load(data.posterLink).into(img)
                    movieName.text = data.movieName
                    movieDescription.text = data.description
                }
                if (test) {
                    ids.tickUnSelect.visibility = View.VISIBLE

                }
                else{
                    ids.tickUnSelect.visibility = View.GONE
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowWatchListMovieAdapter.ViewHolder {
        ids = AddWatchListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(ids)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        movie?.get(position)?.let { holder.bind(it) }

        holder.itemView.setOnClickListener {
            if (holder.itemView.tickUnSelect.isVisible){
                if (holder.itemView.tickSelect.isVisible) {
                    holder.itemView.tickSelect.visibility = View.GONE
                    movie?.get(position)?.let { it1 -> it1.movieToken?.let { it2 ->
                        Store.movieTokenList.remove(it2)
                    } }
                }
                else {
                    holder.itemView.tickSelect.visibility = View.VISIBLE
                    movie?.get(position)?.let { it1 -> it1.movieToken?.let { it2 ->
                        Store.movieTokenList.add(it2)
                    } }
                }
            }
            else{
                Store.movieToken = movie?.get(position)?.movieToken.toString()
                Store.from_SeeAll = "watchList"
                Store.movieTokenFrom = "watchList"
                holder.itemView.context.startActivity(Intent(holder.itemView.context, MovieDescription::class.java))
            }
        }
        holder.itemView.setOnLongClickListener {
            test = true
            notifyDataSetChanged()
//            holder.itemView.tickUnSelect.visibility = View.VISIBLE
            fromShowWatchListMovieAdapter.onItemLongPress(movie, test)
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return movie!!.size
    }

    interface FromShowWatchListMovieAdapter{
        fun onItemLongPress(movie: List<DataXXXXXX?>?, test: Boolean)
    }
    override fun getItemViewType(position: Int) = position
    override fun getItemId(position: Int) = position.toLong()
}