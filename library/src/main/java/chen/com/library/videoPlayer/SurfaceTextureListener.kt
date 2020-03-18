package chen.com.library.videoPlayer

import android.graphics.SurfaceTexture
import android.view.TextureView

interface SurfaceTextureListener :TextureView.SurfaceTextureListener {

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {

    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        return true
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
    }
}