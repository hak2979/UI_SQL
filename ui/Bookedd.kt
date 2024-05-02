package com.example.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import org.json.JSONException

class Bookedd : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookedd)

        val lin = findViewById<LinearLayout>(R.id.to_a)
        val currentUser = intent.getIntExtra("id", 0)
        val auth = FirebaseAuth.getInstance().currentUser

        // Get the current user's bookings from the PHP script
        val url = "http://192.168.100.146/Booking_fetch.php?id=$currentUser"

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    // Iterate through the JSON array
                    for (i in 0 until response.length()) {
                        val gig = response.getJSONObject(i)

                        val book_gigd = layoutInflater.inflate(R.layout.profile_boo, null)
                        val name_ofperson = book_gigd.findViewById<TextView>(R.id.book_name)
                        val job_ofp = book_gigd.findViewById<TextView>(R.id.book_job)
                        val date_ofp = book_gigd.findViewById<TextView>(R.id.book_date)
                        val time_ofp = book_gigd.findViewById<TextView>(R.id.book_time)
                        val img = book_gigd.findViewById<ImageView>(R.id.book_img)

                        val date = gig.getString("date")
                        val time = gig.getString("time")
                        val uid_to = gig.getString("uid_to")

                        // Get the name of the person associated with the booking
                        getName(uid_to) { name ->
                            name_ofperson.text = name

                            // Load the image of the person associated with the booking
                            getImg(uid_to) { imgUrl ->
                                Picasso.get()
                                    .load(imgUrl)
                                    .transform(RoundedTransformation(8f))
                                    .fit()
                                    .into(img)
                            }
                        }

                        job_ofp.text = "Job" // You need to get the job information from somewhere
                        date_ofp.text = date
                        time_ofp.text = time

                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        params.setMargins(10, 20, 10, 20)
                        book_gigd.layoutParams = params

                        lin.addView(book_gigd)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Log.e("FetchData", "Error occurred: ${error.message}")
            })

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this@Bookedd).add(jsonArrayRequest)
    }

    fun getName(uid: String, callback: (String) -> Unit) {
        val url = "http://192.168.100.146/GetName.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> { response ->
                callback.invoke(response)
            },
            Response.ErrorListener { error ->
                // Handle error
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id"] = uid
                return params
            }
        }

        Volley.newRequestQueue(this@Bookedd).add(stringRequest)
    }

    fun getImg(uid: String, callback: (String) -> Unit) {
        val url = "http://192.168.100.146/Getimg.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> { response ->
                callback.invoke(response)
            },
            Response.ErrorListener { error ->
                // Handle error
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id"] = uid
                return params
            }
        }

        Volley.newRequestQueue(this@Bookedd).add(stringRequest)
    }

    fun end_ac(view: View) {
        finish()
    }

    class RoundedTransformation(private val radius: Float) : Transformation {
        override fun transform(source: Bitmap): Bitmap {
            val bitmap = Bitmap.createBitmap(source.width, source.height, source.config)
            val canvas = Canvas(bitmap)
            val paint = Paint()
            paint.isAntiAlias = true
            val rect = Rect(0, 0, source.width, source.height)
            val rectF = RectF(rect)
            canvas.drawRoundRect(rectF, radius, radius, paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(source, rect, rect, paint)
            source.recycle()
            return bitmap
        }

        override fun key(): String = "rounded(radius=$radius)"
    }
}