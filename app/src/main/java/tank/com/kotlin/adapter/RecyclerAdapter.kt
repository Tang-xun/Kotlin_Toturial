package tank.com.kotlin.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater.from
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import tank.com.kotlin.R
import tank.com.kotlin.model.Animal

/**
 * tank.com.kotlin.adapter <br/>
 * Created by tank325 on 2018/12/19/10:33 AM.
 */
class RecyclerAdapter(animals: ArrayList<Animal>, layoutId: Int) : RecyclerView.Adapter<RecyclerAdapter.AnimalViewHolder>() {

    private var listAnimal: List<Animal>? = null

    private var rowLayout: Int? = 0

    init {
        listAnimal = animals
        rowLayout = layoutId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        return AnimalViewHolder(from(parent.context).inflate(this.rowLayout!!, parent, false))
    }

    override fun getItemCount(): Int {
        return listAnimal!!.size
    }

    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
        holder.animal_name!!.text = (listAnimal!![position].animal_name)
        holder.animal_details!!.text = (listAnimal!![position].animal_details)
    }

    class AnimalViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        var animal_name: TextView? = null
        var animal_details: TextView? = null

        init {
            animal_name = itemView!!.findViewById(R.id.recycle_item_name)
            animal_details = itemView.findViewById(R.id.recycle_item_details)
        }
    }
}