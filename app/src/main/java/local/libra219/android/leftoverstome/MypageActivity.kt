package local.libra219.android.leftoverstome

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MypageActivity : AppCompatActivity() {
    private val texts = arrayOf("abc ", "bcd", "cde", "def", "efg",
        "fgh", "ghi", "hij", "ijk", "jkl", "klm")

    /**
     * リストビューに表示させるリストデータ。
     */
    private var _list: List<Map<String, String>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        _list = createList()

        val lvSiteList = findViewById<ListView>(R.id.lvSiteList)
        val from = arrayOf("name", "url")
        val to = intArrayOf(android.R.id.text1, android.R.id.text2)
        val adapter = SimpleAdapter(
            applicationContext,
            _list,
            android.R.layout.simple_list_item_2,
            from,
            to
        )
        lvSiteList.adapter = adapter
        lvSiteList.onItemClickListener = ListItemClickListenter()
    }

    private fun createList(): List<Map<String, String>>? {
        val list: MutableList<Map<String, String>> =
            ArrayList()
        var map: MutableMap<String, String> =
            HashMap()
        for (i in 1..10){
            map = HashMap()
            map["name"] = i.toString()
            map["url"] = "$i:URL"
            Log.d("TAG", map["name"].toString())
            Log.d("TAG", map["url"].toString())
            list.add(map)
        }
        map["name"] = "@IT"
        map["url"] = "https://www.atmarkit.co.jp/"
        list.add(map)
        map = HashMap()
        map["name"] = "CodeZine"
        map["url"] = "https://codezine.jp/"
        list.add(map)
        map = HashMap()
        map["name"] = "EnterpriseZine"
        map["url"] = "https://enterprisezine.jp/"
        list.add(map)
        map = HashMap()
        map["name"] = "gihyo.jp"
        map["url"] = "https://gihyo.jp/"
        list.add(map)
        map = HashMap()
        map["name"] = "ITmediaエンタープライズ"
        map["url"] = "https://www.itmedia.co.jp/enterprise/"
        list.add(map)
        map = HashMap()
        map["name"] = "日経 xTECH"
        map["url"] = "https://tech.nikkeibp.co.jp/"
        list.add(map)
        return list
    }

    /**
     * リストがタップされたときのリスナクラス。
     */
    private class ListItemClickListenter : OnItemClickListener {
        override fun onItemClick(
            parent: AdapterView<*>?,
            view: View,
            position: Int,
            id: Long
        ) {
//            val item: Map<String, String> =
//            val url = item["url"]
//            val uri = Uri.parse(url)
        }
    }
}
