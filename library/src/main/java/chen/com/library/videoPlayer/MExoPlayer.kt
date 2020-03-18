package chen.com.library.videoPlayer

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util


class MExoPlayer() {
    var player: SimpleExoPlayer? = null

    fun loadUrl(context: Context, url: String) {
        if (player == null) {
            player = SimpleExoPlayer.Builder(context).build()
        }
        val userAgent = Util.getUserAgent(context, context.packageName)
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(context, userAgent)
        val uri: Uri = Uri.parse(url)
        val mediaSources: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
        player?.prepare(mediaSources)
    }

    fun play() {
        if (player == null) return
        if (player!!.playbackState == ExoPlayer.STATE_ENDED) {
            player!!.seekToDefaultPosition()
        }
        player!!.playWhenReady = true
    }

    fun pause() {
        if (player == null) return
        if (player!!.playbackState == ExoPlayer.STATE_READY) {
            player!!.playWhenReady = false
        }
    }

    fun release() {
        player?.release()
        player = null
    }

    companion object {

        @JvmSynthetic
        private var player: MExoPlayer? = null
            get() {
                if (field == null) {
                    field = MExoPlayer()
                }
                return field
            }

        @JvmSynthetic
        fun getInstance(): MExoPlayer {
            return player!!
        }

    }

}