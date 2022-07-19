package com.GlobalCinemaRelease.sdc

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.view.isVisible
import com.GlobalCinemaRelease.sdc.databinding.ActivityMoviePlayerBinding
import com.GlobalCinemaRelease.sdc.databinding.CustomControllerBinding
import com.GlobalCinemaRelease.sdc.msg.listener.setOnDebounceListener
import com.GlobalCinemaRelease.sdc.msg.toast
import com.GlobalCinemaRelease.sdc.obj.Store
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.StyledPlayerControlView
import com.google.android.exoplayer2.ui.StyledPlayerView
import kotlinx.android.synthetic.main.activity_movie_player.*
import kotlinx.android.synthetic.main.custom_controller.*
import kotlinx.android.synthetic.main.custom_controller.view.*
import kotlinx.android.synthetic.main.item_view_pager.view.*

class MoviePlayer : AppCompatActivity(),Player.Listener {
    private val ids by lazy { ActivityMoviePlayerBinding.inflate(layoutInflater) }
    private lateinit var player: ExoPlayer
    private lateinit var styledPlayerView: PlayerView
    private lateinit var mediaItem1: MediaItem
    private lateinit var video: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ids.root)

        video = Store.movieLink
        setupPlayer()
        addVideo()

        commendBtn.setOnDebounceListener { toast("click") }

        var mute = true
        sound.setOnClickListener {
            if (mute){
                sound.setImageResource(R.drawable.ic_mute)
                player.volume = 0f
            }else{
                sound.setImageResource(R.drawable.ic_sound)
                player.volume = 1f
            }
            mute = !mute
        }

        var isPortrait = true
        exo_fullscreen.setOnDebounceListener {
            if (isPortrait) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                exo_fullscreen.setImageResource(R.drawable.ic_fullscreen_exit_24)
                styledPlayerView.exo_bottom_bar.visibility = View.VISIBLE
                styledPlayerView.commendBtn.visibility = View.VISIBLE
                ids.videoBackBtn.visibility = View.GONE
            } else {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                exo_fullscreen.setImageResource(R.drawable.ic_fullscreen_24)
                styledPlayerView.exo_bottom_bar.visibility = View.GONE
                styledPlayerView.commendBtn.visibility = View.GONE
                ids.videoBackBtn.visibility = View.VISIBLE
            }
            isPortrait = !isPortrait
        }

        ids.apply {
            videoMovieTitle.ellipsize = TextUtils.TruncateAt.MARQUEE
            videoMovieTitle.isSelected

            videoMovieTitle.text = Store.movieName
            videoMovieDescription.text = Store.movieDesc

            videoBackBtn.setOnDebounceListener {
                finish()
            }
        }
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }else{
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }
    private fun addVideo(){
        mediaItem1 = MediaItem.fromUri(video)
        player.addMediaItem(mediaItem1)
        player.play()
        player.prepare()
    }
    private fun setupPlayer() {
        player = ExoPlayer.Builder(this)
            .setSeekForwardIncrementMs(10000)
            .setSeekBackIncrementMs(10000)
            .build()
        styledPlayerView = ids.videoView
        styledPlayerView.player = player
        player.addListener(this)
        styledPlayerView.keepScreenOn = true
    }
    @SuppressLint("SwitchIntDef")
    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        when(playbackState){

            Player.STATE_BUFFERING -> {
                progressBar.visibility = View.VISIBLE
                player.pause()
            }
            Player.STATE_READY -> {
                progressBar.visibility = View.INVISIBLE
                player.play()
            }
        }
    }

    override fun onBackPressed() {
        player.stop()
        Store.movieLink = ""
        Store.payState = ""
        video = ""
        val state = intent.getStringExtra("state")
        if (state == "1") startActivity(Intent(this@MoviePlayer, MovieDescription::class.java))
        super.onBackPressed()
    }

    override fun onResume() {
        player.play()
        super.onResume()
    }

    override fun onPause() {
        player.pause()
        super.onPause()
    }
}