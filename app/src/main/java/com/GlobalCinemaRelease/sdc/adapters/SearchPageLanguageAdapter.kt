package com.GlobalCinemaRelease.sdc.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.GlobalCinemaRelease.sdc.SeeAllMovies
import com.GlobalCinemaRelease.sdc.dataClass.LanguageSlideData
import com.GlobalCinemaRelease.sdc.databinding.LanguageSlideBinding
import com.GlobalCinemaRelease.sdc.obj.Store
import com.GlobalCinemaRelease.sdc.response.DataXXXXX

class SearchPageLanguageAdapter(private val language: List<DataXXXXX>,
                                private val listLanguage: ArrayList<LanguageSlideData>,) :
    RecyclerView.Adapter<SearchPageLanguageAdapter.ViewHolder>(){
    private lateinit var ids: LanguageSlideBinding

    inner class ViewHolder(ids: LanguageSlideBinding) : RecyclerView.ViewHolder(ids.root) {
        fun bind(data: DataXXXXX, lang : LanguageSlideData) {
            ids.apply {
                languageTv.text= data.view_name!!
                cardLayoutId.setBackgroundColor(lang.bgColor)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchPageLanguageAdapter.ViewHolder {
        ids = LanguageSlideBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(ids)
    }

    override fun getItemCount(): Int {
        return language.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(language[position],listLanguage[position])
        holder.itemView.setOnClickListener {
            Store.resTitle = language[position].name.toString()
            Store.searchToken = language[position].token.toString()
            Store.type = "Language"
            holder.itemView.context.startActivity(Intent(holder.itemView.context, SeeAllMovies::class.java))
        }
    }
    override fun getItemViewType(position: Int) = position
    override fun getItemId(position: Int) = position.toLong()
}