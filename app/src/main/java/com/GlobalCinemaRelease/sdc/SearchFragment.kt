package com.GlobalCinemaRelease.sdc

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.GlobalCinemaRelease.sdc.adapters.LanguageSlideAdapterHP
import com.GlobalCinemaRelease.sdc.adapters.SearchMoviesAdapter
import com.GlobalCinemaRelease.sdc.adapters.SearchPageLanguageAdapter
import com.GlobalCinemaRelease.sdc.dataClass.LanguageSlideData
import com.GlobalCinemaRelease.sdc.databinding.FragmentSearchBinding
import com.GlobalCinemaRelease.sdc.interfaces.ResponseApi
import com.GlobalCinemaRelease.sdc.msg.listener.setOnDebounceListener
import com.GlobalCinemaRelease.sdc.obj.Store
import com.GlobalCinemaRelease.sdc.response.SearchMoviesDC
import com.GlobalCinemaRelease.sdc.response.SearchPageDetailsDC
import com.google.gson.JsonObject
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {
    private lateinit var ids : FragmentSearchBinding
    private val languageList = arrayListOf<LanguageSlideData>()
    lateinit var loader: Dialog
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ids = FragmentSearchBinding.inflate(inflater, container, false)

        searchFragmentDetails(HomePage.userToken)

        var txt: CharSequence
        ids.searchEdTxt.doAfterTextChanged {
            if (ids.searchEdTxt.text.isNotEmpty()) {
                ids.apply {
                    totalView.visibility = View.GONE
                    recyclerViewSearchPgLanguage.visibility = View.GONE
                    searchViewVisible.visibility = View.VISIBLE
                    txt = ids.searchEdTxt.text.toString()
                    searchResult.text = "Search result $txt"
                }
                searchedMovies(txt as String)
            }else{
                ids.apply {
                    totalView.visibility = View.VISIBLE
                    recyclerViewSearchPgLanguage.visibility = View.VISIBLE
                    searchViewVisible.visibility = View.GONE
                }
            }
        }
        languageList.add(LanguageSlideData(Color.parseColor("#fad0a4")))
        languageList.add(LanguageSlideData(Color.parseColor("#e3f8aa")))
        languageList.add(LanguageSlideData(Color.parseColor("#a8f6e8")))
        languageList.add(LanguageSlideData(Color.parseColor("#a6ccf6")))
        languageList.add(LanguageSlideData(Color.parseColor("#e6a8f8")))
        languageList.add(LanguageSlideData(Color.parseColor("#f7aead")))
        languageList.add(LanguageSlideData(Color.parseColor("#f5aae9")))
        languageList.add(LanguageSlideData(Color.parseColor("#b2acfc")))
        languageList.add(LanguageSlideData(Color.parseColor("#6f8fb4")))
        languageList.add(LanguageSlideData(Color.parseColor("#9f98d8")))


        /*-----------------*/
        loader = Dialog(this@SearchFragment.activity!!, android.R.style.Theme_Translucent_NoTitleBar)
        val view: View = View.inflate(this@SearchFragment.activity!!, R.layout.loader, null)
        loader.setContentView(view)
        loader.setCancelable(true)
        loader.create()
        loader.show()
        /*-----------------*/

        return ids.root
    }

    private fun searchFragmentDetails(userToken: String?) {
        try {
            val jsonObject = JsonObject()

            jsonObject.addProperty("userToken", userToken)

            ResponseApi().searchPageDetails(jsonObject).enqueue(object : Callback<SearchPageDetailsDC?> {
                override fun onResponse(
                    call: Call<SearchPageDetailsDC?>,
                    response: Response<SearchPageDetailsDC?>,
                ) {
                    if (response.isSuccessful && response.body()?.code == 201){
                        loader.dismiss()
                        // set up the search page default view settings

                        ids.apply {
                            headerName1.text = response.body()?.data?.get(0)?.data?.get(0)?.name
                            headerName2.text = response.body()?.data?.get(0)?.data?.get(1)?.name

                            suggested1.text = response.body()?.data?.get(1)?.title

                            title1.text = response.body()?.data?.get(1)?.data?.get(0)?.name
                            title1.setOnDebounceListener {
                                Store.resTitle = response.body()?.data?.get(1)?.data?.get(0)?.name.toString()
                                Store.searchToken = response.body()?.data?.get(1)?.data?.get(0)?.token.toString()
                                Store.type = response.body()?.data?.get(1)?.searchTitle.toString()
                                startActivity(Intent(this@SearchFragment.activity, SeeAllMovies::class.java))
                            }

                            title2.text = response.body()?.data?.get(1)?.data?.get(1)?.name
                            title2.setOnDebounceListener {
                                Store.resTitle = response.body()?.data?.get(1)?.data?.get(1)?.name.toString()
                                Store.searchToken = response.body()?.data?.get(1)?.data?.get(1)?.token.toString()
                                Store.type = response.body()?.data?.get(1)?.searchTitle.toString()
                                startActivity(Intent(this@SearchFragment.activity, SeeAllMovies::class.java))
                            }

                            title3.text = response.body()?.data?.get(1)?.data?.get(2)?.name
                            title3.setOnDebounceListener {
                                Store.resTitle = response.body()?.data?.get(1)?.data?.get(2)?.name.toString()
                                Store.searchToken = response.body()?.data?.get(1)?.data?.get(2)?.token.toString()
                                Store.type = response.body()?.data?.get(1)?.searchTitle.toString()
                                startActivity(Intent(this@SearchFragment.activity, SeeAllMovies::class.java))
                            }

                            title4.text = response.body()?.data?.get(1)?.data?.get(3)?.name
                            title4.setOnDebounceListener {
                                Store.resTitle = response.body()?.data?.get(1)?.data?.get(3)?.name.toString()
                                Store.searchToken = response.body()?.data?.get(1)?.data?.get(3)?.token.toString()
                                Store.type = response.body()?.data?.get(1)?.searchTitle.toString()
                                startActivity(Intent(this@SearchFragment.activity, SeeAllMovies::class.java))
                            }

                            title5.text = response.body()?.data?.get(1)?.data?.get(4)?.name
                            title5.setOnDebounceListener {
                                Store.resTitle = response.body()?.data?.get(1)?.data?.get(4)?.name.toString()
                                Store.searchToken = response.body()?.data?.get(1)?.data?.get(4)?.token.toString()
                                Store.type = response.body()?.data?.get(1)?.searchTitle.toString()
                                startActivity(Intent(this@SearchFragment.activity, SeeAllMovies::class.java))
                            }

                            title6.text = response.body()?.data?.get(1)?.data?.get(5)?.name
                            title6.setOnDebounceListener {
                                Store.resTitle = response.body()?.data?.get(1)?.data?.get(5)?.name.toString()
                                Store.searchToken = response.body()?.data?.get(1)?.data?.get(5)?.token.toString()
                                Store.type = response.body()?.data?.get(1)?.searchTitle.toString()
                                startActivity(Intent(this@SearchFragment.activity, SeeAllMovies::class.java))
                            }

                            suggest2.text = response.body()?.data?.get(2)?.title

// Search by language setups
                            recyclerViewSearchPgLanguage.layoutManager =
                                LinearLayoutManager(this@SearchFragment.activity, LinearLayoutManager.HORIZONTAL, false)
                            recyclerViewSearchPgLanguage.adapter =
                            response.body()?.data?.get(2)?.data?.let {
                                SearchPageLanguageAdapter(it, languageList)
                            }
// Search by language setups ends

                            seeAllSearchPg1.setOnDebounceListener {
                                startActivity(Intent(this@SearchFragment.activity, SuggestedMovieForYou::class.java))
                            }
                        }

                        Store.tempSuggestMovieTitle = response.body()?.data?.get(1)?.data!!
                        Store.typeStatic = response.body()?.data?.get(1)?.searchTitle.toString()
                    }
                    else{
                        loader.dismiss()
                        Toast.makeText(activity, "Check Connection", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<SearchPageDetailsDC?>, t: Throwable) {
                    Log.i("test", t.message.toString())
                    loader.dismiss()
                }
            })
        }
        catch (e: JSONException){
            e.printStackTrace()
            loader.dismiss()
        }
    }

    private fun searchedMovies(searchString: String) {
        try {
            val jsonObject = JsonObject()

            jsonObject.addProperty("userToken", HomePage.userToken)
            jsonObject.addProperty("searchString", searchString)

            ResponseApi().searchMoviesRes(jsonObject).enqueue(object : Callback<SearchMoviesDC?> {
                override fun onResponse(
                    call: Call<SearchMoviesDC?>,
                    response: Response<SearchMoviesDC?>,
                ) {
                    if (response.isSuccessful && response.body()?.code == 201){

                        ids.apply {
                            recyclerSearchMovieList.layoutManager =
                                GridLayoutManager(this@SearchFragment.activity, 2)
                            recyclerSearchMovieList.adapter = response.body()?.data?.let {
                                SearchMoviesAdapter(it)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<SearchMoviesDC?>, t: Throwable) {
                    Log.i("test", t.message.toString())
                    Toast.makeText(this@SearchFragment.activity, "Fail to Fetch data", Toast.LENGTH_SHORT).show()
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