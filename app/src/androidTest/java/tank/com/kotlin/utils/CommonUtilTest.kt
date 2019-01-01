package tank.com.kotlin.utils

import android.util.Log
import org.junit.After
import org.junit.Before
import org.junit.Test

class CommonUtilTest {

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun md5Test() {
        val name = "tangxun";
        Log.i(TAG, "$name -> ${name.md5()}")
    }

    @Test
    fun toMMSSTest() {
        val time = 10 * 1000 * 60 * 60L + 1000 * 60
        val res = CommonUtil.toMMSS(time)
        Log.i(TAG, "$time -> $res")
    }

    @Test
    fun getWithAndHeithTest() {
        Log.i(TAG, "D_height -> ${CommonUtil.height()}")
        Log.i(TAG, "D_width -> ${CommonUtil.width()}")

        Log.i(TAG, "height -> ${CommonUtil.getWidowHeight()}")
        Log.i(TAG, "width -> ${CommonUtil.getWindowWidth()}")
    }

    companion object {
        val TAG = CommonUtilTest::class.java.simpleName
    }
}