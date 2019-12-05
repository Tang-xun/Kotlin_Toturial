package tank.com.kotlin.utils

import android.graphics.SurfaceTexture
import android.os.Binder
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.Surface
import tank.com.kotlin.view.PlayTextureView
import tv.danmaku.ijk.media.player.AndroidMediaPlayer
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.lang.ref.SoftReference
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

class MediaPlayerTool private constructor() : IMediaPlayer.OnCompletionListener, IMediaPlayer.OnErrorListener,
        IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnInfoListener, IMediaPlayer.OnPreparedListener {

    private var mMediaPlayer: IMediaPlayer? = null

    var mVideoListener: VideoListener? = null
        set(value)  {field = value}

    private var mSurfaceTexture: SurfaceTexture? = null
    // 记录上次播放器的hashcode
    private var mPlayHashCode = AtomicInteger(0)

    // 旋转角度
    var mRotation: Long = 0L
        get() = field
    // 视屏时长
    var mDuration: Long = 0L
        get() = field
    // 视频音量
    var mVolume: Float? = 0f
        get() = field
        set(value) {
            field = value
            field?.let { mMediaPlayer?.setVolume(it, it) }
        }

    // ijk lib is load success
    private var loadIjkSuccess = false

    private var mPlayTextViewSoftRef: SoftReference<PlayTextureView>? = null

    private var mCacheMediaDataSource: CacheMediaDataSource? = null

    init {
        try {
            IjkMediaPlayer.loadLibrariesOnce(null)
            IjkMediaPlayer.native_profileBegin("libijkplayer.so")
            loadIjkSuccess = true
        } catch (e: UnsatisfiedLinkError) {
            e.printStackTrace()
            loadIjkSuccess = false
        }
    }

    fun initMediaPlayer() {
        if (loadIjkSuccess) {
            val ijkMediaPlayer: IjkMediaPlayer = IjkMediaPlayer()
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1)
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1)
            mMediaPlayer = ijkMediaPlayer
        } else {
            mMediaPlayer = AndroidMediaPlayer()
        }

        try {
            mMediaPlayer?.setOnInfoListener(this)
            mMediaPlayer?.setOnErrorListener(this)
            mMediaPlayer?.setOnCompletionListener(this)
            mMediaPlayer?.setOnBufferingUpdateListener(this)
            mMediaPlayer?.setOnPreparedListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCompletion(p0: IMediaPlayer?) {
        mVideoListener?.onComplete()
    }

    fun prepare() {
        try {
            mMediaPlayer?.prepareAsync()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun start() = mMediaPlayer?.start()

    fun pause() = mMediaPlayer?.pause()

    fun stop() = mMediaPlayer?.stop()

    fun seekTo(msec: Long) = mMediaPlayer?.seekTo(msec)

    fun onDestroy() {
        reset()
        IjkMediaPlayer.native_profileEnd()
    }

    fun isPlaying() = mMediaPlayer?.isPlaying ?: false

    fun isLooping() = mMediaPlayer?.isLooping ?: false

    fun getVideoWidth(): Int = mMediaPlayer?.videoWidth ?: 0

    fun getCurrentPosition(): Long? = mMediaPlayer?.currentPosition ?: 0


    fun setLooping(isLooping: Boolean) {
        this.mMediaPlayer?.isLooping = isLooping
    }

    fun setPlayTextureView(playTextureView: PlayTextureView) {
        mPlayTextViewSoftRef?.clear()
        mPlayTextViewSoftRef = SoftReference(playTextureView)
    }

    fun setSurfaceTexture(sft: SurfaceTexture?) = sft?.let {
        mMediaPlayer?.setSurface(Surface(it))
        mSurfaceTexture = it
    }

    fun getAvailableSurfaceTexture(): SurfaceTexture? {
        cleanTextureViewParent()
        return mSurfaceTexture
    }

    fun setDataSource(url: String) {
        try {
            CacheMediaDataSource(url).let {
                mCacheMediaDataSource = it
                mMediaPlayer!!.dataSource = url
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun cleanTextureViewParent() {
        mPlayTextViewSoftRef?.get()?.resetTextureView()
    }


    fun reset() {

        if (handler.hasMessages(PLAY_PROGRESS)) {
            handler.removeMessages(PLAY_PROGRESS)
        }

        mPlayTextViewSoftRef?.get()?.resetTextureView()
        mPlayTextViewSoftRef?.let {
            it.clear()
            mPlayTextViewSoftRef = null
        }

        mSurfaceTexture?.let {
            it.release()
            mSurfaceTexture = null
        }

        mVideoListener?.let {
            it.onStop()
            mVideoListener = null
        }

        mCacheMediaDataSource?.let {
            mCacheMediaDataSource = null
        }

        if (mMediaPlayer != null && mPlayHashCode.get() != mMediaPlayer.hashCode()) {
            mPlayHashCode.set(mMediaPlayer.hashCode())
            val releaseMediaPlay: IMediaPlayer = mMediaPlayer as IMediaPlayer

            mMediaPlayer = null

            thread {
                releaseMediaPlay.stop()
                releaseMediaPlay.release()
            }.start()
        }

    }


    override fun onError(iMediaPlayer: IMediaPlayer?, what: Int, extra: Int): Boolean {
        mCacheMediaDataSource?.onError()
        reset()
        return true
    }

    override fun onBufferingUpdate(iMediaPlayer: IMediaPlayer?, percent: Int) {
        mVideoListener?.onBufferProcess(percent)
    }

    override fun onInfo(iMediaPlayer: IMediaPlayer?, what: Int, extra: Int): Boolean {
        if (what == IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED) {
            mVideoListener?.onRotationInfo(extra)
            mRotation = extra.toLong()
        }
        return true
    }

    override fun onPrepared(iMediaPlayer: IMediaPlayer?) {
        mMediaPlayer?.start().let {
            iMediaPlayer?.duration?.let {
                mDuration = it
            }
            handler.sendEmptyMessage(PLAY_PROGRESS)
            mVideoListener?.onStart()
            return
        }
    }

    companion object {
        private const val PLAY_PROGRESS = 1

        private var instance: MediaPlayerTool? = null
            get() {
                if (field == null) {
                    field = MediaPlayerTool()
                }
                return field
            }

        @Synchronized
        fun get(): MediaPlayerTool {
            return instance!!
        }
    }

    private var handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message?) {
            if (msg?.what == PLAY_PROGRESS) {
                if (mVideoListener != null && mMediaPlayer!!.isPlaying) {
                    mVideoListener!!.onPlayProcess(mMediaPlayer!!.currentPosition)
                }
                sendEmptyMessageDelayed(PLAY_PROGRESS, 100)
            }
        }
    }

    class MediaPlayerService : Binder() {
        fun getService(): MediaPlayerTool {
            return MediaPlayerTool.get()
        }
    }

    abstract class VideoListener {
        abstract fun onStart()
        abstract fun onStop()
        abstract fun onComplete()
        abstract fun onRotationInfo(rotation: Int)
        abstract fun onPlayProcess(currentProcess: Long)
        open fun onBufferProcess(process: Int) {}
    }
}