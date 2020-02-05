package local.libra219.android.leftoverstome

import android.app.AlertDialog
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.activity_login_register.*
import kotlinx.android.synthetic.main.activity_manager_shop_info.*
import java.util.*

class LoginRegisterActivity : AppCompatActivity() {

    private val TAG = "LoginRegisterActivity"

    /** Firebase **/
    private var fs: FirebaseFirestore? = null

    init {
        fs = FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)

        val userSerial = intent.getStringExtra("USER_SERIAL")
        val userName = intent.getStringExtra("USER_NAME")
        val userEmail = intent.getStringExtra("USER_EMAIL")

        tv_register_user.text = userName
        tv_register_email.text = userEmail

        rBtn_sale.setOnClickListener {
            Toast.makeText(this, "選択", Toast.LENGTH_SHORT).show()
            layout_sale.visibility = View.VISIBLE
        }

        rBtn_buy.setOnClickListener {
            layout_sale.visibility = View.GONE
        }

        btn_register_next.setOnClickListener {
            val radioId = rg_class.checkedRadioButtonId
            val radioButton = findViewById<RadioButton>(radioId)
            val radioIndex = rg_class.indexOfChild(radioButton)

            val getTextEmail = tv_register_email.text.toString()
            val getTextUser = tv_register_user.text.toString()
            var getTextClass = ""
            var getTextShopName = ""
            var getTextShopEx = ""
            var getTextShopAdd = ""
            var getTextShopImg = "https://"
            var setMsgSale = ""

            /** 住所から緯度経度取得 **/
            var gcoder: Geocoder? = Geocoder(this, Locale.getDefault())
            val maxResults = 1 // リターンする件数
            var lstAddr: List<Address?>
            var latitude = 0.0
            var longitude = 0.0
            var latLng: GeoPoint = GeoPoint(0.0 , 0.0)

            var dataSetUserMap: MutableMap<String, Any?>
            var dataSetShopMap: MutableMap<String, Any?>


            /*****************************************************************************/

            dataSetUserMap = mutableMapOf(
                "name" to getTextUser,
                "email" to getTextEmail,
                "attribute" to radioIndex,
                "serial" to userSerial
            )

            if (onRadioCheck(radioIndex)){
                if (radioIndex == 0){
                    getTextClass = "購入"
                }else{
                    getTextClass = "販売"
                    getTextShopName = et_register_shop_name.text.toString()
                    getTextShopEx = et_register_shop_ex.text.toString()
                    getTextShopAdd = et_register_shop_add.text.toString()
                    setMsgSale = "店舗名：${getTextShopName}\n" +
                            "店舗説明：${getTextShopEx}\n" +
                            "店舗住所：${getTextShopAdd}\n"

                    lstAddr = gcoder!!.getFromLocationName(et_register_shop_add.text.toString(), maxResults)

                    if (lstAddr != null && lstAddr.size > 0) {
                        // 緯度・経度取得
                        val address = lstAddr[0]!!
                        latitude = address.latitude
                        longitude = address.longitude

                        latLng = GeoPoint(latitude, longitude)

                        Log.d(TAG, "緯度: $latitude　経度: $longitude")
                    }

                }

                var setMsg = "以下の内容で登録しますが、よろしいですか？\n" +
                        "\n" +
                        "メールアドレス：${getTextEmail}\n" +
                        "ユーザー名：${getTextUser}\n" +
                        "利用区分：${getTextClass}\n" +
                        setMsgSale

                AlertDialog.Builder(this)
                    .setTitle("ご確認")
                    .setMessage(setMsg)
                    .setPositiveButton("OK") { dialog, which ->
                        Log.d(TAG, "入力Ok")

                        fs!!.collection("user")
                            .add(dataSetUserMap)
                            .addOnSuccessListener { user ->
                                Log.d(TAG, "====================ユーザー登録成功=====================")
                                Toast.makeText(this, "${user.id}", Toast.LENGTH_SHORT).show()

                                LoginData().userId = user.id
                                LoginData().userName = getTextUser
                                LoginData().userSerial = userSerial
                                LoginData().userAttribute = radioIndex

                                if (radioIndex == 1){
                                    /** ショップ登録あり **/
                                    fs!!.collection("shop")
                                        .add(
                                            mutableMapOf(
                                                "name" to getTextShopName,
                                                "description" to getTextShopEx,
                                                "address" to getTextShopAdd,
                                                "lat_lng" to latLng,
                                                "img" to getTextShopImg,
                                                "users" to user.id
                                            )
                                        )
                                        .addOnSuccessListener {shop ->
                                            Log.d(TAG, "====================ショップ登録成功=====================")
                                            Toast.makeText(this, "登録しました", Toast.LENGTH_SHORT).show()
                                            LoginData().shopId = shop.id
                                            LoginData().shopName = getTextShopName
                                            LoginData().description = getTextShopEx
                                            LoginData().shopAddress = getTextShopAdd
                                            LoginData().shopLatitude = latitude
                                            LoginData().shopLongitude = longitude
                                            LoginData().shopUserId = user.id
                                            val intent = Intent(this, ManagerActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        }
                                        .addOnFailureListener {
                                            Log.d(TAG, "====================ショップ登録失敗=====================")
                                            Toast.makeText(this, "登録に失敗しました", Toast.LENGTH_SHORT).show()
                                        }
                                }else{
                                    /** ショップ登録なし **/
                                    Toast.makeText(this, "登録しました", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, MapsActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }

                            }
                            .addOnFailureListener { e ->
                                Log.d(TAG, "====================登録失敗=====================\n" +
                                        "ERROR : $e")
                                Toast.makeText(this, "登録に失敗しました", Toast.LENGTH_SHORT).show()
                            }


                    }
                    .setNegativeButton("No") { dialog, which ->
                        Log.d(TAG, "入力No")
                    }
                    .show()

            }
        }

    }

    override fun onStart() {
        super.onStart()
    }

    private fun onRadioCheck(radioIndex: Int): Boolean{
        var valid = true

        var setNameErrorMsg = ""
        var setExErrorMsg = ""
        var setAddErrorMsg = ""

        if (radioIndex == 1){
            if (TextUtils.isEmpty(et_register_shop_name.text)){
                setNameErrorMsg = "店舗名が未入力です。"
                valid = false
            }

            if (TextUtils.isEmpty(et_register_shop_ex.text)){
                setExErrorMsg = "店舗説明が未入力です。"
                valid = false
            }

            if (TextUtils.isEmpty(et_register_shop_add.text)){
                setAddErrorMsg = "店舗住所が未入力です。"
                valid = false
            }
            tv_register_name_error.setText(setNameErrorMsg, TextView.BufferType.NORMAL)
            tv_register_ex_error.setText(setExErrorMsg, TextView.BufferType.NORMAL)
            tv_register_add_error.setText(setAddErrorMsg, TextView.BufferType.NORMAL)
        }

        return valid
    }
}
