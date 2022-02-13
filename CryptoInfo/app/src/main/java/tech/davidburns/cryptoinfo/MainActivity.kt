package tech.davidburns.cryptoinfo

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var cryptoList : ArrayList<Pair<String, String>>
    private lateinit var cryptoRecyclerView : RecyclerView
    private lateinit var fabConvert : FloatingActionButton
    private lateinit var fabAnimateIn : Animation
    private lateinit var fabAnimateOut : Animation
    private lateinit var cryptoListAdapter : CryptoListAdapter
    private lateinit var tracker: SelectionTracker<Long>
    private var previousSelectionSize = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cryptoList = ArrayList()
        cryptoListAdapter = CryptoListAdapter(cryptoList)

        cryptoRecyclerView = findViewById(R.id.crypto_recycler_view)
        fabConvert = findViewById(R.id.fab_compute)
        fabAnimateIn = AnimationUtils.loadAnimation(this,R.anim.button_open)
        fabAnimateOut = AnimationUtils.loadAnimation(this,R.anim.button_close)

        cryptoRecyclerView.adapter = cryptoListAdapter

        tracker = SelectionTracker.Builder<Long>(
            "cryptoSelection",
            cryptoRecyclerView,
            CryptoItemKeyProvider(cryptoRecyclerView),
            CryptoItemDetailsLookup(cryptoRecyclerView),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        tracker.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    val items = tracker.selection!!.size()
                    if (items >= 2 && previousSelectionSize < 2) {
                        fabConvert.visibility = View.VISIBLE
                        fabConvert.startAnimation(fabAnimateIn)
                    } else if (items < 2 && previousSelectionSize >= 2) {
                        fabConvert.startAnimation(fabAnimateOut)
                    }
                    previousSelectionSize = items
                }
            })
        cryptoListAdapter.tracker = tracker

        fabConvert.setOnClickListener {
            val intent = Intent(this, CryptoDetailActivity::class.java)
            val bundle = Bundle()
            val selectedList : ArrayList<String> = ArrayList()
            tracker.selection!!.forEach { i -> selectedList.add(cryptoList[i.toInt()].first) }
            bundle.putStringArrayList("crypto_list", selectedList)
            Log.e("MainActivity", "this happens")
            startActivity(intent)
        }

        callAPI()
    }

    private fun callAPI() {
        val queue = Volley.newRequestQueue(this)
        val url = "https://apiv2.bitcoinaverage.com/info/indices/names/"

        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener<JSONObject> { response ->
                Log.e("MainActivity", response.toString())
                val cryptoNames =
                    Gson().fromJson<CryptoNames>(response.toString(), CryptoNames::class.java)
                for ((key, value) in cryptoNames.crypto.orEmpty()) {
                    cryptoList.add(Pair(key, value))
                    //Log.e("MainActivity", "$key $value")
                }
                cryptoListAdapter.notifyItemRangeInserted(0, cryptoList.size)
            },
            Response.ErrorListener{ error ->
                Log.e("MainActivity", error.message )
            }) {

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json";
                headers["x-ba-key"] = "YmVmZTY1MmMyOWNhNDViYWI4ZjA5NjA2ODI0ODMxZDk"
                return headers
            }
        }

        queue.add(jsonObjectRequest)
    }
}