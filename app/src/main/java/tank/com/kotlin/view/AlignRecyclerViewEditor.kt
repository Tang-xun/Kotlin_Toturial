package tank.com.kotlin.view

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import tank.com.kotlin.R

class AlignRecyclerViewEditor(context: Context, attributeSet: AttributeSet, defStyleType: Int) : RelativeLayout(context, attributeSet, defStyleType) {


    var mRecyclerView: RecyclerView? = null
    var mCutView: View? = null

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed,l,t,r,b)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.align_recyclerview_editor, this, true)
        mCutView = findViewById(R.id.cut_time_line)
        mRecyclerView = findViewById(R.id.pic_frame_layout)

        mRecyclerView?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

    }

}