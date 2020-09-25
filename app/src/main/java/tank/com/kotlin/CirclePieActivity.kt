package tank.com.kotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import tank.com.kotlin.model.CirclePieData
import tank.com.kotlin.view.CirclePieView

class CirclePieActivity : AppCompatActivity() {

    private var mCirclePieView: CirclePieView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.circle_pie_activity)
        mCirclePieView = findViewById(R.id.circle_content)
        initData()
    }

    private fun initData() {
        Log.i(TAG, "initData start ==> ")
        val start = System.currentTimeMillis()
        val pieData: ArrayList<CirclePieData> = ArrayList()
        for (i in 0 until 20) {
            pieData.add(CirclePieData(i.toString(), 1f))
        }
        Log.i(TAG, "initData finish ==> $pieData ${System.currentTimeMillis() - start}ms")

        mCirclePieView?.setData(pieData)
    }

    companion object {
        val TAG: String = CirclePieActivity::class.java.simpleName
    }

}
