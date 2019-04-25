package tank.com.kotlin

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import tank.com.kotlin.audio.AccAudioEncoder
import tank.com.kotlin.utils.CommonUtil
import tank.com.kotlin.utils.MediaMateUtils

/**
 *  @author: vancetang
 *  @date:   2019/1/31 2:31 PM
 */
class VideoFrameActivity : AppCompatActivity() {

    private lateinit var preViewIv: ImageView

    private lateinit var thumbnailList: LinearLayout

    private lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_cover_list_layout)
        Log.i(TAG, "onCreate ::: ")
        mContext = this
        initView()
    }

    private fun initView() {
        preViewIv = findViewById(R.id.video_pre_iv)
        thumbnailList = findViewById(R.id.video_thumbnail_ll)
        initPreView()
        initThumbnailAdapter()
    }

    private fun initPreView() {
        Log.i(TAG, "initPreView ::: ")
        val width = preViewIv.width
        val height = preViewIv.height

        runBlocking {
            MediaMateUtils.fetchFirstFrame(applicationContext, videoPath, width, height)
        }.let {
            preViewIv.setImageBitmap(it)
        }
    }

    private fun initThumbnailAdapter() {
        Log.i(TAG, "initThumbnailAdapter ::: ")
        runBlocking {

            MediaMateUtils.fetchVideoFrame(applicationContext, 6, videoPath)
        }.let { it ->

            it?.forEach {
                val iv = ImageView(mContext)
                iv.layoutParams = ViewGroup.LayoutParams(CommonUtil.dip2Px(55), CommonUtil.dip2Px(55))
                iv.setImageBitmap(it)
                thumbnailList.addView(iv)
                Log.i(TAG, Gson().toJson(it))
            }
        }

        runBlocking {
            Log.i(TAG, "will encode audio info ")
            AccAudioEncoder(videoPath)
            return@runBlocking
        }

    }

    companion object {
        private const val TAG = "VideoFrameActivity"
        private const val videoPath = "/mnt/sdcard/Download/mediaTest.mp4"
        private const val videoUri = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"
    }

}