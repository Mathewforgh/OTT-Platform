package com.GlobalCinemaRelease.sdc.msg

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.view.View
import com.GlobalCinemaRelease.sdc.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.no_internet_popup.view.*

fun checkInternetPopUp (context: Context, internet: String) {
    val done: View = View.inflate(context,R.layout.no_internet_popup,null)
    val dialog = BottomSheetDialog(context, R.style.ThemeOverlay_App_BottomSheetDialog)
    dialog.setContentView(done)
    dialog.setCancelable(true)
    if (internet == "0") {
        dialog.show()
        val network = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        network.registerDefaultNetworkCallback(object: ConnectivityManager.NetworkCallback(){
            override fun onAvailable(network: Network) {
                context.startActivity(Intent(context, context::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                super.onAvailable(network)
            }
        })
        done.button2.setOnClickListener {
            val intent = Intent(context, context::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(intent)
        }
    }
    else {
        dialog.dismiss()
    }
}