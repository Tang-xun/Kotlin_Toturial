package tank.com.kotlin.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.util.AttributeSet
import android.view.TextureView
import android.view.ViewGroup
import android.widget.FrameLayout
import kotlin.math.max

@SuppressLint("ViewConstructor")
class PlayTextureView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : FrameLayout(context, attrs, defStyleAttr) {

  constructor(context: Context) : this(context, null, 0)
  constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

  private var mTextureView: TextureView? = null
  private var mSurfaceTexture: SurfaceTexture? = null

  private var mSurfaceTextureListener: TextureView.SurfaceTextureListener? = null
    set(value) {
      field = value
    }

  private var mVideoWidth: Int? = null
  private var mVideoHeight: Int? = null

  init {
    initTextureView(null)
  }

  fun initTextureView(surfaceTexture: SurfaceTexture?) {
    mTextureView = TextureView(getContext())

    mTextureView?.let {

      it.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
      it.addOnLayoutChangeListener { v, l, t, r, b, ol, ot, or, ob ->
        if (mVideoWidth != 0 && mVideoHeight != 0) {
          setVideoCenter(mTextureView?.width!!.toFloat(), mTextureView?.height!!.toFloat(), mVideoWidth!!.toFloat(), mVideoHeight!!.toFloat())
        }
      }

      mSurfaceTexture = surfaceTexture?.let { surfaceTexture } ?: newSurfaceTexture()

      it.surfaceTexture = mSurfaceTexture

      it.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture?, p1: Int, p2: Int) {
          mSurfaceTextureListener?.onSurfaceTextureSizeChanged(p0, p1, p2)
        }

        override fun onSurfaceTextureUpdated(p0: SurfaceTexture?) {
          mSurfaceTextureListener?.onSurfaceTextureUpdated(p0)
        }

        override fun onSurfaceTextureDestroyed(p0: SurfaceTexture?): Boolean {
          mSurfaceTextureListener?.onSurfaceTextureDestroyed(p0)
          return false
        }

        override fun onSurfaceTextureAvailable(p0: SurfaceTexture?, p1: Int, p2: Int) {
          mSurfaceTextureListener?.onSurfaceTextureAvailable(p0, p1, p2)
        }
      }

      addView(mTextureView, 0)
    }
  }

  private fun newSurfaceTexture(): SurfaceTexture {
    val textures: IntArray = IntArray(1)
    GLES20.glGenTextures(1, textures, 0)
    val texName = textures[0]
    val surfaceTexture = SurfaceTexture(texName)
    surfaceTexture.detachFromGLContext()
    return surfaceTexture
  }

  private fun setVideoCenter(width: Float, height: Float, videoWidth: Float, videoHeight: Float) {
    Matrix().let {
      val sx = width.div(videoWidth)
      val sy = height.div(videoHeight)
      val maxScale = max(sx, sy)

      it.preTranslate((videoWidth.minus(width)).div(2), (videoHeight.minus(height)).div(2))
      it.preScale((videoWidth / width), (videoHeight / height))
      it.postScale(maxScale, maxScale, width.div(2), height.div(2))

      mTextureView?.setTransform(it)
      mTextureView?.postInvalidate()
      return
    }
  }

  fun setVideoSize(width: Int, height: Int) {
    mVideoWidth = width
    mVideoHeight = height
  }

  fun resetTextureView() {
    resetTextureView(null)
  }

  fun resetTextureView(surfaceTexture: SurfaceTexture?) {
    removeView(mTextureView)
    initTextureView(surfaceTexture)
  }

  fun getSurfaceTexture(): SurfaceTexture? {
    mSurfaceTexture?.let { return it } ?: run {
      return null
    }
  }

}