package tank.com.kotlin.audio

import android.media.MediaPlayer
import android.text.TextUtils
import android.util.Log
import java.io.FileDescriptor

/**
 *  @author: vancetang
 *  @date:   2019/4/2 4:07 PM
 */
class AudioPlayer {

    var mMediaPlayer: MediaPlayer = MediaPlayer()

    fun setDataSource(path: String?) {
        if (TextUtils.isEmpty(path)) {
            Log.i(TAG, "setDataSource fail path($path) is inValid")
            return
        }
        mMediaPlayer.setDataSource(path)
    }

    fun setDataSource(fd: FileDescriptor?, offset: Long = 0L, length: Long) {
        if (fd?.valid() == false) {
            Log.i(TAG, "setDataSource fail fd($fd) is inValid")
            return
        }
        if (mMediaPlayer.isPlaying) {
            mMediaPlayer.stop()
        }

        mMediaPlayer.setDataSource(fd, offset, length)
    }

    fun play() {

    }

    fun stop() {

    }

    companion object {
        private const val TAG = "AudioPlayer"
    }

}