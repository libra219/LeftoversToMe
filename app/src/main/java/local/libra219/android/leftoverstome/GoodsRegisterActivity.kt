package local.libra219.android.leftoverstome

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_goods_register.*


class GoodsRegisterActivity : AppCompatActivity() {
    private val TAG = "GoodsRegisterActivity"

    var dummyShopId = 4

    /** Firebase  **/
    private var fs: FirebaseFirestore? = null
    private var itemCount = 0
    var ls: Any? = null
    var ccc: Any? = null

    data class itemData(
        var name: String = "",
        var price: Int = 0,
        var sale: Int = 0,
        var img: String = "",
        var explanation: String = "",
        var shop_id: Int = 0,
        var keep_id: String = "0"
    )

    init {
        Log.d(TAG, "=================init===========================")

        fs = FirebaseFirestore.getInstance()
        fs!!.collection("item").get()
        Log.d(TAG,  "getItem count"+itemCount.toString())

        ls = fs!!.collection("item").get().addOnSuccessListener {

            ccc = it.toObjects(InfoData::class.java)
            itemCount = it.size()

        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goods_register)

        getItemList()
        Log.d(TAG,  "getItem count"+itemCount.toString())

        btn_goods_success.setOnClickListener {
            Log.d(TAG, ls.toString())
            Log.d(TAG, ccc.toString())
            Log.d(TAG, itemCount.toString())

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
                        var proData = itemData(name, price.toInt(),90,"https://", explanation, dummyShopId)
                        setItem(itemCount, proData)

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

    private fun getItemList(){
        Log.d(TAG, "======================getItemList Start=========================")
        var itemListCount = 0

        Log.d(TAG, "=================no get===========================")
        Log.d(TAG,  "getItem count"+itemCount.toString())


    }

    private fun setItem(id: Int, itemData: itemData){
        Log.d(TAG, "======================setItemList Start=========================")
        Log.d(TAG, ls.toString())
        Log.d(TAG, ccc.toString())
        Log.d(TAG, itemCount.toString())

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
