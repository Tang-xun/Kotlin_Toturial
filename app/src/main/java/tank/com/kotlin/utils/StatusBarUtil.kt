package tank.com.kotlin.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.google.zxing.client.result.VINParsedResult
import tank.com.kotlin.R
import java.lang.Exception
import java.lang.reflect.Field
import java.lang.reflect.Method

class StatusBarUtil {

    companion object {
        private var immersiveMode: Boolean? = null
            set(value) {
                field = value
            }
            get() = field
        private var blockText: Boolean? = null
            set(value) {
                field = value
            }
            get() = field

        fun setColor(activity: Activity, color: Int, isFrontColorDark: Boolean) {
            if (isFullScreen(activity)) {
                return
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                immersiveMode = true
                setStatusBarColor(activity, color, isFrontColorDark)
            }
        }

        fun setCoverStatus(activity: Activity, isFrontColorDark: Boolean) {
            if (isFullScreen(activity)) {
                return
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                immersiveMode = true
                setStatusBarColor(activity, 0, isFrontColorDark)
            }
        }

        private val TAG: String = StatusBarUtil::class.java.simpleName

        fun isFullScreen(activity: Activity): Boolean {
            val flags: Int = activity.window.attributes.flags
            Log.i(TAG, "isFullScreen --> $flags")
            return WindowManager.LayoutParams.FLAG_FULLSCREEN and flags == WindowManager.LayoutParams.FLAG_FULLSCREEN
        }

        fun createStatusBarView(activity: Activity, color: Int): View {
            val statusBarView = View(activity)
            statusBarView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity))
            statusBarView.setBackgroundColor(color)
            return statusBarView
        }

        fun setRootView(activity: Activity) {
            val rootView: ViewGroup = (activity.findViewById(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
            rootView.fitsSystemWindows = true
            rootView.clipToPadding = true
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun setStatusBarColor(activity: Activity, color: Int, isFrontColorDark: Boolean) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                var defaultColor: Int = color
                if (setMIUIStatusBarLightMode(activity, isFrontColorDark)) {
                    setAndroidStatusTextColor(activity, isFrontColorDark)
                    blockText = true
                } else if (setFLYMEStatusBarLightMode(activity, isFrontColorDark)) {
                    blockText = true
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    blockText = true
                    setAndroidStatusTextColor(activity, isFrontColorDark)
                } else {
                    defaultColor = activity.resources.getColor(R.color.my_black)
                }
                activity.window.statusBarColor = defaultColor
            }
        }

        /**
         * 设置MIUI系统状态栏字体为深色，需要MIUI6.0以上版本支持
         *
         * @param activity
         * @param isFrontColorDart 是否把状态栏字体颜色设置成深色
         */
        @SuppressLint("PrivateApi")
        fun setMIUIStatusBarLightMode(activity: Activity, isFrontColorDark: Boolean): Boolean {
            val window: Window? = activity.window

            var result = false

            if (window != null) {
                val clazz: Class<Window> = Window::class.java
                try {
                    var dartModeFlag = 0
                    val layoutParams: Class<*>? = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
                    val field: Field = layoutParams!!.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
                    dartModeFlag = field.getInt(layoutParams)
                    var extraFlagField: Method = clazz.getMethod("setExtraFlags", Int::class.java, Int::class.java)
                    if (isFrontColorDark) {
                        // 设置状态栏黑色字体，且背景透明
                        extraFlagField.invoke(window, dartModeFlag, dartModeFlag)
                    } else {
                        // 清除黑色字体
                        extraFlagField.invoke(window, 0, dartModeFlag)
                    }
                    result = true
                } catch (e: Exception) {
                    // not miui
                }
            }
            return result
        }

        // 系统兼容性问题
        fun setFLYMEStatusBarLightMode(activity: Activity, isFrontColorDark: Boolean): Boolean {
            val window: Window? = activity.window
            var result: Boolean = false
            if (window != null) {
                try {
                    val lp: WindowManager.LayoutParams = window.attributes
                    val dartFlag = WindowManager.LayoutParams::class.java.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
                    val meizuFlag: Field = WindowManager.LayoutParams::class.java.getDeclaredField("meizuFlags")
                    dartFlag.isAccessible = true
                    meizuFlag.isAccessible = true

                    val bit: Int = dartFlag.getInt(null)
                    var value: Int = meizuFlag.getInt(lp)

                    if (isFrontColorDark) {
                        value = value or bit
                    } else {
                        value = value and bit.inv()
                    }
                    meizuFlag.setInt(lp, value)
                    window.attributes = lp

                    result = true
                } catch (e: Exception) {
                    // not meizu
                }
            }
            return result
        }

        fun getStatusBarHeight(context: Context): Int {
            var statusBarHeight: Int = -1
            val resourceId: Int = context.resources.getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = context.resources.getDimensionPixelSize(resourceId)
            }
            return statusBarHeight
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun setAndroidStatusTextColor(activity: Activity, isFrontColorDark: Boolean) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                activity.window.decorView.systemUiVisibility = if (isFrontColorDark) {
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                }
            }
        }

        fun setPadding(context: Context, view: View) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.setPadding(view.paddingLeft, view.paddingTop + getStatusBarHeight(context), view.paddingRight, view.paddingBottom)
            }
        }
    }


}