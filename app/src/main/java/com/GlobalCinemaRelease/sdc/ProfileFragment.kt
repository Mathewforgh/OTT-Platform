package com.GlobalCinemaRelease.sdc

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.GlobalCinemaRelease.sdc.adapters.GiftedHistoryAdapter
import com.GlobalCinemaRelease.sdc.adapters.LanguageSlideAdapterHP
import com.GlobalCinemaRelease.sdc.adapters.PaymentMovieHistoryAdapter
import com.GlobalCinemaRelease.sdc.adapters.PreferredLanguageAdapter
import com.GlobalCinemaRelease.sdc.dataClass.LanguageSlideData
import com.GlobalCinemaRelease.sdc.databinding.FragmentProfileBinding
import com.GlobalCinemaRelease.sdc.interfaces.ResponseApi
import com.GlobalCinemaRelease.sdc.msg.listener.setOnDebounceListener
import com.GlobalCinemaRelease.sdc.obj.Store
import com.GlobalCinemaRelease.sdc.response.ProfilePgDetailsDC
import com.google.gson.JsonObject
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {
    private var ids: FragmentProfileBinding? = null
    lateinit var loader: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Store.from == "guest") {
            startActivity(Intent(activity, LoginSignPage::class.java))
            Store.from = ""
        }
    }
/*---------codes is here -----------------*/
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        ids = FragmentProfileBinding.inflate(inflater, container, false)

        ids?.settingBtnTv?.setOnDebounceListener {
            ids?.settingBtnTv?.startAnimation(this@ProfileFragment.activity?.let { it1 ->
                Store.rotate(it1)
            })
            startActivity(Intent(activity, SettingsPage::class.java))
        }
/*----------------------- tab controls set Up-----------------*/
    ids!!.profilePaymentHistory.setOnDebounceListener {
        ids!!.profilePaymentHistory.setBackgroundResource(R.drawable.indicator_rounded_bg)
        ids!!.profilePaymentHistory.isAllCaps = true
        ids!!.profileGiftedHistory.isAllCaps = false
        ids!!.profilePrefLanguage.isAllCaps = false

        ids!!.profilePaymentHistory.setTextColor(ContextCompat.getColor(this@ProfileFragment.activity!!,R.color.indicatorColor))
        ids!!.profileGiftedHistory.setTextColor(ContextCompat.getColor(this@ProfileFragment.activity!!,R.color.movie_descrip_movie_name))
        ids!!.profilePrefLanguage.setTextColor(ContextCompat.getColor(this@ProfileFragment.activity!!,R.color.movie_descrip_movie_name))

        ids!!.profileGiftedHistory.setBackgroundResource(R.drawable.profile_chip_unselected_bg)
        ids!!.profilePrefLanguage.setBackgroundResource(R.drawable.profile_chip_unselected_bg)

        ids!!.recyclerViewPreLanguageLayout.visibility = View.GONE
        ids!!.recyclerViewGiftedHistory.visibility = View.GONE
        ids!!.recyclerViewPayHistory.visibility = View.VISIBLE

    }
    ids!!.profileGiftedHistory.setOnDebounceListener {
        ids!!.profileGiftedHistory.setBackgroundResource(R.drawable.indicator_rounded_bg)
        ids!!.profileGiftedHistory.isAllCaps = true
        ids!!.profilePrefLanguage.isAllCaps = false
        ids!!.profilePaymentHistory.isAllCaps = false

        ids!!.profileGiftedHistory.setTextColor(ContextCompat.getColor(this@ProfileFragment.activity!!,R.color.indicatorColor))
        ids!!.profilePrefLanguage.setTextColor(ContextCompat.getColor(this@ProfileFragment.activity!!,R.color.movie_descrip_movie_name))
        ids!!.profilePaymentHistory.setTextColor(ContextCompat.getColor(this@ProfileFragment.activity!!,R.color.movie_descrip_movie_name))

        ids!!.profilePaymentHistory.setBackgroundResource(R.drawable.profile_chip_unselected_bg)
        ids!!.profilePrefLanguage.setBackgroundResource(R.drawable.profile_chip_unselected_bg)

        ids!!.recyclerViewPreLanguageLayout.visibility = View.GONE
        ids!!.recyclerViewGiftedHistory.visibility = View.VISIBLE
        ids!!.recyclerViewPayHistory.visibility = View.GONE

    }
    ids!!.profilePrefLanguage.setOnDebounceListener {
        ids!!.profilePrefLanguage.setBackgroundResource(R.drawable.indicator_rounded_bg)
        ids!!.profilePrefLanguage.isAllCaps = true
        ids!!.profileGiftedHistory.isAllCaps = false
        ids!!.profilePaymentHistory.isAllCaps = false

        ids!!.profilePrefLanguage.setTextColor(ContextCompat.getColor(this@ProfileFragment.activity!!,R.color.indicatorColor))
        ids!!.profilePaymentHistory.setTextColor(ContextCompat.getColor(this@ProfileFragment.activity!!,R.color.movie_descrip_movie_name))
        ids!!.profileGiftedHistory.setTextColor(ContextCompat.getColor(this@ProfileFragment.activity!!,R.color.movie_descrip_movie_name))

        ids!!.profilePaymentHistory.setBackgroundResource(R.drawable.profile_chip_unselected_bg)
        ids!!.profileGiftedHistory.setBackgroundResource(R.drawable.profile_chip_unselected_bg)

        ids!!.recyclerViewPreLanguageLayout.visibility = View.VISIBLE
        ids!!.recyclerViewGiftedHistory.visibility = View.GONE
        ids!!.recyclerViewPayHistory.visibility = View.GONE

    }
/*----------------------- tab controls set Up-----------------*/

    /*---------------------Language recycler view setup ----------*/
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

    userToken = HomePage.userToken

//    setUpPrefLanguage(userToken!!)
    /*---------------------Language recycler view setup Ended----------*/

    ids?.preLanChangeBtn?.setOnDebounceListener {
        startActivity(Intent(activity, LanguageSelectionPage::class.java))
    }

    /*-----------------*/
    loader = Dialog(this@ProfileFragment.activity!!, android.R.style.Theme_Translucent_NoTitleBar)
    val view: View = View.inflate(this@ProfileFragment.activity!!, R.layout.loader, null)
    loader.setContentView(view)
    loader.setCancelable(true)
    loader.create()
    loader.show()
    /*-----------------*/

    return ids?.root
    }
/*---------codes is here Ended -----------------*/
    companion object {
    var userToken: String? = null
    val languageList = arrayListOf<LanguageSlideData>()
    }

    private fun setUpPrefLanguage(userToken: String){
        try {
            val jsonObject = JsonObject()
            jsonObject.addProperty("userToken", userToken)

            ResponseApi().setUpProfilePgDetails(jsonObject).enqueue(object : Callback<ProfilePgDetailsDC?> {
                override fun onResponse(
                    call: Call<ProfilePgDetailsDC?>,
                    response: Response<ProfilePgDetailsDC?>
                ) {
                    if (response.isSuccessful){
                        loader.dismiss()
                        if (response.body()?.code == 201){

                            ids?.apply {
                                recyclerViewPrefLanguage.layoutManager =
                                    LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                                recyclerViewPrefLanguage.adapter =
                                    PreferredLanguageAdapter(response.body()?.preferedLanguages!!, languageList)

                                recyclerViewPayHistory.layoutManager =
                                    LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                                recyclerViewPayHistory.adapter =
                                    PaymentMovieHistoryAdapter(response.body()?.paymentHistory)

                                recyclerViewGiftedHistory.layoutManager =
                                    LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                                recyclerViewGiftedHistory.adapter =
                                    GiftedHistoryAdapter(response.body()?.giftedHistory)
                            }
                        }
                    }
                    else{
                        Toast.makeText(this@ProfileFragment.activity,
                            "Error", Toast.LENGTH_SHORT).show()
                        loader.dismiss()
                    }
                }

                override fun onFailure(call: Call<ProfilePgDetailsDC?>, t: Throwable) {
                    Toast.makeText(this@ProfileFragment.activity, "${t.message}", Toast.LENGTH_SHORT).show()
                    loader.dismiss()
                }
            })
        }
        catch (e: JSONException){
            e.printStackTrace()
            loader.dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        setUpPrefLanguage(userToken!!)
    }
}