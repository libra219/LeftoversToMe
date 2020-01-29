package local.libra219.android.leftoverstome

import android.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView



class RecyclerAdapter {
    var mDataset: ArrayList<String>? = null

    class ItemViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var mTextView: TextView

        init {
            mTextView = v.findViewById(local.libra219.android.leftoverstome.R.id.textView1)
        }
    }

    fun MyAdapter(mDataset: ArrayList<String>?) {
        this.mDataset = mDataset
    }

    fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder? {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(
                local.libra219.android.leftoverstome.R.layout.card_list_item,
                parent,
                false
            )
        return ItemViewHolder(v)
    }

    fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val data: String
        data = mDataset!![position]
        holder.mTextView.text = mDataset!![position]
        holder.mTextView.setOnClickListener{ object : View.OnClickListener{
            override fun onClick(v: View?) {
                removeFromDataset(data)
            }
        }

        }
    }

    fun getItemCount(): Int {
        return mDataset!!.size
    }

    protected fun removeFromDataset(data: String?) {
        for (i in 0 until mDataset!!.size) {
            if (mDataset!![i].equals(data)) {
                mDataset!!.removeAt(i)
//                notifyItemRemoved(i)
                break
            }
        }
    }
}