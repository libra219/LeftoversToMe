package local.libra219.android.leftoverstome


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_start_top.*



class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private lateinit var auth: FirebaseAuth

    private var fs: FirebaseFirestore? = null

    init {
        fs = FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_top)


        val btnIntent = findViewById<Button>(R.id.btn_top_registration)
        btnIntent.setOnClickListener (object : View.OnClickListener {
            override fun onClick(v: View?) {
                //3.Intentクラスのオブジェクトを生成。
                val intent = Intent(this@MainActivity, SingupActivity::class.java)
                //生成したオブジェクトを引数に画面を起動！
                startActivity(intent)
            }
        })

        btn_top_login.setOnClickListener {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        btn_top_map.setOnClickListener {
            val intent = Intent(this@MainActivity, MapsActivity::class.java)
            startActivity(intent)
        }

        btn_top_manager.setOnClickListener {
            val intent = Intent(this@MainActivity, ManagerActivity::class.java)
            startActivity(intent)
        }

        btn_start_login.setOnClickListener {
            createSignInIntent()
        }

        auth = FirebaseAuth.getInstance()
    }

    public override fun onStart() {
        super.onStart()
//         Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser

    }

    private fun createSignInIntent() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
//            AuthUI.IdpConfig.FacebookBuilder().build(),
//            AuthUI.IdpConfig.TwitterBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100){
            val response = IdpResponse.fromResultIntent(data)
            Log.d("onActivityResult","$response")

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                Log.d("onActivityResult","ok $user")
                Log.d("onActivityResult","${user?.displayName} ")

                val userSerial = user?.uid
                val userName = user?.displayName
                val userEmail = user?.email
                Log.d(TAG, "========================== LOGIN ID: $userSerial =======================")

                if (userSerial != null){
                    fs!!.collection("user")
                        .whereEqualTo("serial", "$userSerial")
                        .addSnapshotListener { snapshot, exception ->
                            if (exception != null){
                                Log.d(TAG, "====================== ERROR ==========================")
                                return@addSnapshotListener
                            }

                            if (snapshot != null && snapshot.documents.size == 0){
                                // 新規登録
                                Log.d(TAG, "=============================== ${snapshot.documents.size} =============================")
                                val intent = Intent(this, LoginRegisterActivity::class.java)
                                intent.putExtra("USER_SERIAL", userSerial)
                                intent.putExtra("USER_NAME", userName)
                                intent.putExtra("USER_EMAIL", userEmail)
                                startActivity(intent)

                            }else if(snapshot != null && snapshot.documents.size != 0){
                                /** 登録済み **/
                                Log.d(TAG, "=============================== ${snapshot.documents.size} =============================")

                                for (doc in snapshot){
                                    Log.d(TAG, "=============================== dco : ${doc} =============================")
                                    Log.d(TAG, "=============================== doc ID :${doc.id} =============================")
                                    Log.d(TAG, "=============================== doc ATTRIBUTE :${doc["attribute"]} =============================")

                                    LoginData().userId = doc.id
                                    LoginData().userSerial = doc["serial"].toString()
                                    LoginData().userName = doc["name"].toString()
                                    LoginData().userAttribute = doc["attribute"].toString().toInt()
                                    LoginData().userEmail = doc["email"].toString()

                                    if (doc["attribute"].toString() == "0"){
                                        /** 購入 **/
                                        Log.d(TAG, "=============================== ユーザーログイン =============================")
                                        val intent = Intent(this, MapsActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }else if (doc["attribute"].toString() == "1"){
                                        /** 販売 **/
                                        Log.d(TAG, "=============================== 販売ログイン =============================")
                                        val intent = Intent(this, ManagerActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }else{
                                        Log.d(TAG, "=============================== イレギュラーログイン =============================")
                                        Toast.makeText(this, "このユーザーのログインはできません。", Toast.LENGTH_SHORT).show()

                                    }
                                }
                            }else{
                                Log.w(TAG, "=============================== query error  ${snapshot} =============================")
                            }
                        }
                }

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Log.d("onActivityResult", response.toString())
            }
        }
    }




}
