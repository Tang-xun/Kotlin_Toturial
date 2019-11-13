package tank.com.kotlin

import android.Manifest.permission.*
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import com.bigkoo.pickerview.TimePickerView
import java.lang.ref.WeakReference
import java.util.*

/**
 * tank.com.kotlin_demon <br/>
 * Created by tank325 on 2018/12/18/4:09 PM.
 */
class MainActivity : AppCompatActivity() {

    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }

    private val neededPermissions = arrayOf(CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)

    val refActivty: WeakReference<Activity> = WeakReference(this)

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        checkPermission()

        findViewById<Button>(R.id.goSurfaceViewBt).setOnClickListener {
            startActivity(Intent(applicationContext, SurfaceViewActivity::class.java))
        }

        findViewById<Button>(R.id.goExpendViewBt).setOnClickListener {
            startActivity(Intent(applicationContext, ExpandableListActivity::class.java))
        }

        findViewById<Button>(R.id.goRecyclerViewBt).setOnClickListener {
            startActivity(Intent(applicationContext, RecyclerViewActivity::class.java))
        }

        findViewById<Button>(R.id.goQRScanBt).setOnClickListener {
            startActivity(Intent(applicationContext, QRCodeScanActivity::class.java))
        }

        findViewById<Button>(R.id.goIJKPlayer).setOnClickListener {
            startActivity(Intent(applicationContext, IJKPlayerActivity::class.java))
        }
        findViewById<Button>(R.id.goCirclePieView).setOnClickListener {
            startActivity(Intent(applicationContext, CirclePieActivity::class.java))
        }
        findViewById<Button>(R.id.goRectProcessView).setOnClickListener {
            startActivity(Intent(applicationContext, RectProcessActivity::class.java))
        }
        findViewById<Button>(R.id.datePickBt).setOnClickListener {
            showPvDatePicker()
        }
        findViewById<Button>(R.id.goVideoFrame).setOnClickListener {
            startActivity(Intent(applicationContext, VideoFrameActivity::class.java))
        }
        findViewById<Button>(R.id.goImageCrop).setOnClickListener {
            startActivity(Intent(applicationContext, ImageCropActivity::class.java))
        }

    }

    private fun showPvDatePicker() {
        val calender = Calendar.getInstance()
        val year = calender.get(Calendar.YEAR)


        val pvDatePicker: TimePickerView = TimePickerView.Builder(this,
                TimePickerView.OnTimeSelectListener { date, v ->
                    val dateStr = android.text.format.DateFormat.format("yyyy-MM-dd", date.time)

                    Toast.makeText(applicationContext, "$dateStr", Toast.LENGTH_LONG).show()
                })
                .setType(TimePickerView.Type.YEAR_MONTH_DAY).setLabel("", "", "", "", "", "")
                .setCancelColor(Color.GRAY)
                .isCenterLabel(true)
                .setRange(1900, year)
                .setTitleText("生日")
                .build()


        pvDatePicker.setDate(calender)
        pvDatePicker.show()

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

    private fun showPermissionAlert(permissions: Array<String?>) {
        AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle(R.string.permission_required)
                .setMessage(R.string.permission_message)
                .setPositiveButton(android.R.string.yes) { _, _ -> requestPermissions(permissions) }
                .create().show()
    }

    private fun requestPermissions(permissions: Array<String?>) {
        ActivityCompat.requestPermissions(this, permissions, SurfaceViewActivity.REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            SurfaceViewActivity.REQUEST_CODE -> {
                for (res in grantResults) {
                    if (res == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(this, R.string.permission_warning, Toast.LENGTH_LONG).show()
                        return
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }



    private lateinit var datePickerDialog: DatePickerDialog

    private fun showDatePickDialog() {
        refActivty.get()?.let { it ->
            val today = Calendar.getInstance()
            datePickerDialog = DatePickerDialog(it, 0, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                Toast.makeText(it, "$year-${month + 1}-$dayOfMonth", Toast.LENGTH_LONG).show()
            }, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH))
            datePickerDialog.show()
        }
    }


}