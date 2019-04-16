package tank.com.kotlin

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import kotlinx.coroutines.runBlocking
import tank.com.kotlin.utils.MediaMateUtils

/**
 *  @author: vancetang
 *  @date:   2019/1/31 2:31 PM
 */
class VideoFrameActivity : AppCompatActivity() {

    var preViewIv: ImageView = findViewById(R.id.video_pre_iv);

//    var thumbnailList: ListView = findViewById(R.id.video_thumbnail_rv)

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.video_cover_list_layout)

        initView()
    }

    fun initView() {
        initPreView()
        initThumbnailAdapter()
    }

    private fun initPreView() {
        val width = preViewIv.width
        val height = preViewIv.height
        runBlocking {
            MediaMateUtils.fetchFirstFrame(applicationContext, videoUri, width, height)
        }.let {
            preViewIv.setImageBitmap(it)
        }
    }



    private fun initThumbnailAdapter() {
    }

    companion object {
        val TAG = VideoFrameActivity::class.java.simpleName
        val videoUri = "https://oimryzjfe.qnssl.com/content/1F3D7F815F2C6870FB512B8CA2C3D2C1.mp4"
    }

}