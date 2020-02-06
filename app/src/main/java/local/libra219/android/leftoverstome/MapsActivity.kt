package local.libra219.android.leftoverstome

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import java.util.ArrayList


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener, GoogleMap.OnInfoWindowClickListener
{

    private lateinit var mMap: GoogleMap

    private var myLocationManager: LocationManager? = null
    private var firebaseDatabase: FirebaseFirestore? = null

    private var manager: LocationManager? = null
    private var dbllat: Double = 34.699
    private var dbllot: Double = 135.492

    private var markerSetList: MutableList<Map<String, Any?>> = ArrayList()
    private var markerSetMap: MutableMap<String, Any?> = hashMapOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        /** Firebase接続 **/
        firebaseDatabase = FirebaseFirestore.getInstance()



        manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

    }

    override fun onResume() {
        super.onResume()

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }
        manager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1F, this)
        manager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 1.0F, this)
    }

    override fun onStop() {
        super.onStop()

        if (manager != null){
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            manager?.removeUpdates(this)
        }
    }

    //メニュー表示
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        val inflater = menuInflater
        inflater.inflate(R.menu.btn_navigation_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.btn_menu_home -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.btn_menu_favorites -> {
                val intent = Intent(this, FavoritesActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.btn_menu_mypage -> {
                val intent = Intent(this, MypageActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    // 現在地取得(一定間隔）
     override fun onLocationChanged(location: Location): Unit {
         val text = "緯度：" + location.latitude.toString() + "経度：" + location.longitude
//        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
         dbllat = location.latitude
         dbllot = location.latitude
     }

     override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

     override fun onProviderEnabled(provider: String?) {}

     override fun onProviderDisabled(provider: String?) {}
    // 現在地の取得はLocationServices.getFusedLocationProviderClientが便利なため使わない、あと触らぬ神に祟りなし

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    override fun onMapReady(googleMap: GoogleMap) {
        val TAG = "onMapReady"
        Log.d("d", "MapReady")

        mMap = googleMap

//        マップの機能を有効化
        mMap.isMyLocationEnabled = true //mMap.setMyLocationEnabled(true)
        mMap.uiSettings.isZoomControlsEnabled = true

        //現在地を取得してズームする。
        LocationServices.getFusedLocationProviderClient(this).let { client ->
            client.lastLocation.addOnCompleteListener(this) { task ->
                if (task.isSuccessful && task.result != null) {
                    Log.d("if", "true")
                    // 位置情報を取得できた場合
                    // task.result.latitude、task.result.longitude で位置情報を取得
                    val myLocation = LatLng(task.result!!.latitude, task.result!!.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15f))
                } else {
                    // 端末の位置情報をOFFにしているなどで、位置情報が取得できなかった場合
                    Log.d("if", "false")
                    // 最初の位置を決め打ちします。ここでは東京駅にしてます。
                    val tokyo = LatLng(35.681298, 139.766247)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tokyo, 15f))
                }
            }
        }

        firebaseDatabase?.collection("shop")
            ?.get()
            ?.addOnSuccessListener { documents ->
                for (document in documents){
                    Log.d(TAG, "=== get shop list === ${document.id} -> ${document.data}" )
                    markerSetMap = hashMapOf()
                    markerSetMap["primary_key"] = document.id
                    markerSetMap["name"] = document.data["name"].toString()
                    markerSetMap["description"] = document.data["description"].toString()
                    markerSetMap["user_id"] = document.data["users"].toString()
                    markerSetMap["img"] = document.data["img"].toString()
                    var latLng: GeoPoint = document.data["lat_lng"] as GeoPoint
                    markerSetMap["lat"] = latLng.latitude
                    markerSetMap["lng"] = latLng.longitude

                    markerSetList.add(markerSetMap)
                }

                /** マーカー生成 **/
                for (list in markerSetList){
                    var latLng = LatLng(list["lat"] as Double, list["lng"] as Double)
                    var pinMap = mMap.addMarker(MarkerOptions()
                        .position(latLng)
                        .title(list["name"].toString())

                    )
                    pinMap.tag = list["primary_key"]
                    Log.d(TAG, "=== get shop list === ${pinMap.tag}" )
                }

            }
            ?.addOnFailureListener { exception ->
                Log.w("MapReady", "Error getting documents.", exception)
                Toast.makeText(baseContext, "データの取得に失敗\nE:Mg002", Toast.LENGTH_LONG).show()
            }

        // マーカーがクリックされた時の処理
        mMap.setOnMarkerClickListener { marker ->
            // タップされたマーカーのタイトルを取得
            val name = marker.title.toString() + marker.tag

//            Toast.makeText(this, name, Toast.LENGTH_SHORT).show()
//
            false
        }

//        マーカーインフォをカスタマイズ
//        mMap.setOnInfoWindowClickListener(object : GoogleMap.OnInfoWindowClickListener {
//            override fun onInfoWindowClick(maker: Marker) {
//                Toast.makeText(baseContext, "onInfo", Toast.LENGTH_SHORT).show()
//                val intent = Intent(this@MapsActivity, MenueActivity::class.java)
////                dialogs(p0?.title.toString())
//            }
//        } )

        mMap.setOnInfoWindowClickListener { marker ->
            val tag = marker.tag.toString()
//            Toast.makeText(this, "=== onInfo ==="+tag, Toast.LENGTH_SHORT).show()

            Log.d("OnInfo", tag)
            val intent = Intent(this, MenueActivity::class.java)
            intent.putExtra("SHOP_TAG", tag)

            startActivity(intent)
        }


//        val cUpdate = CameraUpdateFactory.newLatLngZoom(
//            LatLng(35.68, 139.76), 12f
//        )
//        mMap.moveCamera(cUpdate)

//        if (dbllat != 0.0 && dbllot != 0.0){
//            var CurrentLocation = CameraUpdateFactory.newLatLngZoom(
//                LatLng(dbllat, dbllot)
//                , 12f
//            )
//            mMap.moveCamera(CurrentLocation)
//        }

    }

//    ボトムシートを表示
    private fun dialogs(shopTitle: String): Boolean {
        Log.d("dialogs", shopTitle)
        val dialog = ItemListDialogFragment.newInstance(1)
        dialog.setTextView(shopTitle)

        dialog.show(supportFragmentManager, dialog.tag)
        return true
    }

//    マーカーのInfoをクリックされたときの処理
    override fun onInfoWindowClick(maker: Marker) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        Toast.makeText(this, "onInfo", Toast.LENGTH_SHORT).show()
        Log.d("onInfoWindowClick", maker.toString())
        this.dialogs(maker.title.toString())
    }

    //    InfoWindowをカスタマイズ
    class StationInfoWindowAdapter(private val context: Context): GoogleMap.InfoWindowAdapter{

        override fun getInfoContents(maker: Marker): View? = null
        override fun getInfoWindow(maker: Marker): View? = null //setWindow(marker)

        private fun setWindow(maker: Marker): View =
            LayoutInflater.from(context).inflate(R.layout.map_info_layout, null).apply {
                findViewById<TextView>(R.id.marker_infowindow_title).text = maker.title
                findViewById<TextView>(R.id.marker_infowindow_snippet).text = maker.snippet
            }
    }




}
