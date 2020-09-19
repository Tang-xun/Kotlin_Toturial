package tank.com.kotlin.view

import android.content.Context
import android.graphics.*
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.OverScroller
import androidx.annotation.AttrRes
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.ViewCompat
import tank.com.kotlin.R
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Created by vancetang on 2020/9/19.
 */
class CustomerRulerView : View {

  private enum class Direction {
    NONE, VERTICAL
  }


  private val onGestureListener = object : GestureDetector.SimpleOnGestureListener() {

    override fun onDown(p0: MotionEvent?): Boolean {
      parent.requestDisallowInterceptTouchEvent(true)
      mCurrentScrollDirection = Direction.NONE
      return true
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
      scroller?.forceFinished(true)

      if (Direction.NONE == mCurrentFlingDirection) {
        mCurrentScrollDirection = if (abs(distanceX) < abs(distanceY)) {
          Direction.VERTICAL
        } else {
          Direction.NONE
        }
      }

      if (mCurrentScrollDirection == Direction.VERTICAL) {
        mCurrentOrigin.y -= distanceY;
        checkOriginY()
        ViewCompat.postInvalidateOnAnimation(this@CustomerRulerView)
      }
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
      scroller.let {
        it.forceFinished(true)
        mCurrentFlingDirection = mCurrentScrollDirection
        if (mCurrentFlingDirection == Direction.VERTICAL) {
          it.fling(
            mCurrentOrigin.x.toInt(),
            mCurrentOrigin.y.toInt(),
            0,
            velocityY.toInt(),
            Int.MIN_VALUE,
            Int.MAX_VALUE,
            Int.MIN_VALUE,
            0)
        }
        return true
      }
    }
  }

  private var label: String = "LABEL"
  private var items: List<*> = ItemCreator.range(0, 60)

  // 游标颜色
  private var cursorColor: Int = Color.RED

  // 刻度颜色
  private var scaleColor: Int = Color.LTGRAY

  // 整刻度颜色
  private var scalePointerColor: Int = Color.DKGRAY

  // 可滚动高度
  private var scrollMaxHeight = 0f

  // 最小刻度高度
  private var scaleHeight = 0

  // 刻度宽度
  private var scaleWidth = 0

  // 整刻度宽度
  private var scalePointerWidth = 0

  // 游标宽度
  private var cursorWidth = 0

  // 刻度画笔宽度
  private var scaleStrokeWidth = 0f

  // 刻度画笔
  private var scalePaint: Paint? = null

  // 10刻度画笔
  private var scalePointerPaint: Paint? = null

  // 10刻度文字画笔
  private var scalePointerTextPaint: Paint? = null

  // 游标刻度
  private var cursorPaint: Paint? = null

  // 游标文字画笔
  private var cursorTextPaint: Paint? = null

  // 标签文字画笔
  private var cursorLabelPaint: Paint? = null

  // 刻度间隔
  private var scaleOffset = 0

  // 刻度与文字间距离
  private var scaleTextOffset = 0

  private var scaleLeft: Int = 0


  // 滚动控制
  private lateinit var scroller: OverScroller

  // 最大滚动距离
  private var maxFlingVelocity = 0

  // 最小滚动距离
  private var minFlingVelocity = 0

  private var touchSlop = 0

  // 当前滚动方向
  private var mCurrentScrollDirection = Direction.NONE

  // 当前惯性滚动方向
  private var mCurrentFlingDirection = Direction.NONE

  // 当前滚动x,y
  private val mCurrentOrigin = PointF(0f, 0f)

  // 手势支持
  private lateinit var mGestureDetector: GestureDetectorCompat


  constructor(context: Context) : super(context) {
    resolveAttribute(context, null, 0, 0)
    init()
  }


  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    resolveAttribute(context, attrs, 0, 0)
    init()
  }

  constructor(context: Context, attrs: AttributeSet, @AttrRes defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    resolveAttribute(context, attrs, defStyleAttr, 0)
    init()
  }

  /**
   * todo
   * @desc init customer attribute from xml
   */
  private fun resolveAttribute(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
    scaleStrokeWidth = dp2Px(context, 1f) / 2f
    setupWidthInfo()
    scalePointerColor = Color.DKGRAY

    val a = context.theme.obtainStyledAttributes(attrs, R.styleable.app, defStyleAttr, defStyleRes)

    for (i in 0 until a.indexCount) {
      val attr = a.getIndex(i)
      when (attr) {
        R.styleable.app_scaleWidth -> {
          setupWidthInfo(a.getDimensionPixelOffset(attr, 50))
        }
        R.styleable.app_scaleHeight -> {
          scaleHeight = a.getDimensionPixelOffset(attr, 5)
        }
        R.styleable.app_cursorColor -> {
          cursorColor = a.getColor(attr, Color.YELLOW)
        }
        R.styleable.app_scaleColor -> {
          scaleColor = a.getColor(attr, Color.LTGRAY)
        }
        R.styleable.app_scalePointerColor -> {
          scalePointerColor = a.getColor(attr, Color.DKGRAY)
        }
      }
    }
    scaleTextOffset = dp2Px(context, 32f)
    a.recycle()
  }

  private fun setupWidthInfo(width: Int = 50) {
    scaleWidth = width
    scalePointerWidth = (scaleWidth * 3 / 2.0f).toInt()
    cursorWidth = (scaleWidth * 10 / 3.0f).toInt()
  }

  /**
   * todo
   * @desc init paint、scroller、gesture
   * @see ViewConfiguration https://blog.csdn.net/luo_boke/article/details/50350459
   * @see Paint https://fandazeng.github.io/2018/09/21/android/view/Paint%E5%9F%BA%E7%A1%80%E7%BB%83%E4%B9%A0/
   */
  private fun init() {
    scroller = OverScroller(context)
    mGestureDetector = GestureDetectorCompat(context, onGestureListener);
    // 最大加速度
    maxFlingVelocity = ViewConfiguration.get(context).scaledMaximumFlingVelocity
    // 最小加速度
    minFlingVelocity = ViewConfiguration.get(context).scaledMinimumFlingVelocity
    // 区别单击子控件和滑动操作的一个阈值
    touchSlop = ViewConfiguration.get(context).scaledTouchSlop

    // 抗锯齿
    scalePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      this.color = scaleColor
      this.style = Paint.Style.FILL
    }

    cursorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      this.color = cursorColor
      this.style = Paint.Style.FILL
    }

    scalePointerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      this.color = scalePointerColor
      this.style = Paint.Style.FILL
    }

    scalePointerTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      this.color = scaleColor
      this.style = Paint.Style.FILL
      this.textSize = spToPx(context, 14f).toFloat()
    }

    cursorTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      this.color = cursorColor
      this.style = Paint.Style.FILL
      this.textSize = spToPx(context, 32f).toFloat()
    }

    cursorLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      this.color = scalePointerColor
      this.style = Paint.Style.FILL
      this.textSize = spToPx(context, 16f).toFloat()
    }
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    val width = measuredWidth
    val height = measuredHeight
  }

  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)
  }

  fun setItems(items: List<*>) {
    this.items = items
  }

  fun getItems(): List<*> {
    return items
  }

  fun setLabel(label: String) {
    this.label = label
    initScaleLeft()
    invalidate()
  }

  override fun onTouchEvent(event: MotionEvent?): Boolean {
    val result = mGestureDetector.onTouchEvent(event)
    if (event?.action == MotionEvent.ACTION_UP
      && mCurrentFlingDirection == Direction.NONE
    ) {
      if (mCurrentFlingDirection == Direction.VERTICAL) {
        snapScroll()
      }
      mCurrentScrollDirection = Direction.NONE
    }

    return result
  }

  override fun computeScroll() {
    super.computeScroll()
    if (scroller.isFinished) {
      if (mCurrentFlingDirection != Direction.NONE) {
        mCurrentFlingDirection = Direction.NONE
        snapScroll()
      }
      return
    }

    if (mCurrentFlingDirection != Direction.NONE && forceFinishScroll()) {
      snapScroll()
    } else if (scroller.computeScrollOffset()) {
      mCurrentOrigin.y = scroller.currY.toFloat()
      checkOriginY()
      ViewCompat.postInvalidateOnAnimation(this)
    } else {
      val startY = when {
        mCurrentOrigin.y > 0 -> {
          0f
        }
        mCurrentOrigin.y < height - measuredHeight -> {
          measuredHeight - scrollMaxHeight
        }
        else -> {
          mCurrentOrigin.y
        }
      }
      scroller.startScroll(0, startY.toInt(), 0, 0, 0)
    }
  }

  private fun checkOriginY() {

  }

  private fun forceFinishScroll(): Boolean {

  }

  private fun snapScroll() {
  }

  private fun initScaleLeft() {
    val labelSize = measureTextSize(cursorLabelPaint!!, label)
    scaleLeft = (measuredWidth - scalePointerWidth + scaleTextOffset + labelSize[0].toInt()) / 2
  }

  companion object {

    fun dp2Px(context: Context, dp: Float): Int {
      return (context.resources.displayMetrics.density * dp).roundToInt()
    }

    fun spToPx(context: Context, sp: Float): Int {
      return (TypedValue.applyDimension(2, sp, context.resources.displayMetrics) + 0.5f).toInt()
    }
  }

  class ItemCreator {
    companion object {
      fun range(start: Int, end: Int): List<Int> {
        return (start..end).map { it }.toList()
      }
    }
  }

  // measure text react
  fun measureTextSize(paint: Paint, text: String): FloatArray {
    if (TextUtils.isEmpty(text)) return floatArrayOf(0f, 0f)
    val width = paint.measureText(text, 0, text.length)
    val bounds = Rect()
    paint.getTextBounds(text, 0, text.length, bounds)
    return floatArrayOf(width, bounds.height().toFloat())
  }
}

}