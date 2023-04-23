package com.example.e_kuhipath.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import com.example.e_kuhipath.R
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory

class VideoDialogFragment : DialogFragment() {
    private var player: SimpleExoPlayer? = null

    // The URL of the video to play
    private var videoUrl: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_video_dialog, container, false)

        // Retrieve the video URL from the arguments
        videoUrl = arguments?.getString("video_url")

        // Initialize the player
        player = SimpleExoPlayer.Builder(requireContext()).build()

        // Create a MediaSource object from the m3u8 URL
        val uri = Uri.parse(videoUrl)
        val mediaSource = HlsMediaSource.Factory(
            DefaultDataSourceFactory(requireContext(), "exoplayer")
        )
            .createMediaSource(MediaItem.fromUri(uri))

        // Set the media source to the player and prepare it
        player?.setMediaSource(mediaSource)
        player?.prepare()

        // Attach the player to the PlayerView
        val playerView = view.findViewById<PlayerView>(R.id.player_view)
        playerView.player = player

        // Add a click listener to the close button to release the player
        val closeButton = view.findViewById<ImageButton>(R.id.close_button)
        closeButton.setOnClickListener {
            dismiss()
        }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release the player when the dialog is destroyed
        player?.release()
    }
}

// clicking on the link code

/*val videoUrl = "https://example.com/playlist.m3u8"
val cardView = findViewById<CardView>(R.id.card_view)
cardView.setOnClickListener {
    val dialogFragment = VideoDialogFragment()
    val args = Bundle()
    args.putString("video_url", videoUrl)
    dialogFragment.arguments = args
    dialogFragment.show(supportFragmentManager, "VideoDialogFragment")
}*/
