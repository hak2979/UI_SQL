package com.example.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging

class Login : AppCompatActivity() {
    private lateinit var name:EditText
    private  lateinit var pass:EditText
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }
    fun init(view: View)
    {
        name=findViewById(R.id.name)
        pass=findViewById(R.id.pass)
    }

    fun s_main(view: View)
    {
        auth= FirebaseAuth.getInstance()
        name=findViewById<EditText>(R.id.name)
        pass=findViewById<EditText>(R.id.pass)

        if(name.text.isNotEmpty() and pass.text.isNotEmpty()){
            val url_ = "http://192.168.100.146/Login.php"
            val stringRequest_in = object : StringRequest(
                Request.Method.POST, url_,
                Response.Listener<String> { response ->
                    if(response!="No user found with the provided credentials")
                    {
                        val user_id=response.toInt()
                        if(user_id>0){
                            getFcmtoken(user_id)
                        }
                    }
                    //Toast.makeText(this, response, Toast.LENGTH_SHORT).show()

                },
                Response.ErrorListener { error ->
                    Toast.makeText(this, "Error occurred: ${error.message}", Toast.LENGTH_SHORT).show()
                }) {
                override fun getParams(): MutableMap<String, String> {
                    val params = HashMap<String, String>()
                    params["email"] = name.text.toString()
                    params["password"] = pass.text.toString()
                    return params
                }
            }

            Volley.newRequestQueue(this).add(stringRequest_in)
        }
    }
    fun ClickFunc(view: View)
    {
        val intent=Intent(this,Home::class.java)
        startActivity(intent)
        finish()
    }
    fun forgot(view: View)
    {
        val intent=Intent(this,Forgot::class.java)
        startActivity(intent)
        finish()
    }

    fun getFcmtoken(id:Int) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                val url_ = "http://192.168.100.146/fcmup.php"
                val stringRequest_in = object : StringRequest(
                    Request.Method.POST, url_,
                    Response.Listener<String> { response ->
                        //Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                        val intent=Intent(this,Main_UI::class.java)
                        intent.putExtra("id",id)
                        startActivity(intent)

                    },
                    Response.ErrorListener { error ->
                        Toast.makeText(this, "Error occurred: ${error.message}", Toast.LENGTH_SHORT).show()
                    }) {
                    override fun getParams(): MutableMap<String, String> {
                        val params = HashMap<String, String>()
                        params["fcmtoken"] = token.toString()
                        params["id"] = id.toString()
                        return params
                    }
                }

                Volley.newRequestQueue(this).add(stringRequest_in)
                // Here you can use the FCM token for further processing
            } else {
                Log.e("FCM Token", "Fetching FCM registration token failed", task.exception)
            }
        }
    }
}