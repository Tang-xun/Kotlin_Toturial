package tank.com.kotlin.utils

import okhttp3.OkHttpClient
import okhttp3.Request
import tank.com.kotlin.MainApplication
import tv.danmaku.ijk.media.player.misc.IMediaDataSource
import java.io.*

class CacheMediaDataSource(url: String) : IMediaDataSource {

    private var mUrl: String = url
    private var mMd5: String = url.md5()

    private var contentLength: Long? = null

    private var localVideoFile: File? = null

    private var isCacheVideo: Boolean? = null

    private var localStream: RandomAccessFile? = null
    private var networkStream: InputStream? = null

    private var mPosition: Long = 0

    /**
     *
     * @param position  视频读取进度
     * @param buffer    缓冲区
     * @param offset    数据偏移位置
     * @param size      读取数据大小
     *
     */
    override fun readAt(position: Long, buffer: ByteArray?, offset: Int, size: Int): Int {
        if (position >= this.contentLength!! || localStream == null) {
            return -1
        }

        val isWriteView = syncInputStream(position)
        var resize: Int = size

        if ((position + size) > contentLength!!) {
            resize = (contentLength!! - position).toInt()
        }

        val bytes: ByteArray
        if (this.isCacheVideo!!) {
            bytes = readByteBySize(localStream, resize)!!
        } else {
            bytes = readByteBySize(networkStream, resize)!!
        }
        // copy 到 播放器缓冲区中
        System.arraycopy(bytes, 0, buffer, offset, resize)

        if (!isCacheVideo!! && isWriteView) {
            localStream!!.write(bytes)
        }

        mPosition += resize

        return resize
    }

    private fun readByteBySize(networkStream: InputStream?, size: Int): ByteArray? {

        val out = ByteArrayOutputStream()

        var buf = ByteArray(size)

        var len: Int

        try {
            // TODO 这种写法未经测试，可能有问题
            while (networkStream!!.read(buf).also { len = it } != -1) {
                out.write(buf, 0, len)

                if (out.size() == size) {
                    return out.toByteArray()
                } else {
                    buf = ByteArray(size - out.size())
                }
            }
        } catch (e: IOException) {

        }

        return null
    }

    private fun readByteBySize(localStream: RandomAccessFile?, size: Int): ByteArray? {

        val out = ByteArrayOutputStream()
        var buf = ByteArray(size)
        var len: Int

        try {
            // TODO 这种写法未经测试，可能有问题
            while (localStream!!.read(buf).also { len = it } != -1) {
                out.write(buf, 0, len)

                if (out.size() == size) {
                    return out.toByteArray()
                } else {
                    buf = ByteArray(size - out.size())
                }
            }
        } catch (e: IOException) {

        }

        return null
    }

    @Throws(IOException::class)
    fun syncInputStream(position: Long): Boolean {
        var isWriteVideo = true

        if (mPosition != position) {
            if (this.isCacheVideo!!) {
                localStream!!.seek(position)
            } else {
                if (mPosition > position) {
                    localStream!!.close()
                    deleteFileByPosition(position)
                    localStream!!.seek(position)

                } else {
                    isWriteVideo = false
                }
                networkStream!!.close()
                networkStream = openHttpClient(position.toInt())
            }
            mPosition = position
        }

        return isWriteVideo
    }

    private fun openHttpClient(startIndex: Int): InputStream? {
        OkHttpClient().newCall(Request.Builder()
                .header("RANGE", "bytes=$startIndex-")
                .url(mUrl)
                .get()
                .build()).execute().body()?.let {
            contentLength = it.contentLength() + startIndex
            return it.byteStream()
        }
        return null
    }

    @Throws(Exception::class)
    private fun deleteFileByPosition(position: Long) {
        var index = position
        val inputStream = FileInputStream(localVideoFile)
        val tempFile: File = VideoLRUCacheUtil.createTempFile(MainApplication.mContext)
        val output = FileOutputStream(tempFile)

        val buf = ByteArray(MAX_BUFFER_SIZE)

        var len: Int
        while (inputStream.read(buf).also { len = it } != -1) {
            if (index <= len) {
                output.write(buf, 0, index.toInt())
                output.close()
                inputStream.close()
                localVideoFile?.delete()
                tempFile.renameTo(localVideoFile)
                localStream = RandomAccessFile(localVideoFile, "rw")
                return
            } else {
                // position 在方法内重新定义并赋值，不知道赋值和修改是否可以生效
                index.minus(len).let { index = it }
                output.write(buf, 0, len)
            }
        }

        tempFile.delete()
    }

    override fun getSize(): Long {
        if (networkStream == null) {
            initInputStream()
        }
        return this.contentLength!!
    }

    @Throws(IOException::class)
    fun initInputStream() {

        val file = checkCache(mMd5)

        if (file != null) {
            contentLength = file.length()
            VideoLRUCacheUtil.updateVideoCacheBean(mMd5, file.absolutePath, file.length())
            isCacheVideo = true
            localVideoFile = file
            contentLength = file.length()
        } else {
            isCacheVideo = false
            networkStream = openHttpClient(0)
            localVideoFile = VideoLRUCacheUtil.createCacheFile(MainApplication.mContext!!, mMd5, contentLength!!)
        }
        localStream = RandomAccessFile(localVideoFile, "rw")
    }

    private fun checkCache(mMd5: String): File? {
        VideoCacheDBUtil.query(mMd5)?.let {
            val file = File(it.videoPath)
            file.exists().let { return file }
        }
        return null
    }

    fun onError() {
        VideoCacheDBUtil.query(mMd5)?.let { VideoCacheDBUtil.delete(it) }
    }

    override fun close() {
        networkStream?.let {
            it.close()
            networkStream = null
        }

        localStream?.let {
            it.close()
            localStream = null
        }
        if (localVideoFile?.length() != contentLength) {
            localVideoFile?.delete()
        }
    }

    companion object {
        // 8k
        private var MAX_BUFFER_SIZE = 1024 * 8
    }
}