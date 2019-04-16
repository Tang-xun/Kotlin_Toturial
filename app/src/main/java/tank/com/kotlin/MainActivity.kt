package tank.com.kotlin

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.bigkoo.pickerview.TimePickerView
import java.lang.ref.WeakReference
import java.text.DateFormat
import java.time.Year
import java.util.*

/**
 * tank.com.kotlin_demon <br/>
 * Created by tank325 on 2018/12/18/4:09 PM.
 */
class MainActivity : AppCompatActivity() {

    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }

    val refActivty: WeakReference<Activity> = WeakReference(this)

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
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