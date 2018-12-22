package tank.com.kotlin

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator
import org.json.JSONException
import org.json.JSONObject

class QRCodeScanFragment : Fragment() {

    private var txtName: TextView? = null
    private var txtSiteName: TextView? = null
    private var txtErrorMsg: TextView? = null
    private var scanQrBt: Button? = null

    private var qrIntentIntegrator: IntentIntegrator? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.qr_code_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        txtName = view!!.findViewById(R.id.qrName)
        txtSiteName = view.findViewById(R.id.qrSiteName)
        txtErrorMsg = view.findViewById(R.id.fragmentQrErrorMsg)

        scanQrBt = view.findViewById(R.id.scanBt)
        scanQrBt!!.setOnClickListener { performAction() }

        qrIntentIntegrator = IntentIntegrator.forFragment(this)
        qrIntentIntegrator!!.setOrientationLocked(false)

        super.onViewCreated(view, savedInstanceState)
    }

    private fun performAction() {
        qrIntentIntegrator?.setPrompt(getString(R.string.scan_qr_code))
        qrIntentIntegrator?.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(activity, R.string.result_not_found, Toast.LENGTH_SHORT).show()
            } else {
                try {
                    val obj = JSONObject(result.contents)
                    txtErrorMsg?.visibility = View.GONE
                    txtName?.text = obj.getString("name")
                    txtSiteName?.text = obj.getString("site_name")
                } catch (e: JSONException) {
                    txtErrorMsg?.visibility = View.VISIBLE
                    txtErrorMsg?.text = result.contents
                    Toast.makeText(activity, result.contents, Toast.LENGTH_LONG).show()
                }

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }

}