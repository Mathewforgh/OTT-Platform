package com.GlobalCinemaRelease.sdc.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.GlobalCinemaRelease.sdc.SeeAllMovies
import com.GlobalCinemaRelease.sdc.databinding.SuggestMovieLayoutBinding
import com.GlobalCinemaRelease.sdc.msg.listener.setOnDebounceListener
import com.GlobalCinemaRelease.sdc.obj.Store
import com.GlobalCinemaRelease.sdc.response.DataXXXX
import com.GlobalCinemaRelease.sdc.response.DataXXXXX

class SuggestMovieTitlesAdapter(private val titleList: List<DataXXXXX?>):
    RecyclerView.Adapter<SuggestMovieTitlesAdapter.ViewHolder>(){

        inner class ViewHolder(val ids: SuggestMovieLayoutBinding): RecyclerView.ViewHolder(ids.root){
            fun binding(data: DataXXXXX){
                ids.SuggestMovieTitle.text = data.name
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val ids = SuggestMovieLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(ids)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.startAnimation(Store.slideDown(holder.itemView.context))
        titleList[position]?.let { holder.binding(it) }

        holder.itemView.setOnDebounceListener {
            Store.resTitle = titleList[position]?.name.toString()
            Store.searchToken = titleList[position]?.token.toString()
            Store.type = Store.typeStatic

            holder.itemView.context.startActivity(Intent(holder.itemView.context, SeeAllMovies::class.java))
        }
    }

    override fun getItemCount(): Int {
        return titleList.size
    }

    override fun getItemViewType(position: Int) = position
    override fun getItemId(position: Int) = position.toLong()
}