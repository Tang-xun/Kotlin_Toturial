package tank.com.kotlin.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import tank.com.kotlin.R

/**
 * TODO: document your custom view class.
 */
class WeatherRankView : View {

    private var _totalScore: Int 
        get() = _totalScore
        set(value) {
            _totalScore = value
        }


    // act user nickName
    private var _nickName: String? = null
    // nickName color
    private var _nickNameColor: Int = Color.RED
    // nickName size
    private var _nickNameSize: Float = 0f

    private var textPaint: TextPaint? = null
    private var textWidth: Float = 0f
    private var textHeight: Float = 0f

    /**
     * The text to draw
     */
    private var nickName: String?
        get() = _nickName
        set(value) {
            _nickName = value
            invalidateTextPaintAndMeasurements()
        }

    /**
     * The font color
     */
    private var nickNameColor: Int
        get() = _nickNameColor
        set(value) {
            _nickNameColor = value
            invalidateTextPaintAndMeasurements()
        }

    /**
     * In the example view, this dimension is the font size.
     */
    private var nickNameSize: Float
        get() = _nickNameSize
        set(value) {
            _nickNameSize = value
            invalidateTextPaintAndMeasurements()
        }

    /**
     * In the example view, this drawable is drawn above the text.
     */
    private var exampleDrawable: Drawable? = null

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.WeatherRankView, defStyle, 0)

        _nickName = a.getString(
            R.styleable.WeatherRankView_nickName)
        _nickNameColor = a.getColor(
            R.styleable.WeatherRankView_nickNameColor,
            nickNameColor)
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        _nickNameSize = a.getDimension(
            R.styleable.WeatherRankView_nickNameSize,
            nickNameSize)

        if (a.hasValue(R.styleable.WeatherRankView_contentColor)) {
            exampleDrawable = a.getDrawable(
                R.styleable.WeatherRankView_contentColor)
            exampleDrawable?.callback = this
        }

        a.recycle()

        // Set up a default TextPaint object
        textPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.LEFT
        }

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements()
    }

    private fun invalidateTextPaintAndMeasurements() {
        textPaint?.let {
            it.textSize = nickNameSize
            it.color = nickNameColor
            textWidth = it.measureText(nickName)
            textHeight = it.fontMetrics.bottom
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // allocations per draw cycle.
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

        nickName?.let {
            // Draw the text.
            canvas.drawText(it,
                paddingLeft + (contentWidth - textWidth) / 2,
                paddingTop + (contentHeight + textHeight) / 2,
                textPaint)
        }

        // Draw the example drawable on top of the text.
        exampleDrawable?.let {
            it.setBounds(paddingLeft, paddingTop,
                paddingLeft + contentWidth, paddingTop + contentHeight)
            it.draw(canvas)
        }
    }
}
