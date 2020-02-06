package local.libra219.android.leftoverstome

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.SimpleAdapter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_keep_list.*
import java.util.ArrayList

class KeepListActivity : AppCompatActivity() {

    private val TAG = "KeepListActivity"

    /** Firebase  **/
    private var fs: FirebaseFirestore? = null
    private lateinit var dataStore: SharedPreferences

    init {
        fs = FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keep_list)
        /** 戻るボタン **/
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dataStore = getSharedPreferences("DataStore", Context.MODE_PRIVATE)
        val userId = dataStore.getString("UserId", "").toString()

        var lvSetList: MutableList<Map<String, String>> = ArrayList()
        var lvSetMap: MutableMap<String, String> = hashMapOf()

        fs!!.collection("item")
            .whereEqualTo("keep_id", userId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null){
                    Log.w(TAG, "=================== ERROR ====================== $exception")
                    return@addSnapshotListener
                }

                if (snapshot != null){
                    lvSetList = arrayListOf()
                    for (doc in snapshot){
                        Log.d(TAG, "========================== doc =================================")
                        Log.d(TAG, "$doc")
                        lvSetMap = hashMapOf()
                        lvSetMap["item_id"] = doc.id
                        lvSetMap["name"] = doc["name"].toString()
                        lvSetMap["explanation"] = doc["explanation"].toString()
                        lvSetList.add(lvSetMap)
                    }

                    val from = arrayOf("name", "explanation")
                    val to = intArrayOf(android.R.id.text1, android.R.id.text2)
                    val adapter = SimpleAdapter(
                        applicationContext,
                        lvSetList,
                        android.R.layout.simple_list_item_2,
                        from,
                        to
                    )
                    lv_keep_list.adapter = adapter

                    lv_keep_list.setOnItemClickListener { parent, view, position, id ->
                        val intent = Intent(this, KeepItemActivity::class.java)
                        intent.putExtra("ITEM_ID", lvSetList[position]["item_id"])

                        startActivity(intent)
                    }
                }else{
                    Log.d(TAG, "Current data: null")
                }
            }
    }


    /** アクションバーの選択 **/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            android.R.id.home->{
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
