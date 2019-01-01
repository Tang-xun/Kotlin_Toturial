package tank.com.kotlin.customer

import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import tank.com.kotlin.R
import tank.com.kotlin.utils.StatusBarUtil

open class BaseActivity : AppCompatActivity() {

    protected var mContext: BaseActivity? = null

    protected var mAlertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(this, R.color.my_black), false)
    }

    override fun onStart() {
        super.onStart()
        StatusBarUtil.setColor(this, Color.WHITE, true)
    }

    fun showDialog() {
        mAlertDialog = AlertDialog.Builder(this)
                .setCancelable(true)
                .setView(View.inflate(this, R.layout.dialog_loading, null))
                .create()
    }

    fun dismissDialog() {
        mAlertDialog?.dismiss()
    }

}