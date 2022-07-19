package com.GlobalCinemaRelease.sdc.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.GlobalCinemaRelease.sdc.databinding.GiftedHistoryMovieListBinding
import com.GlobalCinemaRelease.sdc.response.GiftedHistory

class GiftedHistoryAdapter(private val giftList: List<GiftedHistory?>?):
    RecyclerView.Adapter<GiftedHistoryAdapter.ViewHolder>(){
        private lateinit var ids: GiftedHistoryMovieListBinding

        inner class ViewHolder(ids: GiftedHistoryMovieListBinding): RecyclerView.ViewHolder(ids.root){
            fun bind(data: GiftedHistory){
                ids.apply {
                    Glide.with(itemView.context).load(data.posterLink).fitCenter().into(giftedHistoryMovieListImg)
                    giftedHistoryMovieTitle.text = data.movieName
                    gfMovieDescriptionTv.text = data.movieDetails
                    gfMovieCostPayment.text = data.Payment
                    gfMoviePurchaseDate.text = data.purchasedDate
                    gfRedeemedBy.text = data.redeemedBy
                    gfId.text = data.giftId
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        ids = GiftedHistoryMovieListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(ids)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        giftList?.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return giftList!!.size
    }
    override fun getItemViewType(position: Int) = position
    override fun getItemId(position: Int) = position.toLong()
}