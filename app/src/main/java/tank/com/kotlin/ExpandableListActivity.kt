package tank.com.kotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.Toast
import tank.com.kotlin.adapter.CustomExpandableListAdapter

/**
 * tank.com.kotlin <br/>
 * Created by tank325 on 2018/12/18/5:20 PM.
 */
class ExpandableListActivity : AppCompatActivity() {

    private val TAG:String = ExpandableListActivity::class.java.simpleName

    private var expandableLv: ExpandableListView? = null

    private var adapter: ExpandableListAdapter? = null

    private var titleList: List<String>? = null

    val data: HashMap<String, List<String>>
        get() {
            val listData = HashMap<String, List<String>>()

            val redMiMobiles = ArrayList<String>()
            redMiMobiles.add("Redmi Y2")
            redMiMobiles.add("Redmi S2")
            redMiMobiles.add("Redmi Note 5 Pro")
            redMiMobiles.add("Redmi Note 5")
            redMiMobiles.add("Redmi 5 plus")
            redMiMobiles.add("Redmi Y1")
            redMiMobiles.add("Redmi 3s plus")

            val micromaxMobiles = ArrayList<String>()
            micromaxMobiles.add("Micromax Bharat Go")
            micromaxMobiles.add("Micromax Bharat 5 Pro")
            micromaxMobiles.add("Micromax Bharat 5")
            micromaxMobiles.add("Micromax Canvas 1")
            micromaxMobiles.add("Micromax Dual 5")

            val sumsungMobiles = ArrayList<String>()
            sumsungMobiles.add("Sumsung Galaxy S9+")
            sumsungMobiles.add("Sumsung Galaxy Note 8")
            sumsungMobiles.add("Sumsung Galaxy S8")
            sumsungMobiles.add("Sumsung Galaxy Note 7")
            sumsungMobiles.add("Sumsung Galaxy S7")
            sumsungMobiles.add("Sumsung Galaxy Note 6")
            sumsungMobiles.add("Sumsung Galaxy A8")

            val appleMobiles = ArrayList<String>()
            appleMobiles.add("Iphone XS Max")
            appleMobiles.add("Iphone XS")
            appleMobiles.add("Iphone X")
            appleMobiles.add("Iphone 8 plus")
            appleMobiles.add("Iphone 8")
            appleMobiles.add("Iphone 7 plus")
            appleMobiles.add("Iphone 7")

            listData["Redmi"] = redMiMobiles
            listData["Micromax"] = micromaxMobiles
            listData["SumSung"] = sumsungMobiles
            listData["Apple"] = appleMobiles

            return listData
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.expendable_activity)
        Log.i(TAG, "onCreate ::: ")
        expandableLv = findViewById(R.id.expendableLv);

        if (expandableLv != null) {
            Log.i(TAG, "$expandableLv is not null")
            val listData = data
            Log.i(TAG, "$listData")
            titleList = ArrayList(listData.keys)

            adapter = CustomExpandableListAdapter(this, titleList as ArrayList<String>, listData)

            expandableLv?.setAdapter(adapter)

            expandableLv?.setOnGroupExpandListener { groupPosition -> Toast.makeText(applicationContext, "${(titleList as ArrayList)[groupPosition]} expanded", Toast.LENGTH_LONG).show() }

            expandableLv?.setOnGroupCollapseListener { groupPosition -> Toast.makeText(applicationContext, "${(titleList as ArrayList)[groupPosition]} collapsed", Toast.LENGTH_LONG).show() }

            expandableLv?.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
                val title = (titleList as ArrayList<String>)[groupPosition]
                val child = listData[(titleList as ArrayList<String>)[groupPosition]]!![childPosition]
                Toast.makeText(applicationContext, "Clicked: $title -> $child", Toast.LENGTH_LONG).show()
                false

            }

        }

    }
}