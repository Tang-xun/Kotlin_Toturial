package tank.com.kotlin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class QRCodeSecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qr_scan_sccond_activity)

        fragmentManager.beginTransaction()
                .add(R.id.second_fragment_container, QRCodeScanFragment(), "QRFragment")
                .commit()
    }
}