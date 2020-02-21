package local.libra219.android.leftoverstome

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.SimpleAdapter
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_keep_list.*
import kotlinx.android.synthetic.main.activity_product.*

class ProductActivity : AppCompatActivity() {
    /**
     * 変数初期化
     */
    private var firebaseDatabase: FirebaseFirestore? = null
    private var fs: FirebaseFirestore? = null
    private val TAG = "ProductActivity"
    private lateinit var dataStore: SharedPreferences
    private var storage: FirebaseStorage? = null

    init {
        storage = FirebaseStorage.getInstance()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        /** 戻るボタン **/
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        /**
         * 画面遷移元から取得
         */
        val title = intent.getStringExtra("PRO_TITLE")
        val explanation = intent.getStringExtra("PRO_EX")
        val price = intent.getStringExtra("PRO_PRICE")
        val key = intent.getStringExtra("PRO_KEY")
        val img = intent.getStringExtra("PRO_IMG")

        // SharedPreferencesインスタンスを生成
        dataStore = getSharedPreferences("DataStore", Context.MODE_PRIVATE)
        val userId = dataStore.getString("UserId", "0")

        tv_product_title.text = title
        tv_product_ex.text = explanation
        tv_product_price.text = price

        /**
         * 画像の挿入、Glide使用
         * loadにURL入れると表示可能
         */
        val imageView = findViewById<ImageView>(R.id.img_product)
        Glide.with(this)
            .load(img)
            .into(imageView)


        /**
         * firebaseのデータベース初期化
         */
        firebaseDatabase = FirebaseFirestore.getInstance()

        /** ボタン押されたとき **/
        btn_product_keep.setOnClickListener {
            firebaseDatabase?.collection("item")?.document(key)
                ?.update("keep_id", userId)
                ?.addOnSuccessListener {
                    Log.d(TAG, "keep OK")
                    Toast.makeText(this, "商品をキープしました！", Toast.LENGTH_SHORT).show()
                    finish()
                }
                ?.addOnFailureListener{e ->
                    Log.w(TAG, "keep ERROR", e)
                    Toast.makeText(this, "商品をキープに失敗しました。", Toast.LENGTH_SHORT).show()
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
