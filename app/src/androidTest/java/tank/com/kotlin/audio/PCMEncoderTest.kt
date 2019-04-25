package tank.com.kotlin.audio

import android.util.Log
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * @author: vancetang
 * @date: 2019/4/20 5:19 PM
 */
class PCMEncoderTest {

    @Before
    fun setUp() {
        Log.i(TAG, "setUp ::: ")
    }

    @After
    fun tearDown() {
        Log.i(TAG, "tearDown ::: ")
    }

    @Test
    fun encode() {
        Log.i(TAG, "encode ::: ")
        val fileLength = File(SOURCE_PATH).length()

        val pcmEncoder = PCMEncoder(AUDIO_BIT_RATE, AUDIO_SAMPLE_RATE, AUDIO_CHANNELS)

        val inputStream = FileInputStream(SOURCE_PATH)

        val outputStream = FileOutputStream(DEST_PATH)

        val startTime = System.currentTimeMillis()

        pcmEncoder.encode(inputStream, outputStream)

        val triple = statists(startTime, fileLength)

        val cost = triple.first
        var speed = triple.second
        var unitStr = triple.third

        speed = Math.round((speed) * 100 / 100).toFloat()

        Log.i(TAG, "encode time  fileLength:$fileLength cost:$cost  speed:$speed/$unitStr")

    }

    private fun statists(startTime: Long, fileLength: Long): Triple<Long, Float, String> {
        val cost = System.currentTimeMillis() - startTime

        var speed = fileLength.toFloat() / cost.toFloat()
        var unitStr = "kb/s"

        if (speed > 1024) {
            speed /= 1024f
            unitStr = "Mb/s"
        }
        return Triple(cost, speed, unitStr)
    }

    companion object {

        private const val TAG = "PCMEncoderTest"

        private const val SOURCE_PATH = "mnt/sdcard/pcm_mixed_4970102756823000038.tmp"

        private const val DEST_PATH = "mnt/sdcard/pcm_mixed_4970102756823000038.mp4"

        // 码率
        private const val AUDIO_BIT_RATE = 96000
        // 声道数
        private const val AUDIO_CHANNELS = 2
        // 采样率
        private const val AUDIO_SAMPLE_RATE = 44100
    }
}

