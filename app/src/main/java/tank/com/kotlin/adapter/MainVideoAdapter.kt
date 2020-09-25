package tank.com.kotlin.adapter

import android.app.Activity
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import tank.com.kotlin.IJKPlayerActivity
import tank.com.kotlin.R
import tank.com.kotlin.model.MainVideoBean
import tank.com.kotlin.utils.IntentUtil
import tank.com.kotlin.view.PlayTextureView


class MainVideoAdapter(activity: IJKPlayerActivity, mainVideoBeans: ArrayList<MainVideoBean>) : Adapter<MainVideoAdapter.VideoViewHolder>() {

    private var mContext: IJKPlayerActivity = activity
    private var mainVideoBeans: ArrayList<MainVideoBean>? = mainVideoBeans

    init {
        Log.i(TAG, "MainVideoAdapter init ${mainVideoBeans.size}")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        return VideoViewHolder(View.inflate(mContext, R.layout.video_item_layout, null))
    }

    override fun getItemCount(): Int = mainVideoBeans?.size ?: 0



    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val vh: VideoViewHolder = holder
        val bean: MainVideoBean? = mainVideoBeans?.get(position)
        Log.i(TAG, "onBindViewHolder $bean")
        bean?.let {
            vh.videoTvName?.text = it.userName
            vh.playTextureView?.setVideoSize(it.width!!, it.height!!)
            vh.videoTvContent?.text = it.content
            vh.videoTvName?.text = it.userName
            Glide.with(mContext).load(it.coverUrl).into(vh.videoIvCover!!)
            Glide.with(mContext).load(it.avatarRes).into(vh.videoIvAvatar!!)

            vh.playTextureView?.setOnClickListener {
                mContext.jumpNotCloseMediaPlay(position)
                IntentUtil.gotoVideoPlayerDetailActivity(mContext as Activity, mainVideoBeans!!, position, vh.playTextureView!!)
            }
            return
        }
    }


    class VideoViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        var relativeLayout: RelativeLayout? = null
        var playTextureView: PlayTextureView? = null
        var videoIvCover: ImageView? = null
        var videoProgressBar: ProgressBar? = null
        var videoTvTime: TextView? = null
        var videoIvPlay: ImageView? = null
        var videoTvContent: TextView? = null
        var videoIvAvatar: ImageView? = null
        var videoTvName: TextView? = null

        init {
            relativeLayout = itemView.findViewById(R.id.rl_video)
            playTextureView = itemView.findViewById(R.id.videoPlayTextureView)
            videoIvCover = itemView.findViewById(R.id.videoIvCover)
            videoProgressBar = itemView.findViewById(R.id.videoProgressBar)
            videoIvPlay = itemView.findViewById(R.id.videoIvPlayIcon)
            videoTvContent = itemView.findViewById(R.id.videoTvContent)
            videoTvName = itemView.findViewById(R.id.videoTvName)
            videoIvAvatar = itemView.findViewById(R.id.videoIvAvatar)
            videoTvTime = itemView.findViewById(R.id.videoTvTime)
        }

    }

    companion object {
        private val TAG = MainVideoAdapter::class.java.simpleName
    }


}