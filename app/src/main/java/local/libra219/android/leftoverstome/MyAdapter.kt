package local.libra219.android.leftoverstome

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private val myDataset: MutableList<Map<String, Any>>, private val setAdapterItem: Int, private val setListView: Int, private val itemList: (MutableList<Map<String, Any>>) -> Unit, private val itemFunc: (Any) -> Unit) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(private val view: View, setListView: Int, val itemList: (MutableList<Map<String, Any>>) -> Unit, val itemFunc: (Any) -> Unit) : RecyclerView.ViewHolder(view){
        val text_View: TextView = view.findViewById(setListView)

        fun setUp(itemName: String, any: Any?, myDataset: MutableList<Map<String, Any>>){
            this.text_View.text = itemName
            this.text_View.tag = any
            this.itemView.setOnClickListener {
                itemList(myDataset)
                itemFunc(any!!)
            }
        }
    }
    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.MyViewHolder {

        // create a new view
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(setAdapterItem, parent, false)

//        val textView = LayoutInflater.from(parent.context)
//            .inflate(R.layout.list_view_adapter_item, parent, false) as TextView
        // set the view's size, margins, paddings and layout parameters

        return MyViewHolder(view, setListView, itemList, itemFunc)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
//        holder.text_View.text = myDataset[position]
        holder.text_View.tag = myDataset[position]["primary_key"]
        holder.setUp(myDataset[position]["name"].toString(), myDataset[position]["primary_key"], myDataset)


//        holder.itemView.setOnClickListener{
//            Log.d("tag", "${position} === ${holder.itemView.context}")
//            Log.d("tag", "${position} === ${it.context}")
//            Toast.makeText(it.context, holder.text_View.text, Toast.LENGTH_SHORT).show()
//
//
//        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size


}


