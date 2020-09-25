package tank.com.kotlin

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.*
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import tank.com.kotlin.adapter.RecyclerAdapter
import tank.com.kotlin.model.Animal

/**
 * tank.com.kotlin <br/>
 * Created by tank325 on 2018/12/18/6:36 PM.
 */
class RecyclerViewActivity : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null

    private var recyclerAdapter: RecyclerAdapter? = null

    private var snapHelper: androidx.recyclerview.widget.LinearSnapHelper? = null

    private var animals: ArrayList<Animal> = ArrayList<Animal>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recyclerview_activity)

        createAnimalList()

        recyclerView = findViewById(R.id.animal_recycle_view_list)
        recyclerView!!.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)

        recyclerAdapter = RecyclerAdapter(animals, R.layout.recycler_list_item)
        recyclerView!!.adapter = recyclerAdapter

        snapHelper = androidx.recyclerview.widget.LinearSnapHelper()

        recyclerView!!.addOnItemTouchListener(RecyclerTouchListener(applicationContext, recyclerView!!, object : IClickListener {
            override fun onClick(view: View, position: Int) {
                Toast.makeText(applicationContext, "${animals[position].animal_name} + is selected ", Toast.LENGTH_SHORT).show()
            }

            override fun onLongClick(view: View, position: Int) {
                Toast.makeText(applicationContext, "${animals[position].animal_name} + is Long Press", Toast.LENGTH_SHORT).show()
            }

        }))

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.recycler_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        var layoutId: Int = R.layout.recycler_list_item
        val spanCount = 2
        when (item!!.itemId) {
            R.id.linearVertical -> {
                recyclerView!!.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
            }
            R.id.linearHorizontal -> {
                layoutId = R.layout.recycler_list_item_horizontal
                recyclerView!!.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
            }
            R.id.gridviewVertical -> {
                recyclerView!!.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, spanCount)
            }
            R.id.gridviewHorizontal -> {
                layoutId = R.layout.recycler_list_item_horizontal
                recyclerView!!.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, spanCount, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
            }
            R.id.staggeredGridviewVertical -> {
                recyclerView!!.layoutManager = androidx.recyclerview.widget.StaggeredGridLayoutManager(spanCount, androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL)
            }
            R.id.staggeredGridviewHorizontal -> {
                layoutId = R.layout.recycler_list_item_horizontal
                recyclerView!!.layoutManager = androidx.recyclerview.widget.StaggeredGridLayoutManager(spanCount, androidx.recyclerview.widget.StaggeredGridLayoutManager.HORIZONTAL)
            }
        }

        recyclerAdapter = RecyclerAdapter(animals, layoutId)
        recyclerView!!.adapter = recyclerAdapter
        snapHelper!!.attachToRecyclerView(recyclerView)

        return super.onOptionsItemSelected(item)
    }

    private fun createAnimalList() {

        val animal1 = Animal()
        animal1.animal_name = "Monkey"
        animal1.animal_details = "Monkeys are haplorhine (\\\"dry-nosed\\\") primates, a paraphyletic group generally possessing tails and consisting of approximately 260 known living species"

        val animal2 = Animal()
        animal2.animal_name = "Buffalo"
        animal2.animal_details = "The African buffalo or Cape buffalo (Syncerus caffer) is a large African bovine"

        val animal3 = Animal()
        animal3.animal_name = "Donkey"
        animal3.animal_details = "The donkey or ass is a domesticated member of the horse family, Equidae. The wild ancestor of the donkey is the African wild ass, E. africanus"

        val animal4 = Animal()
        animal4.animal_name = "Dog"
        animal4.animal_details = "The domestic dog is a domesticated canid which has been selectively bred over millennia for various behaviours, sensory capabilities, and physical attributes"

        val animal5 = Animal()
        animal5.animal_name = "Goat"
        animal5.animal_details = "The domestic goat is a subspecies of goat domesticated from the wild goat of southwest Asia and Eastern Europe"

        val animal6 = Animal()
        animal6.animal_name = "Tiger"
        animal6.animal_details = "The tiger is the largest cat species, most recognisable for their pattern of dark vertical stripes on reddish-orange fur with a lighter underside"

        val animal7 = Animal()
        animal7.animal_name = "Lion"
        animal7.animal_details = "The lion is one of the big cats in the genus Panthera and a member of the family Felidae."

        val animal8 = Animal()
        animal8.animal_name = "Leopard"
        animal8.animal_details = "The leopard is one of the five \\\"big cats\\\" in the genus Panthera"

        val animal9 = Animal()
        animal9.animal_name = "Cheetah"
        animal9.animal_details = "The cheetah, also known as the hunting leopard, is a big cat that occurs mainly in eastern and southern Africa and a few parts of Iran"

        val animal10 = Animal()
        animal10.animal_name = "Rat"
        animal10.animal_details = "Rats are various medium-sized, long-tailed rodents of the superfamily Muroidea"

        animals.add(animal1)
        animals.add(animal2)
        animals.add(animal3)
        animals.add(animal4)
        animals.add(animal5)
        animals.add(animal6)
        animals.add(animal7)
        animals.add(animal8)
        animals.add(animal9)
        animals.add(animal10)
    }

    interface IClickListener {

        fun onClick(view: View, position: Int)

        fun onLongClick(view: View, position: Int)
    }

    class RecyclerTouchListener(context: Context, recyclerView: RecyclerView, private var clickListener: IClickListener) : RecyclerView.OnItemTouchListener {

        private var gestureDetector: GestureDetector

        init {
            gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onLongPress(e: MotionEvent?) {
                    val child: View? = recyclerView.findChildViewUnder(e!!.x, e.y);
                  if (child != null) {
                    clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child))
                  }
                }

                override fun onSingleTapUp(e: MotionEvent?): Boolean {
                    return true
                }
            })
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
            Log.i(TAG, "onTouchEvent $rv $e")
        }

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            val child: View? = rv.findChildViewUnder(e.x, e.y)
            if (child != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child))
            }
            return false
        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        }
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume ${recyclerView?.childCount}")
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "nPause ${recyclerView?.childCount}")
    }

    companion object {
        val TAG: String = RecyclerViewActivity::class.java.simpleName
    }


}