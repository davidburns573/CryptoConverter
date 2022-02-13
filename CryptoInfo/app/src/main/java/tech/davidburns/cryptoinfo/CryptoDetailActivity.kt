package tech.davidburns.cryptoinfo

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class CryptoDetailActivity : AppCompatActivity(){
    private lateinit var txtCryptoTitle : TextView
    private lateinit var cryptoTitle: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crypto)

        val bundle = intent.extras
        //cryptoTitle = bundle!!["name"] as String
        txtCryptoTitle = findViewById(R.id.txt_crypto_title)
        //txtCryptoTitle.text = cryptoTitle

        //APICall(bundle["acr"] as String)
    }

    private fun APICall(symbol : String) {
        val queue = Volley.newRequestQueue(this)
//        val url = "https://apiv2.bitcoinaverage.com/indices/crypto/ticker/$symbol"
        val url = "https://apiv2.bitcoinaverage.com/indices/global/ticker/all?crypto=BTC"
        Log.e("CryptoDetailActivity", url)

        val jsonObjectRequest = object : JsonObjectRequest(
            Method.GET, url, null,
            Response.Listener<JSONObject> { response ->
                Log.e("CryptoDetailActivity", response.toString())
            },
            Response.ErrorListener{ error ->
                Log.e("CryptoDetailActivity", error.message )
            }) {

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["x-ba-key"] = "YmVmZTY1MmMyOWNhNDViYWI4ZjA5NjA2ODI0ODMxZDk"
                return headers
            }
        }

        queue.add(jsonObjectRequest)
    }
}