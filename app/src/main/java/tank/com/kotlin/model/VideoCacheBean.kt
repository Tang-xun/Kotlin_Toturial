package tank.com.kotlin.model

import com.litesuits.orm.db.annotation.Column
import com.litesuits.orm.db.annotation.PrimaryKey
import com.litesuits.orm.db.enums.AssignType
import java.io.Serializable


class VideoCacheBean : Serializable {

    @PrimaryKey(AssignType.BY_MYSELF)
    @Column(KEY)
    var key: String? = null
        get() = field
        set(value) {
            field = value
        }


    @Column(PLAY_TIME)
    var playTime: Long? = null
        get() = field
        set(value) {
            field = value
        }


    @Column(PLAY_COUNT)
    var playCount: Int? = 0
        get() = field
        set(value) {
            field = value
        }


    @Column(VIDEO_PATH)
    var videoPath: String? = null
        get() = field
        set(value) {
            field = value
        }


    @Column(FILE_SIZE)
    var fileSize: Long? = null
        get() = field
        set(value) {
            field = value
        }


    companion object {
        const val PLAY_TIME: String = "playTime"
        const val KEY: String = "key"
        const val PLAY_COUNT: String = "playCount"
        const val VIDEO_PATH: String = "videoPath"
        const val FILE_SIZE: String = "fileSize"
    }

}