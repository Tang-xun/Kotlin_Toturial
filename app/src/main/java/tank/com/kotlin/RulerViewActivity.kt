package tank.com.kotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import tank.com.kotlin.view.CustomerRulerView

/**
 * Created by vancetang on 2020/9/22.
 */
class RulerViewActivity: AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.ruler_view_activity)
    findViewById<CustomerRulerView>(R.id.custom_ruler_view).apply {
      this.setItems(CustomerRulerView.ItemCreator.range(start = 0, end = 200))
      this.setLabel("mm")
    }

  }

}