package local.libra219.android.leftoverstome

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"
    private var fs: FirebaseFirestore? = null

    init {
        fs = FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_login_email.setOnClickListener {
            createSignInIntent()


        }

        btn_login_check.setOnClickListener {

            val user = FirebaseAuth.getInstance().currentUser
            if (user != null){
                Log.d(TAG, "USER : "+user.toString())

                user?.let {
                    // Name, email address, and profile photo Url
                    val name = user.displayName
                    val email = user.email
                    val photoUrl = user.photoUrl

                    // Check if user's email is verified
                    val emailVerified = user.isEmailVerified

                    // The user's ID, unique to the Firebase project. Do NOT use this value to
                    // authenticate with your backend server, if you have one. Use
                    // FirebaseUser.getToken() instead.
                    val uid = user.uid

                    Log.d(TAG, name.toString())
                    Log.d(TAG, email.toString())
                    Log.d(TAG, photoUrl.toString())
                    Log.d(TAG, emailVerified.toString())
                    Log.d(TAG, uid)
                }
            }else{
                Log.d(TAG, "================== NOOOOOOOOOOOOOOOOOOO =================")
            }
        }

        btn_login_out.setOnClickListener {
            AuthUI.getInstance()
                .signOut(this)
//                .addOnSuccessListener {
//                    Toast.makeText(this, "ログアウトしました。", Toast.LENGTH_SHORT).show()
//                }
                .addOnCompleteListener {
                    Toast.makeText(this, "ログアウトしました。", Toast.LENGTH_SHORT).show()
                }
        }

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


                Log.d(TAG, "============== LOGIN ID ===============")
                Toast.makeText(this, "IDcheck", Toast.LENGTH_SHORT).show()

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

                            }else{
                                Log.w(TAG, "=============================== query error  ${snapshot} =============================")
                            }
                        }
//                        .get()
//                        .addOnSuccessListener {
//                            Log.d(TAG, "========================== user ok ==========================")
//                            Log.d(TAG, "========================== ${it.documents} ==========================")
//
//                        }
//                        .addOnCanceledListener {
//                            Log.d(TAG, "========================== user no ==========================")
//
//                        }
                }

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Log.d("onActivityResult",response.toString())
            }
        }
    }
}
