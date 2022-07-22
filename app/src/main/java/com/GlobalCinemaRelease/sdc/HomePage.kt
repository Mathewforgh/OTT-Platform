package com.GlobalCinemaRelease.sdc

import android.content.*
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Network
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.GlobalCinemaRelease.sdc.dataClass.LanguageSlideData
import com.GlobalCinemaRelease.sdc.databinding.ActivityHomePageBinding
import com.GlobalCinemaRelease.sdc.msg.*
import com.GlobalCinemaRelease.sdc.obj.Store
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_theme_page.*
import me.ibrahimsn.lib.SmoothBottomBar
import kotlin.system.exitProcess

class HomePage : AppCompatActivity(), ConnectionReceiver.ConnectionReceiverListener {
    private val ids by lazy { ActivityHomePageBinding.inflate(layoutInflater) }
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ids.root)

        baseContext.registerReceiver(ConnectionReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        MyApp.instance.setConnectionListener(this)

        sharedPreferences = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        userToken = sharedPreferences.getString("userToken","")             /*get user token from sharedPref */


        navController = findNavController(R.id.fragmentContainerView)
        setSupportActionBar(ids.myToolBar)
        setupSmoothBottomMenu()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        val bottomNavigationView: Toolbar = ids.myToolBar
        setupWithNavController(bottomNavigationView, navController)

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
    }

    private fun setupSmoothBottomMenu() {
        val popupMenu = PopupMenu(this, null)
        popupMenu.inflate(R.menu.bottom_nav)
        val menu = popupMenu.menu
        ids.smoothBar.setupWithNavController(menu, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    companion object{
        var userToken: String? = null
        val languageList = arrayListOf<LanguageSlideData>()
    }

    override fun onBackPressed() {
        val box = AlertDialog.Builder(this)
        if (Store.from == "guest"){
            startActivity(
                Intent(this@HomePage, LoginSignPage::class.java))
        }
        else if(ids.smoothBar.itemActiveIndex != 0) {
            navController.navigate(R.id.bot_nva_home)
            ids.smoothBar.itemActiveIndex = 0
        }
        else {
            with(box) {
                setMessage("Do you want to close this application ?")
                setCancelable(false)
                setPositiveButton("Okay") { _, _ ->
                    finishAffinity()
                }
                setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }
                setTitle("SDC")
                create()
                show()
            }
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected){
            Store.internet = "1"
            checkInternetPopUp(this@HomePage, Store.internet)
        }
        else{
            Store.internet = "0"
            checkInternetPopUp(this@HomePage, Store.internet)
        }
    }
}
