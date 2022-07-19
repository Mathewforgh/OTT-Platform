package com.GlobalCinemaRelease.sdc

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.GlobalCinemaRelease.sdc.adapters.LanguageSlideAdapter
import com.GlobalCinemaRelease.sdc.dataClass.LanguageSlideData
import com.GlobalCinemaRelease.sdc.databinding.ActivityLanguageSelectionPageBinding
import com.GlobalCinemaRelease.sdc.interfaces.OnClick
import com.GlobalCinemaRelease.sdc.interfaces.ResponseApi
import com.GlobalCinemaRelease.sdc.msg.listener.setOnDebounceListener
import com.GlobalCinemaRelease.sdc.msg.toast
import com.GlobalCinemaRelease.sdc.response.CodeResponse
import com.GlobalCinemaRelease.sdc.response.Data
import com.GlobalCinemaRelease.sdc.response.UpdateLanguageToken
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LanguageSelectionPage : AppCompatActivity(),OnClick {
    private val ids by lazy { ActivityLanguageSelectionPageBinding.inflate(layoutInflater) }
    private lateinit var languageSlideAdapter: LanguageSlideAdapter
    lateinit var sharedPref: SharedPreferences
    private val tokens = ArrayList<String>()
    companion object {
        private val languageList = arrayListOf<LanguageSlideData>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ids.root)
        sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)

        val layoutManager = GridLayoutManager(this, 2)
        ids.languageRecyclerView.layoutManager = layoutManager

        toast("Please Select Language")

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

//        languageList.add(LanguageSlideData(Color.parseColor("#E9AAFF")))
//        languageList.add(LanguageSlideData(Color.parseColor("#FFAAAA")))
//        languageList.add(LanguageSlideData(Color.parseColor("#AAFFF1")))
//        languageList.add(LanguageSlideData(Color.parseColor("#AACFFF")))
//        languageList.add(LanguageSlideData(Color.parseColor("#E9AAFF")))
//        languageList.add(LanguageSlideData(Color.parseColor("#FFAAAA")))
//        languageList.add(LanguageSlideData(Color.parseColor("#FFAAAA")))
//        languageList.add(LanguageSlideData(Color.parseColor("#FFDAAA")))
//        languageList.add(LanguageSlideData(Color.parseColor("#EAFFAA")))
//        languageList.add(LanguageSlideData(Color.parseColor("#AAFFF1")))
        ids.languageRecyclerView.adapter?.notifyItemChanged(languageList.size)

        process()
        ids.languageContinueBtn.setOnDebounceListener {
            val userToken = sharedPref.getString("userToken","").toString()
//            toast("btn click-$userToken")
            updateLanguage(tokens, userToken)
        }
        ids.LangPgBackBtn.setOnDebounceListener {
            finishAffinity()
        }
    }

    private fun process() {
        ResponseApi().getLanguage().enqueue(object : Callback<CodeResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<CodeResponse>, response: Response<CodeResponse>) {
                if (response.isSuccessful){
                    if (response.body()?.code == 201){
                        languageSlideAdapter = LanguageSlideAdapter(response.body()!!.data!!, languageList,this@LanguageSelectionPage)
                        languageSlideAdapter.notifyDataSetChanged()
                        ids.languageRecyclerView.adapter = languageSlideAdapter
                    }
                }else{
                    toast("some error")
                }
            }

            override fun onFailure(call: Call<CodeResponse>, t: Throwable) {
                toast("fails")
                Log.e("languageSelection", "OnFailure" + t.message)
            }
        })
    }
//    update language with api
    private fun updateLanguage(tokens: ArrayList<String>, userToken: String?){
        val jsonObject = JsonObject()
    try {
        val array = JsonArray()
        for (i in tokens){
            array.add(i)
        }
        jsonObject.addProperty("userToken", userToken)
        jsonObject.add("languageTokens", array)
        Log.i("test","uT-$userToken\nlT-$tokens")
//        api call
        ResponseApi().languageTokenUpdate(jsonObject).enqueue(object : Callback<UpdateLanguageToken?> {
            override fun onResponse(
                call: Call<UpdateLanguageToken?>,
                response: Response<UpdateLanguageToken?>
            ) {
                if (response.isSuccessful){
                    if (response.body()?.code == 201){
                        startActivity(Intent(this@LanguageSelectionPage, HomePage::class.java))
                    }
                }
            }
            override fun onFailure(call: Call<UpdateLanguageToken?>, t: Throwable) {
                Log.i("languageSelectionPage", "error-${t.message}")
            }
        })
    }
        catch (e: JSONException){
            e.printStackTrace()
        }
    }

    override fun onItemClick(
        position: Int,
        holder: LanguageSlideAdapter.ViewHolder,
        data: List<Data>,
        checkLanguage: ImageView
    ) {
        if(checkLanguage.isVisible){  //status == false
            val itemPosition = data[position]
            val token = itemPosition.token
            checkLanguage.isVisible = false // status = true and visibility = true
            tokens.remove(token)
            Log.e("test","$tokens")
        }
        else{
            checkLanguage.isVisible = true
            val itemPosition = data[position]
            val token = itemPosition.token.toString()
            tokens.add(token)
            Log.e("test","$tokens")
        }
    }

    override fun onBackPressed() {
        finishAffinity()
        super.onBackPressed()
    }
}
