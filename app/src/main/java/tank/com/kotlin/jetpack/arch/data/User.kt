package tank.com.kotlin.jetpack.arch.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "user_info")
class User {

    constructor(name:String) {
        this.name = name
        this.cTime = Date(System.currentTimeMillis()).toString()
    }

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Long = 0L

    @ColumnInfo(name = "name")
    var name: String = ""

    @ColumnInfo(name = "")
    var phone: String = ""

    @ColumnInfo(name = "address")
    var address: String = ""

    @ColumnInfo(name = "email")
    var email: String = ""

    @ColumnInfo(name = "create_time")
    var cTime: String = ""

    @ColumnInfo(name = "update_time")
    var uTime: String = ""
}