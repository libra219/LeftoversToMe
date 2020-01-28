package local.libra219.android.leftoverstome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_product.*

class ProductActivity : AppCompatActivity() {
    /**
     * 変数初期化
     */
    private var firebaseDatabase: FirebaseFirestore? = null
    private val TAG = "ProductActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        /**
         * 画面遷移元から取得
         */
        val title = intent.getStringExtra("PRO_TITLE")
        val explanation = intent.getStringExtra("PRO_EX")
        val price = intent.getStringExtra("PRO_PRICE")
        val key = intent.getStringExtra("PRO_KEY")
        // ユーザーデータ代理
        val testId = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

        tv_product_title.text = title
        tv_product_ex.text = explanation
        tv_product_price.text = price

        /**
         * firebaseのデータベース初期化
         */
        firebaseDatabase = FirebaseFirestore.getInstance()

        btn_product_keep.setOnClickListener {
            firebaseDatabase?.collection("item")?.document(key)
                ?.update("keep_id", testId)
                ?.addOnSuccessListener {
                    Log.d(TAG, "update OK")
                    finish()
                }
                ?.addOnFailureListener{e ->
                    Log.w(TAG, "update ERROR", e)
                }
        }
    }



}
