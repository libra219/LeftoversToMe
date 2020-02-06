package local.libra219.android.leftoverstome

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_keep_item.*

class KeepItemActivity : AppCompatActivity() {
    private val TAG = "KeepItemActivity"

    /** Firebase  **/
    private var fs: FirebaseFirestore? = null
    private lateinit var dataStore: SharedPreferences


    init {
        fs = FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keep_item)

        dataStore = getSharedPreferences("DataStore", Context.MODE_PRIVATE)

        val itemId = intent.getStringExtra("ITEM_ID").toString()

        fs!!.collection("item")
            .document(itemId)
            .get()
            .addOnSuccessListener {
                if(it != null){
                    tv_keep_item_name.text = it["name"].toString()
                    tv_keep_item_price.text = it["price"].toString()
                    tv_keep_item_id.text = it["keep_id"].toString()
                    tv_keep_item_limit.text = "2020/02/07 18:00"
                }
            }

        btn_keep_item_del.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("キープを取消します")
                .setMessage("よろしいですか？")
                .setPositiveButton("OK") { dialog, which ->
                    Log.d(TAG, "入力Ok")
                    fs!!.collection("item")
                        .document(itemId)
                        .update(
                            mapOf(
                                "keep_id" to "0"
                            )
                        )
                        .addOnSuccessListener {
                            Log.d(TAG, "====================keep 取り消し 成功=====================")
                            Toast.makeText(this, "キープを取り消しました", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Log.d(TAG, "====================keep 取り消し失敗=====================\n" +
                                    "ERROR : $e")
                            Toast.makeText(this, "取り消しに失敗しました", Toast.LENGTH_SHORT).show()
                        }
                }
                .setNegativeButton("No") { dialog, which ->
                    Log.d(TAG, "入力No")
                }
                .show()
        }
    }

}
