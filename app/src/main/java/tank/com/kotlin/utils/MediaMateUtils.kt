package tank.com.kotlin.utils

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log

/**
 *  @author:    tank
 *  @date:      2019/1/31 10:36 AM
 */
class MediaMateUtils {

    companion object {
        val TAG = MediaMateUtils::class.java.simpleName

        /**
         * @param context
         * @param frameCount 需要的缩略图数量
         * @param path 视频路径、播放资源Uri
         */
        fun fetchVideoFrame(context: Context, frameCount: Int, path: String?): MutableList<Bitmap>? {

            var duration: Long

            val isInvalidPath: Boolean = path?.isEmpty() ?: true

            if (isInvalidPath) return null

            val isHttpUri: Boolean = path?.startsWith("http") ?: false

            val frameList: MutableList<Bitmap> = mutableListOf()
            val retriever = MediaMetadataRetriever()

            retriever.apply {
                if (isHttpUri) setDataSource(context, Uri.parse(path)) else setDataSource(path)
                duration = extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0
            }

            if (duration == 0L) {
                Log.i(TAG, "video duration is null , check pls")
            }

            val frameStep: Long = duration / frameCount

            retriever.let {
                for (i: Long in 0 until duration step frameStep) {
                    frameList.add(it.getFrameAtTime(i * 1000L, MediaMetadataRetriever.OPTION_CLOSEST_SYNC))
                }
                it.release()
            }

            return frameList
        }

        fun fetchFirstFrame(context: Context, path: String?, width: Int, height: Int): Bitmap? {

            val isHttpUri: Boolean = path?.startsWith("http") ?: false

            val isInvalidPath: Boolean = path?.isEmpty() ?: true

            if (isInvalidPath) return null

            val retriever = MediaMetadataRetriever()

            var firstFrame: Bitmap

            retriever.apply {
                if (isHttpUri) setDataSource(context.applicationContext, Uri.parse(path)) else setDataSource(path)
            }.let {
                firstFrame = it.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                it.release()
            }

            return firstFrame
        }

    }


}