package com.example.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val country_show_spin: Spinner = findViewById(R.id.country)
        val city_show_spin:Spinner=findViewById(R.id.city)
        ArrayAdapter.createFromResource(
            this,
            R.array.country_show,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            country_show_spin.adapter = adapter
        }
        ArrayAdapter.createFromResource(
            this,
            R.array.city_showi,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            city_show_spin.adapter = adapter
        }

    }

    fun log_(view: View)
    {
        val intent=Intent(this,Login::class.java)
        startActivity(intent)
        finish()
    }
    fun ClickFunction(view: View) {
        // Perform the action you want when the signup button is clicked
        val email = findViewById<EditText>(R.id.email)
        val pass = findViewById<EditText>(R.id.password)
        val phone = findViewById<EditText>(R.id.contactno)
        val country = findViewById<Spinner>(R.id.country)
        val city = findViewById<Spinner>(R.id.city)
        val name = findViewById<EditText>(R.id.name)

        if (email.text.isNotEmpty() && pass.text.isNotEmpty() && phone.text.isNotEmpty()) {

            val url = "http://192.168.100.146/Users.php"

            val stringRequest = object : StringRequest(
                Request.Method.POST, url,
                Response.Listener<String> { response ->
                    Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                    val user_id=response.toInt()

                    val url_ = "http://192.168.100.146/UserInfo.php"
                    val stringRequest_in = object : StringRequest(
                        Request.Method.POST, url_,
                        Response.Listener<String> { response ->
                            Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                            val intent=Intent(this,Verify_UI::class.java)
                            startActivity(intent)

                        },
                        Response.ErrorListener { error ->
                            Toast.makeText(this, "Error occurred: ${error.message}", Toast.LENGTH_SHORT).show()
                        }) {
                        override fun getParams(): MutableMap<String, String> {
                            val params = HashMap<String, String>()
                            params["city"] = city.selectedItem.toString()
                            params["country"] = country.selectedItem.toString()
                            params["email"] = email.text.toString()
                            params["fcmtoken"] = ""
                            params["img"] = "https://firebasestorage.googleapis.com/v0/b/uial-ef265.appspot.com/o/images%2Fimage%3A220894?alt=media&token=940ee3e7-1b64-42d2-94c2-80fc2de0911c"
                            params["img_b"] = "https://firebasestorage.googleapis.com/v0/b/uial-ef265.appspot.com/o/images%2Fimage%3A55373?alt=media&token=eb9a7a7f-f547-43e1-9581-c6c41c2b9389"
                            params["name"] = name.text.toString()
                            params["phone"] = phone.text.toString()
                            params["id"] = user_id.toString()
                            return params
                        }
                    }

                    Volley.newRequestQueue(this).add(stringRequest_in)


                },
                Response.ErrorListener { error ->
                    Toast.makeText(this, "Error occurred: ${error.message}", Toast.LENGTH_SHORT).show()
                }) {
                override fun getParams(): MutableMap<String, String> {
                    val params = HashMap<String, String>()
                    params["email"] = email.text.toString()
                    params["password"] = pass.text.toString()
                    return params
                }
            }

            Volley.newRequestQueue(this).add(stringRequest)

        } else {
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
        }
    }
}
