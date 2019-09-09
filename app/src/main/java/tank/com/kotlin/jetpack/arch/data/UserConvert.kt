package tank.com.kotlin.jetpack.arch.data

import androidx.room.TypeConverter
import java.util.*

class UserConvert {

    @TypeConverter
    private fun dateToLong(date: Date?) = if (date == null) System.currentTimeMillis() else date.time

    @TypeConverter
    private fun LongTodate(time: Long) = if (time >= 0L) Date(time) else Date(System.currentTimeMillis())

}