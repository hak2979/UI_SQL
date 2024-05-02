package com.example.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore.Video
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.CalendarView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Book_cal : AppCompatActivity() {

    var timedo= arrayOf("10.00 AM","11.00 AM","12.00 PM")
    var break_p:Boolean=false
    var time_b= arrayOf(true,false,false)
    private var username:Int=0
    private var to_user:Int=0
    private lateinit var date_s:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_cal)

        username=intent.getIntExtra("id",0)
        to_user=intent.getIntExtra("to_user",0)


        val cal = findViewById<CalendarView>(R.id.cal)

        cal.setOnDateChangeListener { view, year, month, dayOfMonth ->
            // Format the selected date to the desired format
            val formattedDate = formatDate(dayOfMonth, month, year)
            date_s=formattedDate
        }
        val ln2=findViewById<LinearLayout>(R.id.tim)
        for(i in timedo.indices)
        {
            val textv=TextView(this)
            textv.text=timedo[i]
            textv.width=resources.getDimensionPixelSize(R.dimen.ex_w)
            val pei=LinearLayout.LayoutParams(
                resources.getDimensionPixelSize(R.dimen.ex_w),
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
            )
            pei.setMargins(5,5,5,5)
            textv.layoutParams=pei
            textv.gravity=android.view.Gravity.CENTER
            textv.setPadding(20,20,20,20)


            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            if(!time_b[i]){
                textv.setBackgroundResource(R.drawable.shape_but)
                textv.isClickable=true
            }
            else{
                textv.setBackgroundResource(R.drawable.forgor__widget_button)
                textv.isClickable=true
            }
            textv.setOnClickListener{
                if(!time_b[i]){
                    time_b[i]=true
                    textv.setBackgroundResource(R.drawable.forgor__widget_button)
                }
                else{
                    time_b[i]=false
                    textv.setBackgroundResource(R.drawable.shape_but)
                }
            }
            ln2.addView(textv)
        }


    }
    fun voice_call(view: View)
    {
        val intent=Intent(this,Voice_call::class.java)
        startActivity(intent)
    }
    fun video_call(view: View)
    {
        val intent=Intent(this,Video_call::class.java)
        startActivity(intent)
    }
    fun end_ac(view: View)
    {
        finish()
    }
    fun Review_s(view: View)
    {

        val url_ = "http://192.168.100.146/Bookings_Send.php"
        val stringRequest_in = object : StringRequest(
            com.android.volley.Request.Method.POST, url_,
            com.android.volley.Response.Listener<String> { response ->
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                finish()

            },
            com.android.volley.Response.ErrorListener { error ->
                Toast.makeText(this, "Error occurred: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["date"] = date_s
                if(time_b[0]){
                    params["time"]=time_b[0].toString()
                }
                else if(time_b[1]){
                    params["time"]=time_b[1].toString()
                }
                else{
                    params["time"]=time_b[2].toString()
                }
                params["uid_from"] = username.toString()
                params["uid_to"] = to_user.toString()
                return params
            }
        }

        Volley.newRequestQueue(this).add(stringRequest_in)
    }
    private fun formatDate(day: Int, month: Int, year: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)

        // Format the date to "dd-MMMM-yyyy" (e.g., 15-March-2024)
        val dateFormat = SimpleDateFormat("dd-MMMM-yyyy", Locale.ENGLISH)
        return dateFormat.format(calendar.time)
    }
    fun Chat_screen(view: View)
    {
        val intent=Intent(this,Chat_p::class.java)
        intent.putExtra("id",username)
        intent.putExtra("to_user",to_user)
        startActivity(intent)
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
                val db1=FirebaseDatabase.getInstance().getReference().child("Users").child(username.toString()).child("UserInfo")
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
