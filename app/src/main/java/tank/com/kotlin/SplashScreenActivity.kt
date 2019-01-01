package tank.com.kotlin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView

class SplashScreenActivity : AppCompatActivity() {

    var remainTimeTv: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)
        remainTimeTv = findViewById(R.id.remainTimeTv)
        countDownTimer(AD_MILLIS_IN_FUTURE, AD_COUNT_DOWN_INTERVAL).start()
    }

    private fun countDownTimer(millisInFuture: Long, countDownInterval: Long): CountDownTimer {
        return object : CountDownTimer(millisInFuture, countDownInterval) {
            override fun onFinish() {
                Log.i(TAG, "onFinish ::: ")
                remainTimeTv!!.text = getString(R.string.txt_finish)
                jumpToMainActivity()
            }

            @SuppressLint("SetTextI18n")
            override fun onTick(p0: Long) {
                Log.i(TAG, "onTick $p0")
                if (p0 > AD_MILLIS_SKIP_LIMIT) {
                    remainTimeTv!!.text = "${((p0 - AD_MILLIS_SKIP_LIMIT) / 1000).toInt()} 可跳过"
                } else {
                    remainTimeTv!!.text = "跳\t过"
                    if (!remainTimeTv!!.hasOnClickListeners()) {
                        remainTimeTv!!.setOnClickListener {
                            cancel()
                            jumpToMainActivity()
                        }
                    }
                }
            }
        }
    }


    private fun jumpToMainActivity() {
        startActivity(Intent(applicationContext, MainActivity::class.java))
        finish()
    }

    companion object {
        val TAG: String = SplashScreenActivity::class.java.simpleName
        const val AD_MILLIS_IN_FUTURE: Long = 5000
        const val AD_MILLIS_SKIP_LIMIT: Long = 3000
        const val AD_COUNT_DOWN_INTERVAL: Long = 1000
    }

}