package tank.com.kotlin

import android.graphics.Rect
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.View
import com.yanzhenjie.permission.AndPermission
import tank.com.kotlin.adapter.MainVideoAdapter
import tank.com.kotlin.customer.BaseActivity
import tank.com.kotlin.model.MainVideoBean
import tank.com.kotlin.utils.CommonUtil
import tank.com.kotlin.utils.MediaPlayerTool

open class IJKPlayerActivity : BaseActivity() {

    private var videoRecycleView: RecyclerView? = null

    private var mMediaDataBeans: ArrayList<MainVideoBean>? = null

    private val mMediaPlayerTool: MediaPlayerTool? = MediaPlayerTool.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ijk_player_activity)
        videoRecycleView = findViewById(R.id.videosRecyclerView)
        videoRecycleView!!.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)

        // create data source
        mMediaDataBeans = MainVideoBean.generateVideoData()
        videoRecycleView!!.adapter = MainVideoAdapter(this, mMediaDataBeans!!)

        // when scroller, make some ont location in Screen center
        androidx.recyclerview.widget.LinearSnapHelper().attachToRecyclerView(videoRecycleView)

        videoRecycleView!!.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val position = parent.getChildAdapterPosition(view)

                if (position > 0) {
                    outRect.top = resources.getDimension(R.dimen.dialog_margin).toInt()
                }
            }
        })

        videoRecycleView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                currentPlayView?.let {
                    val playRange: Boolean = isPlayRange(currentPlayView, recyclerView)

                    if (isPlayRange(currentPlayView, recyclerView)) {
                        mMediaPlayerTool?.reset()
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    checkPlayVideo()
                    if (currentPlayView == null) {
                        playVideoByPosition(-1)
                    }
                }

            }
        })

        // 申请文件读写权限
        AndPermission.with(this)
                .permission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onDenied {
                    Log.i(TAG, "onDenied -> $it")
                }.onGranted {
                    Log.i(TAG, "onGranted -> $it")
                }.start()
    }

    var currentPlayView: View? = null

    private fun playVideoByPosition(resumePosition: Int) {
        val isResumePlay = resumePosition >= 0

        if (isResumePlay && videoPositionList.size == 0 || mMediaPlayerTool == null) {
            return
        }

        if (!isResumePlay) {
            mMediaPlayerTool.reset()
        }

        var playPosition: Int = 0

        if (isResumePlay) {
            playPosition = resumePosition
        } else {
            if (currentPlayIndex > videoPositionList.size) {
                currentPlayIndex = 0
            }

            if (!videoPositionList.isEmpty()) {
                playPosition = videoPositionList[currentPlayIndex]
            } else {
                return
            }
        }


        val vh = videoRecycleView?.findViewHolderForAdapterPosition(playPosition) as MainVideoAdapter.VideoViewHolder

        currentPlayView = vh.relativeLayout

        if (isResumePlay) {
            vh.videoProgressBar?.visibility = View.GONE
            vh.videoIvPlay?.visibility = View.GONE
            vh.videoIvCover?.visibility = View.GONE
        } else {
            vh.videoIvPlay?.visibility = View.GONE
            vh.videoIvCover?.visibility = View.VISIBLE
            vh.videoProgressBar?.visibility = View.VISIBLE
            vh.videoTvTime?.text = ""

            mMediaPlayerTool.initMediaPlayer()

            val videoUrl = mMediaDataBeans!![playPosition].videoUrl
            if (videoUrl != null) {
                mMediaPlayerTool.setDataSource(videoUrl)
            }
        }

        mMediaPlayerTool.mVolume = 0f
        mVideoListener = object : MediaPlayerTool.VideoListener() {
            override fun onStart() {
                vh.videoIvPlay?.visibility = View.GONE
                vh.videoProgressBar?.visibility = View.GONE
                vh.videoIvCover!!.postDelayed({ vh.videoIvCover!!.visibility = View.GONE }, 300)
            }

            override fun onStop() {
                vh.videoIvPlay?.visibility = View.VISIBLE
                vh.videoProgressBar?.visibility = View.GONE
                vh.videoIvCover?.visibility = View.VISIBLE
                vh.videoTvTime?.text = ""
                currentPlayView = null
            }

            override fun onComplete() {
                currentPlayIndex = currentPlayIndex.plus(1)
                playVideoByPosition(-1)
            }

            override fun onRotationInfo(rotation: Int) {
                vh.playTextureView?.rotation = rotation.toFloat()
            }

            override fun onPlayProcess(currentProcess: Long) {
                val time = CommonUtil.toMMSS(mMediaPlayerTool.mDuration.minus(currentProcess))
                vh.videoTvTime?.text = time
            }
        }
        mMediaPlayerTool.mVideoListener = mVideoListener


        if (isResumePlay) {
            vh.playTextureView?.resetTextureView(mMediaPlayerTool.getAvailableSurfaceTexture())
            mMediaPlayerTool.setPlayTextureView(vh.playTextureView!!)
            vh.playTextureView?.postInvalidate()
        } else {
            vh.playTextureView?.resetTextureView()
            mMediaPlayerTool.setPlayTextureView(vh.playTextureView!!)
            mMediaPlayerTool.setSurfaceTexture(vh.playTextureView!!.getSurfaceTexture())
            mMediaPlayerTool.prepare()
        }


    }

    private var isFirst: Boolean = true

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (isFirst) {
            Log.i(TAG, "onWindowFocusChanged $hasFocus")
            isFirst = false
            refreshVideo()
        }
    }


    private fun refreshVideo() {
        Log.i(TAG, "refreshVideo start ::: ")
        mMediaPlayerTool!!.let {
            mMediaPlayerTool.reset()
            checkPlayVideo()
            playVideoByPosition(-1)
        }
        Log.i(TAG, "refreshVideo end ::: ")
    }

    private var currentPlayIndex: Int = -1

    private var videoPositionList = ArrayList<Int>();

    private var mVideoListener: MediaPlayerTool.VideoListener? = null

    private fun checkPlayVideo() {
        currentPlayIndex = 0
        videoPositionList.clear()

        val childCount = videoRecycleView!!.childCount

        Log.i(TAG, "checkPlayVideo -> $childCount")

        for (i in 0 until childCount) {
            val childView = videoRecycleView?.getChildAt(i)

            val playRange: Boolean = isPlayRange(childView!!.findViewById(R.id.rl_video), videoRecycleView)
            if (playRange) {
                val position = videoRecycleView!!.getChildAdapterPosition(childView)

                if (position >= 0 && !videoPositionList.contains(position)) {
                    videoPositionList.add(position)
                }
            }
        }
    }

    private fun isPlayRange(childView: View?, parentView: View?): Boolean {

        if (childView == null || parentView == null) {
            return false
        }

        val childLocal = IntArray(2)
        childView.getLocationOnScreen(childLocal)

        val parentLocal = IntArray(2)
        parentView.getLocationOnScreen(parentLocal)

        return childLocal[1] >= parentLocal[1] && childLocal[1] <= parentLocal[1] + parentView.height - childView.height
    }

    private var jumpVideoPosition: Int = -1

    open fun jumpNotCloseMediaPlay(position: Int) {
        this.jumpVideoPosition = position
    }

    override fun onResume() {
        super.onResume()

        if (jumpVideoPosition != -1
                && (videoPositionList.size > currentPlayIndex
                        && isCurrentVideoEqualJumpVideo())
                && mMediaPlayerTool != null
                && mMediaPlayerTool.isPlaying()) {
            playVideoByPosition(jumpVideoPosition);
        } else {
            Log.i(TAG, "onResume :::: refreshVideo ")
            refreshVideo()
        }
        jumpVideoPosition = -1
    }

    override fun onPause() {
        super.onPause()

        mMediaPlayerTool?.let {
            if (videoPositionList.size > currentPlayIndex && isCurrentVideoEqualJumpVideo()) {
                videoRecycleView?.postDelayed({
                    mVideoListener?.onStop()
                }, 300)
                return
            } else {
                mMediaPlayerTool.reset()
                if (!videoPositionList.contains(jumpVideoPosition)) {
                    videoPositionList.add(jumpVideoPosition)
                }
                currentPlayIndex = videoPositionList.indexOf(jumpVideoPosition)
                return
            }
        }
    }

    private fun isCurrentVideoEqualJumpVideo() =
            jumpVideoPosition == videoPositionList[currentPlayIndex]

    companion object {
        val TAG = IJKPlayerActivity::class.java.simpleName
    }


}