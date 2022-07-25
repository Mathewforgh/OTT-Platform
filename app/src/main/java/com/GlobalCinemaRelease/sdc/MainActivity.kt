package com.GlobalCinemaRelease.sdc

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.GlobalCinemaRelease.sdc.adapters.ViewPagerAdapter
import com.GlobalCinemaRelease.sdc.databinding.ActivityMainBinding
import com.GlobalCinemaRelease.sdc.msg.listener.setOnDebounceListener
import com.GlobalCinemaRelease.sdc.msg.toast
import com.GlobalCinemaRelease.sdc.obj.Store

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
   private val ids by lazy { ActivityMainBinding.inflate(layoutInflater) }
    companion object{
        var showIntro = "intro"
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        sharedPreferences = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        if (!sharedPreferences.getBoolean(showIntro, true) && Store.theme == ""){
            startActivity(Intent(this@MainActivity, LoginSignPage::class.java))
        }

        super.onCreate(savedInstanceState)
        setContentView(ids.root)

        val image = ArrayList<ViewPagerData>()


        image.add(ViewPagerData(R.drawable.intro1,R.string.title1,R.string.title2))
        image.add(ViewPagerData(R.drawable.intro2,R.string.title3,R.string.title4))
        image.add(ViewPagerData(R.drawable.intro3,R.string.title5,R.string.title6))

        val adapter = ViewPagerAdapter(image)
        ids.viewPagerId.adapter = adapter

        ids.viewPagerId.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                changeIndicator()
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }
            

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                changeIndicator()
            }
        })
        ids.tvSkipId.setOnDebounceListener{
        sharedPreferences.edit().apply {
            putBoolean(showIntro, false)
            apply()
        }
            startActivity(Intent(this, LoginSignPage::class.java))
        }

    }
    @SuppressLint("ResourceAsColor")
    fun changeIndicator(){
        when(ids.viewPagerId.currentItem){
            0->{
                ids.indicate1.setBackgroundColor(applicationContext.resources.getColor(R.color.indicatorColor))
                ids.indicate2.setBackgroundColor(applicationContext.resources.getColor(R.color.white))
                ids.indicate3.setBackgroundColor(applicationContext.resources.getColor(R.color.white))
                ids.tvSkipId.setText(R.string.skip)
                val bar = ids.cardIndic1Id.layoutParams
                bar.width = 75
                ids.cardIndic1Id.layoutParams = bar

                val bar1 = ids.cardIndic2Id.layoutParams
                bar1.width = 40
                ids.cardIndic2Id.layoutParams = bar1

                val bar2 = ids.cardIndic3Id.layoutParams
                bar2.width = 40
                ids.cardIndic3Id.layoutParams = bar2
            }
            1->{
                ids.indicate1.setBackgroundColor(applicationContext.resources.getColor(R.color.white))
                ids.indicate2.setBackgroundColor(applicationContext.resources.getColor(R.color.indicatorColor))
                ids.indicate3.setBackgroundColor(applicationContext.resources.getColor(R.color.white))
                ids.tvSkipId.setText(R.string.skip)
                val bar = ids.cardIndic1Id.layoutParams
                bar.width = 40
                ids.cardIndic1Id.layoutParams = bar

                val bar1 = ids.cardIndic2Id.layoutParams
                bar1.width = 75
                ids.cardIndic2Id.layoutParams = bar1

                val bar2 = ids.cardIndic3Id.layoutParams
                bar2.width = 40
                ids.cardIndic3Id.layoutParams = bar2
            }
            2->{
                ids.indicate1.setBackgroundColor(applicationContext.resources.getColor(R.color.white))
                ids.indicate2.setBackgroundColor(applicationContext.resources.getColor(R.color.white))
                ids.indicate3.setBackgroundColor(applicationContext.resources.getColor(R.color.indicatorColor))
                ids.tvSkipId.setText(R.string.skip_done)
                val bar = ids.cardIndic1Id.layoutParams
                bar.width = 40
                ids.cardIndic1Id.layoutParams = bar
                val bar1 = ids.cardIndic2Id.layoutParams
                bar1.width = 40
                ids.cardIndic2Id.layoutParams = bar1
                val bar2 = ids.cardIndic3Id.layoutParams
                bar2.width = 75
                ids.cardIndic3Id.layoutParams = bar2
            }
        }
    }
}