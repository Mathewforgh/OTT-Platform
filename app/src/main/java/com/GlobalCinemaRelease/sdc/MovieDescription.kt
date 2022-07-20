package com.GlobalCinemaRelease.sdc

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ShareCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.GlobalCinemaRelease.sdc.adapters.SuggestionMoviesAdapter
import com.GlobalCinemaRelease.sdc.databinding.ActivityMovieDescriptionBinding
import com.GlobalCinemaRelease.sdc.interfaces.ResponseApi
import com.GlobalCinemaRelease.sdc.msg.*
import com.GlobalCinemaRelease.sdc.msg.listener.setOnDebounceListener
import com.GlobalCinemaRelease.sdc.obj.Store
import com.GlobalCinemaRelease.sdc.response.AddToWatchListDC
import com.GlobalCinemaRelease.sdc.response.GiftCouponDC
import com.GlobalCinemaRelease.sdc.response.MoviesDescriptionData
import com.GlobalCinemaRelease.sdc.response.UpdateMoviePlayedDC
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.payment_done_bottom_sheet.view.*
import kotlinx.android.synthetic.main.redeem_coupon_layout.view.*
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class MovieDescription : AppCompatActivity(), ConnectionReceiver.ConnectionReceiverListener {
    private val ids by lazy { ActivityMovieDescriptionBinding.inflate(layoutInflater) }
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var cost: String
    private var isRented: Boolean = false
    lateinit var movieToken: String
    private var userToken: String = ""
    lateinit var loader: Dialog
    lateinit var barLayot: ConstraintLayout
    lateinit var successPaymentDialog: BottomSheetDialog

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ids.root)

        /*for checking the internet is connection*/
        baseContext.registerReceiver(ConnectionReceiver(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        MyApp.instance.setConnectionListener(this)
/*----------------------------------*/
        barLayot = ids.snackBarLay
        /*-----------------*/
        loader = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar)
        val view: View = View.inflate(this, R.layout.loader, null)
        loader.setContentView(view)
        loader.setCancelable(true)
        loader.create()
        loader.show()
        /*-----------------*/

        sharedPreferences = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        userToken = sharedPreferences.getString("userToken", "").toString()
        sharedPreferences.edit().apply {
            putString("movieToken", Store.movieToken)
                .apply()
        }
        Store.movieToken = sharedPreferences.getString("movieToken", "").toString()

        if (intent.getStringExtra("fromHome") == "fromHome") {
            Store.movieTokenSet = Store.movieToken
            Store.movieTokenFrom = "Home"
        } else if (Store.movieTokenFrom == "Home") {
            Store.movieTokenSet = Store.movieToken
        } else if (Store.movieTokenFrom == "watchList") {
            Store.movieTokenSet = Store.movieToken
        } else {
            Store.movieTokenSet = Store.movieTokenDeepLink
            Store.movieTokenFrom = ""
        }

        /*deep link fire base --------------------------------------*/
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                if (pendingDynamicLinkData != null) {
                    val deepLink: Uri? = pendingDynamicLinkData.link
                    Log.i("dynamic link", "deeplink" + deepLink.toString())
                    val movieTokenSplit = deepLink?.toString()?.split("=")
                    val movie = movieTokenSplit!![1]
                    Store.movieTokenDeepLink = movie
                    Store.movieTokenSet = Store.movieTokenDeepLink
                    setMoviesDescription(userToken, Store.movieTokenSet)
                }
            }
            .addOnFailureListener(this) {
                Log.i("dynamic link", "fails --$it")
            }


        setMoviesDescription(userToken, Store.movieTokenSet)
        btnClickListeners()

        ids.descriptionPageMovieName.apply {
            ellipsize = TextUtils.TruncateAt.MARQUEE
            isSelected = true
        }

        //isRent true then, RentNow btn change into play now btn
        if (isRented || Store.payState == "true") {
            ids.rentNowTv.text = getString(R.string.playNow)
            ids.constraintLayout2.visibility = View.GONE
            ids.watchPartyBtn.visibility = View.VISIBLE
        }


/*-----------payment success , isRented == true then, bottom sheet open-------------------------------*/
        val done: View = layoutInflater.inflate(R.layout.payment_done_bottom_sheet, null)
        successPaymentDialog = BottomSheetDialog(this, R.style.ThemeOverlay_App_BottomSheetDialog)

        if (Store.payState == "true") {
            setMoviesDescription(userToken, Store.movieTokenSet)
            successPaymentDialog.setContentView(done)
            successPaymentDialog.show()
            Store.payState = "false"
        }
        done.PaySuc_watchNow_btn.setOnDebounceListener {
            ids.rentNowTv.text = getString(R.string.playNow)
            ids.constraintLayout2.visibility = View.GONE
            ids.watchPartyBtn.visibility = View.VISIBLE
            successPaymentDialog.cancel()
            isRented = true
            Store.giftCoupon = ""
            Log.i("test", "done.PaySuc_watchNow_btn--Store.movieLink ${Store.movieLink} ")
            startActivity(
                Intent(this@MovieDescription, MoviePlayer::class.java).putExtra("state", "1"))
        }
        done.cancel_btn_img.setOnDebounceListener {
            successPaymentDialog.cancel()
        }
        done.gift_now_btn2.setOnDebounceListener {
            loader.show()
            if (Store.giftCoupon.isEmpty()){
                loader.show()
                setUpCouponCode()
            }else{
                ShareCompat.IntentBuilder.from(this)
                    .setChooserTitle("SDC Global Cinema Release")
                    .setText("*SDC Global Cinema Release*\n" +
                            "${Store.movieName}_Movie Gift Coupon Code is Here: ${Store.giftCoupon}")
                    .setType("text/plain")
                    .startChooser()
                Toast.makeText(this@MovieDescription, "May be your previous Gift Coupon Not Claimed", Toast.LENGTH_SHORT).show()
            }
        }
/*--------------payment success , isRented == true then, bottom sheet open = finished---------------------*/
        ids.watchListBtn.setOnDebounceListener {
            if (Store.from == "guest") startActivity(Intent(this, LoginSignPage::class.java))
            else addToWatchList(Store.movieTokenSet)
        }
    }

    private fun addToWatchList(movieToken: String) {
        try {
            val jsonObject = JsonObject()

            jsonObject.addProperty("userToken", HomePage.userToken)
            jsonObject.addProperty("movieToken", movieToken)

            ResponseApi().addToWatchList(jsonObject).enqueue(object : Callback<AddToWatchListDC?> {
                override fun onResponse(
                    call: Call<AddToWatchListDC?>,
                    response: Response<AddToWatchListDC?>,
                ) {
                    if (response.isSuccessful && response.body()?.code == 201) {
                        Toast.makeText(this@MovieDescription,
                            "Added to WatchList",
                            Toast.LENGTH_SHORT).show()
                        Store.currentBannerToken = ""

                        val img = resources.getDrawable(R.drawable.ic_check)                                         // change the img to tick on click
                        ids.watchListBtn.setCompoundDrawablesWithIntrinsicBounds(img,null,null,null)

                    } else if (response.body()?.code == 503) {
                        val bar: Snackbar = Snackbar.make(barLayot,
                            response.body()?.message.toString(),
                            Snackbar.LENGTH_SHORT)
                        bar.setBackgroundTint(Color.BLACK)
                        bar.show()
                    } else {
                        Toast.makeText(this@MovieDescription,
                            "Something went wrong",
                            Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AddToWatchListDC?>, t: Throwable) {
                    Log.i("test", t.message.toString())
                    Toast.makeText(this@MovieDescription,
                        "Check Internet Connection",
                        Toast.LENGTH_LONG).show()
                }
            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun btnClickListeners() {
        ids.rentNowBtn.setOnDebounceListener {
            if (Store.from == "guest") {
                startActivity(
                    Intent(this@MovieDescription, LoginSignPage::class.java))
                Store.from = ""
            } else {
                Store.movieCost = cost
                startActivity(
                    Intent(this, ProceedToPayment::class.java))
            }
        }
        ids.shareNowBtn.setOnDebounceListener {
            ShareCompat.IntentBuilder.from(this)
                .setText("*SDC Global Cinema Release*\n" + Store.shareNowLink)
                .setChooserTitle("SDC Global Cinema Release")
                .setType("text/plain")
                .startChooser()
            ids.shareNowBtn.animate().apply {
                duration = 1000
                rotationYBy(3f)
            }.start()
        }
        ids.giftNowBtn.setOnDebounceListener {
            if (Store.giftCoupon.isEmpty()) {
                loader.show()
                setUpCouponCode()
            } else {
                ShareCompat.IntentBuilder.from(this)
                    .setChooserTitle("SDC Global Cinema Release")
                    .setText("*SDC Global Cinema Release*\n" +
                            "${Store.movieName}_Movie Gift Coupon Code is Here: ${Store.giftCoupon}")
                    .setType("text/plain")
                    .startChooser()
                Toast.makeText(this@MovieDescription, "May be your previous Gift Coupon Not Claimed", Toast.LENGTH_SHORT).show()
            }
        }
        ids.movieBackBtn.setOnDebounceListener {
            finish()
        }
    }

    private fun setUpCouponCode() {
        try {
            val jsonObject = JsonObject()

            jsonObject.addProperty("userToken", userToken)
            jsonObject.addProperty("movieToken", Store.movieTokenSet)
            jsonObject.addProperty("rentToken", Store.rentToken)

            ResponseApi().generateGiftCouponCode(jsonObject)
                .enqueue(object : Callback<GiftCouponDC?> {
                    override fun onResponse(
                        call: Call<GiftCouponDC?>,
                        response: Response<GiftCouponDC?>,
                    ) {
                        if (response.isSuccessful && response.body()?.code == 201) {
                            Store.giftCoupon = ""
                            Store.giftCoupon = response.body()?.coupanCode.toString()
                            successPaymentDialog.dismiss()
                            startActivity(Intent(this@MovieDescription, this@MovieDescription::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))

                            ShareCompat.IntentBuilder.from(this@MovieDescription)
                                .setChooserTitle("SDC Global Cinema Release")
                                .setText("*SDC Global Cinema Release*\n${Store.movieName}_Movie Gift Coupon Code is Here: ${Store.giftCoupon}")
                                .setType("text/plain")
                                .startChooser()
                            loader.dismiss()
                        } else {
                            toast("You Haven't Purchase this Movie")
                            Log.i("test","${response.body()?.message}")
                            loader.dismiss()
                        }
                    }

                    override fun onFailure(call: Call<GiftCouponDC?>, t: Throwable) {
                        Log.i("tag", t.message.toString())
                        toast("Check Internet Connection")
                        loader.dismiss()
                    }
                })
        } catch (e: JSONException) {
            e.printStackTrace()
            loader.dismiss()
        }
    }

    private fun setMoviesDescription(userToken: String, movieToken: String) {
        val jsonObject = JsonObject()

        try {
            jsonObject.addProperty("userToken", userToken)
            jsonObject.addProperty("movieToken", movieToken)

/*-------------------------call the Api to fetch the movies details */

            ResponseApi().movieDescriptionDetails(jsonObject)
                .enqueue(object : Callback<MoviesDescriptionData?> {
                    override fun onResponse(
                        call: Call<MoviesDescriptionData?>,
                        response: Response<MoviesDescriptionData?>
                    ) {
                        if (response.isSuccessful) {
                            loader.dismiss()
                            if (response.body()?.code == 201) {
                                Store.posterLink =
                                    response.body()?.movieDetails?.posterLink.toString()

                                Glide.with(this@MovieDescription)
                                    .load(response.body()?.movieDetails?.posterLink)
                                    .into(ids.imageView4)

                                ids.apply {
                                    descriptionPageMovieName.text =
                                        response.body()?.movieDetails!!.movieName
                                    Store.movieName =
                                        response.body()?.movieDetails!!.movieName.toString()

                                    movieTitleDetailsTv.text =
                                        response.body()?.movieDetails!!.movieDetails!!
                                    Store.movieDesc =
                                        response.body()?.movieDetails?.movieDetails.toString()

                                    descriptionPageStarringTv.text =
                                        response.body()?.movieDetails!!.starring

                                    textView12.text =
                                        response.body()?.movieDetails!!.movieDescription

                                    descriptionPageMovieCostPayment.text =
                                        response.body()?.movieDetails!!.inrAmount

                                    movieDurationTv.text =
                                        response.body()?.movieDetails!!.rentDetails!!
                                }
                                cost = response.body()?.movieDetails!!.inrAmount.toString()

                                Store.shareNowLink =
                                    response.body()?.movieDetails?.dyanamic_url.toString()
                                Store.giftCoupon =
                                    response.body()?.movieDetails?.giftCoupan.toString()

                                Store.rentToken =
                                        response.body()?.movieDetails?.rentToken.toString()

                                        /*--------------------------set suggestion movies list */
                                ids.descriptionPageRecyclerView.layoutManager =
                                    LinearLayoutManager(this@MovieDescription,
                                        LinearLayoutManager.HORIZONTAL,
                                        false)
//                            if (response.body()?.searchTitle == "Suggestion") {
                                ids.descriptionPageRecyclerView.adapter =
                                    SuggestionMoviesAdapter(response.body()?.suggestions!!)
//                            }
                                ids.descriptionPagePlayTrailerBtn.setOnDebounceListener {
                                    Store.movieLink =
                                        response.body()?.movieDetails!!.trailer_link.toString()
                                    Log.i("test", "descriptionPagePlayTrailerBtn--Store.movieLink ${Store.movieLink} ")
                                    startActivity(Intent(this@MovieDescription,
                                        MoviePlayer::class.java))
                                }
                                isRented = response.body()?.movieDetails!!.isRented!!
                                if (isRented) {
//                                    fullMovieLink =
//                                        response.body()?.movieDetails!!.movie_link.toString()
                                    ids.rentNowTv.text = getString(R.string.playNow)
                                    ids.constraintLayout2.visibility = View.GONE
                                    ids.watchPartyBtn.visibility = View.VISIBLE
                                    Store.movieLink = response.body()?.movieDetails!!.movie_link.toString()
                                    Log.i("test", "while isRented true--Store.movieLink ${Store.movieLink} ")
                                }
                                if (Store.payState == "true" || Store.code == "redeemed") {
                                    Store.movieLink =
                                        response.body()?.movieDetails?.movie_link.toString()
                                    Log.i("test", "Store.payState == \"true\" || Store.code == \"redeemed\"--Store.movieLink ${Store.movieLink} ")
                                }
                                ids.descripRentNowBtn.setOnDebounceListener {
                                    isRented = response.body()?.movieDetails!!.isRented!!
                                    if (!isRented) {        //if not true, so false, condition true
                                        if (Store.from == "guest") {
                                            startActivity(
                                                Intent(this@MovieDescription,
                                                    LoginSignPage::class.java)
                                            )
                                        } else {
                                            Store.movieCost = cost
                                            startActivity(
                                                Intent(this@MovieDescription,
                                                    ProceedToPayment::class.java))
                                        }
                                    } else {
                                        Store.movieLink = ""
                                        Store.movieLink =
                                            response.body()?.movieDetails?.movie_link.toString()
                                        Log.i("test", "descripRentNowBtn isRent true--Store.movieLink ${Store.movieLink} ")
                                        Store.rentToken = response.body()?.movieDetails?.rentToken.toString()
                                        notifyMoviePlayed()
                                        startActivity(Intent(this@MovieDescription,
                                            MoviePlayer::class.java))
                                    }
                                }
                                ids.suggestMovieSeeAllBtnTv.setOnDebounceListener {
                                    Store.searchToken =
                                        response.body()?.suggestions?.get(0)?.movieToken.toString()
                                    Store.type = response.body()?.searchTitle.toString()
                                    Store.resTitle = ids.suggTV.text.toString()
                                    startActivity(Intent(this@MovieDescription,
                                        SeeAllMovies::class.java))
                                    Store.from_SeeAll = "MDSeeAll"
                                }
                                Store.watchList = response.body()?.movieDetails?.isWatchlist!!
                                if (Store.watchList){
                                    val img = resources.getDrawable(R.drawable.ic_check)
                                    ids.watchListBtn.setCompoundDrawablesWithIntrinsicBounds(img,null,null,null)
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<MoviesDescriptionData?>, t: Throwable) {
                        Log.e("Fail1", t.message.toString())
                    }
                })
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        when (Store.from_SeeAll) {
            "seeAll" -> {
                if (Store.fromSuggest == "fromSuggest") {
                    super.onBackPressed()
                } else {
                    Store.from_SeeAll = ""
                    Store.movieToken = ""
                    finish()
                }
            }
            "watchList" -> {
                Store.from_SeeAll = ""
                Store.movieToken = ""
                super.onBackPressed()
            }
            else -> {
                if (Store.backOnPayDone == "done") {
                    Store.backOnPayDone = ""
                    startActivity(Intent(this, HomePage::class.java))
                } else {
                    finish()
                }
            }
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected) {
            Store.internet = "1"
            checkInternetPopUp(this@MovieDescription, Store.internet)
        } else {
            Store.internet = "0"
            checkInternetPopUp(this@MovieDescription, Store.internet)
        }
    }

    override fun onResume() {
        super.onResume()
//        setMoviesDescription(userToken, Store.movieTokenSet)
        /* Coupon code redeemed Bottom sheet set up*/
        val botSheet: View = layoutInflater.inflate(R.layout.redeem_coupon_layout, null)
        val dialog2 = BottomSheetDialog(this, R.style.ThemeOverlay_App_BottomSheetDialog)

        if (Store.code == "redeemed") {
            setMoviesDescription(userToken, Store.movieTokenSet)
            dialog2.setContentView(botSheet)
            dialog2.show()
            Store.code = ""
            botSheet.cancelBtn.setOnDebounceListener {
                dialog2.dismiss()
            }
            botSheet.watchNowBtn.setOnClickListener {
                startActivity(Intent(this@MovieDescription, this@MovieDescription::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                notifyMoviePlayed()
                startActivity(
                    Intent(this@MovieDescription, MoviePlayer::class.java)
                        .putExtra("state", "1"))
                dialog2.dismiss()
            }
        }
    }

    private fun notifyMoviePlayed() {
        try {
            val jsonObject = JsonObject()

            jsonObject.addProperty("userToken", HomePage.userToken)
            jsonObject.addProperty("movieToken", Store.movieToken)
            jsonObject.addProperty("rentToken", Store.rentToken)

            ResponseApi().updateMoviePlayed(jsonObject).enqueue(object : Callback<UpdateMoviePlayedDC?> {
                override fun onResponse(
                    call: Call<UpdateMoviePlayedDC?>,
                    response: Response<UpdateMoviePlayedDC?>,
                ) {
                    if (response.isSuccessful){
                        Log.i("test", "updated movie played rent token-${Store.rentToken},${Store.movieToken}")
                    }else Log.i("test", "not updated movie played rent token-${Store.rentToken},${Store.movieToken}")
                }

                override fun onFailure(call: Call<UpdateMoviePlayedDC?>, t: Throwable) {
                    Log.i("test", "not updated movie played- api fails")
                }
            })
        }
        catch (e: JSONException){
            e.printStackTrace()
        }
    }
}
