package local.libra219.android.leftoverstome

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AndroidRuntimeException
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
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
        /** 戻るボタン **/
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dataStore = getSharedPreferences("DataStore", Context.MODE_PRIVATE)

        val itemId = intent.getStringExtra("ITEM_ID").toString()
        val qrSize = 500

        fs!!.collection("item")
            .document(itemId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null){
                    Log.w(TAG, "=================== ERROR ====================== $exception")
                    return@addSnapshotListener
                }

                if (snapshot != null){
                    tv_keep_item_name.text = snapshot["name"].toString()
                    tv_keep_item_price.text = snapshot["price"].toString()
                    tv_keep_item_id.text = snapshot["keep_id"].toString()
                    tv_keep_item_limit.text = "2020/02/07 18:00"

                    if (snapshot["keep_id"] == "0"){
                        Toast.makeText(this, "受け取り完了しました。", Toast.LENGTH_SHORT).show()
                        finish()
                    }

                    try {
                        val barcodeEncoder = BarcodeEncoder()
                        /** QRコードをBitmapで生成 **/
                        val qpMap = barcodeEncoder.encodeBitmap(tv_keep_item_id.text.toString(), BarcodeFormat.QR_CODE, qrSize, qrSize)

                        /** QRコード表示 **/
                        img_qr.setImageBitmap(qpMap)

                    }catch (e: WriterException){
                        throw AndroidRuntimeException("Barcode Error.", e)
                    }
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
