package tank.com.kotlin.adapter

import android.content.Context
import android.graphics.PorterDuff
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.*
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import tank.com.kotlin.R
import tank.com.kotlin.model.MainVideoBean
import tank.com.kotlin.utils.CommonUtil
import tank.com.kotlin.view.PlayTextureView
import tank.com.kotlin.view.VideoTouchView

class VideoDetailAdapter(private val mContext: Context, private val mainVideoBeans: ArrayList<MainVideoBean>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VdViewHolder(mContext, View.inflate(mContext, R.layout.video_item_detail, null))
    }

    override fun getItemCount(): Int {
        return mainVideoBeans.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val vh = holder as VdViewHolder

        val bean = mainVideoBeans[position]
        vh.videoTvPb?.text = ""
        vh.videoPlayPb?.progress = 0
        vh.videoPlayPb?.secondaryProgress = 0
        vh.playTextureView?.setVideoSize(bean.width!!, bean.height!!)
        Glide.with(mContext).load(bean.avatarRes).into(vh.videoIvAvatar!!)
        Glide.with(mContext).load(bean.coverUrl).into(vh.ivCover!!)
        vh.videoTvContent?.text = bean.content
        vh.videoTvName?.text = bean.userName
        setVideoSize(vh, bean.width!!, bean.height!!)
    }

    private fun setVideoSize(vh: VideoDetailAdapter.VdViewHolder, width: Int, height: Int) {
        val videoRatio = width.toFloat().div(height.toFloat())
        val windowWidth = CommonUtil.getWindowWidth()
        val windowHeight = CommonUtil.getWidowHeight()

        val windowRation: Float = windowWidth.toFloat() / windowHeight.toFloat()
        val layoutParams: ViewGroup.LayoutParams = vh.videoTouchView!!.layoutParams

        if (videoRatio > windowRation) {
            layoutParams.width = windowWidth
            layoutParams.height = (layoutParams.width / videoRatio).toInt()
        } else {
            layoutParams.width = (layoutParams.height * videoRatio).toInt()
            layoutParams.height = windowHeight
        }
        vh.videoTouchView!!.layoutParams = layoutParams
    }

    class VdViewHolder(context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {
        var videoTouchView: VideoTouchView? = null
        var ivCover: ImageView? = null
        var playTextureView: PlayTextureView? = null
        var videoLoadPb: ProgressBar? = null
        var videoPlayPb: ProgressBar? = null
        var videoTvPb: TextView? = null
        var videoRelateLayout: RelativeLayout? = null
        var videoChangeIvPb: ImageView? = null
        var videoChangeTvPb: TextView? = null
        var videoIvAvatar: ImageView? = null
        var videoTvContent: TextView? = null
        var videoTvName: TextView? = null

        init {
            videoTouchView = itemView.findViewById(R.id.vdTouchView)
            ivCover = itemView.findViewById(R.id.vdIvCover)
            playTextureView = itemView.findViewById(R.id.vdPtv)
            videoLoadPb = itemView.findViewById(R.id.vdLoadProcess)
            videoPlayPb = itemView.findViewById(R.id.vdPlayProcess)
            videoTvPb = itemView.findViewById(R.id.vdTvProcess)
            videoRelateLayout = itemView.findViewById(R.id.vdRlProcess)
            videoChangeIvPb = itemView.findViewById(R.id.vdRlIvProcess)
            videoChangeTvPb = itemView.findViewById(R.id.vdRlTvProcess)
            videoIvAvatar = itemView.findViewById(R.id.vdIvCover)
            videoTvContent = itemView.findViewById(R.id.vdTvContent)
            videoTvName = itemView.findViewById(R.id.vdTvName)
            itemView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            videoPlayPb?.progressDrawable?.setColorFilter(getColor(context, R.color.white), PorterDuff.Mode.SRC_IN)
        }
    }


}