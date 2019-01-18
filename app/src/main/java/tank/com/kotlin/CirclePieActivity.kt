package tank.com.kotlin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import tank.com.kotlin.model.CirclePieData
import tank.com.kotlin.view.CirclePieView

class CirclePieActivity : AppCompatActivity() {

    val names: List<String> = arrayListOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    var values: FloatArray = floatArrayOf(10f, 12f, 32f, 23f, 33f, 5f, 1f)

    var mCirclePieView: CirclePieView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.circle_pie_activity)
        mCirclePieView = findViewById(R.id.circle_content)
        initData()
    }

    private fun initData() {
        Log.i(TAG, "initData start ==> ")
        var start = System.currentTimeMillis()
        var pieData: ArrayList<CirclePieData> = ArrayList()
        for (i in 0..names.size) {
            pieData.add(CirclePieData(names[i], values[i]))
        }
        Log.i(TAG, "initData finish ==> $pieData ${System.currentTimeMillis() - start}ms")

        mCirclePieView?.setData(pieData)
    }

    companion object {
        val TAG: String = CirclePieActivity::class.java.simpleName
    }

}
