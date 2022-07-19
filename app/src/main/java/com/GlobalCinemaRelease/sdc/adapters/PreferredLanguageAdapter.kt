package com.GlobalCinemaRelease.sdc.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.GlobalCinemaRelease.sdc.dataClass.LanguageSlideData
import com.GlobalCinemaRelease.sdc.databinding.LanguageSlideBinding
import com.GlobalCinemaRelease.sdc.response.PreferedLanguage

class PreferredLanguageAdapter(private val preLan: List<PreferedLanguage?>,
                               private val bgColor: ArrayList<LanguageSlideData>):
    RecyclerView.Adapter<PreferredLanguageAdapter.ViewHolder>(){
    private lateinit var ids: LanguageSlideBinding

    inner class ViewHolder(ids: LanguageSlideBinding): RecyclerView.ViewHolder(ids.root){
        fun binding(data: PreferedLanguage?, bgColor: LanguageSlideData){
            ids.apply {
                languageTv.text = data?.view_name
                cardLayoutId.setBackgroundColor(bgColor.bgColor)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        ids = LanguageSlideBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return ViewHolder(ids)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding(preLan[position]!!, bgColor[position])
    }

    override fun getItemCount(): Int {
        return preLan.size
    }
    override fun getItemViewType(position: Int) = position
    override fun getItemId(position: Int) = position.toLong()
}