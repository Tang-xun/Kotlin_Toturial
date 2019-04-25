package tank.com.kotlin.media

/**
 *  @author: vancetang
 *  @date:   2019/4/18 4:55 PM
 */
interface MediaProcessListener {

    fun onProcess(type: Int, now: Long, total: Long)

    fun onError(type: AMediaDecode.DecodeType, code: Int, msg: Int)

    fun onComplete(type: Int)

}