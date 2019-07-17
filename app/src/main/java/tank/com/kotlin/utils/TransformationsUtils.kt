package tank.com.kotlin.utils

import android.graphics.Bitmap
import android.widget.ImageView
import com.bumptech.glide.request.target.ImageViewTarget

/**
 *  @author: vancetang
 *  @date:   2019-07-06 14:17
 */
class TransformationsUtils(view: ImageView?) : ImageViewTarget<Bitmap>(view) {

    var target: ImageView? = null

    override fun setResource(resource: Bitmap?) {
        resource?.let {
            view.setImageBitmap(it)
            var height = it.height
            var width = it.height

            var targetWidth = view.width

            var scale = width * 1.0F / targetWidth * 1.0F

            val targetHeight = height * scale




        }

    }
}