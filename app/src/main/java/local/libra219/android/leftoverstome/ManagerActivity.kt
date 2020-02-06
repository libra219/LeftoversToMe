package local.libra219.android.leftoverstome

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.firestore.FirebaseFirestore

class ManagerActivity : AppCompatActivity() {

    private val TAG = "ManagerActivity"
    /** Firebase **/
    private var fs: FirebaseFirestore? = null
    private lateinit var dataStore: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager)

        fs = FirebaseFirestore.getInstance()
        dataStore = getSharedPreferences("DataStore", Context.MODE_PRIVATE)

        val userId =  dataStore.getString("UserId", "")

        /** shop情報取得 **/
        fs!!.collection("shop")
            .whereEqualTo("users",userId)
            .addSnapshotListener { snapshot, e ->
                if(e != null){
                    Log.w(TAG, "======================== ERROR =======================")
                    return@addSnapshotListener
                }

                if (snapshot != null){
                    for (doc in snapshot){
                        val editor = dataStore.edit()
                        editor.putString("shopId", doc.id)
                        editor.putString("shopName", doc["name"].toString())
                        editor.putString("shopDescription", doc["name"].toString())
                        editor.putString("shopAddress", doc["description"].toString())
                        editor.putString("shopUserId", doc["users"].toString())
                        editor.apply()
                    }
                }

            }

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}
