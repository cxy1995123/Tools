package chen.com.tools.fragment

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import chen.com.library.fragment.BaseFragment
import chen.com.tools.R
import chen.com.tools.activity.FragmentSupporterActivity
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.C.VIDEO_SCALING_MODE_SCALE_TO_FIT
import com.google.android.exoplayer2.C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoListener
import kotlinx.android.synthetic.main.fragment_exo_player.*
import java.io.File

class ExoPlayerFragment : BaseFragment() {

    companion object {
        @JvmStatic
        fun launch(activity: Activity) {
            FragmentSupporterActivity.launcher(activity, ExoPlayerFragment::class.java)
        }
    }

    var exoPlayer: SimpleExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exoPlayer = SimpleExoPlayer.Builder(context!!).build()
    }

    override fun layoutRes(): Int {
        return R.layout.fragment_exo_player
    }

    override fun onViewInit(view: View) {

        val dataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context!!, context!!.packageName))

        val root = Environment.getExternalStorageDirectory().absolutePath

        val filePath = "$root/Movies/v5.mp4"

        val uri = Uri.fromFile(File(filePath))

        val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri)



        exoPlayer?.addAnalyticsListener(object : AnalyticsListener {
            override fun onLoadingChanged(eventTime: AnalyticsListener.EventTime, isLoading: Boolean) {
                super.onLoadingChanged(eventTime, isLoading)
                if (isLoading) {
                    exoPlayer?.playWhenReady = true
                }
            }

            override fun onVideoSizeChanged(eventTime: AnalyticsListener.EventTime, width: Int, height: Int, unappliedRotationDegrees: Int, pixelWidthHeightRatio: Float) {
                super.onVideoSizeChanged(eventTime, width, height, unappliedRotationDegrees, pixelWidthHeightRatio)
//                Log.i("TAG", "${width}+${height}")
//                val sss = (width * 1f) / (height * 1f)
//                val layoutParams = playerView.layoutParams
//                layoutParams.height = (playerView.measuredWidth / sss).toInt()
//                playerView.layoutParams = layoutParams
            }
        })

        exoPlayer?.addVideoListener(object : VideoListener {
            override fun onRenderedFirstFrame() {
                super.onRenderedFirstFrame()
            }
        })
        exoPlayer?.prepare(videoSource)
        playerView.player = exoPlayer
    }

    override fun onDestroyView() {
        super.onDestroyView()
        exoPlayer?.release()

    }

}


