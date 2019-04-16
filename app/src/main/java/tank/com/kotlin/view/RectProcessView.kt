package tank.com.kotlin.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View

/**
 *  @author: vancetang
 *  @date:   2019/2/15 3:21 PM
 */
class RectProcessView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : View(context, attrs, defStyleAttr) {

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, -1)

    // 设置当前进度
    private var mCurrentProcess: Int = 0

    private var mProcessPath: Path = Path()
    private var mProcessPathDest: Path = Path()
    private var mProcessPathMeasure: PathMeasure = PathMeasure()

    private var mWidth: Int = 0
    private var mHeight: Int = 0
    private var mBaseRect: RectF = RectF()

    private var mRectPaint: Paint = Paint()
    private var mProcessRectPaint: Paint = Paint()
    private var mTotalSideLength: Float = 0F

    init {
        // setup baseRect
        mRectPaint.color = Color.GRAY
        mRectPaint.strokeWidth = 1F
        mRectPaint.textSize = 20F
        mRectPaint.isAntiAlias = true
        mRectPaint.style = Paint.Style.STROKE

        // setup processRect
        mProcessRectPaint.color = Color.BLUE
        mProcessRectPaint.strokeWidth = 20F
        mProcessRectPaint.isAntiAlias = true
        mProcessRectPaint.style = Paint.Style.STROKE
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        mHeight = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        setMeasuredDimension(mWidth, mHeight)
        mTotalSideLength = (mWidth + mHeight) * 2F
        mBaseRect = RectF(0F, 0F, mWidth.toFloat(), mHeight.toFloat())
        Log.i(TAG, "onMeasure ::: mWidth->$mWidth mHeight->$mHeight mTotalSideLength->$mTotalSideLength mRect->$mBaseRect")
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawRoundRect(mBaseRect, 20f, 20f, mRectPaint)
        canvas?.drawText(mCurrentProcess.toString(), 0F, 0F, mRectPaint)
        // 绘制 坐标系
        mRectPaint.color = Color.RED
        canvas?.drawLine(0F, -mHeight / 2F, 0F, mHeight / 2F, mRectPaint)
        canvas?.drawLine(-mWidth / 2F, 0F, mWidth / 2F, 0F, mRectPaint)

        calRectPath()

        canvas?.drawPath(mProcessPathDest, mProcessRectPaint)
    }

    private fun calRectPath() {
        mProcessPath.addRoundRect(mBaseRect, 20F, 20F, Path.Direction.CW)
        mProcessPathMeasure = PathMeasure(mProcessPath, false)
        val endLength = mProcessPathMeasure.length * mCurrentProcess / 100F
        mProcessPathDest.reset()
        mProcessPathMeasure.getSegment(0F, endLength, mProcessPathDest, true)
    }

    fun setProcess(process: Int) {
        Log.i(TAG, "setProcess ::: process->$process")
        if (process in 0..100) {
            mCurrentProcess = process
            invalidate()
        }
    }

    companion object {
        const val TAG = "RectProcessView"
    }

}
