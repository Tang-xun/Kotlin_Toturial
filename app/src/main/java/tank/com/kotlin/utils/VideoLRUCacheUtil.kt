package tank.com.kotlin.utils

import android.content.Context
import android.os.Environment
import tank.com.kotlin.model.VideoCacheBean
import java.io.File
import java.io.IOException

class VideoLRUCacheUtil {

    companion object {
        // max size is 500MB
        private val MAX_CACHE_SIZE: Long = 1024 * 1024 * 500
        // max time is a week
        private val MAX_CACHE_TIME: Long = 1000 * 60 * 60 * 24 * 7

        @Throws(IOException::class)
        fun createTempFile(context: Context?): File {
            val tempFile: File = File(context!!.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), System.currentTimeMillis().toString())

            if (!tempFile.exists()) {
                tempFile.createNewFile()
            }
            return tempFile
        }

        fun createCacheFile(context: Context, md5: String, fileSize: Long): File {
            val fileDir: File = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val cacheFile: File = File(fileDir, md5)
            if (!cacheFile.exists()) {
                cacheFile.createNewFile()
            }
            updateVideoCacheBean(md5, cacheFile.absolutePath, fileSize)
            return cacheFile
        }

        fun updateVideoCacheBean(key: String, videoPath: String, fileSize: Long) {
            var videoCacheBean: VideoCacheBean? = VideoCacheDBUtil.query(key)

            if (videoCacheBean == null) {
                videoCacheBean = VideoCacheBean()
                videoCacheBean.key = key
                videoCacheBean.videoPath = videoPath
                videoCacheBean.fileSize = fileSize
            }

            videoCacheBean.playCount = videoCacheBean.playCount?.plus(1)
            videoCacheBean.playTime = System.currentTimeMillis()

            VideoCacheDBUtil.save(videoCacheBean)
        }

        /**
         * check caches and clean some cache
         */
        fun checkCacheSize(context: Context?) {
            val videoCaches: ArrayList<VideoCacheBean>? = VideoCacheDBUtil.query()

            if (videoCaches == null) {
                // nothing need check
                return
            }

            var currentSize = 0L
            val currentTime = System.currentTimeMillis()

            videoCaches.forEach { videoBean ->
                // delete invalid cache
                if (videoBean.fileSize == 0L) {
                    val file = File(videoBean.videoPath)

                    if (!file.exists() || file.length() != videoBean.fileSize) {
                        VideoCacheDBUtil.delete(videoBean)
                        return@forEach
                    }
                }

                // delete expired cache
                if ((currentTime - videoBean.playTime!!) > MAX_CACHE_TIME) {
                    VideoCacheDBUtil.delete(videoBean)
                    return@forEach
                }

                // delete much overflow cache
                if ((currentSize + videoBean.fileSize!!) > MAX_CACHE_SIZE) {
                    VideoCacheDBUtil.delete(videoBean)
                } else {
                    currentSize = currentSize.plus(videoBean.fileSize!!)
                }
            }

            removeUnSafeCache(context!!.getFileStreamPath(Environment.DIRECTORY_DOWNLOADS))
        }

        fun removeUnSafeCache(file: File) {
            if (!file.exists()) {
                return
            }
            if (file.isDirectory) {
                file.listFiles().forEach { f ->
                    this.removeUnSafeCache(f)
                }
            } else {
                val key = file.name
                val videoCacheBean = VideoCacheDBUtil.query(key)
                if (videoCacheBean == null || !videoCacheBean.videoPath.equals(file.absolutePath)) {
                    file.delete()
                }
            }
        }

    }

}