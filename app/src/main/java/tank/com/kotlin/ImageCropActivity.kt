package tank.com.kotlin

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.TransitionOptions
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

/**
 *  @author: vancetang
 *  @date:   2019-07-04 21:20
 */
class ImageCropActivity : AppCompatActivity() {

    private lateinit var mOImageView: ImageView
    private lateinit var mCenterCrop: ImageView
    private lateinit var mFitHeight: ImageView
    private lateinit var mFitWith: ImageView

    init {


    }

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_crop)

        initUI()

        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(IMAGE_PATH, options)

        var width = options.outWidth
        var height = options.outHeight


        if (width > height) {
            width = 720
            height = (width * 9.0F / 16).toInt()
        }

        if (width < height) {
            height = 720
            width = height * 9 / 16
        }



        Glide.with(this).asBitmap().load(IMAGE_PATH).into(mOImageView)

        Glide.with(this).asBitmap().load(IMAGE_PATH).override(width, height).into(mCenterCrop)

        Glide.with(this).asBitmap().load(IMAGE_PATH).override(160, 90).into(mFitWith)

        Glide.with(this).asBitmap().load(IMAGE_PATH).override(90, 160).into(mFitHeight)


    }

    /**
     * 保存Bitmap到文件
     *
     * @param bitmap
     * @param savePath
     * @return
     */
    fun bitmapToFile(bitmap: Bitmap, savePath: String, quality: Int): Boolean {

        var resu = false

        val file = File(savePath)

        var bos: BufferedOutputStream? = null

        try {
            bos = BufferedOutputStream(FileOutputStream(file))

            if (bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos)) {
                bos.flush()
                resu = true
            } else {
                resu = false
            }

        } catch (e: Exception) {
            resu = false
        } finally {
            if (bos != null) {
                try {
                    bos.close()
                } catch (e2: Exception) {
                    resu = false
                }

            }
        }

        return resu
    }

    private fun initUI() {
        mOImageView = findViewById(R.id.crop_origin)
        mCenterCrop = findViewById(R.id.crop_center)
        mFitHeight = findViewById(R.id.crop_fit_height)
        mFitWith = findViewById(R.id.crop_fit_width)
    }


    companion object {
        const val IMAGE_PATH = "/mnt/sdcard/DCIM/Camera/IMG_20190502_102944.jpg"
    }

}