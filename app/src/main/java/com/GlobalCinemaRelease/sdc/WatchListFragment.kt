package com.GlobalCinemaRelease.sdc

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.GlobalCinemaRelease.sdc.adapters.SearchMoviesAdapter
import com.GlobalCinemaRelease.sdc.adapters.ShowWatchListMovieAdapter
import com.GlobalCinemaRelease.sdc.databinding.FragmentWatchListBinding
import com.GlobalCinemaRelease.sdc.interfaces.ResponseApi
import com.GlobalCinemaRelease.sdc.msg.listener.setOnDebounceListener
import com.GlobalCinemaRelease.sdc.obj.Store
import com.GlobalCinemaRelease.sdc.response.DataXXXXXX
import com.GlobalCinemaRelease.sdc.response.RemoveMovieFwatchListDC
import com.GlobalCinemaRelease.sdc.response.SearchMoviesDC
import com.GlobalCinemaRelease.sdc.response.WatchListMovieShowDC
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class WatchListFragment : Fragment(), ShowWatchListMovieAdapter.FromShowWatchListMovieAdapter {
    private lateinit var ids: FragmentWatchListBinding
    lateinit var loader: Dialog
    var responseArray: ArrayList<DataXXXXXX?> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ids = FragmentWatchListBinding.inflate(inflater, container, false)

        showWatchListMovie()

        var txt: String
        ids.searchEdTxt.doAfterTextChanged { it ->
            txt = it.toString()
            if (txt.isNotEmpty()) {
                Log.i("tag", responseArray.toString())
                txt = ids.searchEdTxt.text.toString().lowercase()
                val filteredList = responseArray.filter { it?.movieName!!.lowercase().contains(txt) }
                val test = false
                ids.apply {
                    RecyclerViewWatchListMovie.layoutManager =
                        GridLayoutManager(this@WatchListFragment.activity, 2)
                    RecyclerViewWatchListMovie.adapter =
                        ShowWatchListMovieAdapter(filteredList,
                            this@WatchListFragment, test)
                }
            }
            else{
                showWatchListMovie()
            }
        }

        /*-----------------*/
        loader = Dialog(this@WatchListFragment.activity!!, android.R.style.Theme_Translucent_NoTitleBar)
        val view: View = View.inflate(this@WatchListFragment.activity!!, R.layout.loader, null)
        loader.setContentView(view)
        loader.setCancelable(true)
        loader.create()
        loader.show()
        /*-----------------*/

        return ids.root
    }

    private fun showWatchListMovie() {
        try {
            val jsonObject = JsonObject()

            jsonObject.addProperty("userToken", HomePage.userToken)

            ResponseApi().watchListMovieShow(jsonObject).enqueue(object : Callback<WatchListMovieShowDC?> {
                override fun onResponse(
                    call: Call<WatchListMovieShowDC?>,
                    response: Response<WatchListMovieShowDC?>,
                ) {
                    if (response.isSuccessful && response.body()?.code == 201){
                        loader.dismiss()
                        val test = false

                        responseArray.clear()
                        responseArray = response.body()?.data!! as ArrayList<DataXXXXXX?> /* = java.util.ArrayList<com.GlobalCinemaRelease.sdc.response.DataXXXXXX?> */

                        ids.apply {
                            RecyclerViewWatchListMovie.layoutManager =
                                GridLayoutManager(this@WatchListFragment.activity, 2)
                            RecyclerViewWatchListMovie.adapter =
                                ShowWatchListMovieAdapter(responseArray,
                                    this@WatchListFragment, test)
                        }
                    }
                    else{
                        //Toast.makeText(this@WatchListFragment.activity, "Something went wrong", Toast.LENGTH_SHORT).show()
                        loader.dismiss()
                    }
                }

                override fun onFailure(call: Call<WatchListMovieShowDC?>, t: Throwable) {
                    Log.i("test", t.message.toString())
                    Toast.makeText(this@WatchListFragment.activity, "Check Internet Connection", Toast.LENGTH_SHORT).show()
                    loader.dismiss()
                }
            })
        }
        catch (e: JSONException){
            e.printStackTrace()
            loader.dismiss()
        }
    }

    override fun onItemLongPress(movie: List<DataXXXXXX?>?, test: Boolean) {
        ids.apply {
            removeMovieBtn.visibility = View.VISIBLE
            removeMovieBtn.setOnDebounceListener {
                if (Store.movieTokenList.isEmpty()){
                    Toast.makeText(activity, "Please select", Toast.LENGTH_SHORT).show()
                }
                else {
                    removeMovieBtn.visibility = View.GONE
                    removeFromWatchList()
                }
            }
            RecyclerViewWatchListMovie.adapter =
                ShowWatchListMovieAdapter(movie,
                    this@WatchListFragment, test)
//            for (position in 0 until movie!!.size){
//                val v = ids.RecyclerViewWatchListMovie.getChildAt(position)
//                    v.tickUnSelect.visibility = View.VISIBLE
//            }
        }
    }

    private fun removeFromWatchList() {
        try {
            val jsonObject = JsonObject()

            val array = JsonArray()
            for (i in Store.movieTokenList){
                array.add(i)
            }

            jsonObject.addProperty("userToken", HomePage.userToken)
            jsonObject.add("movieTokens", array)

            ResponseApi().removeMovieFromWatchList(jsonObject).enqueue(object : Callback<RemoveMovieFwatchListDC?> {
                override fun onResponse(
                    call: Call<RemoveMovieFwatchListDC?>,
                    response: Response<RemoveMovieFwatchListDC?>,
                ) {
                    if (response.isSuccessful && response.body()?.code == 201){
                        Toast.makeText(this@WatchListFragment.activity, "Removed", Toast.LENGTH_SHORT)
                            .show()
                        showWatchListMovie()
                        Store.movieTokenList.clear()
                    }
                }

                override fun onFailure(call: Call<RemoveMovieFwatchListDC?>, t: Throwable) {
                    Log.i("test", t.message.toString())
                    Toast.makeText(activity, "Check Internet Connection", Toast.LENGTH_SHORT).show()
                }
            })
        }
        catch (e: JSONException){
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        ids.searchEdTxt.text.clear()
    }
}