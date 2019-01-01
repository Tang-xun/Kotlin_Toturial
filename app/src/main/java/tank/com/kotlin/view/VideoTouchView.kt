package tank.com.kotlin.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import tank.com.kotlin.R

class VideoTouchView(context: Context, atts: AttributeSet?, defStyleAttr: Int) : FrameLayout(context, atts, defStyleAttr) {

    constructor(context: Context, atts: AttributeSet) : this(context, atts, 0)
    constructor(context: Context) : this(context, null, 0)

    private var slideMove: Float = 0f

    private var slideClick: Float = 0f

    var mOnTouchSlideListener: OnTouchSlideListener? = null
        set(value) {
            field = value
        }

    init {
        slideMove = resources.getDimension(R.dimen.video_slide_move)
        slideClick = resources.getDimension(R.dimen.video_slide_click)
    }

    private var downX: Float = 0f

    private var downY: Float = 0f

    private var isSliding: Boolean = false

    private var dontSlide: Boolean = false

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.rawX
                downY = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                mOnTouchSlideListener?.let {
                    val moveX = event.rawX
                    val moveY = event.rawY

                    val slideX = moveX - downX
                    var slideY = moveY - downY

                    if (isSliding) {
                        mOnTouchSlideListener!!.onSlide(slideX)
                        downX = moveX
                    } else {
                        if (Math.abs(slideX) > slideMove && !dontSlide) {
                            requestDisallowInterceptTouchEvents(this, true)
                            isSliding = true
                            downX = moveX
                        } else {
                            dontSlide = true
                        }
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                requestDisallowInterceptTouchEvents(this, false)
                if (isSliding) mOnTouchSlideListener?.onUp() else {
                    val upX = event.rawX
                    val upY = event.rawY

                    if (Math.abs(downX - upX) > slideClick && Math.abs(downY - upY) > slideClick) {
                        mOnTouchSlideListener?.onClick()
                    }
                }
                isSliding = false
                dontSlide = false
            }
        }
        return true
    }

    private fun requestDisallowInterceptTouchEvents(viewGroup: ViewGroup, b: Boolean) {
        val parent: ViewGroup

        if (viewGroup.parent is ViewGroup) {
            parent = viewGroup.parent as ViewGroup
            // TODO('Notice : there have some different logic with origin source ')
            requestDisallowInterceptTouchEvents(parent, b)
            parent.requestDisallowInterceptTouchEvent(b)
        }
    }

    interface OnTouchSlideListener {
        fun onSlide(distant: Float)
        fun onUp()
        fun onClick()
    }

}