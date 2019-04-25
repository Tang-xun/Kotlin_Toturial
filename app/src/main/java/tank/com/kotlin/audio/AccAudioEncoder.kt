package tank.com.kotlin.audio

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaExtractor
import android.media.MediaFormat
import android.util.Log
import com.google.gson.Gson
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 *  @author: vancetang
 *  @date:   2019/3/28 3:08 PM
 */
class AccAudioEncoder(rawAudioFile: String?) : IAudioEncoder(rawAudioFile) {

    private var mMediaExtractor: MediaExtractor? = null

    private var mMediaFormat: MediaFormat? = null
    private var mAudioIndex: Int = MediaConstant.Constant.MEDIA_INVALID_INDEX

    init {
        rawAudioFile?.let { path ->
            mMediaExtractor = MediaExtractor()
            mMediaExtractor?.apply {
                setDataSource(path)
            }?.let {
                // select
                for (index in 0..it.trackCount) {
                    val format: MediaFormat = it.getTrackFormat(index)
                    Log.i(TAG, "format -> ${Gson().toJson(format)}")
                    if (isAudioTruck(format.getString(MediaFormat.KEY_MIME) ?: "none")) {
                        mAudioIndex = index
                        mMediaFormat = format
                        break
                    }
                }
                return@let
            } ?: notifyStateChange(EncodeStateType.Error, MediaConstant.Errors.ERROR_INIT_MEDIA_EXTRACTOR, "init Media Extractor error")
        } ?: notifyStateChange(EncodeStateType.Error, MediaConstant.Errors.ERROR_INIT_MEDIA_PATH_INVALID, "media path is invalid")
    }

    fun extractAudio(destPath:String) {

        mMediaExtractor?.selectTrack(mAudioIndex)

        // loop to read data





    }

    override fun createAccAudioEncoder(): MediaCodec {

        val mediaCodec: MediaCodec = MediaCodec.createDecoderByType(AUDIO_MIME)
        mediaCodec.configure(
                MediaFormat().apply {
                    setString(MediaFormat.KEY_MIME, AUDIO_MIME)
                    setInteger(MediaFormat.KEY_BIT_RATE, AUDIO_BIT_RATE)
                    setInteger(MediaFormat.KEY_CHANNEL_COUNT, AUDIO_CHANNEL_COUNT)
                    setInteger(MediaFormat.KEY_SAMPLE_RATE, AUDIO_SAMPLE_RATE)
                    setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC)

                }, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)

        return mediaCodec
    }

    private var mRawInputBuffer = ByteArray(1024 * 4)


    override fun encodeToFile(outAudioFile: String) {

        var inputStream: FileInputStream? = null
        var outputStream: FileOutputStream? = null

        try {
            inputStream = FileInputStream(mRawAudioFile)
            outputStream = FileOutputStream(outAudioFile)

            val mediaCodec = createAccAudioEncoder()
            mediaCodec.start()

            val audioInputBuffer = mediaCodec.inputBuffers
            var audioOutputBuffer = mediaCodec.outputBuffers

            var sawInputEos = false
            var sawOutputEos = false

            // duration
            var audioTimeUs = 0L

            val bufferInfo: MediaCodec.BufferInfo = MediaCodec.BufferInfo()

            var readRawAudioEos = false

            var readRawCount = 0
            var rawAudioSize = 0

            var lastAudioPresentationTimeUS = 0L

            var inputBufIndex = 0
            var outputBufIndex = 0

            while (!sawOutputEos) {

                if (!sawInputEos) {

                    inputBufIndex = mediaCodec.dequeueInputBuffer(10000)

                    if (inputBufIndex >= 0) {

                        val inputByteBuffer = audioInputBuffer[inputBufIndex]

                        inputByteBuffer.clear()

                        val bufferSize = inputByteBuffer.remaining()

                        if (bufferSize != mRawInputBuffer.size) {
                            mRawInputBuffer = ByteArray(bufferSize)
                        }

                        if (!readRawAudioEos) {

                            readRawCount = inputStream.read(mRawInputBuffer)

                            if (readRawCount == -1) readRawAudioEos = true
                        }

                        // read eos
                        if (readRawAudioEos) {
                            mediaCodec.queueInputBuffer(inputBufIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                            sawInputEos = true
                        } else {
                            // reading
                            inputByteBuffer.put(mRawInputBuffer, 0, readRawCount)
                            rawAudioSize += readRawCount
                            mediaCodec.queueInputBuffer(inputBufIndex, 0, readRawCount, audioTimeUs, 0)
                            audioTimeUs = (rawAudioSize / 2 / mAudioBytesPerSample) * 1000000L
                        }
                    }
                }

                outputBufIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 10000)

                if (outputBufIndex > -1) {
                    if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                        mediaCodec.releaseOutputBuffer(outputBufIndex, false)
                        continue
                    }

                    if (bufferInfo.size != 0) {
                        val outBuffer = audioOutputBuffer[outputBufIndex]
                        outBuffer.position(bufferInfo.offset)
                        outBuffer.limit(bufferInfo.offset + bufferInfo.size)

                        if (lastAudioPresentationTimeUS < bufferInfo.presentationTimeUs) {
                            lastAudioPresentationTimeUS = bufferInfo.presentationTimeUs

                            val outBufferSize = bufferInfo.size
                            val outPacketSize = outBufferSize + 7

                            outBuffer.position(bufferInfo.offset)
                            outBuffer.limit(bufferInfo.offset + outPacketSize)

                            val outData = ByteArray(outPacketSize)

                            // add ADTS header
                            addADTStoPacket(outData, outPacketSize)

                            outBuffer.get(outData, 7, outBufferSize)

                            outputStream.write(outData, 0, outData.size)

                        } else {
                            Log.i(TAG, "error sample ! It's presentationTimeUs should not lower than before")
                        }

                    }

                    mediaCodec.releaseOutputBuffer(outputBufIndex, false)

                    if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        sawOutputEos = true
                    }

                } else if (outputBufIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    Log.i(TAG, "output buffers change --> ${mediaCodec.outputBuffers}")
                    audioOutputBuffer = arrayOf(mediaCodec.getOutputBuffer(outputBufIndex))
                } else if (outputBufIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    Log.i(TAG, "output format change --> ${mediaCodec.outputFormat}")
                }
            }
        } catch (ignore: Exception) {
            Log.e(TAG, "fun encodeToFile error ->", ignore)
        } finally {
            try {
                inputStream?.close()
            } catch (ignore: Exception) {
            }
            try {
                outputStream?.close()
            } catch (ignore: Exception) {
            }
        }
    }

    /**
     * Add ADTS header at the beginning of each and every AAC packet. This is
     * needed as MediaCodec encoder generates a packet of raw AAC data.
     *
     * Note the packetLen must count in the ADTS header itself.
     **/
    private fun addADTStoPacket(packet: ByteArray?, packetSize: Int = 0) {

        val profile = 2 // ACC LC
        val freqIdx = 4 // 44.1kHz
        val chanCfg = 2 // channel config

        packet?.apply {
            this[0] = 0xFF.toByte()
            this[1] = 0xF9.toByte()
            // (profile - 1 << 6) + freqIdx >> 2 + chanCfg >> 2
            this[2] = ((profile - 1).shl(6) + freqIdx.shr(2) + chanCfg.shr(2)).toByte()
            // chanCfg & 3 << 6 + packetSize >> 11
            this[3] = ((chanCfg and 3).shl(6) + packetSize.shr(11)).toByte()
            // packetSize & 0x7FF >> 3
            this[4] = (packetSize and 0x7FF).shr(3).toByte()
            // packetSize & 7 << 5 + 0x1F
            this[5] = ((packetSize and 7).shl(5) + 0x1F).toByte()

            this[6] = 0xFC.toByte()
        }
    }


}