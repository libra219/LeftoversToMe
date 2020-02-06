package local.libra219.android.leftoverstome

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_goods_register.*


class GoodsRegisterActivity : AppCompatActivity() {
    private val TAG = "GoodsRegisterActivity"

    private lateinit var dataStore: SharedPreferences

    /** Firebase  **/
    private var fs: FirebaseFirestore? = null

    data class itemData(
        var name: String = "",
        var price: Int = 0,
        var sale: Int = 0,
        var img: String = "",
        var explanation: String = "",
        var shop_id: String = "0",
        var keep_id: String = "0"
    )

    init {
        Log.d(TAG, "=================init===========================")

        fs = FirebaseFirestore.getInstance()

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goods_register)

        // SharedPreferencesインスタンスを生成
        dataStore = this!!.getSharedPreferences("DataStore", Context.MODE_PRIVATE)

        Log.d(TAG, "====================== LoginData ${dataStore.all} =======================")


        btn_goods_success.setOnClickListener {

            val name = et_goods_name.text.toString()
            val price = et_goods_price.text.toString()
            val explanation = et_ex.text.toString()

            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(price) && !TextUtils.isEmpty(explanation)){
                Log.d(TAG, "$name === $price")
                AlertDialog.Builder(this) // FragmentではActivityを取得して生成
                    .setTitle("以下の内容で登録します")
                    .setMessage("商品名：$name \n" +
                            "価格：$price \n" +
                            "\n" +
                            "よろしいですか？")
                    .setPositiveButton("OK") { dialog, which ->
                        Log.d(TAG, "入力Ok")
                        var proData = itemData(name, price.toInt(),90,"https://", explanation, dataStore.getString("UserId",null).toString())
                        setItem(1, proData)

                    }
                    .setNegativeButton("No") { dialog, which ->
                        Log.d(TAG, "入力No")
                    }
                    .show()
            }else{
                AlertDialog.Builder(this) // FragmentではActivityを取得して生成
                    .setTitle("未入力項目があります")
                    .setMessage("全て入力してください")
                    .setPositiveButton("OK") { dialog, which ->
                        Log.d(TAG, "未入力OK")
                    }
                    .show()
            }

        }

    }


    private fun setItem(id: Any, itemData: itemData){
        Log.d(TAG, "======================setItemList Start=========================")

        fs!!.collection("item")
            .document()
            .set(itemData)
            .addOnSuccessListener {
                Log.d(TAG, "setItemList ok === $it")
                Toast.makeText(this, "商品登録完了しました！", Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener {
                Log.d(TAG, "setItemList no === $it")
                Toast.makeText(this, "商品登録に失敗しました。", Toast.LENGTH_SHORT).show()
            }
    }
}
