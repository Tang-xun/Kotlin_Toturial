package tank.com.kotlin_demon

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button

/**
 * tank.com.kotlin_demon <br/>
 * Created by tank325 on 2018/12/18/4:09 PM.
 */
class MainActivity : AppCompatActivity() {
    private val TAG: String = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        Log.i(TAG, "onCreate ::: ")
        findViewById<Button>(R.id.goSurfaceViewBt).setOnClickListener {
            startActivity(Intent(applicationContext, SurfaceViewActivity::class.java))
        }
    }

}