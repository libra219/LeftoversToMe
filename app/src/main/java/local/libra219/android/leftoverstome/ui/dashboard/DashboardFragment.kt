package local.libra219.android.leftoverstome.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import local.libra219.android.leftoverstome.*

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private val TAG = "DashboardFragment"

    /** Firebase  **/
    private var fs: FirebaseFirestore? = null

    private var loginData = LoginData()

    private var _itemSetList: MutableList<Map<String, Any>> = mutableListOf()
    private var itemSetMap: MutableMap<String, Any> = mutableMapOf()

    init {
        fs = FirebaseFirestore.getInstance()
        loginData.shopId = 4
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val textView: TextView = root.findViewById(R.id.text_dashboard)
        dashboardViewModel.text.observe(this, Observer {
            textView.text = it
        })


        fs!!.collection("item")
            .whereEqualTo("shop_id", loginData.shopId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null){
                    Log.w(TAG, "エラー", exception)
                    return@addSnapshotListener
                }

                if (snapshot != null){
                    Log.d(TAG, snapshot.toString())
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

                var setList: MutableList<String> = mutableListOf()
                for (list in _itemSetList){
                    setList.add(list["name"] as String)
                }

                viewManager = LinearLayoutManager(root.context)
                viewAdapter = MyAdapter(_itemSetList, R.layout.list_view_adapter_item, R.id.tv_card_list_title
                    ,{

                }
                    ,{
                        Toast.makeText(this.context, it.toString(), Toast.LENGTH_LONG).show()
                        val intent = Intent(this.context, ManagerItemActivity::class.java)
                        intent.putExtra("PRIMARY_KEY", it.toString())
                        startActivity(intent)
                })


                recyclerView = root.findViewById<RecyclerView>(R.id.rv_dashboard).apply {
                    // use this setting to improve performance if you know that changes
                    // in content do not change the layout size of the RecyclerView
                    Log.d(TAG, "===========findView=============")
                    setHasFixedSize(true)

                    // use a linear layout manager
                    layoutManager = viewManager

                    // specify an viewAdapter (see also next example)
                    adapter = viewAdapter
                }


            }

        return root
    }

    private fun getItemList(){
        Log.d(TAG, "===========================getList Start=============================")
    }

}