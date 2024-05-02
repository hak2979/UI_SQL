package com.example.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.ui.R.drawable.heart_solid
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.json.JSONArray
import org.json.JSONException

class Main_UI : AppCompatActivity() {
    private lateinit var db: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var currentUser:Int=0
    private var name_gigs= arrayOf("John Cooper","Martin Wat","Emma","Wills")
    private var price_gigs= arrayOf("$1500/Session","$500/Session","$109/Session","$80/Session",)
    private var Experience_gigs= arrayOf("UX Designer","Lead - Technolgy","Lead Corporation","@Meta")
    private var availible_gigs= arrayOf(true,false,false,false)
    private var categories= arrayOf("All","Education","Entreprenuership","Personal Growth","Caree","Etaaa")
    private var lik= arrayOf(false,false,false,false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_ui)

        currentUser = intent.getIntExtra("id", 0)
        var url = "http://192.168.100.146/GetName.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> { response ->
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                val nameTextView = findViewById<TextView>(R.id.mains_name)
                nameTextView.text = response.toString()
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error occurred: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id"] = currentUser.toString()
                return params
            }
        }

        Volley.newRequestQueue(this).add(stringRequest)



        val fu=findViewById<LinearLayout>(R.id.categor)
        for(component in categories)
        {
            val button=Button(this)
            button.text=component
            button.setBackgroundResource(R.drawable.categories)

            val col = android.graphics.Color.parseColor("#157177")
            button.setTextColor(col)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(20, 0, 20, 0)
            button.layoutParams = params
            button.setPadding(20,0,20,0)
            fu.addView(button)
        }




        val xs=currentUser.toString()
        url = "http://192.168.100.146/GetGigs.php?uid=$xs"
        val gigsLayout = findViewById<LinearLayout>(R.id.gigs)
        val gi_gsLayout = findViewById<LinearLayout>(R.id.gigs_recent)
        val gigsLayout_ = findViewById<LinearLayout>(R.id.gigs_edu)
        var index=0

        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    // Check if the response is an empty array
                    if (response.length() == 0) {
                        Log.e("FetchData", "No gigs found")
                    }

                    // Iterate through the JSON array
                    for (i in 0 until response.length()) {
                        val gig = response.getJSONObject(i)

                        val gigLayout = layoutInflater.inflate(R.layout.card_fiver, null)
                        val nameTextView = gigLayout.findViewById<TextView>(R.id.name_service)
                        val rateTextView = gigLayout.findViewById<TextView>(R.id.rate_service)
                        val experienceTextView = gigLayout.findViewById<TextView>(R.id.Experience_of_parent)
                        val availabilityTextView = gigLayout.findViewById<TextView>(R.id.is_availibil)
                        val imageButton = gigLayout.findViewById<ImageButton>(R.id.imageButton_of_parent)

                        val gigPrice = gig.getString("session")
                        val gigField = gig.getString("field")

                        url = "http://192.168.100.146/GetName.php"

                        val stringRequest = object : StringRequest(
                            Request.Method.POST, url,
                            Response.Listener<String> { response ->
                                Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                                nameTextView.text = response.toString()
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
                        // Set the TextViews with the retrieved data

                        rateTextView.text = "$" + gigPrice
                        experienceTextView.text = gigField

                        // Set availability text (not sure where you intend to get this information from)
                        val isAvailable = true // Placeholder value
                        if (isAvailable) {
                            availabilityTextView.text = "Available"
                            val greenColor = android.graphics.Color.parseColor("#00FF00")
                            availabilityTextView.setTextColor(greenColor)
                        } else {
                            availabilityTextView.text = "Not Available"
                        }

                        // Set up click listener for the gigLayout
                        gigLayout.setOnClickListener {
                            val intent = Intent(this@Main_UI, Detail::class.java)
                            val gigId = gig.getInt("id")
                            if (gigId > 0) {
                                intent.putExtra("id", gigId)
                                intent.putExtra("from",currentUser)
                                startActivity(intent)
                            } else {
                                Toast.makeText(this@Main_UI, "Gig ID is null", Toast.LENGTH_SHORT).show()
                            }
                        }

                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        params.setMargins(10, 0, 10, 0)
                        gigLayout.layoutParams = params

                        gigsLayout.addView(gigLayout)
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
    fun profil(view: View)
    {
        val intent=Intent(this,pred::class.java)
        intent.putExtra("id",currentUser)
        startActivity(intent)
    }
    fun searchui(view: View)
    {
        val intent=Intent(this,Search::class.java)
        startActivity(intent)
    }
    fun add_newmento(view: View)
    {
        val intent=Intent(this,Add_t::class.java)
        startActivity(intent)
    }
    fun Chat_press(view: View)
    {
        val intent=Intent(this,chat_main::class.java)
        startActivity(intent)
    }


    fun home_screen(view: View)
    {
        val intent=Intent(this,Main_UI::class.java)
        startActivity(intent)
    }
    fun Searc_screen(view: View)
    {
        val intent=Intent(this,Search::class.java)
        startActivity(intent)
    }
    fun Plus_screen(view: View)
    {
        val intent=Intent(this,Add_t::class.java)
        intent.putExtra ("id",this.currentUser)
        startActivity(intent)
    }
    fun FunNot(view: View)
    {
        val intent=Intent(this,Notifications::class.java)
        startActivity(intent)
    }
}