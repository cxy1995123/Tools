package chen.com.library.videoPlayer

import android.content.Context
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.annotation.AttrRes
import chen.com.library.R
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.source.MediaSourceEventListener
import kotlinx.android.synthetic.main.view_video_player.view.*
import java.io.IOException
import kotlin.math.min

class MVideoPlayerView : FrameLayout, OnExoPlayerListener {
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor  (context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        inflate(context, R.layout.view_video_player, this)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val progress = (seekBar?.progress ?: 0) * 1f / 100
                val duration = MExoPlayer.getInstance().player?.duration ?: 0
                MExoPlayer.getInstance().player?.seekTo((duration * progress).toLong())
            }
        })

    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val videoFormat = MExoPlayer.getInstance().player?.videoFormat
        if (videoFormat != null) {
            changeViewSize(videoFormat.width, videoFormat.height)
        }
    }

    private fun changeViewSize(width: Int, height: Int) {
        val layoutParams = textureView.layoutParams
        val ratio = min((measuredWidth * 1f) / (width * 1f), (measuredHeight * 1f) / (height * 1f))
        layoutParams.width = (width * ratio).toInt()
        layoutParams.height = (height * ratio).toInt()
        textureView.layoutParams = layoutParams
    }


    fun play(url: String) {
        MExoPlayer.getInstance().loadUrl(context, url)
        MExoPlayer.getInstance().player?.addAnalyticsListener(this)
        if (textureView.isAvailable) {
            MExoPlayer.getInstance().player?.clearVideoSurface()
            MExoPlayer.getInstance().player?.setVideoTextureView(textureView)
            MExoPlayer.getInstance().player?.playWhenReady = true
        } else {
            textureView.surfaceTextureListener = object : SurfaceTextureListener {
                override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
                    textureView.surfaceTexture = null
                    MExoPlayer.getInstance().player?.clearVideoSurface()
                    MExoPlayer.getInstance().player?.setVideoTextureView(textureView)
                    MExoPlayer.getInstance().player?.playWhenReady = true
                    super.onSurfaceTextureAvailable(surface, width, height)
                }
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        MExoPlayer.getInstance().release()
        MExoPlayer.getInstance().player?.addAnalyticsListener(this)
    }

    override fun onPlayerError(eventTime: AnalyticsListener.EventTime, error: ExoPlaybackException) {
        super.onPlayerError(eventTime, error)
        Log.i("MVideoPlayerView", "onPlayerError: ");
    }

    override fun onLoadError(eventTime: AnalyticsListener.EventTime, loadEventInfo: MediaSourceEventListener.LoadEventInfo, mediaLoadData: MediaSourceEventListener.MediaLoadData, error: IOException, wasCanceled: Boolean) {
        super.onLoadError(eventTime, loadEventInfo, mediaLoadData, error, wasCanceled)
        Log.i("MVideoPlayerView", "onLoadError: ");
    }


    override fun onVideoSizeChanged(eventTime: AnalyticsListener.EventTime, width: Int, height: Int, unappliedRotationDegrees: Int, pixelWidthHeightRatio: Float) {
        super.onVideoSizeChanged(eventTime, width, height, unappliedRotationDegrees, pixelWidthHeightRatio)
        changeViewSize(width, height)
    }

    override fun onPlayerStateChanged(eventTime: AnalyticsListener.EventTime, playWhenReady: Boolean, playbackState: Int) {
        super.onPlayerStateChanged(eventTime, playWhenReady, playbackState)
        when (playbackState) {
            Player.STATE_BUFFERING -> {
                Log.i("MVideoPlayerView", "onPlayerStateChanged: STATE_BUFFERING");
                bufferBar.visibility = View.VISIBLE
            }
            Player.STATE_ENDED -> {
                Log.i("MVideoPlayerView", "onPlayerStateChanged: STATE_ENDED");
                bufferBar.visibility = View.GONE
            }
            Player.STATE_IDLE -> {
                Log.i("MVideoPlayerView", "onPlayerStateChanged: STATE_IDLE");
                bufferBar.visibility = View.GONE
            }
            Player.STATE_READY -> {
                Log.i("MVideoPlayerView", "onPlayerStateChanged: STATE_READY");
                bufferBar.visibility = View.GONE
            }
        }
    }

    override fun onLoadingChanged(eventTime: AnalyticsListener.EventTime, isLoading: Boolean) {
        super.onLoadingChanged(eventTime, isLoading)
    }

    override fun onRenderedFirstFrame(eventTime: AnalyticsListener.EventTime, surface: Surface?) {
        super.onRenderedFirstFrame(eventTime, surface)
        Log.i("MVideoPlayerView", "onMetadata: " + MExoPlayer.getInstance().player?.currentPosition);
        Log.i("MVideoPlayerView", "onMetadata: " + MExoPlayer.getInstance().player?.duration);
    }

    override fun onMetadata(eventTime: AnalyticsListener.EventTime, metadata: Metadata) {
        super.onMetadata(eventTime, metadata)
        Log.i("MVideoPlayerView", "onMetadata: " + MExoPlayer.getInstance().player?.currentPosition);
    }
}