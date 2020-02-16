package local.libra219.android.leftoverstome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import kotlinx.android.synthetic.main.activity_delivery.*

class DeliveryActivity : AppCompatActivity() {

    private val TAG = "DeliveryActivity"

    /** Firebase  **/
    private var fs: FirebaseFirestore? = null
    private lateinit var primaryKey: String
    private lateinit var name: String
    private lateinit var price: String
    private lateinit var keepId: String

    init {
        fs = FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery)
        /** 戻るボタン **/
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        /** 受け取り **/
        primaryKey = intent.getStringExtra("PRIMARY_KEY")
        name = intent.getStringExtra("NAME")
        price = intent.getStringExtra("PRICE")
        keepId = intent.getStringExtra("KEEP_ID")

        tv_delivery_name.text = name
        tv_delivery_price.text = price
        tv_delivery_id.text = keepId
        tv_delivery_limit.text = "2020/2/7 18:00"

        btn_delivery_ok.setOnClickListener {
            fs!!.collection("item")
                .document(primaryKey)
                .update("keep_id", "0")
                .addOnSuccessListener {
                    Log.d(TAG, "======================= $TAG CLEAN OK ================================")
                    Toast.makeText(this, "キープが完了しました！", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Not CLEAN ERRER $e")
                    Toast.makeText(this, "キープに失敗しました！", Toast.LENGTH_SHORT).show()
                }
        }

        btn_delivery_del.setOnClickListener {
            fs!!.collection("item")
                .document(primaryKey)
                .delete()
                .addOnSuccessListener {
                    Log.d(TAG, "======================= $TAG DELETE CLEAN OK ================================")
                    Toast.makeText(this, "削除しました！", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Not DELETE ERRER $e")
                    Toast.makeText(this, "失敗しました！", Toast.LENGTH_SHORT).show()
                }
        }

        btn_delivery_qr_read.setOnClickListener {
            val intentIntegrator = IntentIntegrator(this).apply {
                captureActivity = MyCaptureActivity::class.java
            }
            intentIntegrator.initiateScan()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data)
        var code: String = intentResult.contents

        if (intentResult != null && code != null){
            if (code == keepId){
                Toast.makeText(this, "ID一致", Toast.LENGTH_SHORT).show()
                fs!!.collection("item")
                    .document(primaryKey)
                    .update("keep_id", "0")
                    .addOnSuccessListener {
                        Log.d(TAG, "======================= $TAG CLEAN OK ================================")
                        Toast.makeText(this, "受け渡し完了しました！", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Not CLEAN ERRER $e")
                        Toast.makeText(this, "認証失敗しました！", Toast.LENGTH_SHORT).show()
                    }
            }else{
                Toast.makeText(this, "ID不一致", Toast.LENGTH_SHORT).show()
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
