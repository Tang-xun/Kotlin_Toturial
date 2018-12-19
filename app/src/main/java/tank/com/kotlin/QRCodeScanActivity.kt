package tank.com.kotlin

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator
import org.json.JSONException
import org.json.JSONObject

/**
 * tank.com.kotlin <br/>
 * Created by tank325 on 2018/12/19/4:59 PM.
 */
class QRCodeScanActivity : AppCompatActivity() {

    internal var txtName: TextView? = null

    internal var txtSiteName: TextView? = null

    internal var txtErrorMsg: TextView? = null

    internal var qrScanBt: Button? = null

    internal var qrScanIntegrator: IntentIntegrator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qr_scan_activity)
        Log.i(TAG, "onCreate ::: ")

        txtName = findViewById(R.id.qrName)
        txtSiteName = findViewById(R.id.qrSiteName)
        txtErrorMsg = findViewById(R.id.errorMsgShow)

        qrScanBt = findViewById(R.id.scanBt)
        qrScanBt!!.setOnClickListener { performAction() }

        qrScanIntegrator = IntentIntegrator(this)

        val showQrFrameBt: Button = findViewById(R.id.showQrScannerBt)
        showQrFrameBt.setOnClickListener {

        }
    }

    private fun performAction() {
        qrScanIntegrator?.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, getString(R.string.result_not_found), Toast.LENGTH_SHORT).show()
            } else {
                try {
                    val obj = JSONObject(result.contents)
                    txtErrorMsg?.visibility = View.GONE
                    txtName?.text = obj.getString("name")
                    txtSiteName?.text = obj.getString("site_name")
                } catch (e: JSONException) {
                    Log.e(TAG, "parse data occur error $e")
                    txtErrorMsg?.visibility = View.VISIBLE
                    txtErrorMsg?.text = result.contents
                    Toast.makeText(this, result.contents, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        private val TAG = QRCodeScanActivity::class.java.simpleName
    }
}