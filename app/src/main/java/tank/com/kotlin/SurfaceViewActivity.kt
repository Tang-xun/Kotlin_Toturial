package tank.com.kotlin

import android.Manifest.permission.CAMERA
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.hardware.Camera
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import android.widget.Toast
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class SurfaceViewActivity : AppCompatActivity(), SurfaceHolder.Callback, Camera.PictureCallback {

    private var surfaceHolder: SurfaceHolder? = null

    private var camera: Camera? = null

    private var surfaceView: SurfaceView? = null

    private val TAG = this.javaClass.simpleName!!

    private val neededPermissions = arrayOf(CAMERA, WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.surfaceview_activity)
        surfaceView = findViewById(R.id.photoSurfaceView)
        val result = checkPermission()
        if (result) {
            setupSurfaceHolder()
        }
    }

    private fun checkPermission(): Boolean {
        val currentAPIVersion = Build.VERSION.SDK_INT;

        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            val permissionNotGranted = ArrayList<String>()
            // judge which permission is not grants
            for (permission in neededPermissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionNotGranted.add(permission)
                }
            }
            if (permissionNotGranted.size > 0) {
                var shouldShowAlert = false
                for (permission in permissionNotGranted) {
                    shouldShowAlert = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
                }
                val arr = arrayOfNulls<String>(permissionNotGranted.size)
                val permissions = permissionNotGranted.toArray(arr)
                if (shouldShowAlert) {
                    showPermissionAlert(permissions)
                } else {
                    requestPermissions(permissions)
                }
                return false
            }
        }
        return true
    }

    private fun setViewVisibility(id: Int, visibility: Int) {
        val view = findViewById<View>(id)
        view!!.visibility = visibility
    }

    private fun setupSurfaceHolder() {
        setViewVisibility(R.id.startBt, View.VISIBLE)
        setViewVisibility(R.id.photoSurfaceView, View.VISIBLE)
        surfaceHolder = surfaceView!!.holder
        surfaceHolder!!.addCallback(this)
        setBtnClick()
    }

    private fun setBtnClick() {
        val startBt = findViewById<Button>(R.id.startBt)
        startBt?.setOnClickListener { capturePhoto() }
    }

    private fun capturePhoto() {
        if (camera != null) {
            camera!!.takePicture(null, null, this)
        }
    }

    private fun showPermissionAlert(permissions: Array<String?>) {
        AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle(R.string.permission_required)
                .setMessage(R.string.permission_message)
                .setPositiveButton(android.R.string.yes) { _, _ -> requestPermissions(permissions) }
                .create().show()
    }

    private fun requestPermissions(permissions: Array<String?>) {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE -> {
                for (res in grantResults) {
                    if (res == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(this, R.string.permission_warning, Toast.LENGTH_LONG).show()
                        setViewVisibility(R.id.showPermissionMsg, View.VISIBLE)
                        return
                    }
                }
                setupSurfaceHolder()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        Log.i(TAG, "surfaceCreated ")
        startCamera()
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        Log.i(TAG, "surfaceChanged")
        resetCamera()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        Log.i(TAG, "surfaceDestroyed")
        releaseCamera()
    }

    override fun onPictureTaken(data: ByteArray?, camera: Camera?) {
        saveImage(data)
        resetCamera()
    }

    /**
     * start camera
     */
    private fun startCamera() {
        camera = Camera.open()
        camera!!.setDisplayOrientation(90)
        try {
            camera!!.setPreviewDisplay(surfaceHolder)
            camera!!.startPreview()
        } catch (e: IOException) {
            Log.e(TAG, "Occur Error At Start Camera \n $e")
        }
    }

    /**
     * reset camera
     */
    private fun resetCamera() {
        if (surfaceHolder!!.surface == null) {
            return
        }
        camera!!.stopPreview()

        try {
            camera!!.setPreviewDisplay(surfaceHolder)
        } catch (e: IOException) {
            Log.e(TAG, "Occur error at resetCamera() \n $e")
        }
        camera!!.startPreview()
    }

    /**
     * save image to sdcard
     */
    private fun saveImage(data: ByteArray?) {
        val outStream: FileOutputStream
        try {
            val fileName = "TUTORIAL_WING_${System.currentTimeMillis()}.jpg"
            val file = File(Environment.getExternalStorageDirectory(), fileName)
            Log.i(TAG, "take photo and save img ${file.absolutePath}")
            outStream = FileOutputStream(file)
            outStream.write(data)
            outStream.close()
        } catch (e: FileNotFoundException) {
            Log.e(TAG, "Occur error at save image FileNotFoundException $e")
        } catch (e: IOException) {
            Log.e(TAG, "Occur error at save image IOException $e")
        }
    }

    /**
     * release camera
     */
    private fun releaseCamera() {
        camera!!.stopPreview()
        camera!!.release()
        camera = null
    }

    companion object {
        const val REQUEST_CODE = 100
    }

}

