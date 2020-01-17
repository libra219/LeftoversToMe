package local.libra219.android.leftoverstome

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_singup.*

class SingupActivity : AppCompatActivity() {
    //     FirebaseAuth インスタンス宣言
    private lateinit var auth: FirebaseAuth

    var TAG = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_singup)

//        FirebaseAuth インスタンス初期化
        auth = FirebaseAuth.getInstance()



//      新規登録
        btn_login_email.setOnClickListener {
            TAG = "createAccount"
            var email = et_singup_email.text.toString()
            var pass = et_singup_password.text.toString()
            Log.d(TAG, "singIn: $email")
            Log.d(TAG, "singIn: $pass")
            if (validateForm()){
                auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(this){task ->
                    if (task.isSuccessful){
                        Log.d(TAG, "createUserWithEmail:success")
                        Toast.makeText(
                            baseContext, "SignUp 成功",
                            Toast.LENGTH_SHORT
                        ).show()
                    }else{
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext, "SignUp 失敗",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser

    }



//        入力チェック
    private fun validateForm(): Boolean{
        var valid = true
        var emailMes = ""
        var passMes = ""

        if (TextUtils.isEmpty(et_singup_email.text)){
            emailMes = "メールアドレスが未入力です。"
            valid = false
        }

        if (TextUtils.isEmpty(et_singup_password.text)){
            passMes = "パスワードが未入力です。"
            valid = false
        }


        if (emailMes != "" || passMes != ""){
            AlertDialog.Builder(this)
                .setTitle("エラー")
                .setMessage(emailMes + "\n" + passMes)
                .setPositiveButton("OK", null)
                .show()
        }

        return valid
    }


}
