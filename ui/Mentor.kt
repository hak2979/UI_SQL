package com.example.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class Mentor : AppCompatActivity() {
    private lateinit var db:DatabaseReference
    private var currentuser=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mentor)
        currentuser=intent.getIntExtra("id",0)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    fun end_ac(view: View)
    {
        finish()
    }
    fun gig_publish(view: View)
    {
        val field=findViewById<EditText>(R.id.mentor_field)
        val des=findViewById<EditText>(R.id.mentor_des)
        val session=findViewById<EditText>(R.id.mentor_session)
        val job_=findViewById<EditText>(R.id.mentor_job)
        if(field.text.isNotEmpty() && des.text.isNotEmpty() && session.text.isNotEmpty() && job_.text.isNotEmpty()){
            val url_ = "http://192.168.100.146/SaveGigs.php"
            val stringRequest_in = object : StringRequest(
                com.android.volley.Request.Method.POST, url_,
                com.android.volley.Response.Listener<String> { response ->
                    Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                    val intent= Intent(this,Main_UI::class.java)
                    intent.putExtra("id",currentuser)
                    startActivity(intent)

                },
                com.android.volley.Response.ErrorListener { error ->
                    Toast.makeText(this, "Error occurred: ${error.message}", Toast.LENGTH_SHORT).show()
                }) {
                override fun getParams(): MutableMap<String, String> {
                    val params = HashMap<String, String>()
                    params["des"] = des.text.toString()
                    params["field"] = field.text.toString()
                    params["job"] = job_.text.toString()
                    params["session"] = session.text.toString()
                    params["id"] = currentuser.toString()
                    return params
                }
            }

            Volley.newRequestQueue(this).add(stringRequest_in)
        }
    }
    fun sendNotification(message: String){
        val jsonObject = JSONObject()
        val notificationObj = JSONObject()
        val dataObj = JSONObject()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser!!.uid).child("UserInfo")
        databaseReference.get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                val name = dataSnapshot.child("name").getValue(String::class.java)
                notificationObj.put("title",name)
                notificationObj.put("body",message)
                dataObj.put("userId",currentUser.uid)
                jsonObject.put("notification",notificationObj)
                jsonObject.put("data",dataObj)
                val auth_=FirebaseAuth.getInstance()
                val db1=FirebaseDatabase.getInstance().getReference().child("Users").child(auth_.currentUser!!.uid.toString()).child("UserInfo")
                db1.get().addOnSuccessListener { dataSnapshot1->
                    if(dataSnapshot1.exists()){
                        jsonObject.put("to",dataSnapshot1.child("fcmtoken").value.toString())
                        callApi(jsonObject)
                    }
                }

            }
        }.addOnFailureListener { exception ->
            // Handle any errors that occur while fetching data
            println("Error fetching data: $exception")
        }

    }
    fun callApi(jsonObject: JSONObject)
    {
        val JSON = "application/json; charset=utf-8".toMediaType()
        val client = OkHttpClient()
        val url = "https://fcm.googleapis.com/fcm/send"
        val body = jsonObject.toString().toRequestBody(JSON)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .header("Authorization", "Bearer AAAAspiI3j0:APA91bGKmOWO_yZqKXtgdTEAApXsCueWKBPkINwEzJK2MJEiHbHpSZKvNKBIOP3yUw0DPUInJBliCRMN9ND5t3IT0I_pBZo5sOBdMCaOLW4RaooRFUEqhWPfyslecIfWzvrvTTXLl5bu")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
                Log.e("API_CALL", "Failed to send notification: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                // Handle success or failure based on response
                if (!response.isSuccessful) {
                    Log.e("API_CALL", "Failed to send notification: ${response.code}")
                } else {
                    Log.d("API_CALL", "Notification sent successfully")
                }
                // Close the response body to release resources
                response.close()
            }
        })
    }
}