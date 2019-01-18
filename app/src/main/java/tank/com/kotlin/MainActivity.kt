package tank.com.kotlin

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

    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        findViewById<Button>(R.id.goSurfaceViewBt).setOnClickListener {
            startActivity(Intent(applicationContext, SurfaceViewActivity::class.java))
        }

        findViewById<Button>(R.id.goExpendViewBt).setOnClickListener {
            startActivity(Intent(applicationContext, ExpandableListActivity::class.java))
        }

        findViewById<Button>(R.id.goRecyclerViewBt).setOnClickListener {
            startActivity(Intent(applicationContext, RecyclerViewActivity::class.java))
        }

        findViewById<Button>(R.id.goQRScanBt).setOnClickListener {
            startActivity(Intent(applicationContext, QRCodeScanActivity::class.java))
        }

        findViewById<Button>(R.id.goIJKPlayer).setOnClickListener {
            startActivity(Intent(applicationContext, IJKPlayerActivity::class.java))
        }
        findViewById<Button>(R.id.goCirclePieView).setOnClickListener {
            startActivity(Intent(applicationContext, CirclePieActivity::class.java))
        }
    }

}