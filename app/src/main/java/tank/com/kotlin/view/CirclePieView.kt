package tank.com.kotlin.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import tank.com.kotlin.model.CirclePieData

class CirclePieView(context: Context, attrSet: AttributeSet, defAttr: Int, defRes: Int) : View(context, attrSet, defAttr, defRes) {

    constructor(context: Context, attrSet: AttributeSet, defAttr: Int) : this(context, attrSet, defAttr, 0)
    constructor(context: Context, attributeSet: AttributeSet) : this(context, attributeSet, 0)

    // set colors
    var colors: LongArray = longArrayOf(0xFFCCFF00, 0xFF6495ED, 0xFFE32636, 0xFF800000, 0xFF808000, 0xFFFF8C69, 0xFF808080,
            0xFFE6B800, 0xFF7CFC00)

    private var mStartAngle: Float = 0F

    private var mWidth: Int = 0
    private var mHeight: Int = mWidth

    private var mRectF: RectF? = null

    private var mPieData: ArrayList<CirclePieData>? = null


    private var mPaint: Paint? = null

    init {
        initPaint()
    }

    private fun initPaint() {
        mPaint = Paint()
        mPaint?.style = Paint.Style.FILL
        mPaint?.isAntiAlias = true
    }

    public fun setData(pieData: ArrayList<CirclePieData>) {
        Log.i(TAG, "setData start")
        val start = System.currentTimeMillis()
        var sumValue = 0F
        pieData.forEach {
            sumValue = sumValue.plus(it.value)
        }
        var colorIndex = 0
        pieData.forEach {
            it.parent = it.value / sumValue
            it.angle = it.parent * 360
            it.color = colors[colorIndex]
            colorIndex = colorIndex.inc()
        }
        invalidate()
        Log.i(TAG, "setData end ===> ${System.currentTimeMillis() - start}")
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
        val r: Float = if (mWidth == height) Math.min(mWidth, mHeight) * 1.0F else mWidth * 1.0F
        mRectF = RectF(-r, -r, r, r)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (null == mPieData) return

        var currentAngle = mStartAngle

        canvas?.translate(mWidth / 2.0f, mHeight / 2.0f)

        mPieData?.forEach {
            canvas?.drawArc(mRectF, currentAngle, it.angle, true, mPaint)
            currentAngle = currentAngle.plus(it.angle)
        }
    }

    companion object {
        val TAG = CirclePieView::class.java.simpleName
    }
}