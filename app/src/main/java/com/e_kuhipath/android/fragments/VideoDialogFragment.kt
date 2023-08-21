package com.e_kuhipath.android.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Point
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.e_kuhipath.android.R
import com.e_kuhipath.android.utils.CustomSpinnerAdapter
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import kotlinx.android.synthetic.main.custom_control_view.*
import kotlinx.android.synthetic.main.fragment_video_dialog.*

class VideoDialogFragment : DialogFragment(), AudioManager.OnAudioFocusChangeListener {
    private var player: SimpleExoPlayer? = null
    private lateinit var playPauseBtn: ImageButton
    private lateinit var fullScreenBtn: ImageButton
    private lateinit var orientationBtn: ImageButton
    private var videoUrl: String? = null
    private var videoThumbnail: String? = null
    private lateinit var thumbnailImageView: ImageView
    private var PRIVATE_MODE = 0
    lateinit var sharedPref: SharedPreferences
    private lateinit var gestureDetector: GestureDetector

    private lateinit var playerView: PlayerView
    private var final_token: String? = null
    private var isFullScreen = false
    private var audioManager:AudioManager? = null

    private lateinit var speedSpinner: Spinner

    private val playbackSpeeds = floatArrayOf(1.0f,1.25f, 1.5f, 2.0f)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE,R.style.playerActivityTheme)
    }

    override fun onStart() {
        super.onStart()
        // Set the width and height of the dialog window
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_video_dialog, container, false)
        thumbnailImageView = view.findViewById(R.id.thumbnail_image_view)
        //val play_button = view.findViewById<ImageButton>(R.id.play_button)
        val fl2 = view.findViewById<View>(R.id.fl2)
        val videotitle = view.findViewById<TextView>(R.id.videotitle)
        sharedPref = requireActivity().getSharedPreferences("sharedpref", PRIVATE_MODE)
        playerView = view.findViewById<PlayerView>(R.id.player_view)
        playPauseBtn = playerView.findViewById(R.id.playPauseBtn)
        fullScreenBtn = playerView.findViewById(R.id.fullScreenBtn)
        orientationBtn = playerView.findViewById(R.id.orientationBtn)
        val play_btn = view.findViewById<ImageButton>(R.id.play_button)
        val nextbtn = playerView.findViewById<ImageButton>(R.id.nextBtn)
        val prevbtn = playerView.findViewById<ImageButton>(R.id.prevBtn)
        playerView.useController = true
        playerView.controllerAutoShow = true
        playerView.controllerHideOnTouch = true

        speedSpinner = view.findViewById(R.id.speedSpinner)

        // Set up the ArrayAdapter for the Spinner
        val adapter = context?.let {
            CustomSpinnerAdapter(
                it,
                resources.getStringArray(R.array.speeds)
            )
        }

        adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        speedSpinner.adapter = adapter

        speedSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedSpeed = playbackSpeeds[position]
                setPlaybackSpeed(selectedSpeed)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }


        orientationBtn.setOnClickListener{
            activity?.requestedOrientation = if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            else
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT

        }
        fullScreenBtn.setOnClickListener{
            if (isFullScreen){
                isFullScreen = false
                playInFullScreen(enable = false)
            }
            else{
                isFullScreen = true
                playInFullScreen(enable = true)
            }
        }

        val accesstoken = sharedPref.getString("accesstoken", "")
        final_token = "Bearer " + accesstoken

        videoUrl = arguments?.getString("video_url")
        videoThumbnail = arguments?.getString("video_thumbnail")
        val videoname = arguments?.getString("videoname")

        Log.i("zz","videourl--->"+videoUrl)
        Log.i("zz","videoThumbnail--->"+videoThumbnail)

        videotitle.text = videoname
        videotitle.isSelected = true
                // Load the video thumbnail into the ImageView
        Glide.with(this)
            .load(videoThumbnail)
            .into(thumbnailImageView)
        val closeButton = view.findViewById<ImageButton>(R.id.close_button)
        closeButton.setOnClickListener {
            dismiss()
        }
        nextbtn.setOnClickListener{
            player?.seekTo(player!!.currentPosition + 10 * 1000)

            showQuickDisplay(10)
        }
        prevbtn.setOnClickListener {
            player?.seekTo(player!!.currentPosition - 10 * 1000)

            showQuickDisplay(-10)
        }

        gestureDetector = GestureDetector(
            requireContext(),
            object : GestureDetector.SimpleOnGestureListener(), GestureDetector.OnDoubleTapListener {

                @SuppressLint("ClickableViewAccessibility")
                override fun onDoubleTap(e: MotionEvent): Boolean {
                    // Your double tap logic here
                    val center = Point(playerView!!.width / 2, playerView.height / 2)
                    val tap = Point(e!!.x.toInt(), e.y.toInt())
                    val distance = tap.x - center.x

                    // Calculate the skip amount based on the tap position
                    val skipSeconds = if (distance > 0) 10 else -10

                    // Skip forward or backward
                    player?.seekTo(player!!.currentPosition + skipSeconds * 1000)

                    showQuickDisplay(skipSeconds)

                    return super.onDoubleTap(e)
                }


            }
        )
/*
        gestureDetector = GestureDetector(requireContext(), object : GestureDetector.SimpleOnGestureListener() {

            override fun onDoubleTap(e: MotionEvent?): Boolean {
                // Calculate the tap position relative to the center of the player view
                val center = Point(playerView!!.width / 2, playerView.height / 2)
                val tap = Point(e!!.x.toInt(), e.y.toInt())
                val distance = tap.x - center.x

                // Calculate the skip amount based on the tap position
                val skipSeconds = if (distance > 0) 10 else -10

                // Skip forward or backward
                player?.seekTo(player!!.currentPosition + skipSeconds * 1000)

                showQuickDisplay(skipSeconds)

                return super.onDoubleTap(e)
            }


        })
*/

        playerView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            playerView.showController()
            Log.i("zz","hhhdshh---->")
            return@setOnTouchListener true
        }
       fl2.setOnClickListener{
           play_btn.visibility = View.GONE
           fl2.visibility = View.GONE
           playerView.visibility = View.VISIBLE
           speedSpinner.visibility = View.VISIBLE
           if (player == null){
               buildPlayer()
           }
           else{
               if(player!!.isPlaying) pauseVideo()
               else playVideo()
           }
           // Initialize the player

       }
       play_btn.setOnClickListener{
           play_btn.visibility = View.GONE
           fl2.visibility = View.GONE
           playerView.visibility = View.VISIBLE
           speedSpinner.visibility = View.VISIBLE

           if (player == null){
               buildPlayer()
           }
           else{
               if(player!!.isPlaying) pauseVideo()
               else playVideo()
           }
           // Initialize the player


       }

        // Set a click listener on the thumbnail to replace it with the PlayerView
        playPauseBtn.setOnClickListener {
            // Replace the thumbnail with the PlayerView
            if (player == null){
                buildPlayer()
            }
            else{
                if(player!!.isPlaying) pauseVideo()
                else playVideo()
            }
        }

        return view
    }

    private fun setPlaybackSpeed(selectedSpeed: Float) {
        val playbackParameters = PlaybackParameters(selectedSpeed)
        player?.setPlaybackParameters(playbackParameters)
    }

    private fun buildPlayer() {
        player = SimpleExoPlayer.Builder(requireContext()).build()


        val dataSourceFactory = DefaultHttpDataSourceFactory("exoplayer")
        dataSourceFactory.defaultRequestProperties!!.set("Authorization", final_token!!)

        // Create a MediaSource object from the m3u8 URL
        val uri = Uri.parse(videoUrl)
        val mediaSource = HlsMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(uri))

        // Set the media source to the player and prepare it
        player?.setMediaSource(mediaSource)
        player?.prepare()

        // Attach the player to the PlayerView

        playerView.player = player
        playVideo()

        playInFullScreen(enable = isFullScreen)
        // Add a click listener to the close button to release the player


        /*playerView.setOnTouchListener { _, event ->
            Log.e("zz","dd")
            playerView.useController = true
            return@setOnTouchListener false

        }*/


        /*val fullscreenButton = view.findViewById<ImageView>(R.id.fullscreen_button)
        fullscreenButton.setOnClickListener {
            // Enter full screen mode
            if(activity?.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            {
                toggleViewsVisibility(true)
            }
            else {
            toggleViewsVisibility(false)
                }
        }*/

    }

    private fun playVideo() {
        playPauseBtn.setImageResource(R.drawable.pause_icon)
        player?.play()
    }
    private fun pauseVideo(){
        playPauseBtn.setImageResource(R.drawable.play_icon)
        player?.pause()
    }
    private fun showQuickDisplay(skipSeconds: Int) {
        val quickDisplayText = if (skipSeconds > 0) "+$skipSeconds sec" else "$skipSeconds sec"
        val quickDisplayViewF = view?.findViewById<TextView>(R.id.quick_display_text_view_f)
        val quickDisplayViewB = view?.findViewById<TextView>(R.id.quick_display_text_view_b)
        if (skipSeconds>0){
            Log.e("zz","for-->"+quickDisplayText)
            quickDisplayViewF?.text = quickDisplayText
            quickDisplayViewF?.visibility = View.VISIBLE
            quickDisplayViewB?.visibility = View.GONE

            quickDisplayViewF?.postDelayed({
                quickDisplayViewF.visibility = View.GONE
            }, 1000) // Adjust the delay time as needed
        }
        else {
            Log.e("zz","back-->"+quickDisplayText)
            quickDisplayViewB?.text = quickDisplayText
            quickDisplayViewB?.visibility = View.VISIBLE
            quickDisplayViewF?.visibility = View.GONE

            quickDisplayViewB?.postDelayed({
                quickDisplayViewB.visibility = View.GONE
            }, 1000) // Adjust the delay time as needed
        }

        // Hide the quick display after a delay

    }


    /* private fun toggleViewsVisibility(isFullScreen: Boolean) {
         // Find all the views
         val fl = view?.findViewById<FrameLayout>(R.id.fl)
         val thumbnailImageView = view?.findViewById<ImageView>(R.id.thumbnail_image_view)
         val closeBtn = view?.findViewById<ImageView>(R.id.close_button)
         val fullscreenBtn = view?.findViewById<ImageView>(R.id.fullscreen_button)
 
 
         // Hide or show views based on the fullscreen mode
         if (isFullScreen) {
             fl?.visibility = View.GONE
             thumbnailImageView?.visibility = View.GONE
             closeBtn?.visibility = View.VISIBLE
             fullscreenBtn?.visibility = View.VISIBLE
             playerView.useController = false
             playerView.controllerHideOnTouch
             activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
 
 
         } else {
 
             fl?.visibility = View.VISIBLE
             thumbnailImageView?.visibility = View.VISIBLE
             closeBtn?.visibility = View.VISIBLE
             fullscreenBtn?.visibility = View.VISIBLE
             playerView.useController = false
             playerView.controllerHideOnTouch
             activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
 
 
         }
     }*/


    /*override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Exit full screen mode when the orientation changes to portrait
            val playerContainer = view?.findViewById<ConstraintLayout>(R.id.fl2)
            playerContainer?.removeView(playerView)
            playerContainer?.visibility = View.GONE
        }
    }*/



    override fun onPause() {
        super.onPause()
        pauseVideo()
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null

        audioManager?.abandonAudioFocus(this)
    }
    override fun onDestroy() {
        super.onDestroy()
        // Release the player when the dialog is destroyed
        player?.release()
        player = null
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        audioManager?.abandonAudioFocus(this)

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        player?.release()
        player = null

        audioManager?.abandonAudioFocus(this)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun show(manager: FragmentManager, tag: String?) {
        super.show(manager, tag)
        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels
        val screenWidth = displayMetrics.widthPixels
        dialog?.window?.setLayout(screenWidth, screenHeight)
        dialog?.window?.attributes?.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        WindowCompat.setDecorFitsSystemWindows(dialog?.window!!,false)
        WindowInsetsControllerCompat(dialog?.window!!, dialog?.window?.decorView!!).let { controller->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val playerContainer = dialog?.findViewById<ConstraintLayout>(R.id.fl2)
            playerContainer?.addView(playerView)
            playerContainer?.visibility = View.VISIBLE
        }
    }

    private fun playInFullScreen(enable:Boolean){
        if (enable){
            playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
            player?.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
            fullScreenBtn.setImageResource(R.drawable.fullscreen_exit_icon)
        }
        else{
            playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            player?.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
            fullScreenBtn.setImageResource(R.drawable.fullscreen_icon)
        }
    }

    override fun onAudioFocusChange(focusChange: Int) {
        if (focusChange <=0) pauseVideo()
    }

    override fun onResume() {
        super.onResume()

        if (audioManager == null) audioManager = activity?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager?.requestAudioFocus(this,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN)
    }

}

// generate otp -> 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
