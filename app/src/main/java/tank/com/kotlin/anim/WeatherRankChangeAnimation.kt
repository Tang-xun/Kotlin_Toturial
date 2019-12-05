package tank.com.kotlin.anim

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

class WeatherRankChangeAnimation : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr)

    /* 当前图榜单用户uid */
    private var mWeatherList:MutableList<Int>? = null

    /* 根据新榜单动画变化控制 */
    enum class changeType {
        First,
        Second,
        Thrid
    }

    fun setWeatherList(user:MutableList<Int>) {
        val animationType : Int;
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }



}