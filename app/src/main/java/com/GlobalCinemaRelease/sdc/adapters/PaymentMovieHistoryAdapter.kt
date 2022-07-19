package com.GlobalCinemaRelease.sdc.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.GlobalCinemaRelease.sdc.MovieDescription
import com.GlobalCinemaRelease.sdc.ProceedToPayment
import com.bumptech.glide.Glide
import com.GlobalCinemaRelease.sdc.RateMovie
import com.GlobalCinemaRelease.sdc.databinding.PaymentHistoryMovieListBinding
import com.GlobalCinemaRelease.sdc.msg.listener.setOnDebounceListener
import com.GlobalCinemaRelease.sdc.obj.Store
import com.GlobalCinemaRelease.sdc.response.PaymentHistory

class PaymentMovieHistoryAdapter(private val paymentList: List<PaymentHistory?>?) :
    RecyclerView.Adapter<PaymentMovieHistoryAdapter.ViewHolder>() {

    inner class ViewHolder(val ids: PaymentHistoryMovieListBinding) :
        RecyclerView.ViewHolder(ids.root) {
        fun bind(dataList: PaymentHistory) {
            ids.apply {
                paymentHistoryMovieTitle.text = dataList.movieName
                movieDescriptionTv.text = dataList.movieDetails
                movieCostPayment.text = dataList.Payment
                moviePurchaseDate.text = dataList.purchasedDate
                Glide.with(itemView.context).load(dataList.posterLink).fitCenter()
                    .into(paymentHistoryMovieListImg)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val ids = PaymentHistoryMovieListBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false)
        return ViewHolder(ids)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(paymentList!![position]!!)

        holder.ids.rateMovieBtn.setOnDebounceListener {
            Store.posterLink = paymentList[position]?.posterLink.toString()
            Store.movieName = paymentList[position]?.movieName.toString()
            Store.movieDesc = paymentList[position]?.movieDetails.toString()
            Store.rentToken = paymentList[position]?.rentToken.toString()
            Store.rating = paymentList[position]?.rating.toString()
            Store.isRated = paymentList[position]?.isRated!!

            holder.itemView.context.startActivity(Intent(holder.itemView.context,
                RateMovie::class.java))
        }

        holder.ids.rentAgainBtn.setOnDebounceListener {
            Store.movieTokenFrom = "Home" /*for identification address will put like Home
            because movie token set see the 85 to 94 lines in movie description page*/
            Store.movieTokenSet = paymentList[position]?.movieToken.toString()
            Store.movieToken = paymentList[position]?.movieToken.toString()
            Store.posterLink = paymentList[position]?.posterLink.toString()
            Store.movieName = paymentList[position]?.movieName.toString()
            val ch = paymentList[position]?.Payment?.split("/")
            val ch1 = ch!![0].split("₹")
            Store.movieCost = ch1[1]
            Store.movieDesc = paymentList[position]?.movieDetails.toString()

            holder.itemView.context.startActivity(Intent(holder.itemView.context,
                ProceedToPayment::class.java))
        }

        holder.ids.paymentHistoryMovieListImg.setOnDebounceListener {
            holder.ids.paymentHistoryMovieListImg.startAnimation(Store.blink(holder.itemView.context))

            Store.movieTokenFrom = "Home" /*for identification address will put like Home
            because movie token set see the 85 to 94 lines in movie description page*/
            Store.movieToken = paymentList[position]?.movieToken.toString()

            holder.itemView.context.startActivity(Intent(holder.itemView.context,
                MovieDescription::class.java))
        }
    }

    override fun getItemCount(): Int {
        return paymentList!!.size
    }

    override fun getItemViewType(position: Int) = position
    override fun getItemId(position: Int) = position.toLong()
}