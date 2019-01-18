package tank.com.kotlin.model

import android.support.annotation.IntegerRes
import org.jetbrains.annotations.NotNull

class CirclePieData(@NotNull name: String, @NotNull value: Float) {

    var name: String = name

    var parent: Float = 0f
    var value: Float = value
    var color: Long = 0
        set(c) {
            field = c
        }
        get() = field
    var angle: Float = 0f
        set(value) {
            field = value
        }
        get() = field

    override fun toString(): String {
        return "CirclePieData(name='$name', parent=$parent, value=$value color=$color angle=$angle)"
    }


}