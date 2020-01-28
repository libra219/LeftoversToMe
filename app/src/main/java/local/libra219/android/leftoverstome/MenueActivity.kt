package local.libra219.android.leftoverstome


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import kotlinx.android.synthetic.main.activity_menue.*
import java.util.*
import kotlin.collections.HashMap
import kotlin.random.Random


class MenueActivity : AppCompatActivity() {
    private val tagName = "MenuActivity"

    private val moshi: Moshi =Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    /** Create StorageReference */
    private var mStorageRef: StorageReference? = null
    private var firebaseDatabase: FirebaseFirestore? = null
    private var fDatabase: FirebaseDatabase? = null

    private var _List: List<Map<String, String>>? = null


    /** List **/
    private var mRecyclerView: RecyclerView? = null
    private val mAdapter: RecyclerView.Adapter<*>? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null
    private val mDataset: ArrayList<String>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menue)
        Log.d(tagName, "===================================onCreate============================================")

        val text = intent.getStringExtra("SHOP_NAME")
        Log.d(tagName,text)
        val sampleShopName: String = "ショップ名：4号店"


        mStorageRef = FirebaseStorage.getInstance().getReference()

//        firebaseのデータベース初期化
        firebaseDatabase = FirebaseFirestore.getInstance()

        fDatabase = FirebaseDatabase.getInstance()

        rv_menu_list.setHasFixedSize(true)
        mLayoutManager = LinearLayoutManager(this)
        mRecyclerView?.layoutManager = mLayoutManager


        getShopInfo(sampleShopName)

    }


    data class setData(var title: String, var shops: Map<Any, Any>, var attribute: String, val serial: String)

    override fun onStart() {
        super.onStart()
        val TAG = "onStart"
        Log.d(tagName, "===================================onStart============================================")

    }



    /**
     * 画像の取得
     */
    private fun getStorageImg(){
//        val tag = "getStorageImg"
//        Log.d(tag, "start")
//
////        これアップロード用
//        val storage = FirebaseStorage.getInstance()
//        Log.d(tag, storage.toString())
////        val storageRef = storage.getReferenceFromUrl("gs://leftoversmap.appspot.com/image")
////        val dir = storageRef.child("")
////        val task = dir.putFile("https://s")
//
//        // FirebaseUI で表示
//        val storageReference = storage.reference
//        Log.d(tag, storageReference.toString())
//        // 表示する場所
//        val imageView = findViewById<ImageView>(R.id.img_view_shop_top)

//        storageReference.downloadUrl.addOnSuccessListener{ Uri ->
//            Log.d(tag, Uri.toString())
//            Glide
//                .with(this)
//                .load(Uri) // pass the image url
//                .into(imageView)
//        }
//
//        val test = storageReference.child("image/kantoku.jpg")
//        val test2 = test.downloadUrl
//
//        Log.d(tag, "test:" + test.toString())
//        Log.d(tag, "test2:" + test2.toString())
//
//        try {
//            Log.d(tag, "YEEEEEEEEEEEEEEEEEEEES")
//            Glide.with(this /* context */)
//                .load(test2)
//                .into(imageView)
//        }catch (e:Exception){
//            Log.d(tag, "NOOOOOOOOOOOOOOO")
//        }


//        storageReference.child("image/kantoku.jpg").downloadUrl
//            .addOnSuccessListener { uri ->
//                Log.d("storage", "ok")
//                Glide.with(this)
//                    .load(uri)
//                    .into(imageView)
//            }.addOnFailureListener {
//                // Handle any errors
//                Log.d("storage", "Noooooooooooooooooooo")
//            }



//        set
//        Glide.with(this /* context */)
//            .load(storageReference)
//            .into(imageView)
//        Log.d(tag, storageReference.toString())

//        val localFile = File.createTempFile("tmp_images", "jpg")
//
//        storageRef.getFile(localFile).addOnSuccessListener { task ->
//            Log.d(tag, "成功")
//
//        }


//        val addOnFailureListener =
//            storageRef.downloadUrl.addOnSuccessListener{ Uri ->
//                // Got the download URL for 'users/me/profile.png'
//
//            }.addOnFailureListener {
//                // Handle any errors
//            }

    }




    /**
     * ショップ情報の取得
     */
    private fun getShopInfo(searchName:String){
        val TAG = "getShopInfo"
        Log.d(tagName, "======================getShopInfo start====================")
        var shopData: setData
        var userData: setData
        var tagNa = "fDatabaseRef"
        var shopDocList: MutableMap<String, Map<String, String>> = mutableMapOf()
        var shopDataList = mutableListOf<Any>()
        val lvSetList: MutableList<Map<String, String>> = ArrayList()
        var lvSetMap: MutableMap<String, String> = HashMap()

        var getData: MutableList<String?> = mutableListOf()
        var shopLists: MutableList<List<String>> = mutableListOf()

        var shopId: Int = 0
        var shopTitleName: String = ""
        var shopDescription: String = ""
        var shopUserId: Int = 0
        var shopImg: String = ""
        var shopLatLng: GeoPoint = GeoPoint(0.0, 0.0)

//        Databaseの取得。SELECT * FROM user WHERE id = 0みたいに取れる
        firebaseDatabase?.collection("shop")?.whereEqualTo("users", 0)
            ?.get()
            ?.addOnSuccessListener { result ->
                if (result != null){
                    for (doc in result){
                        Log.d(TAG, "${doc.id} => ${doc.data}")
                        shopData = setData(doc.data?.getValue("name") as String,
                            doc.data?.getValue("shops") as Map<Any, Any>,
                            doc.data?.getValue("attribute") as String,
                            doc.data?.getValue("serial") as String
                        )
                        shopDataList.add(shopData)
                        Log.d(TAG, "shopTitle:"+shopData.title)

                    }
//                    Log.d("getShopInfo", "DocumentSnapshot data: ${result}")
//                    Log.d("getShopInfo", "DocumentSnapshot data: ${result.data?.get("name")}")
//                    Log.d("getShopInfo", "DocumentSnapshot data: ${result.data?.get("shoops")}")
//                    infoData.shopTitle = result.data?.get("name").toString()

                } else {
                    Log.d("getShopInfo", "No such document")
                    Toast.makeText(baseContext, "データの取得に失敗\nE:Mg001", Toast.LENGTH_LONG).show()
                }
            }
            ?.addOnFailureListener { exception ->
                Log.w("getShopInfo", "Error getting documents.", exception)
                Toast.makeText(baseContext, "データの取得に失敗\nE:Mg002", Toast.LENGTH_LONG).show()
            }


//        販売情報
//        変数初期化




        Log.d(tagNa, "========================================================================")
        /** ショップ情報取得 **/
        firebaseDatabase?.collection("shop")
            ?.whereEqualTo("name", searchName)
            ?.get()
            ?.addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "=== shop === ${document.id} => ${document.data}")
//                    Log.d(TAG, "${document.id} => ${document.get("shops")}")
                    shopId = Integer.parseInt(document.id)
                    shopTitleName = document.get("name").toString()
                    shopDescription = document.get("description").toString()
                    Log.d(TAG, shopId.toString())
                }
                /** 商品情報取得 **/
                firebaseDatabase?.collection("item")
                    ?.whereEqualTo("shop_id", shopId)
                    ?.get()
                    ?.addOnSuccessListener{ documents ->
                        for (document in documents){
                            Log.d(TAG, "=== item === ${document.id} => ${document.data}")
                            shopDocList.put(
                                document.id,
                                mapOf("shop_id" to "${document.get("shop_id")}",
                                    "name" to "${document.get("name")}",
                                    "explanation" to "${document.get("explanation")}",
                                    "price" to "${document.get("price")}",
                                    "sale" to "${document.get("sale")}",
                                    "img" to "${document.get("img")}"
                                    ))
                            shopDataList.add(shopDocList)
                        }
                        for (list in shopDocList) {
                            Log.d(TAG, "=== List === ${list.value["shop_id"]}")
                            var i = 0
                            getData = mutableListOf()
                            shopLists.add((listOf(list.value["shop_id"].toString(), list.value["name"].toString(), list.value["explanation"].toString(), list.value["price"].toString(), list.key)))
                            i.inc()
                            lvSetMap = HashMap()
                        }


                        val list1: MutableList<Map<String, String>> = ArrayList()
                        var map1: MutableMap<String, String>
                        var i = 0
                        for (list in shopLists){
                            Log.d("shopTitle", shopLists[i][1])
                            map1 = java.util.HashMap()
                            map1["name"] = shopLists[i][1]
                            map1["ex"] = shopLists[i][2]
                            Log.d("TAG", map1["name"].toString())
                            Log.d("TAG", map1["ex"].toString())
                            list1.add(map1)
                            i++
                        }
                        _List = list1


                        val from = arrayOf("name", "ex")
                        val to = intArrayOf(android.R.id.text1, android.R.id.text2)
                        val adapter = SimpleAdapter(
                            applicationContext,
                            _List,
                            android.R.layout.simple_list_item_2,
                            from,
                            to
                        )
                        lv_menu_list.adapter = adapter

//                        タップ処理
                        lv_menu_list.setOnItemClickListener { parent, view, position, id ->
                            Toast.makeText(this, "${shopLists[position]} + $id", Toast.LENGTH_SHORT).show()
                            var intent = Intent(this, ProductActivity::class.java)
                            intent.putExtra("PRO_TITLE", shopLists[position][1])
                            intent.putExtra("PRO_EX", shopLists[position][2])
                            intent.putExtra("PRO_PRICE", shopLists[position][3])
                            intent.putExtra("PRO_KEY", shopLists[position][4])

                            startActivity(intent)
                        }




















//
//                        val texts = arrayOf("abc ", "bcd", "cde", "def", "efg",
//                            "fgh", "ghi", "hij", "ijk", "jkl", "klm")
//                        val listView = ListView(this)
//                        setContentView(R.layout.activity_menue)
//
//                        // simple_list_item_1 は、 もともと用意されている定義済みのレイアウトファイルのID
//                        val arrayAdapter = ArrayAdapter(this,
//                            android.R.layout.simple_list_item_1, texts)
//
//
//
//                        lv_menu_list.adapter=arrayAdapter
//
//
//                        lv_menu_list.setOnItemClickListener { parent, view, position, id ->
//                            val title = view.findViewById<TextView>(android.R.id.text1).text
//                            Toast.makeText(this, "$title: $id", Toast.LENGTH_SHORT).show()
//                        }
                        tv_menue_title.text = shopTitleName
                        tv_menue_sabtitle.text = shopDescription

                    }
                    ?.addOnFailureListener { exception ->
                        Log.w(TAG, "エラー", exception)
                    }
            }
            ?.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

        Log.d(tagNa, "==============================ITEM======================================")
        Log.d(TAG, shopId.toString())






    }


    private fun getShopInfoAll(){
        Log.d(tagName, "======================getShopInfoAll start====================")
//        Databaseのすべて取得。SELECT * FROM userみたいに取れる
        firebaseDatabase?.collection("user")
            ?.get()
            ?.addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("getShopInfoAll", "${document.id} => ${document.data}")
                }
            }
            ?.addOnFailureListener { exception ->
                Log.w("getShopInfoAll", "Error getting documents.", exception)
                Toast.makeText(baseContext, "データの取得に失敗\nE:Mg003", Toast.LENGTH_LONG).show()
            }
    }

    private fun setShopInfo(){
        Log.d(tagName, "======================setShopInfo start====================")
        val TAG = "setShopInfo"

        val random = Random
        for (i in 1..10) {
            var lat = random.nextDouble(34.5, 34.9)
            var lng = random.nextDouble(135.0, 136.0)
            val data1 = hashMapOf(
                "name" to "ショップ名：${i}号店",
                "description" to "サンプル${i}",
                "img" to "https://",
                "users" to i,
                "lat_lng" to GeoPoint( lat, lng)
            )
            val itemData1 = hashMapOf(
                "name" to "商品サンプル${i}",
                "explanation" to "説明欄サンプル${i}",
                "img" to "https://",
                "price" to i * 100,
                "sale" to 90 - i
            )

            val itemListData1 = hashMapOf(
                "shop_id" to 4,
                "item_id" to i
            )

            firebaseDatabase?.collection("item_list")?.document("${i}")
                ?.set(itemListData1)
                ?.addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                ?.addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        }
    }



    /**
     * リストがタップされたときのリスナクラス。
     */
    private class ListItemClickListenter : AdapterView.OnItemClickListener {
        override fun onItemClick(
            parent: AdapterView<*>?,
            view: View,
            position: Int,
            id: Long
        ) {
//            val item: Map<String, String> = _List.get(position)
//            val url = item["url"]
        }
    }



    /**
     * 再描画
     */
    fun reload() {
        val intent = intent
        overridePendingTransition(0, 0)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
    }
}