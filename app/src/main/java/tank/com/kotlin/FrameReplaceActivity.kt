package tank.com.kotlin

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Observable
import javax.microedition.khronos.egl.EGL

fun Context.showToast(msg: String) {
  Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
}


/**
 * Created by vancetang on 2020/9/25.
 */
class FrameReplaceActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_frame_replace)
    this.showToast("onCreate");

    findViewById<Button>(R.id.glsl_change).setOnClickListener {
      glslChange()
    }

    findViewById<Button>(R.id.blend_change).setOnClickListener {
      blendChange()
    }
  }

  private fun blendChange() {
    replace(false)
  }

  private fun glslChange() {
    replace(true)
  }

  private fun replace(isBlend: Boolean) {
    Observable.fromCallable {
      return@fromCallable initEgl()
    }


  }

  private fun initEgl(): EGL {

  }

}