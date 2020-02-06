package local.libra219.android.leftoverstome.ui.notifications

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import local.libra219.android.leftoverstome.*

class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private val TAG = "NotificationsFragment"

    /** Firebase  **/
    private var fs: FirebaseFirestore? = null

    private lateinit var dataStore: SharedPreferences


    private var _itemSetList: MutableList<Map<String, Any>> = mutableListOf()
    private var itemSetMap: MutableMap<String, Any> = mutableMapOf()

    init {
        fs = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
            ViewModelProviders.of(this).get(NotificationsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)
        val textView: TextView = root.findViewById(R.id.text_notifications)
        notificationsViewModel.text.observe(this, Observer {
            textView.text = it
        })

        // SharedPreferencesインスタンスを生成
        dataStore = this.context!!.getSharedPreferences("DataStore", Context.MODE_PRIVATE)
        val shopId = dataStore.getString("shopId", "")

        fs!!.collection("item")
            .whereEqualTo("shop_id", shopId)
            .whereGreaterThan("keep_id", "0")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null){
                    Log.w(TAG, "スナップショットエラー", exception)
                    return@addSnapshotListener
                }

                if (snapshot != null){
                    Log.d(TAG, "============ snapshot ============ $snapshot")
                    for (document in snapshot){
                        Log.d(TAG, document.toString())
                        itemSetMap = mutableMapOf()
                        itemSetMap["primary_key"] = document.id
                        itemSetMap["name"] = document.get("name").toString()
                        itemSetMap["explanation"] = document.get("explanation").toString()
                        itemSetMap["price"] = document.get("price").toString()
                        itemSetMap["sale"] = document.get("sale").toString()
                        itemSetMap["img"] = document.get("img").toString()
                        itemSetMap["keep_id"] = document.get("keep_id").toString()
                        itemSetMap["shop_id"] = document.get("shop_id").toString()
                        _itemSetList.add(itemSetMap)
                    }
                }

                viewManager = LinearLayoutManager(root.context)
                viewAdapter = MyAdapter(_itemSetList, R.layout.list_view_adapter_item, R.id.tv_card_list_title
                    ,{
                        Log.d(TAG, "===================== it ===================== $it \n" +
                                "${it[0]["primary_key"]}")
                        val intent = Intent(this.context, DeliveryActivity::class.java)
                        intent.putExtra("PRIMARY_KEY", it[0]["primary_key"].toString())
                        intent.putExtra("NAME", it[0]["name"].toString())
                        intent.putExtra("PRICE", it[0]["price"].toString())
                        intent.putExtra("KEEP_ID", it[0]["keep_id"].toString())
                        startActivity(intent)
                    }
                    ,{
                        Toast.makeText(this.context, it.toString(), Toast.LENGTH_LONG).show()

                    })


                recyclerView = root.findViewById<RecyclerView>(R.id.rv_notifications).apply {
                    // use this setting to improve performance if you know that changes
                    // in content do not change the layout size of the RecyclerView
                    Log.d(TAG, "===========findView=============")
                    setHasFixedSize(false)

                    // use a linear layout manager
                    layoutManager = viewManager

                    // specify an viewAdapter (see also next example)
                    adapter = viewAdapter
                }


            }


        return root
    }
}