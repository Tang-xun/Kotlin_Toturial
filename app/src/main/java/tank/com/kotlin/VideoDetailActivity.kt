package tank.com.kotlin

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import tank.com.kotlin.adapter.VideoDetailAdapter
import tank.com.kotlin.customer.BaseActivity
import tank.com.kotlin.model.MainVideoBean
import tank.com.kotlin.utils.CommonUtil
import tank.com.kotlin.utils.IntentUtil
import tank.com.kotlin.utils.MediaPlayerTool
import tank.com.kotlin.view.VideoTouchView
import java.util.*

class VideoDetailActivity : BaseActivity() {

    private var mediaPlayerTool: MediaPlayerTool = MediaPlayerTool.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_detail_activity)
        initIntent()
        initUI()

        mVideoDetailRv?.post {
            mVideoDetailRv?.scrollToPosition(playPosition)
            playVisibleVideo(mediaPlayerTool.isPlaying())
        }
    }

    private var playPosition: Int = 0

    private var mainVideoBeans: ArrayList<MainVideoBean>? = null

    private var mVideoDetailRv: RecyclerView? = null

    private var pagerSnapHelper: androidx.recyclerview.widget.PagerSnapHelper? = null

    private var linearLayoutManager: androidx.recyclerview.widget.LinearLayoutManager? = null

    private var mIvClose: ImageView? = null
    private fun initIntent() {
        playPosition = intent.getIntExtra(IntentUtil.INTENT_PLAY_POSITION, 0)
        mainVideoBeans = intent.getSerializableExtra(IntentUtil.INTENT_DATA_LIST) as ArrayList<MainVideoBean>
    }

    private var mPlayView: View? = null

    private fun initUI() {

        mVideoDetailRv = findViewById(R.id.videoDetailRv)
        mIvClose = findViewById(R.id.videoIVClose)

        linearLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        mVideoDetailRv?.layoutManager = linearLayoutManager

        pagerSnapHelper = androidx.recyclerview.widget.PagerSnapHelper()
        pagerSnapHelper?.attachToRecyclerView(mVideoDetailRv)
        mVideoDetailRv?.adapter = VideoDetailAdapter(this, mainVideoBeans!!)

        mVideoDetailRv?.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (pagerSnapHelper?.findSnapView(linearLayoutManager) != mPlayView) {
                        playVisibleVideo(false)
                    }
                }
            }
        })

        mIvClose?.setOnClickListener {
            onBackPressed()
        }
    }

    private val isResumePlay: Boolean = false

    private var changeProgressTime: Long = 0L

    private fun playVisibleVideo(playing: Boolean) {
        val snapView = pagerSnapHelper?.findSnapView(linearLayoutManager) ?: return

        val position: Int = linearLayoutManager!!.getPosition(snapView)

        if (position < 0) return

        if (!isResumePlay) {
            mediaPlayerTool.reset()
        }

        mPlayView = snapView

        val viewHolder: VideoDetailAdapter.VdViewHolder = mVideoDetailRv?.getChildViewHolder(snapView) as VideoDetailAdapter.VdViewHolder

        if (isResumePlay) {
            viewHolder.videoLoadPb?.visibility = View.GONE
            viewHolder.ivCover?.visibility = View.GONE
            viewHolder.playTextureView?.rotation = mediaPlayerTool.mRotation?.toFloat() ?: 0f
        } else {
            viewHolder.videoLoadPb?.visibility = View.VISIBLE
            viewHolder.ivCover?.visibility = View.VISIBLE

            mediaPlayerTool.initMediaPlayer()
            mediaPlayerTool.setDataSource(mainVideoBeans!![position].videoUrl!!)
        }

        viewHolder.videoTouchView?.mOnTouchSlideListener = object : VideoTouchView.OnTouchSlideListener {
            override fun onSlide(distant: Float) {
                mediaPlayerTool.let {
                    if (!viewHolder.videoRelateLayout!!.isShown) {
                        viewHolder.videoRelateLayout?.visibility = View.VISIBLE
                        changeProgressTime = mediaPlayerTool.getCurrentPosition() ?: 0
                    }

                    changeProgressText(viewHolder, distant)
                    return
                } ?: return
            }

            override fun onUp() {
                if (viewHolder.videoRelateLayout?.isShown == true) {
                    viewHolder.videoRelateLayout?.visibility = View.GONE
                }
                mediaPlayerTool.seekTo(changeProgressTime)
            }

            override fun onClick() {
                mContext?.onBackPressed()
            }
        }

        mediaPlayerTool.mVolume = 1f

        mediaPlayerTool.mVideoListener = object : MediaPlayerTool.VideoListener() {

            override fun onStart() {
                viewHolder.videoLoadPb?.visibility = View.GONE
                viewHolder.ivCover?.postDelayed({ viewHolder.ivCover?.visibility = View.GONE }, 300)
            }

            override fun onStop() {
                viewHolder.videoLoadPb?.visibility = View.GONE
                viewHolder.ivCover?.visibility = View.VISIBLE
                viewHolder.videoPlayPb?.progress = 0
                viewHolder.videoPlayPb?.secondaryProgress = 0
                viewHolder.videoTvPb?.text = ""
                mPlayView = null
            }

            override fun onComplete() {
                onStop()
                if (position + 1 > mainVideoBeans!!.size) {
                    mVideoDetailRv?.smoothScrollToPosition(0)
                } else {
                    mVideoDetailRv?.smoothScrollToPosition(position.plus(1))
                }
            }

            override fun onRotationInfo(rotation: Int) {
                viewHolder.playTextureView!!.rotation = rotation.toFloat()
            }

            @SuppressLint("SetTextI18n")
            override fun onPlayProcess(currentProcess: Long) {
                val process = currentProcess.toFloat() / mediaPlayerTool.mDuration * 100;
                viewHolder.videoPlayPb?.progress = process.toInt()

                val currentMMSS = CommonUtil.toMMSS(currentProcess)
                val durationMMSS = CommonUtil.toMMSS(mediaPlayerTool.mDuration)

                viewHolder.videoChangeTvPb?.text = "$currentMMSS/ $durationMMSS"
            }

            fun onBufferProgress(progress: Int) {
                viewHolder.videoPlayPb?.secondaryProgress = progress
            }
        }

        if (isResumePlay) {
            viewHolder.playTextureView!!.resetTextureView(mediaPlayerTool.getAvailableSurfaceTexture())
            mediaPlayerTool.setPlayTextureView(viewHolder.playTextureView!!)
            viewHolder.playTextureView!!.postInvalidate()
        } else {
            viewHolder.playTextureView!!.resetTextureView()
            mediaPlayerTool.setPlayTextureView(viewHolder.playTextureView!!)
            mediaPlayerTool.setSurfaceTexture(viewHolder.playTextureView!!.getSurfaceTexture())
            mediaPlayerTool.prepare()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun changeProgressText(viewHolder: VideoDetailAdapter.VdViewHolder, distant: Float) {

        val radio = distant / viewHolder.videoPlayPb?.width!!

        changeProgressTime = changeProgressTime.plus(mediaPlayerTool.mDuration * radio).toLong()

        if (changeProgressTime < 0) {
            changeProgressTime = 0
        }

        if (changeProgressTime > mediaPlayerTool.mDuration) {
            changeProgressTime = mediaPlayerTool.mDuration
        }

        val changeTimeMMSS = CommonUtil.toMMSS(changeProgressTime)
        val rawTimeMMSS = CommonUtil.toMMSS(mediaPlayerTool.mDuration)

        viewHolder.videoChangeTvPb?.text = "$changeTimeMMSS/ $rawTimeMMSS"

        if (changeProgressTime > mediaPlayerTool.mDuration) {
            viewHolder.videoChangeIvPb?.setImageResource(R.mipmap.video_fast_forward)
        } else {
            viewHolder.videoChangeIvPb?.setImageResource(R.mipmap.video_fast_back)
        }
    }

    private var isFirst = true

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (isFirst) isFirst = false
    }

    override fun onResume() {
        super.onResume()

        if (!isFirst && !mediaPlayerTool.isPlaying()) {
            playVisibleVideo(false)
        }
    }

    private var donotPause: Boolean? = null

    override fun onPause() {
        super.onPause()
        if (mediaPlayerTool.isPlaying()) {
            if (donotPause!!) {
                val snapView: View? = pagerSnapHelper?.findSnapView(linearLayoutManager)
                if (snapView != null && linearLayoutManager?.getPosition(snapView) != playPosition) {
                    mediaPlayerTool.reset()
                }
            } else {
                mediaPlayerTool.reset()
            }
        }
    }

    override fun finish() {
        super.finish()
        donotPause = true
    }

}
