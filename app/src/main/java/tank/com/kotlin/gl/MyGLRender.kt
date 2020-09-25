package tank.com.kotlin.gl

import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by vancetang on 2020/9/25.
 */
class MyGLRender : GLSurfaceView.Renderer {

  /**
   * 每一帧绘制时被调用。
   */
  override fun onDrawFrame(gl: GL10?) {
  }

  /**
   * 当 GLSurfaceView 创建时调用，主要做一些准备工作。
   */
  override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
  }

  /**
   * 当 GLSurfaceView 视图改变时调用，第一次创建时也会被调用。
   */
  override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
  }
}