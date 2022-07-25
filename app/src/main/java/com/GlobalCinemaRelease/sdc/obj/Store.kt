package com.GlobalCinemaRelease.sdc.obj

import android.content.Context
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.LiveData
import com.GlobalCinemaRelease.sdc.HomePage
import com.GlobalCinemaRelease.sdc.MovieDescription
import com.GlobalCinemaRelease.sdc.R
import com.GlobalCinemaRelease.sdc.interfaces.ResponseApi
import com.GlobalCinemaRelease.sdc.response.DataXX
import com.GlobalCinemaRelease.sdc.response.DataXXXXX
import com.GlobalCinemaRelease.sdc.response.MoviesDescriptionData
import com.google.gson.JsonObject
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

object Store {
    var rentToken: String = ""
    var movieToken: String = ""
    var movieTokenDeepLink: String = ""
    var movieTokenSet: String = ""
    var movieTokenFrom = ""
    var code: String = ""


    var giftCoupon: String = ""
    var rating: String =""
    var isRated: Boolean = false
    var watchList by Delegates.notNull<Boolean>()
    var currentBannerNum: String = ""

    var movieCost: String =""
    var movieLink: String =""
    var movieName: String =""
    var movieDesc: String =""

    var posterLink: String =""

    var phNumber: String = ""
    var countryCode : String = ""

    var userName : String = ""
    var userEmail : String = ""

    var from : String = ""
    var from_SeeAll : String = ""
    var fromSuggest: String = ""

    var payState: String = ""

    var searchToken: String = ""
    var type: String = ""
    var typeStatic: String = ""
    var resTitle: String = ""
    var backOnPayDone: String = ""

    var shareNowLink: String = ""

    var tempVar: String = ""
    var internet: String = "1"
    var theme: String = ""

    var socialIdFrom: String= ""
    var socialId: String = ""
    var ActivityFinish: String = ""

    var currentBannerToken: String = ""

    var movieTokenList = ArrayList<String>()

    lateinit var tempBanner: List<DataXX?>

    lateinit var tempSuggestMovieTitle: List<DataXXXXX?>


/* Animations functions here ---------------------------------*/
    fun blink(context: Context): Animation {
        return AnimationUtils.loadAnimation(context, R.anim.blink_btn)
    }
    fun zoomOut(context: Context):Animation{
        return AnimationUtils.loadAnimation(context, R.anim.zoom_out)
    }
    fun rotate(context: Context):Animation{
        return AnimationUtils.loadAnimation(context, R.anim.rotate)
    }
    fun slideDown(context: Context):Animation{
        return AnimationUtils.loadAnimation(context, R.anim.slide_down)
    }
    fun moveRight(context: Context):Animation{
        return AnimationUtils.loadAnimation(context, R.anim.move_right)
    }
    fun fadeIn(context: Context):Animation{
        return AnimationUtils.loadAnimation(context, R.anim.fade_in)
    }
    fun zoomFadeIn(context: Context):Animation{
        return AnimationUtils.loadAnimation(context, R.anim.recycler_view_animation)
    }
}