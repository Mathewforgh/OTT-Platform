package com.GlobalCinemaRelease.sdc

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.GlobalCinemaRelease.sdc.adapters.BannerViewPagerAdapter
import com.GlobalCinemaRelease.sdc.adapters.LanguageSlideAdapterHP
import com.GlobalCinemaRelease.sdc.adapters.CategoryItemMoviesAdapter
import com.GlobalCinemaRelease.sdc.adapters.MainRecyclerViewAdapter
import com.GlobalCinemaRelease.sdc.dataClass.LanguageSlideData
import com.GlobalCinemaRelease.sdc.databinding.FragmentHomeBinding
import com.GlobalCinemaRelease.sdc.interfaces.ResponseApi
import com.GlobalCinemaRelease.sdc.msg.ZoomOutPageTransformer
import com.GlobalCinemaRelease.sdc.msg.listener.setOnDebounceListener
import com.GlobalCinemaRelease.sdc.obj.Store
import com.GlobalCinemaRelease.sdc.response.AddToWatchListDC
import com.GlobalCinemaRelease.sdc.response.DataX
import com.GlobalCinemaRelease.sdc.response.HomePageData
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    private lateinit var ids: FragmentHomeBinding
    lateinit var loader: Dialog
    lateinit var barLayout: RelativeLayout
    private val sliderHandler = Handler(Looper.getMainLooper())
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        ids = FragmentHomeBinding.inflate(inflater, container, false)

        barLayout = ids.barLayout


       setMovies(HomePage.userToken, HomePage.languageList)

        ids.homePageShareNowBtn.setOnDebounceListener {
            ids.homePageShareNowBtn.startAnimation(this@HomeFragment.activity?.let { it1 ->
                Store.blink(it1)
            })
            ShareCompat.IntentBuilder.from(this.requireActivity())
                .setText("*SDC Global Cinema Release*\n" + Store.shareNowLink)
                .setChooserTitle("SDC Global Cinema Release")
                .setType("text/plain")
                .startChooser()
        }

/*banner slider */
        val sliderRunnable = Runnable {
            ids.homePageBannerViewPager.currentItem = ids.homePageBannerViewPager.currentItem + 1
        }

        ids.homePageBannerViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 5000)
            }
        })


        /*-----------------*/
        loader = Dialog(this@HomeFragment.activity!!, android.R.style.Theme_Translucent_NoTitleBar)
        val view: View = View.inflate(this@HomeFragment.activity!!, R.layout.loader, null)
        loader.setContentView(view)
        loader.setCancelable(true)
        loader.create()
        loader.show()
        /*-----------------*/

        return ids.root
    }

    private fun setMovies(userToken: String?, languageList: ArrayList<LanguageSlideData>) {
        val jsonObject = JsonObject()
        try {
            jsonObject.addProperty("userToken", userToken)

/*---------------------------- call the Api to set movies---------------*/
            ResponseApi().setHomePageMovie(jsonObject).enqueue(object : Callback<HomePageData?> {
                override fun onResponse(
                    call: Call<HomePageData?>,
                    response: Response<HomePageData?>,
                ) {
                    if (response.isSuccessful) {
                        Store.ActivityFinish = "finish"
                        loader.dismiss()
                        if (response.body()?.code!! == 201) {
                            ids.apply {
                                homePageBannerViewPager.adapter =
                                    response.body()?.data?.get(0)?.let {
                                        it.data?.let { it1 -> BannerViewPagerAdapter(it1) }
                                    }
                                homePageBannerViewPager.setPageTransformer(ZoomOutPageTransformer())

                                homePageBannerViewPager.registerOnPageChangeCallback(object :
                                    ViewPager2.OnPageChangeCallback() {
                                    override fun onPageScrolled(
                                        position: Int,
                                        positionOffset: Float,
                                        positionOffsetPixels: Int,
                                    ) {
                                        changeNames()
                                        super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                                        if (Store.watchList)
                                            ids.watchListImg.setImageResource(R.drawable.ic_watchlist_tick)
                                        else
                                            ids.watchListImg.setImageResource(R.drawable.add_watchlist)
                                    }

                                    override fun onPageScrollStateChanged(state: Int) {
                                        super.onPageScrollStateChanged(state)
                                        changeNames()
                                    }
                                })
  // Home Page Main Recycler View Adapter set up Here...
                                MainRecyclerView.layoutManager =
                                    LinearLayoutManager(this@HomeFragment.activity, LinearLayoutManager.VERTICAL, false)
                                MainRecyclerView.adapter =
                                    this@HomeFragment.activity?.let {
                                        MainRecyclerViewAdapter(it,
                                            response.body()?.data as MutableList<DataX>?)
                                    }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<HomePageData?>, t: Throwable) {
                    Toast.makeText(this@HomeFragment.activity, "${t.message}", Toast.LENGTH_LONG)
                        .show()
                }
            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun changeNames() {
        ids.homePageBannerMovieName.text =
            Store.tempBanner[ids.homePageBannerViewPager.currentItem]?.movieName.toString()
        ids.homePageBannerMovieDescription.text =
            Store.tempBanner[ids.homePageBannerViewPager.currentItem]?.movieDetails.toString()
        Store.currentBannerToken =
            Store.tempBanner[ids.homePageBannerViewPager.currentItem]?.movieToken.toString()
        Store.shareNowLink =
            Store.tempBanner[ids.homePageBannerViewPager.currentItem]?.dyanamic_url.toString()

        Store.watchList =
            Store.tempBanner[ids.homePageBannerViewPager.currentItem]?.isWatchlist!!

        Store.currentBannerNum = ids.homePageBannerViewPager.currentItem.toString()
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
                        Toast.makeText(this@HomeFragment.activity,
                            "Added to WatchList",
                            Toast.LENGTH_SHORT).show()
                        Store.currentBannerToken = ""

                    } else if (response.body()?.code == 503) {
                        val bar: Snackbar = Snackbar.make(barLayout,response.body()?.message.toString(), Snackbar.LENGTH_SHORT)
                        bar.setBackgroundTint(Color.BLACK)
                        bar.show()
                    }
                    else {
                        Toast.makeText(this@HomeFragment.activity,
                            "Something went wrong",
                            Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AddToWatchListDC?>, t: Throwable) {
                    Log.i("test", t.message.toString())
                    Toast.makeText(this@HomeFragment.activity,
                        "Check Internet Connection",
                        Toast.LENGTH_LONG).show()
                }
            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        setMovies(HomePage.userToken, HomePage.languageList)

        ids.apply {
            homePageWatchListBtn.setOnDebounceListener {
                homePageWatchListBtn.startAnimation(this@HomeFragment.activity?.let { it1 ->
                    Store.blink(it1)
                })
                if (Store.from == "guest"){
                    startActivity(Intent(this@HomeFragment.activity, LoginSignPage::class.java))
                }else if (Store.currentBannerNum == homePageBannerViewPager.currentItem.toString()){
                    watchListImg.setImageResource(R.drawable.ic_watchlist_tick)
                    addToWatchList(Store.currentBannerToken)
                    setMovies(HomePage.userToken, HomePage.languageList)
                }
            }
        }
    }
}