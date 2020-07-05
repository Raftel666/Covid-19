package rafael.gaeta.covid_19

import android.app.DownloadManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import java.text.DecimalFormat
import java.text.NumberFormat

class MainActivity : AppCompatActivity() {

    var adaptadorPaises: PaisesAdaptador? = null
    var listaPaises: ArrayList<Pais>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listaPaises = ArrayList<Pais>()
        adaptadorPaises = PaisesAdaptador(listaPaises!!, this)
        miRecyclerCovid.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.VERTICAL,
            false
        )
        miRecyclerCovid.adapter = adaptadorPaises

        val queue = Volley.newRequestQueue(this)
        val url = "https://wuhan-coronavirus-api.laeyoung.endpoint.ainize.ai/jhu-edu/latest"

        val peticionDatosCovid =
            JsonArrayRequest(Request.Method.GET, url, null, Response.Listener { response ->
                for (index in 0 until response.length()) {
                    try {
                        val paisJson = response.getJSONObject(index)
                        val nombrePais = paisJson.getString("countryregion")
                        var numeroConfirmados = paisJson.getInt("confirmed")
                        val numeroMuertos = paisJson.getInt("deaths")
                        val numeroRecuperados = paisJson.getInt("recovered")
                        val countryCodeJson = paisJson.getJSONObject("countrycode")
                        val codigoPais = countryCodeJson.getString("iso2")
                        //Objeto Kotlin
                        val paisIndividual = Pais(
                            nombrePais,
                            numeroConfirmados,
                            numeroMuertos,
                            numeroRecuperados,
                            codigoPais
                        )
                        listaPaises!!.add(paisIndividual)
                    } catch (e: JSONException) {
                        Log.wtf("JsonError", e.localizedMessage)
                    }
                }
                listaPaises!!.sortByDescending { it.confirmados }
                adaptadorPaises!!.notifyDataSetChanged()
            },
                Response.ErrorListener { error ->
                    Log.e("error_volley", error.localizedMessage)
                })
        queue.add(peticionDatosCovid)
    }
}