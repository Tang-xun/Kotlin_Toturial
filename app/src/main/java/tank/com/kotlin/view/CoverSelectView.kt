package tank.com.kotlin.view

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import tank.com.kotlin.R

/**
 *  @author: vancetang
 *  @date:   2019/2/1 5:40 PM
 */
class CoverSelectView(context: Context, attrSet: AttributeSet?, defAttr: Int, defRes: Int) :
        ConstraintLayout(context, attrSet, defAttr),
        View.OnTouchListener,
        View.OnClickListener {

    constructor(context: Context) : this(context, null, -1, -1)

    constructor(context: Context, attrSet: AttributeSet) : this(context, attrSet, -1, -1)
    constructor(context: Context, attrSet: AttributeSet, defAttr: Int) : this(context, attrSet, defAttr, -1)

    // pre view imageView
    private var mPreViewIv: ImageView

    // select rect imageView
    private var mSelectView: ImageView
    // thumb container
    private var mThumbLinearLayout: LinearLayout

    private var mThumbCount: Int

    init {
        LayoutInflater.from(context).inflate(R.layout.video_cover_list_layout, this, true)
        mThumbCount = 5
        mPreViewIv = findViewById(R.id.video_pre_iv)
        mSelectView = findViewById(R.id.video_thumbnail_select)
        mThumbLinearLayout = findViewById(R.id.video_thumbnail_ll)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        Log.i(TAG, "onLayout changed->$changed left->$left top->$top right->$right bottom->$bottom")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var specMode = MeasureSpec.getMode(widthMeasureSpec)
        var specSize = MeasureSpec.getSize(widthMeasureSpec)
        when (specMode) {
            MeasureSpec.AT_MOST -> {

            }
            MeasureSpec.EXACTLY -> {

            }
            MeasureSpec.UNSPECIFIED -> {

            }
        }

    }


    companion object {
        val TAG = CoverSelectView::class.java.simpleName ?: "CoverSelectView"
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
