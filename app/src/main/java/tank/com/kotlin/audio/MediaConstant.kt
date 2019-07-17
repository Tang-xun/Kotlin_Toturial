package tank.com.kotlin.audio

/**
 *  @author: vancetang
 *  @date:   2019/4/16 8:32 PM
 */
class MediaConstant {

    object Constant{
        // invalid media index
        const val MEDIA_INVALID_INDEX = -1
        // MediaCodec dequeueInputBuffer
        const val MEDIA_READ_TIMEOUT = 10000

        const val MEDIA_ACC_HEAD_LENGTH = 7
    }

    object Errors {
        const val ERROR_INIT_MEDIA_PATH_INVALID = -100001
        // init media extractor error
        const val ERROR_INIT_MEDIA_EXTRACTOR = -100002
        // not found audio truck
        const val ERROR_INIT_NOT_FOUND_AUDIO_TRUCK = -100003
    }
}