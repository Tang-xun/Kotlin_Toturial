package tank.com.kotlin.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 *  @author: vancetang
 *  @date:   2019/3/29 6:26 PM
 *
 *  note :
 *  1. prepare must first called
 *
 *  AudioFormat :
 *
 *  sample: 44100 khz
 *  channel: CHANNEL_IN_STEREO
 *  format: ENCODING_PCM_16BIT
 *
 */
class AudioRecordHelper private constructor(var outFilePath: String) {

    @Volatile
    private var isPause = false

    private var isInitEd = false

    private var mData: ByteArray? = null

    private var mOutPutFile: File? = null

    private var mByteBufferSize: Int = 0

    private var mAudioRecord: AudioRecord? = null

    init {
        prepare()
    }

    /**
     * prepare for audioRecord
     *
     *
     *
     * @param outFilePath output file path
     *
     */
    private fun prepare() {

        if (isInitEd) {
            throw IllegalStateException("prepare need call repetition")
        }

        // init
        mOutPutFile = File(outFilePath).apply {
            if (!exists()) createNewFile()
            if (!canRead()) this.setReadable(true)
            if (!canWrite()) this.setWritable(true)
        }

        if (mAudioRecord != null) {
            return
        }

        try {
            mAudioRecord = createAudioWithDefault()

            mByteBufferSize = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE, AUDIO_CHANNEL_CONFIG, AUDIO_FORMAT)

            mData = ByteArray(mByteBufferSize)
        } catch (e: IllegalArgumentException) {
        }

        isInitEd = true
    }


    @Synchronized
    fun startRecord() {

        if (isReading) {
            return
        }

        isReading = true
        var outputSystem: FileOutputStream? = null
        try {
            outputSystem = FileOutputStream(mOutPutFile)

            var readFlag: Int

            while (isReading) {

                readFlag = mAudioRecord?.read(mData, 0, mByteBufferSize) ?: AudioRecord.ERROR_INVALID_OPERATION

                if (readFlag == AudioRecord.ERROR_INVALID_OPERATION || readFlag == AudioRecord.ERROR_BAD_VALUE) {
                    // error
                }
                outputSystem.write(mData)
            }
        } catch (e: java.lang.Exception) {
            Log.i(TAG, "recording occur error ${e.message}")
        } finally {
            outputSystem?.close()
        }
    }

    fun pause() {
        isPause = true
    }

    /**
     * stop
     */
    fun stopRecord() {

        isReading = false

        if (mAudioRecord?.state == AudioRecord.STATE_UNINITIALIZED) {
            return
        }
        try {
            mAudioRecord?.apply {
                stop()
                release()
            }
        } catch (ignore: Exception) {
            Log.e(TAG, "called on error state " + ignore.message)
        }
    }

    private fun createAudioWithDefault(): AudioRecord {

        return AudioRecord(MediaRecorder.AudioSource.MIC, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL_CONFIG, AUDIO_FORMAT, mByteBufferSize)
    }

    private fun pcmToWav(outFilePath: String?) {
        if (outFilePath.isNullOrEmpty()) {
            return
        }

        var inputStream: FileInputStream? = null
        var outputStream: FileOutputStream? = null
        // file length
        var audioLength = 0L
        // audio duration
        var audioDuration = 0L

        val sampleRate = AUDIO_SAMPLE_RATE
        val channels = AUDIO_CHANNEL_CONFIG

        val bitRate = 16 * AUDIO_SAMPLE_RATE * channels / 8
        val data = ByteArray(mByteBufferSize)
        try {

            inputStream = FileInputStream(mOutPutFile)
            outputStream = FileOutputStream(outFilePath)
            audioLength = inputStream.channel.size()
            audioDuration = audioLength + 36

            // write header
            writeOutPutFileHeader(outputStream, audioLength, audioDuration, sampleRate, channels, bitRate)

            // write data
            while (inputStream.read(data) != -1) {
                outputStream.write(data)
            }
        } catch (e: Exception) {
        }

    }

    private fun writeOutPutFileHeader(outputStream: FileOutputStream?, length: Long, duration: Long, sampleRate: Int, channel: Int, byteRate: Int) {

        outputStream?.let {
            val headBytes = byteArrayOf(
                    'R'.toByte(),
                    'I'.toByte(),
                    'F'.toByte(),
                    'F'.toByte(),
                    ((length shr 0) and (0xFF)).toByte(),
                    ((length shr 8) and (0xFF)).toByte(),
                    ((length shr 16) and (0xFF)).toByte(),
                    ((length shr 24) and (0xFF)).toByte(),
                    'W'.toByte(),
                    'A'.toByte(),
                    'V'.toByte(),
                    'E'.toByte(),
                    'f'.toByte(),
                    'm'.toByte(),
                    't'.toByte(),
                    ' '.toByte(),
                    // 4 bytes : size of 'fmt' chunk
                    16.toByte(),
                    0.toByte(),
                    0.toByte(),
                    0.toByte(),
                    // format = 1
                    1.toByte(),
                    0.toByte(),
                    channel.toByte(),
                    0.toByte(),
                    ((sampleRate shr 0) and (0xFF)).toByte(),
                    ((sampleRate shr 8) and (0xFF)).toByte(),
                    ((sampleRate shr 16) and (0xFF)).toByte(),
                    ((sampleRate shr 24) and (0xFF)).toByte(),
                    ((byteRate shr 0) and (0xFF)).toByte(),
                    ((byteRate shr 8) and (0xFF)).toByte(),
                    ((byteRate shr 16) and (0xFF)).toByte(),
                    ((byteRate shr 24) and (0xFF)).toByte(),
                    // block align
                    (2 * 16 / 8).toByte(),
                    0.toByte(),
                    16.toByte(),
                    0.toByte(),
                    // data
                    'd'.toByte(),
                    'a'.toByte(),
                    't'.toByte(),
                    'a'.toByte(),
                    // duration
                    ((duration shr 0) and (0xFF)).toByte(),
                    ((duration shr 8) and (0xFF)).toByte(),
                    ((duration shr 16) and (0xFF)).toByte(),
                    ((duration shr 24) and (0xFF)).toByte()
            )
            outputStream.write(headBytes)
        }
    }

    @Volatile
    var isReading = false

    interface AudioRecordListener {
        fun onStart()
        fun onError()
        fun onStop()
    }

    companion object {
        const val TAG = "AudioRecordHelper"

        const val AUDIO_SAMPLE_RATE = 44100

        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT

        const val AUDIO_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO

        private var instance: AudioRecordHelper? = null

        fun getInstance(outPutFile: String) = {
            instance ?: synchronized(this) {
                instance ?: AudioRecordHelper(outPutFile).also { instance = it }
            }
        }
    }


}