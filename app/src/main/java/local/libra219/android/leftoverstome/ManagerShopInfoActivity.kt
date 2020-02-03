package local.libra219.android.leftoverstome

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.activity_manager_shop_info.*

class ManagerShopInfoActivity : AppCompatActivity() {
    private val TAG = "ManagerShopInfoActivity"
    private var fs: FirebaseFirestore? = null

    private var loginData = LoginData()

    init {
        fs = FirebaseFirestore.getInstance()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_shop_info)

        fs!!.collection("shop")
            .whereEqualTo("users", loginData.userId)
            .get()
            .addOnSuccessListener { documents ->
                Log.d(TAG, "============================ OnSuccess =================================")
                if (documents != null){
                    for (document in documents){
                        Log.d(TAG, document.toString())
                        loginData.shopId = document.id.toInt()
                        loginData.shopName = document.get("name").toString()
                        loginData.shopUserId = document.get("users").toString().toInt()
                        loginData.shopImg = document.get("img").toString()
                        loginData.shopAddress = document.get("address").toString()
                        loginData.description = document.get("description").toString()
                        val LatLng : GeoPoint = document.get("lat_lng") as GeoPoint
                        loginData.shopLatitude = LatLng.latitude
                        loginData.shopLongitude = LatLng.longitude
                        Log.d(TAG, loginData.toString())
                    }

                    et_manager_shop_name.setText(loginData.shopName, TextView.BufferType.NORMAL)
                    et_manager_shop_description.setText(loginData.description, TextView.BufferType.NORMAL)
                    et_manager_shop_add.setText(loginData.shopAddress, TextView.BufferType.NORMAL)
                    et_manager_shop_lat.setText(loginData.shopLatitude.toString(), TextView.BufferType.NORMAL)
                    et_manager_shop_lng.setText(loginData.shopLongitude.toString(), TextView.BufferType.NORMAL)
                }
            }



        btn_manager_shop_info_change.setOnClickListener {
            Log.d(TAG, "========================= BTN CHANGE =========================${loginData.shopId}")
            AlertDialog.Builder(this)
                .setTitle("以下の内容で変更します")
                .setMessage("店舗名：${et_manager_shop_name.text} \n" +
                        "説明：${et_manager_shop_description.text} \n" +
                        "住所：${et_manager_shop_add.text} \n" +
                        "\n" +
                        "よろしいですか？")
                .setPositiveButton("OK") { dialog, which ->
                    Log.d(TAG, "入力Ok")
                    fs!!.collection("shop")
                        .document(loginData.shopId.toString())
                        .set(
                            mapOf(
                                "name" to et_manager_shop_name.text.toString(),
                                "description" to et_manager_shop_description.text.toString(),
                                "address" to et_manager_shop_add.text.toString(),
                                "lat_lng" to GeoPoint(et_manager_shop_lat.text.toString().toDouble(), et_manager_shop_lng.text.toString().toDouble()),
                                "users" to loginData.userId,
                                "img" to "https://"
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
