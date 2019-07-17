package tank.com.kotlin.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory

/**
 *  @author: vancetang
 *  @date:   2019-07-06 11:58
 */
class BitmapCropHelper {

    companion object {

        private const val MaxSize = 720

        fun cropBitmapWithScale(input: String, scale: Float): Bitmap? {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            var bitmap = BitmapFactory.decodeFile(input, options)

            var width = options.outWidth
            var height = options.outHeight


            // 图片比例
            val bitmapScale = when {
                width > height -> // w > h 比例 16:9
                    16 * 1.0F / 9
                width < height -> // w < h 比例 9:16
                    9 * 1.0F / 16
                else -> // w == h 比例 1:1
                    1.0f
            }

            var finalWidth = 0F
            var finalHeight = 0F

            if (bitmapScale > 1) {
                if (width > MaxSize) {
                    width = MaxSize
                }
                height = (1 / (width * bitmapScale)).toInt()
            } else if (bitmapScale < 1) {

                // 如果超过最大尺寸，则使用最大尺寸
                if (height > MaxSize) {
                    height = MaxSize
                }
                width = (height * bitmapScale).toInt()
            } else {
                // 1:1
                width = height
            }
            options.inJustDecodeBounds = false

            bitmap = BitmapFactory.decodeFile(input, options)

            return Bitmap.createScaledBitmap(bitmap, width, height,false)
        }

    }

}