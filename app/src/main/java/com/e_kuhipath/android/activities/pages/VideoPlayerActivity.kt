package com.e_kuhipath.android.activities.pages

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Point
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.WindowManager.LayoutParams
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.bumptech.glide.Glide
import com.e_kuhipath.android.R
import com.e_kuhipath.android.databinding.ActivityVideoPlayerBinding
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import kotlinx.android.synthetic.main.activity_video_player.*
import kotlinx.android.synthetic.main.fragment_video_dialog.*
import java.lang.Exception

class VideoPlayerActivity: AppCompatActivity(), AudioManager.OnAudioFocusChangeListener, GestureDetector.OnGestureListener {

    private lateinit var binding: ActivityVideoPlayerBinding
    lateinit var context: Context
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
    private var audioManager: AudioManager? = null

    private lateinit var videoTitle: TextView
    private lateinit var gestureDetectorCompat: GestureDetectorCompat
    private val playbackSpeeds = floatArrayOf(1.0f, 1.25f, 1.5f, 2.0f)
    private var subcourseid: String? = null
    private var videos: String? = null
    private var videoid: String? = null
    private var pdfpath: String? = null

    private var playbackPosition: Long = 0


    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE)
        //requestWindowFeature(Window.FEATURE_NO_TITLE)
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }*/
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setTheme(R.style.playerActivityTheme)
        setContentView(binding.root)

        val videospeeds = resources.getStringArray(R.array.speeds)
        val arrayAdapter = ArrayAdapter<String>(this, R.layout.dropdownitem, videospeeds)
        val autoCompleteView = binding.autoCompleteTextViewPlaybackspeed
        autoCompleteView.setAdapter(arrayAdapter)

        autoCompleteView.setOnItemClickListener { adapterView, view, i, l ->
            val selectedSpeed = playbackSpeeds[i]
            setPlaybackSpeed(selectedSpeed)
        }
        thumbnailImageView = findViewById(R.id.thumbnail_image_view)
        //val play_button = view.findViewById<ImageButton>(R.id.play_button)
        val fl2 = findViewById<View>(R.id.fl2)
        videoTitle = findViewById<TextView>(R.id.video_title)
        sharedPref = this.getSharedPreferences("sharedpref", PRIVATE_MODE)
        playerView = findViewById<PlayerView>(R.id.player_view)
        playPauseBtn = playerView.findViewById(R.id.playPauseBtn)
        fullScreenBtn = playerView.findViewById(R.id.fullScreenBtn)
        orientationBtn = playerView.findViewById(R.id.orientationBtn)
        val play_btn = findViewById<ImageButton>(R.id.play_button)
        val nextbtn = playerView.findViewById<ImageButton>(R.id.nextBtn)
        val prevbtn = playerView.findViewById<ImageButton>(R.id.prevBtn)
        playerView.useController = true
        playerView.controllerAutoShow = true
        playerView.controllerHideOnTouch = true

        //for immersive mode
        /*WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }*/
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val playerContainer = findViewById<ConstraintLayout>(R.id.fl2)
            playerContainer?.addView(playerView)
            playerContainer?.visibility = View.VISIBLE
        }

        orientationBtn.setOnClickListener{
            requestedOrientation = if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            else
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE){
                playInFullScreen(true)
                linlayoutstudentprofile.visibility = View.GONE
                videoTitle.visibility = View.GONE
                playbackspeed.visibility = View.GONE
                download_pdf.visibility = View.GONE
                val playerView: PlayerView = findViewById(R.id.player_view)
                val layoutParams = playerView.layoutParams
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT // Set height to wrap content
                playerView.layoutParams = layoutParams
                WindowCompat.setDecorFitsSystemWindows(window, false)
                WindowInsetsControllerCompat(window, binding.root).let { controller ->
                    controller.hide(WindowInsetsCompat.Type.systemBars())
                    controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }

            }
            else if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT){
                playInFullScreen(false)
                /*WindowCompat.setDecorFitsSystemWindows(window, false)
                WindowInsetsControllerCompat(window, binding.root).let { controller ->
                    controller.show(WindowInsetsCompat.Type.systemBars())
                }*/
                val playerView: PlayerView = findViewById(R.id.player_view)
                val layoutParams = playerView.layoutParams
                val heightInDp = 300 // Desired height in dp

                val heightInPixels = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    heightInDp.toFloat(),
                    resources.displayMetrics
                ).toInt()

                layoutParams.height = heightInPixels // Set height to 300dp in pixels
                playerView.layoutParams = layoutParams
                linlayoutstudentprofile.visibility = View.VISIBLE
                videoTitle.visibility = View.VISIBLE
                playbackspeed.visibility = View.VISIBLE
                download_pdf.visibility = View.VISIBLE


            }
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

        videoUrl = intent.getStringExtra("video_url")
        videoThumbnail = intent.getStringExtra("video_thumbnail")
        val videoname = intent.getStringExtra("videoname")
        subcourseid = intent.getStringExtra("subcourseid")
        videos = intent.getStringExtra("totalvideos")
        videoid = intent.getStringExtra("videoid")
        pdfpath = intent.getStringExtra("pdfpath")


        Log.i("zz","videourl--->"+videoUrl)
        Log.i("zz","videoThumbnail--->"+videoThumbnail)
        Log.i("zz","videoname--->"+videoname)

        if (videoname.equals(null)){
            videoTitle.visibility = View.GONE
        }
        else {
            videoTitle.text = videoname
            videoTitle.isSelected = true
        }
        // Load the video thumbnail into the ImageView
        Glide.with(this)
            .load(videoThumbnail)
            .into(thumbnailImageView)
        val closeButton = findViewById<ImageView>(R.id.close_button)
        closeButton.setOnClickListener {
            Log.e("cll","closebutton--->")
            player?.release()
            player = null

            audioManager?.abandonAudioFocus(this)
            val intent = Intent(this,PaidCourseDetailsActivity::class.java)
            intent.putExtra("subcourseid",subcourseid)
            intent.putExtra("totalvideos",videos)
            startActivity(intent)

        }
        nextbtn.setOnClickListener{
            player?.seekTo(player!!.currentPosition + 10 * 1000)

            showQuickDisplay(10)
        }
        prevbtn.setOnClickListener {
            player?.seekTo(player!!.currentPosition - 10 * 1000)

            showQuickDisplay(-10)
        }


        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {

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

        playerView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            playerView.showController()
            Log.i("zz","hhhdshh---->")
            return@setOnTouchListener true
        }
        fl2.setOnClickListener{
            play_btn.visibility = View.GONE
            fl2.visibility = View.GONE
            playbackspeed.visibility = View.VISIBLE
            playerView.visibility = View.VISIBLE
     //       speedSpinner.visibility = View.VISIBLE
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
            playbackspeed.visibility = View.VISIBLE

            // speedSpinner.visibility = View.VISIBLE

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
        if (pdfpath.isNullOrEmpty()){
            download_pdf.visibility = View.GONE
        }
        download_pdf.setOnClickListener {
                Toast.makeText(this,"Downloading...",Toast.LENGTH_LONG).show()
                val video_pdf = "https://www.ekuhipath.com/api/ekuhipath-v1/video-course/get-video-pdf/" +  videoid
                Log.i("ee","video_pdf--->"+video_pdf)
            val replacedVideoname = videoname!!
                .replace("/", "-")
                .replace("\\", "-")
                .replace(":", "-")
                .replace("*", "-")
                .replace("?", "-")
                .replace("<<", "-")
                .replace("<", "-")
                .replace(">", "-")
                .replace("|", "-")
                Log.i("zzz","downloadinvoice----->")
                val accesstoken = sharedPref.getString("accesstoken", "")
                val final_token = "Bearer " + accesstoken
                val mgr = this.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                try {
                    if (mgr!=null) {
                        val request = DownloadManager.Request(Uri.parse(video_pdf))
                        request.addRequestHeader("Authorization", final_token)
                        request.setMimeType("application/pdf")
                        request.setDescription("Downloading...")
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION)
                        request.setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_DOWNLOADS,
                            "eKuhipath-${replacedVideoname}.pdf"
                        )

                        mgr.enqueue(request)
                        // Toast.makeText(this,"Download Started!!!",Toast.LENGTH_LONG).show()
                        Toast.makeText(this,"Downloaded Successfully",Toast.LENGTH_LONG).show()
                       /* val snackbar =
                            Snackbar.make(feeinvoicecl, "Downloaded to  Downloads/eKuhipath-${videoname}.pdf", Snackbar.LENGTH_INDEFINITE)
                        snackbar.setAction(
                            "x",
                            View.OnClickListener { // Call your action method here
                                snackbar.dismiss()
                            })
                        snackbar.show()*/
                    }
                    else{
                        Toast.makeText(this,"Download Unsuccessfull!!!",Toast.LENGTH_LONG).show()
                    }
                }catch (e: Exception){
                    Log.i("zzz","exception--->"+e)
                }


            }

    }

    private fun buildPlayer() {
        player = SimpleExoPlayer.Builder(this).build()


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
        window.addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON)
        playVideo()

        playInFullScreen(enable = isFullScreen)

    }
    private fun showQuickDisplay(skipSeconds: Int) {
        val quickDisplayText = if (skipSeconds > 0) "+$skipSeconds sec" else "$skipSeconds sec"
        val quickDisplayViewF = findViewById<TextView>(R.id.quick_display_text_view_f)
        val quickDisplayViewB = findViewById<TextView>(R.id.quick_display_text_view_b)
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

        if (audioManager == null) audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager?.requestAudioFocus(this,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN)

        player?.seekTo(playbackPosition)

    }

    private fun setPlaybackSpeed(selectedSpeed: Float) {
        val playbackParameters = PlaybackParameters(selectedSpeed)
        player?.setPlaybackParameters(playbackParameters)
    }
    private fun playVideo() {
        playPauseBtn.setImageResource(R.drawable.pause_icon)

        player?.seekTo(playbackPosition)
        player?.play()
    }
    private fun pauseVideo(){
        playPauseBtn.setImageResource(R.drawable.play_icon)
        player?.pause()

        playbackPosition = player?.currentPosition ?: 0
    }

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
       requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        audioManager?.abandonAudioFocus(this)

    }
    override fun onDown(p0: MotionEvent?): Boolean {
        return false
    }
    override fun onShowPress(p0: MotionEvent?) = Unit
    override fun onSingleTapUp(p0: MotionEvent?): Boolean = false
    override fun onLongPress(p0: MotionEvent?) = Unit
    override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean = false

    override fun onScroll(event: MotionEvent?, event1: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        return true
    }

    override fun onBackPressed() {
        Log.e("cll","closebutton--->")
        player?.release()
        player = null

        audioManager?.abandonAudioFocus(this)
        val intent = Intent(this,PaidCourseDetailsActivity::class.java)
        intent.putExtra("subcourseid",subcourseid)
        intent.putExtra("totalvideos",videos)
        startActivity(intent)
    }

}