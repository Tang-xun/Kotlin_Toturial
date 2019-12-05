package tank.com.kotlin.utils

import android.app.Activity
import android.content.Intent
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import android.view.View
import tank.com.kotlin.VideoDetailActivity
import tank.com.kotlin.model.MainVideoBean

class IntentUtil {
    companion object {
        const val INTENT_DATA_LIST = "intent_data_list"
        const val INTENT_PLAY_POSITION = "intent_play_position"

        fun gotoVideoPlayerDetailActivity(activity: Activity, videoBeanList:ArrayList<MainVideoBean>,position:Int ,animation: View) {
            val intent = Intent(activity, VideoDetailActivity::class.java)
            intent.putExtra(INTENT_DATA_LIST, videoBeanList)
            intent.putExtra(INTENT_PLAY_POSITION, position)
            val compat: ActivityOptionsCompat = ActivityOptionsCompat.makeClipRevealAnimation(animation,0,0,animation.width,animation.height)
            ActivityCompat.startActivity(activity, intent, compat.toBundle())
        }
    }
}