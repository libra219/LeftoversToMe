package local.libra219.android.leftoverstome

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.activity_manager_shop_info.*
import java.util.*


class ManagerShopInfoActivity : AppCompatActivity() {
    private val TAG = "ManagerShopInfoActivity"
    private var fs: FirebaseFirestore? = null

    private lateinit var dataStore: SharedPreferences

    init {
        fs = FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_shop_info)
        /** 戻るボタン **/
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // SharedPreferencesインスタンスを生成
        dataStore = getSharedPreferences("DataStore", Context.MODE_PRIVATE)

        fs!!.collection("shop")
            .whereEqualTo("users", dataStore.getString("UserId", "").toString())
            .get()
            .addOnSuccessListener { documents ->
                Log.d(TAG, "============================ OnSuccess =================================")
                if (documents != null){
                    for (doc in documents){
                        Log.d(TAG, doc.toString())

                        val editor = dataStore.edit()
                        editor.putString("shopId", doc.id)
                        editor.putString("shopName", doc["name"].toString())
                        editor.putString("shopDescription", doc["description"].toString())
                        editor.putString("shopAddress", doc["address"].toString())
                        editor.putString("shopUserId", doc["users"].toString())
                        editor.putString("ShopImg", doc["img"].toString())
                        //editor.commit();
                        editor.apply()

                    }

                    et_manager_shop_name.setText(dataStore.getString("shopName", ""), TextView.BufferType.NORMAL)
                    et_manager_shop_description.setText(dataStore.getString("shopDescription", ""), TextView.BufferType.NORMAL)
                    et_manager_shop_add.setText(dataStore.getString("shopAddress", ""), TextView.BufferType.NORMAL)
                    et_manager_shop_lat.setText(dataStore.getString("", ""), TextView.BufferType.NORMAL)
                    et_manager_shop_lng.setText(dataStore.getString("", ""), TextView.BufferType.NORMAL)
                }
            }



        btn_manager_shop_info_change.setOnClickListener {
            Log.d(TAG, "========================= BTN CHANGE =========================")

            /** 住所から緯度経度取得 **/
            var gcoder: Geocoder? = Geocoder(this, Locale.getDefault())
            val maxResults = 1 // リターンする件数
            var lstAddr: List<Address?>
            var latitude :Double = 0.0
            var longitude :Double = 0.0

            lstAddr = gcoder!!.getFromLocationName(et_manager_shop_add.text.toString(), maxResults)

            if (lstAddr != null && lstAddr.size > 0){
                // 緯度・経度取得
                val addr = lstAddr[0]!!
                latitude = addr.latitude
                longitude = addr.longitude

                Log.d(TAG, "緯度: $latitude　経度: $longitude")

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
                            .document(dataStore.getString("shopId", "").toString())
                            .update(
                                mapOf(
                                    "name" to et_manager_shop_name.text.toString(),
                                    "description" to et_manager_shop_description.text.toString(),
                                    "address" to et_manager_shop_add.text.toString(),
                                    "lat_lng" to GeoPoint(latitude, longitude),
                                    "users" to dataStore.getString("UserId", "").toString(),
                                    "img" to "https://"
                                )
                            )
                            .addOnSuccessListener {
                                Log.d(TAG, "====================Change 成功=====================")
                                Toast.makeText(this, "変更しました", Toast.LENGTH_SHORT).show()
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

            }else{
                Toast.makeText(this, "住所が存在しません。", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
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

    private fun reload() {
        val intent = intent
        overridePendingTransition(0, 0)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
    }
}
