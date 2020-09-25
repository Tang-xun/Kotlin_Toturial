package tank.com.kotlin

import android.content.Context
import android.media.*
import android.media.MediaCodecList.ALL_CODECS
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import android.util.Log
import com.google.gson.Gson
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.util.*
import android.media.MediaFormat




/**
 *  @author: vancetang
 *  @date:   2019/4/16 9:11 PM
 */
@RunWith(AndroidJUnit4::class)
class MediaExtractorTest {

    private var mContext: Context? = null
    private val kNumInputBytes = 256 * 1024
    private val kTimeoutUs: Long = 10000

    @Before
    fun setUp() {
        mContext = InstrumentationRegistry.getTargetContext()
    }



    @Test
    fun TestMediaExtractor() {

        val mmr = MediaMetadataRetriever()

        val extractor = MediaExtractor()

        val file = File(TEST_MP4_PATH)

        Log.i(TAG, "file =>  ${file.exists()} ${file.length()} ${file.canRead()} ${file.canWrite()} ${file.canonicalFile}")

        val inputStream = FileInputStream(TEST_MP4_PATH)

        Log.i(TAG, "$inputStream ${inputStream.fd} ${inputStream.channel}")

        mmr.setDataSource(inputStream.fd)

        for (i in 0 until 25) {
            Log.i(TAG, "$i ${mmr.extractMetadata(i)}")
        }

        for (index in 0..extractor.trackCount) {
            val format: MediaFormat = extractor.getTrackFormat(index)
            Log.i(TAG, "$index => ${Gson().toJson(format)}")
        }
    }

    @Test
    fun testAMRNBEncoders() {
        val formats = LinkedList<MediaFormat>()
        val kBitRates = intArrayOf(4750, 5150, 5900, 6700, 7400, 7950, 10200, 12200)
        for (j in kBitRates.indices) {
            val format = MediaFormat()
            format.setString(MediaFormat.KEY_MIME, "audio/3gpp")
            format.setInteger(MediaFormat.KEY_SAMPLE_RATE, 8000)
            format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1)
            format.setInteger(MediaFormat.KEY_BIT_RATE, kBitRates[j])
            formats.push(format)
        }
        testEncoderWithFormats("audio/3gpp", formats)
    }

    @Test
    fun testAMRWBEncoders() {
        val formats = LinkedList<MediaFormat>()
        val kBitRates = intArrayOf(6600, 8850, 12650, 14250, 15850, 18250, 19850, 23050, 23850)
        for (j in kBitRates.indices) {
            val format = MediaFormat()
            format.setString(MediaFormat.KEY_MIME, "audio/amr-wb")
            format.setInteger(MediaFormat.KEY_SAMPLE_RATE, 16000)
            format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1)
            format.setInteger(MediaFormat.KEY_BIT_RATE, kBitRates[j])
            formats.push(format)
        }
        testEncoderWithFormats("audio/amr-wb", formats)
    }

    @Test
    fun testAACEncoders() {
        val formats = LinkedList<MediaFormat>()
        val kAACProfiles = intArrayOf(2 /* OMX_AUDIO_AACObjectLC */, 5 /* OMX_AUDIO_AACObjectHE */, 39 /* OMX_AUDIO_AACObjectELD */)
        val kSampleRates = intArrayOf(8000, 11025, 22050, 44100, 48000)
        val kBitRates = intArrayOf(64000, 128000)
        for (k in kAACProfiles.indices) {
            for (i in kSampleRates.indices) {
                if (kAACProfiles[k] == 5 && kSampleRates[i] < 22050) {
                    // Is this right? HE does not support sample rates < 22050Hz?
                    continue
                }
                for (j in kBitRates.indices) {
                    for (ch in 1..2) {
                        val format = MediaFormat()
                        format.setString(MediaFormat.KEY_MIME, "audio/mp4a-latm")
                        format.setInteger(
                                MediaFormat.KEY_AAC_PROFILE, kAACProfiles[k])
                        format.setInteger(
                                MediaFormat.KEY_SAMPLE_RATE, kSampleRates[i])
                        format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, ch)
                        format.setInteger(MediaFormat.KEY_BIT_RATE, kBitRates[j])
                        formats.push(format)
                    }
                }
            }
        }
        testEncoderWithFormats("audio/mp4a-latm", formats)
    }

    fun testEncoderWithFormats(mime: String, formats: List<MediaFormat>) {
        val componentNames = getEncoderNamesForType(mime)

        for (componentName in componentNames) {
            Log.d(TAG, "testing component '$componentName'")
            for (format in formats) {
                Log.d(TAG, "testing format '$format'")
//                Log.i(TAG, "format equal -> ${mime == format.getString(MediaFormat.KEY_MIME)}")
//                testEncoder(componentName, format)
            }
        }
    }

    @Test
    fun testEncoderName() {
        MediaCodecList(ALL_CODECS).codecInfos.forEach {
            Log.i(TAG, "${it.name}\t${it.isEncoder}")
        }
    }

    private fun getEncoderNamesForType(mime: String): List<String> {
        val names = LinkedList<String>()
        val n = MediaCodecList.getCodecCount()
        for (i in 0 until n) {
            val info = MediaCodecList.getCodecInfoAt(i)
            if (!info.isEncoder) {
                continue
            }
            if (!info.name.startsWith("OMX.")) {
                // Unfortunately for legacy reasons, "AACEncoder", a
                // non OMX component had to be in this list for the video
                // editor code to work... but it cannot actually be instantiated
                // using MediaCodec.
                Log.d(TAG, """skipping '${info.name}'.""")
                continue
            }
            Log.i(TAG, "find match ${info.name}")
            val supportedTypes = info.supportedTypes
            for (j in supportedTypes.indices) {
                if (supportedTypes[j].equals(mime, ignoreCase = true)) {
                    names.push(info.name)
                    break
                }
            }
        }
        return names
    }


    private fun queueInputBuffer(codec: MediaCodec, inputBuffers: Array<ByteBuffer>, index: Int): Int {
        val buffer = inputBuffers[index].apply {
            clear()
        }
        val size = buffer.limit()
        val zeroes = ByteArray(size)
        buffer.put(zeroes)
        codec.queueInputBuffer(index, 0 /* offset */, size, 0 /* timeUs */, 0)
        return size
    }

    private fun dequeueOutputBuffer(codec: MediaCodec, outputBuffers: Array<ByteBuffer>, index: Int, info: MediaCodec.BufferInfo) {

        codec.releaseOutputBuffer(index, false /* render */)

    }

    private fun testEncoder(componentName: String, format: MediaFormat) {
        var codec = MediaCodec.createByCodecName(componentName)
        try {
            codec.configure(
                    format,
                    null /* surface */,
                    null /* crypto */,
                    MediaCodec.CONFIGURE_FLAG_ENCODE)
        } catch (e: IllegalStateException) {
            Log.e(TAG, "codec '$componentName' failed configuration.")
            assertTrue("codec '$componentName' failed configuration.", false)
        }
        codec.start()
        val codecInputBuffers = codec.inputBuffers
        var codecOutputBuffers = codec.outputBuffers
        var numBytesSubmitted = 0
        var doneSubmittingInput = false
        var numBytesDequeued = 0
        while (true) {
            var index: Int
            if (!doneSubmittingInput) {
                index = codec.dequeueInputBuffer(kTimeoutUs /* timeoutUs */)
                if (index != MediaCodec.INFO_TRY_AGAIN_LATER) {
                    if (numBytesSubmitted >= kNumInputBytes) {
                        codec.queueInputBuffer(
                                index,
                                0 /* offset */,
                                0 /* size */,
                                0 /* timeUs */,
                                MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                        Log.d(TAG, "queued input EOS.")
                        doneSubmittingInput = true
                    } else {
                        val size = queueInputBuffer(codec, codecInputBuffers, index)
                        numBytesSubmitted += size
                        Log.d(TAG, "queued $size bytes of input data.")
                    }
                }
            }
            val info = MediaCodec.BufferInfo()

            index = codec.dequeueOutputBuffer(info, kTimeoutUs /* timeoutUs */)

            if (index == MediaCodec.INFO_TRY_AGAIN_LATER) {

            } else if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
            } else if (index == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                codecOutputBuffers = codec.outputBuffers
            } else {
                dequeueOutputBuffer(codec, codecOutputBuffers, index, info)
                numBytesDequeued += info.size
                if ((info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    Log.d(TAG, "dequeued output EOS.")
                    break
                }

                Log.d(TAG, "dequeued " + info.size + " bytes of output data.")
            }
        }
        Log.d(TAG, "queued a total of " + numBytesSubmitted + "bytes, "
                + "dequeued " + numBytesDequeued + " bytes.")
        val sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
        val channelCount = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
        val inBitrate = sampleRate * channelCount * 16  // bit/sec
        val outBitrate = format.getInteger(MediaFormat.KEY_BIT_RATE)
        val desiredRatio = outBitrate * 1.0F / inBitrate
        val actualRatio = numBytesDequeued * 1.0F / numBytesSubmitted
        if (actualRatio < 0.9F * desiredRatio || actualRatio > 1.1F * desiredRatio) {
            Log.w(TAG, "desiredRatio = " + desiredRatio
                    + ", actualRatio = " + actualRatio)
        }
        codec.release()
        codec = null
    }

    companion object {

        private const val TAG = "MediaExtractorTest"

        private const val TEST_MP4_PATH = "/mnt/sdcard/Download/mediatest.mp4"
    }
}