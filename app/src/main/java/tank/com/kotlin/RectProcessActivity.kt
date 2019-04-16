package tank.com.kotlin

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import tank.com.kotlin.customer.BaseActivity
import tank.com.kotlin.view.RectProcessView

/**
 *  @author: vancetang
 *  @date:   2019/2/15 4:26 PM
 */
class RectProcessActivity : BaseActivity() {

    private lateinit var mProcessEt: EditText

    private lateinit var mProcessBt: Button

    private lateinit var mRectProcessView: RectProcessView

    private fun initView() {
        mProcessBt = findViewById(R.id.processBt)
        mProcessEt = findViewById(R.id.processEt)
        mRectProcessView = findViewById(R.id.rectProcessView)
        mProcessBt.setOnClickListener {
            val inputText = mProcessEt.text.toString()
            var process = 0
            try {
                process = inputText.toInt()
                if (process in 0..100) {
                    mRectProcessView.setProcess(process)
                } else {
                    mRectProcessView.setProcess(0)
                }
            } catch (ignore: NumberFormatException) {
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rect_process_activity)
        initView()
    }

}