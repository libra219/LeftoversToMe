package local.libra219.android.leftoverstome

import android.Manifest
import android.R.attr
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_goods_register.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


class GoodsRegisterActivity : AppCompatActivity() {
    private val TAG = "GoodsRegisterActivity"

    private lateinit var dataStore: SharedPreferences

    /** Firebase  **/
    private var fs: FirebaseFirestore? = null
    //ストレージのインスタンス
    private var storage: FirebaseStorage? = null

    companion object {
        const val CAMERA_REQUEST_CODE = 1
        const val CAMERA_PERMISSION_REQUEST_CODE = 2
    }

    data class itemData(
        var name: String = "",
        var price: Int = 0,
        var sale: Int = 0,
        var img: String = "",
        var explanation: String = "",
        var shop_id: String = "0",
        var keep_id: String = "0"
    )

    private lateinit var bitmapImage: Bitmap
    private lateinit var downloadUri: Uri


    init {
        Log.d(TAG, "=================init===========================")

        fs = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

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
                            "説明：$explanation \n" +
                            "よろしいですか？")
                    .setPositiveButton("OK") { dialog, which ->
                        Log.d(TAG, "入力Ok")
                        // ランダム生成IDを取得
                        val getIdString = fs!!.collection("item").document().id

                        // 画像の取得
                        img_register_cam.isDrawingCacheEnabled = true
                        img_register_cam.buildDrawingCache()
                        val getBitmapImage = (img_register_cam.drawable as BitmapDrawable).bitmap
                        val baos = ByteArrayOutputStream()
                        getBitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val data = baos.toByteArray()
                        val nowTime = getToTime()

                        // ストレージ
                        val storageRef = storage!!.reference

                        /** ストリームをつかって画像の送信 **/
                        try {
                            val child = storageRef.child("image/${getIdString}")
                            var uploadTask = child.putBytes(data)
                            uploadTask.addOnFailureListener {
                                Log.w(TAG, "============================ sendImageData: uploadTask error ===============================")
                                Log.w(TAG, "============================ error log: ${it}===============================")
                            }.addOnSuccessListener {
                                Log.d(TAG, "============================ sendImageData: uploadTask OK ===============================")
                                Log.d(TAG, "============================ log: i${it} ===============================")
                                Toast.makeText(this, "画像の投稿が完了", Toast.LENGTH_SHORT).show()
                            }
                            uploadTask.continueWithTask { task ->
                                if (!task.isSuccessful) {
                                    task.exception?.let {
                                        throw it
                                    }
                                }
                                child.downloadUrl
                            }.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    downloadUri = task.result!!

                                    Log.d(TAG, "============== downloadUritest: $downloadUri ============")

                                    /** データベースに登録 **/
                                    var proData = itemData(name, price.toInt(),90, downloadUri.toString(), explanation, dataStore.getString("shopId",null).toString())
                                    setItem(getIdString, proData)


                                } else {
                                    // Handle failures
                                    // ...
                                }
                            }


                        }catch (e: Exception){
                            Log.w(TAG, "============================ sendImageData: catch error ===============================")
                            Log.w(TAG, "============================ sendImageData: log $e ===============================")

                        }

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

        img_register_cam.setOnClickListener {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).resolveActivity(packageManager)?.let {
                if (checkCameraPermission()) {
                    takePicture()
                } else {
                    grantCameraPermission()
                }
            } ?: Toast.makeText(this, "カメラを扱うアプリがありません", Toast.LENGTH_LONG).show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "================================== onActivityResult ====================================")

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.extras?.get("data")?.let {
                bitmapImage = it as Bitmap
                img_register_cam.setImageBitmap(bitmapImage)
            }

        }
    }


    /**
     * 商品をFirebaseに登録する関数
     */
    private fun setItem(id: String, itemData: itemData){
        Log.d(TAG, "======================setItemList Start=========================")

        fs!!.collection("item")
            .document(id)
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

    /**
     * 現在時刻の取得
     */
    private fun getToTime(): String {
        val date = Date()
        val format = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        return format.format(date)
    }


    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
        }

        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    private fun grantCameraPermission() =
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )

    private fun checkCameraPermission() = PackageManager.PERMISSION_GRANTED ==
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA)
}
