package tank.com.kotlin.gl

import android.opengl.EGL14
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLDisplay

/**
 * Created by vancetang on 2020/9/25.
 */
class EGLRender {

  companion object {

    private const val CFG_RED_SIZE = 8
    private const val CFG_GREEN_SIZE = 8
    private const val CFG_BLUE_SIZE = 8
    private const val CFG_ALPHA_SIZE = 8
    private const val CFG_DEPTH_SIZE = 16
    private const val CFG_STENCIL_SIZE = 0

    // EGL config
    private val EGL_CONFIGS = intArrayOf(
      EGL10.EGL_RED_SIZE,
      CFG_RED_SIZE,
      EGL10.EGL_GREEN_SIZE,
      CFG_GREEN_SIZE,
      EGL10.EGL_BLUE_SIZE,
      CFG_BLUE_SIZE,
      EGL10.EGL_ALPHA_SIZE,
      CFG_ALPHA_SIZE,
      EGL10.EGL_DEPTH_SIZE,
      CFG_DEPTH_SIZE,
      EGL10.EGL_STENCIL_SIZE,
      CFG_STENCIL_SIZE,
      EGL10.EGL_RENDERABLE_TYPE,
      EGL14.EGL_OPENGL_ES2_BIT,
      EGL10.EGL_NONE)

    private val EGL_ATTRIBUTE = intArrayOf(
      EGL14.EGL_CONTEXT_CLIENT_VERSION,
      2,
      EGL10.EGL_NONE)


    fun getEGLConfig(egl: EGL10, display: EGLDisplay): EGLConfig? {
      val configCount = intArrayOf()
      if (!egl.eglChooseConfig(display, EGL_CONFIGS, null, 0, configCount)) {
        return null
      }

      val configs = arrayOf<EGLConfig>()
      if (!egl.eglChooseConfig(display, EGL_CONFIGS, configs, configCount[0], configCount)) {
        return null
      }
      return chooseEGLConfig(egl, display, configs, configCount[0])

    }

    private fun chooseEGLConfig(egl: EGL10, display: EGLDisplay, configs: Array<EGLConfig>?, count: Int): EGLConfig? {
      if (configs == null) {
        return null;
      }

      val value = intArrayOf(1)


    }

  }


  fun create() {

  }

  fun destroy() {

  }


}