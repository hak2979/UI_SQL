package com.example.ui

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.json.JSONException
import kotlin.math.exp

class Search_results : AppCompatActivity() {
    private var name_gigs= arrayOf("John Cooper","Martin Wat","Emma","Wills")
    private var price_gigs= arrayOf("$1500/Session","$500/Session","$109/Session","$80/Session",)
    private var Experience_gigs= arrayOf("UX Designer","Lead - Technolgy","Lead Corporation","@Meta")
    private var availible_gigs= arrayOf(true,false,false,false)
    private var categories= arrayOf("All","Education","Entreprenuership","Personal Growth","Caree","Etaaa")
    private var lik= arrayOf(false,false,false,false)
    private lateinit var recyclerview: RecyclerView
    private lateinit var mUser: MutableList<gig_d>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

        mUser= mutableListOf()
        recyclerview=findViewById(R.id.recycler_view_gigs)
        recyclerview.layoutManager= LinearLayoutManager(this)
        fetchGigsFromFirestore()
        val searchEditText = findViewById<EditText>(R.id.search_view)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                (recyclerview.adapter as? Search_result_adapte)?.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

    }
    fun Home_Press(view: View)
    {
        val intent=Intent(this,Home::class.java)
        startActivity(intent)
        finish()
    }
    fun Search_interface(view: View)
    {
        finish()
    }
    fun end_ac(view: View)
    {
        finish()
    }
    fun home_screen(view: View)
    {
        val intent=Intent(this,Main_UI::class.java)
        startActivity(intent)
        finish()
    }
    fun Searc_screen(view: View)
    {
        val intent=Intent(this,Search::class.java)
        startActivity(intent)
        finish()
    }
    fun Plus_screen(view: View)
    {
        val intent=Intent(this,Add_t::class.java)
        startActivity(intent)
        finish()
    }
    fun Chat_screen(view: View)
    {
        val intent=Intent(this,chat_main::class.java)
        startActivity(intent)
        finish()
    }

    private fun fetchGigsFromFirestore() {

        var url = "http://192.168.100.146/GetGigs.php"
        val adapter_=Search_result_adapte(this@Search_results,mUser)
        recyclerview.adapter=adapter_
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    // Check if the response is an empty array
                    if (response.length() == 0) {
                        Log.e("FetchData", "No gigs found")
                    }

                    // Iterate through the JSON array
                    for (i in 0 until response.length()) {
                        val gig = response.getJSONObject(i)
                        val field = gig.getString("field")
                        val session = gig.getString("session")
                        url = "http://192.168.100.146/GetName.php"
                        // Request for name
                        val stringRequest = object : StringRequest(
                            Method.POST, url,
                            Response.Listener<String> { response ->
                                val name = response.toString()
                                // Add to mUser after getting the name
                                mUser.add(gig_d(name, session, field, gig.getInt("id").toString()))
                                // Notify adapter after adding to mUser
                                adapter_.notifyItemInserted(mUser.size - 1)
                            },
                            Response.ErrorListener { error ->
                                Toast.makeText(this, "Error occurred: ${error.message}", Toast.LENGTH_SHORT).show()
                            }) {
                            override fun getParams(): MutableMap<String, String> {
                                val params = HashMap<String, String>()
                                params["id"] = gig.getInt("id").toString()
                                return params
                            }
                        }

                        Volley.newRequestQueue(this).add(stringRequest)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Log.e("FetchData", "Error occurred: ${error.message}")
            })

// Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(jsonArrayRequest)


    }

    companion object {
        private const val TAG = "Search_results"
    }


}