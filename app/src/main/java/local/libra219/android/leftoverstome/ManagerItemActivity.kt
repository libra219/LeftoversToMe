package local.libra219.android.leftoverstome

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_manager_item.*


class ManagerItemActivity : AppCompatActivity() {

    private val TAG = "ManagerItemActivity"
    private var fs: FirebaseFirestore? = null

    init {
        fs = FirebaseFirestore.getInstance()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_item)
        /**
         * 遷移前から取得
         */
        val primaryKey = intent.getStringExtra("PRIMARY_KEY")
        Log.d(TAG, primaryKey)
        var name: String = ""
        var price = 0
        var explanation: String = ""

        /** PrimaryKeyから表示内容検索 **/
        fs!!.collection("item")
            .document(primaryKey)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot != null){
                    et_manager_shop_name.setText(snapshot["name"].toString(), TextView.BufferType.NORMAL)
                    et_item_price.setText(snapshot["price"].toString(), TextView.BufferType.NORMAL)
                    et_manager_shop_description.setText(snapshot["explanation"].toString(), TextView.BufferType.NORMAL)

                }
            }



        /** 変更ボタン処理 **/
        btn_manager_shop_info_change.setOnClickListener {
            name = et_manager_shop_name.text.toString()
            price = et_item_price.text.toString().toInt()
            explanation = et_manager_shop_description.text.toString()

            AlertDialog.Builder(this)
                .setTitle("以下の内容で変更します")
                .setMessage("商品名：$name \n" +
                        "価格：$price \n" +
                        "説明：$explanation \n" +
                        "\n" +
                        "よろしいですか？")
                .setPositiveButton("OK") { dialog, which ->
                    Log.d(TAG, "入力Ok")
                    fs!!.collection("item")
                        .document(primaryKey)
                        .update(
                            mapOf(
                                "name" to name,
                                "price" to price,
                                "explanation" to explanation
                            )
                        )
                        .addOnSuccessListener {
                            Log.d(TAG, "====================Change 成功=====================")
                            Toast.makeText(this, "変更しました", Toast.LENGTH_SHORT).show()
                            reload()
                        }
                        .addOnFailureListener { e ->
                            Log.d(TAG, "====================Change 失敗=====================\n" +
                                    "ERROR : $e")
                            Toast.makeText(this, "変更に失敗しました", Toast.LENGTH_SHORT).show()
                        }
                }
                .setNegativeButton("No") { dialog, which ->
                    Log.d(TAG, "入力No")
                }
                .show()
        }

        /** 削除ボタン **/
        btn_item_del.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("削除します")
                .setMessage("復元できませんがよろしいですか？")
                .setPositiveButton("OK") { dialog, which ->
                    Log.d(TAG, "入力Ok")
                    fs!!.collection("item")
                        .document(primaryKey)
                        .delete()
                        .addOnSuccessListener {
                            Log.d(TAG, "====================Delete 成功=====================")
                            Toast.makeText(this, "削除しました", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Log.d(TAG, "====================Delete 失敗=====================\n" +
                                    "ERROR : $e")
                            Toast.makeText(this, "削除に失敗しました", Toast.LENGTH_SHORT).show()
                        }
                }
                .setNegativeButton("No") { dialog, which ->
                    Log.d(TAG, "入力No")
                }
                .show()
        }
    }

    private fun reload() {
        val intent = intent
        overridePendingTransition(0, 0)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
    }
}
