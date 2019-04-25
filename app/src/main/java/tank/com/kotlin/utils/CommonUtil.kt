package tank.com.kotlin.utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import tank.com.kotlin.MainApplication
import java.math.BigInteger
import java.security.MessageDigest

fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}

class CommonUtil {
    companion object {
        val TAG = CommonUtil::class.java.simpleName
        fun toMMSS(time: Long): String {
            if (time <= 0) {
                return "00:00"
            }
            // 秒数
            val ss = (time / 1000).toInt()
            // 分钟数
            val mm = ss / 60
            // 小时数
            val hh = mm / 60

            // 剩余秒数
            val s = if (ss % 60 < 10) {
                "0${ss % 60}"
            } else {
                "${ss % 60}"
            }
            // 剩余分钟数
            val m = if (mm % 60 < 10) {
                "0${mm % 60}"
            } else {
                "${mm % 60}"
            }

            return if (hh > 0) {
                "$hh:$m:$s"
            } else {
                "$mm:$s"
            }
        }

        fun getWindowWidth(): Int {
            return fetchDisplayMetrics().widthPixels
        }

        fun getWidowHeight(): Int {
            return fetchDisplayMetrics().heightPixels
        }

        fun width(): Int {
            val wm: WindowManager = MainApplication.mainApplication!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            return wm.defaultDisplay.width
        }

        fun height(): Int {
            val wm: WindowManager = MainApplication.mainApplication!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            return wm.defaultDisplay.height
        }


        private fun fetchDisplayMetrics(): DisplayMetrics {
            val wm: WindowManager = MainApplication.mainApplication!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val metrics = DisplayMetrics()
            wm.defaultDisplay.getMetrics(metrics)
            return metrics
        }

        fun dip2Px(dip: Int): Int {
            return (fetchDisplayMetrics().density * dip + 0.5f).toInt()
        }

        fun px2Dip(px: Int): Int {
            return (px / fetchDisplayMetrics().density + 0.5f).toInt()
        }

    }
}