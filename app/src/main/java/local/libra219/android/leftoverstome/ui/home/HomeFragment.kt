package local.libra219.android.leftoverstome.ui.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_home.*
import local.libra219.android.leftoverstome.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    private val TAG = "HomeFragment"

    private lateinit var dataStore: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "========================onCreateView================================")

        // SharedPreferencesインスタンスを生成
        dataStore = this.context!!.getSharedPreferences("DataStore", Context.MODE_PRIVATE)

        Log.d(TAG, "==================== ログイン ${dataStore.all} =================")



        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        val linearLayoutFrag1: LinearLayout = root.findViewById(R.id.frag_layout1)
        val linearLayoutFrag2: LinearLayout = root.findViewById(R.id.frag_layout2)

        homeViewModel.text.observe(this, Observer {
            textView.text = it
            linearLayoutFrag1.setOnClickListener {
                val intent = Intent(this.context, GoodsRegisterActivity::class.java)
                startActivity(intent)
            }
            linearLayoutFrag2.setOnClickListener {
                val intent = Intent(this.context, ManagerShopInfoActivity::class.java)
                startActivity(intent)
            }
            frag_layout3.setOnClickListener {
                Log.v(TAG, "============================")
            }
        })

        return root
    }




}