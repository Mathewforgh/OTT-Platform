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
import com.GlobalCinemaRelease.sdc.response.Suggestion

class SuggestionMoviesAdapter(private val suggestionMovie: List<Suggestion?>): RecyclerView.Adapter<SuggestionMoviesAdapter.ViewHolder>() {
    private lateinit var ids: HomePageMoviesLayoutsBinding

    inner class ViewHolder(ids: HomePageMoviesLayoutsBinding): RecyclerView.ViewHolder(ids.root){
        fun bind(movies: Suggestion){
            ids.apply {
                //ids.moviesLayoutId.setImageResource(movies.movieImg)
                Glide.with(itemView.context).load(movies.posterLink).fitCenter().into(ids.moviesLayoutId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionMoviesAdapter.ViewHolder {
        ids = HomePageMoviesLayoutsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(ids)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(suggestionMovie[position]!!)

        holder.itemView.setOnDebounceListener {
            Store.movieToken = suggestionMovie[position]?.movieToken.toString()
            Store.fromSuggest = "fromSuggest"
            holder.itemView.context.startActivity(
                Intent(holder.itemView.context, MovieDescription::class.java)
                    .putExtra("fromHome", "fromHome"))
        }
    }

    override fun getItemCount(): Int {
        return suggestionMovie.size
    }
    override fun getItemViewType(position: Int) = position
    override fun getItemId(position: Int) = position.toLong()
}