package tank.com.kotlin.media

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.text.TextUtils
import android.util.Log
import android.view.Surface

/**
 *  @author: vancetang
 *  @date:   2019/4/18 4:38 PM
 */
abstract class AMediaDecode(var type: DecodeType, var videoPath: String, var outputPath: String, var surface: Surface) : Thread() {


    private var mProcessListeners: ArrayList<MediaProcessListener>? = null

    private var mOutputType = if (TextUtils.isEmpty(outputPath)) OUTPUT_TYPE_SURFACE else OUTPUT_TYPE_SURFACE

    protected var mMediaFormat: MediaFormat? = null

    protected var mMediaCodec: MediaCodec? = null

    protected var mExtractor: MediaExtractor? = null


    init {
        Log.i(TAG, "will decode ::: ")
    }

    /**
     *
     */
    fun addProcessListener(listener: MediaProcessListener?) {
        if (mProcessListeners == null) {
            mProcessListeners = ArrayList()
        }
        if (listener == null) {
            Log.w(TAG, "removeProcessListener fail listener is null")
            return
        }
        if (mProcessListeners?.contains(listener) == true) {
            Log.w(TAG, "removeProcessListener fail listener has contains")
            return
        }
        mProcessListeners?.add(listener)
    }

    /**
     *
     */
    fun removeProcessListener(listener: MediaProcessListener?) {
        if (mProcessListeners == null) {
            Log.w(TAG, "removeProcessListener mProcessListeners is null")
            return
        }
        if (listener == null) {
            Log.w(TAG, "removeProcessListener fail listener is null")
            return
        }
        if (mProcessListeners?.contains(listener) == true) mProcessListeners?.remove(listener)
    }

    override fun run() {
        super.run()
        prepare()
    }

    private fun prepare() {
        mExtractor = MediaExtractor()
        if (mExtractor == null) {
            mProcessListeners?.forEach {
            }
        }

        mExtractor?.setDataSource(videoPath)
        mMediaCodec = MediaCodec.createDecoderByType("audio/mp4a-latm")
    }

    abstract fun decodeMedia()

    companion object {
        private const val TAG = "AMediaDecode"

        // 解码数据写入[文件]
        private const val OUTPUT_TYPE_FILE = 10001

        // 解码数据输出到[Surface]
        private const val OUTPUT_TYPE_SURFACE = 10002
    }

    enum class DecodeType {
        Video, Audio
    }

}