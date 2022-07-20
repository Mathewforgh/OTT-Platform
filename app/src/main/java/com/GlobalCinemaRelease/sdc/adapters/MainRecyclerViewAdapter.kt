package com.GlobalCinemaRelease.sdc.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.GlobalCinemaRelease.sdc.HomePage
import com.GlobalCinemaRelease.sdc.SeeAllMovies
import com.GlobalCinemaRelease.sdc.databinding.MainRecyclerViewItemBinding
import com.GlobalCinemaRelease.sdc.msg.listener.setOnDebounceListener
import com.GlobalCinemaRelease.sdc.obj.Store
import com.GlobalCinemaRelease.sdc.response.DataX
import com.GlobalCinemaRelease.sdc.response.DataXX

class MainRecyclerViewAdapter(
    private val context: Context,
    private val allCategory: MutableList<DataX>?,
) :
    RecyclerView.Adapter<MainRecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(val ids: MainRecyclerViewItemBinding) :
        RecyclerView.ViewHolder(ids.root) {
        fun bind(titles: DataX, language: List<DataXX>) {

            ids.apply {
                if (titles.type == "language") {
                    seeAllTV.visibility = View.GONE
                    CategoryRecyclerView.layoutManager =
                        LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
                    CategoryRecyclerView.adapter =
                        LanguageSlideAdapterHP(language, HomePage.languageList)
                }
                else if (language.isNotEmpty() && titles.type != "Banner") {
                    CategoryTitle.text = titles.title

                    CategoryRecyclerView.layoutManager =
                        LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
                    CategoryRecyclerView.adapter =
                        titles.data?.let { CategoryItemMoviesAdapter(it) }
                }
                else {
                    ids.apply {
                        seeAllTV.visibility = View.GONE
                        mainViewRecycler.visibility = View.GONE
                        linearLayout19.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val ids =
            MainRecyclerViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(ids)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        allCategory?.get(position)?.data?.let { holder.bind(allCategory[position], it) }

        holder.ids.seeAllTV.setOnDebounceListener {
            Store.searchToken = allCategory?.get(position)?.token.toString()
            Store.type = allCategory?.get(position)?.searchTitle.toString()
            Store.resTitle = allCategory?.get(position)?.title.toString()

            holder.itemView.context.startActivity(Intent(holder.itemView.context,
                SeeAllMovies::class.java))
        }
    }

    override fun getItemCount(): Int {
        return allCategory!!.size
    }

    override fun getItemViewType(position: Int) = position
    override fun getItemId(position: Int) = position.toLong()
}