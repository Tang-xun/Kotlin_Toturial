package tank.com.kotlin.audio

import android.annotation.SuppressLint
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.util.Log

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer

/**
 * PCMEncoder
 *
 * Encode PCM stream into MP4 file stream.
 *
 * Creates encoder with given params for output file
 * @param bitrate Bitrate of the output file, higher bitrate brings better voice, but
 * lager file. eg. 64k is enough for speech, its about 28MB/hour after encode.
 * @param sampleRate sampling rate of pcm.
 * @param channelCount channel count of pcm.
 */
class PCMEncoder(private val bitrate: Int, private val sampleRate: Int, private val channelCount: Int) {
    private var freqIdx = -1

    private var mediaFormat: MediaFormat? = null
    private var mediaCodec: MediaCodec? = null
    private var codecInputBuffers: Array<ByteBuffer>? = null
    private var codecOutputBuffers: Array<ByteBuffer>? = null
    private var bufferInfo: MediaCodec.BufferInfo? = null
    private var totalBytesRead: Int = 0
    private var presentationTimeUs: Long = 0
    private var tempBuffer: ByteArray? = null

    init {
        for (i in SUPPORTED_SAMPLE_RATES.indices) {
            if (sampleRate == SUPPORTED_SAMPLE_RATES[i]) {
                freqIdx = i
                break
            }
        }
        if (freqIdx == -1) {
            throw IllegalArgumentException("Not support sample rate $sampleRate")
        }
    }

    /**
     * Encodes input stream
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun encode(inputStream: InputStream, outputStream: OutputStream) {
        prepare()

        var hasMoreData = true
        while (hasMoreData) {
            hasMoreData = readInputs(inputStream)
            writeOutputs(outputStream)
        }

        inputStream.close()
        outputStream.close()

        stop()
    }

    private fun prepare() {
        try {
            Log.i(TAG, "Preparing PCMEncoder")

            mediaFormat = MediaFormat.createAudioFormat(COMPRESSED_AUDIO_FILE_MIME_TYPE, sampleRate, channelCount)
            mediaFormat!!.setInteger(MediaFormat.KEY_AAC_PROFILE, COMPRESSED_AUDIO_PROFILE)
            mediaFormat!!.setInteger(MediaFormat.KEY_BIT_RATE, bitrate)

            mediaCodec = MediaCodec.createEncoderByType(COMPRESSED_AUDIO_FILE_MIME_TYPE)
            mediaCodec!!.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            mediaCodec!!.start()

            codecInputBuffers = mediaCodec!!.inputBuffers
            codecOutputBuffers = mediaCodec!!.outputBuffers

            bufferInfo = MediaCodec.BufferInfo()

            totalBytesRead = 0
            presentationTimeUs = 0
            tempBuffer = ByteArray(2 * sampleRate)
        } catch (e: IOException) {
            Log.e(TAG, "Exception while initializing PCMEncoder", e)
        }

    }

    private fun stop() {
        Log.i(TAG, "Stopping PCMEncoder")
        mediaCodec!!.stop()
        mediaCodec!!.release()
    }

    @Throws(IOException::class)
    private fun readInputs(inputStream: InputStream): Boolean {
        var hasMoreData = true
        var inputBufferIndex = 0
        var currentBatchRead = 0
        while (inputBufferIndex != -1 && hasMoreData && currentBatchRead <= 50 * sampleRate) {
            inputBufferIndex = mediaCodec!!.dequeueInputBuffer(CODEC_TIMEOUT.toLong())

            if (inputBufferIndex >= 0) {
                val buffer = codecInputBuffers!![inputBufferIndex]
                buffer.clear()

                val bytesRead = inputStream.read(tempBuffer!!, 0, buffer.limit())
                if (bytesRead == -1) {
                    mediaCodec!!.queueInputBuffer(inputBufferIndex, 0, 0, presentationTimeUs, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                    hasMoreData = false
                } else {
                    totalBytesRead += bytesRead
                    currentBatchRead += bytesRead
                    buffer.put(tempBuffer, 0, bytesRead)
                    mediaCodec!!.queueInputBuffer(inputBufferIndex, 0, bytesRead, presentationTimeUs, 0)
                    presentationTimeUs = 1000000L * (totalBytesRead / 2) / sampleRate
                }
            }
        }
        return hasMoreData
    }

    @SuppressLint("WrongConstant")
    @Throws(IOException::class)
    private fun writeOutputs(outputStream: OutputStream) {
        var outputBufferIndex = 0
        while (outputBufferIndex != MediaCodec.INFO_TRY_AGAIN_LATER) {
            outputBufferIndex = mediaCodec!!.dequeueOutputBuffer(bufferInfo!!, CODEC_TIMEOUT.toLong())
            if (outputBufferIndex >= 0) {
                if (bufferInfo!!.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0 && bufferInfo!!.size != 0) {
                    mediaCodec!!.releaseOutputBuffer(outputBufferIndex, false)
                } else {
                    // Write ADTS header and AAC data to frame.
                    val outPacketSize = bufferInfo!!.size + 7    // 7 is ADTS size
                    val data = ByteArray(outPacketSize)  //space for ADTS header included
                    addADTStoPacket(data, outPacketSize)

                    val encodedData = codecOutputBuffers!![outputBufferIndex]
                    encodedData.position(bufferInfo!!.offset)
                    encodedData.limit(bufferInfo!!.offset + bufferInfo!!.size)
                    encodedData.get(data, 7, bufferInfo!!.size)
                    encodedData.clear()
                    outputStream.write(data, 0, outPacketSize)  //open FileOutputStream beforehand

                    mediaCodec!!.releaseOutputBuffer(outputBufferIndex, false)
                }
            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                codecOutputBuffers = mediaCodec!!.outputBuffers
            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                mediaFormat = mediaCodec!!.outputFormat
            }
        }
    }

    /**
     * Add ADTS header at the beginning of each and every AAC packet.
     * This is needed as MediaCodec encoder generates a packet of raw
     * AAC data.
     *
     * Note the packetLen must count in the ADTS header itself.
     */
    private fun addADTStoPacket(packet: ByteArray, packetLen: Int) {
        val profile = COMPRESSED_AUDIO_PROFILE
        val freqIdx = this.freqIdx
        val chanCfg = channelCount

        // fill in ADTS data
        packet[0] = 0xFF.toByte()
        packet[1] = 0xF9.toByte()
        packet[2] = ((profile - 1 shl 6) + (freqIdx shl 2) + (chanCfg shr 2)).toByte()
        packet[3] = ((chanCfg and 3 shl 6) + (packetLen shr 11)).toByte()
        packet[4] = (packetLen and 0x7FF shr 3).toByte()
        packet[5] = ((packetLen and 7 shl 5) + 0x1F).toByte()
        packet[6] = 0xFC.toByte()
    }

    companion object {
        private const val TAG = "PCMEncoder"

        private val COMPRESSED_AUDIO_FILE_MIME_TYPE = "audio/mp4a-latm"
        private val COMPRESSED_AUDIO_PROFILE = MediaCodecInfo.CodecProfileLevel.AACObjectLC

        private val CODEC_TIMEOUT = 10000

        private val SUPPORTED_SAMPLE_RATES = intArrayOf(96000, // 0: 96000 Hz
                88200, // 1: 88200 Hz
                64000, // 2: 64000 Hz
                48000, // 3: 48000 Hz
                44100, // 4: 44100 Hz
                32000, // 5: 32000 Hz
                24000, // 6: 24000 Hz
                22050, // 7: 22050 Hz
                16000, // 8: 16000 Hz
                12000, // 9: 12000 Hz
                11025, // 10: 11025 Hz
                8000, // 11: 8000 Hz
                7350)// 12: 7350 Hz
        // 13: Reserved
        // 14: Reserved
        // 15: frequency is written explictly
    }
}