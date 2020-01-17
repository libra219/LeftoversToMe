package local.libra219.android.leftoverstome


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_start_top.*



class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

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

        auth = FirebaseAuth.getInstance()
    }

    public override fun onStart() {
        super.onStart()
//         Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser

    }





}
