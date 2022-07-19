package com.GlobalCinemaRelease.sdc.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.GlobalCinemaRelease.sdc.response.Data
import com.GlobalCinemaRelease.sdc.dataClass.LanguageSlideData
import com.GlobalCinemaRelease.sdc.databinding.LanguageSlideBinding
import com.GlobalCinemaRelease.sdc.interfaces.OnClick
import com.GlobalCinemaRelease.sdc.msg.listener.setOnDebounceListener

class LanguageSlideAdapter(private val data: List<Data>,
                           private val listLanguage: ArrayList<LanguageSlideData>,
                           private val onClick: OnClick) : RecyclerView.Adapter<LanguageSlideAdapter.ViewHolder>() {
    private lateinit var ids: LanguageSlideBinding

    inner class ViewHolder(ids: LanguageSlideBinding) : RecyclerView.ViewHolder(ids.root) {
        fun bind(data: Data,lang : LanguageSlideData) {

            ids.apply {
                languageTv.text = data.view_name!!
                cardLayoutId.setBackgroundColor(lang.bgColor)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        ids = LanguageSlideBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(ids)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position],listLanguage[position])
        val checkLanguage = ids.check
        holder.itemView.setOnDebounceListener {
            onClick.onItemClick(position,holder,data,checkLanguage) // status
        }
    }
    override fun getItemViewType(position: Int) = position
    override fun getItemId(position: Int) = position.toLong()
}