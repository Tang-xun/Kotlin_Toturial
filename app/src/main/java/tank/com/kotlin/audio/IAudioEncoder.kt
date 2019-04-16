package tank.com.kotlin.audio

import android.media.MediaCodec
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList

/**
 *  @author: vancetang
 *  @date:   2019/3/28 2:49 PM
 */
abstract class IAudioEncoder(rawAudioFile: String?) {

    companion object {
        const val TAG = "IAudioEncoder"

        // mime
        const val AUDIO_MIME = "audio/mp4a-latm"
        // channel count
        const val AUDIO_CHANNEL_COUNT = 2
        // bit rate
        const val AUDIO_BIT_RATE = 128000
        // sample rate
        const val AUDIO_SAMPLE_RATE = 44100
        //
        const val mAudioBytesPerSample = 44100 * 16 / 8
    }

    protected var mEncoderListeners: List<WeakReference<EncodeListener>>? = CopyOnWriteArrayList<WeakReference<EncodeListener>>()

    protected var mRawAudioFile: String? = null

    init {
        mRawAudioFile = rawAudioFile
    }

    abstract fun createAccAudioEncoder(): MediaCodec

    abstract fun encodeToFile(outAudioFile: String)

    protected fun notifyStateChange(type: EncodeStateType, code: Int, msg: String) {
        when (type) {
            EncodeStateType.Process -> mEncoderListeners?.forEach {
                it.get()?.onEncoderProcess(code, msg)
            }

            EncodeStateType.Complete -> mEncoderListeners?.forEach {
                it.get()?.onEncoderComplete(code, msg)
            }

            EncodeStateType.Error -> mEncoderListeners?.forEach {
                it.get()?.onEncoderError(code, msg)
            }
        }
    }

    enum class EncodeStateType(v: Int) {
        Process(1), Complete(2), Error(2)
    }

    interface EncodeListener {

        fun onEncoderError(code: Int, msg: String)

        fun onEncoderComplete(code: Int, msg: String)

        fun onEncoderProcess(process: Int, msg: String)
    }
}