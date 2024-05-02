package com.example.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class Review_ui : AppCompatActivity() {
    private lateinit var db: DatabaseReference
    private var username:Int=0
    private var fromus:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_ui)
        username = intent.getIntExtra("id",0)
        fromus=intent.getIntExtra("from",0)
        retrieveImageFromFirebase()
        var url = "http://192.168.100.146/GetName.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> { response ->
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                val nameTextView = findViewById<TextView>(R.id.rev_name)
                nameTextView.text = response.toString()
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error occurred: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id"] = username.toString()
                return params
            }
        }

        Volley.newRequestQueue(this).add(stringRequest)

    }

    fun retrieveImageFromFirebase() {
        var url = "http://192.168.100.146/Getimg.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> { response ->
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show()

                // Load a circular placeholder image
                val placeholderDrawable = getCirclePlaceholderDrawable()

                // Load the retrieved image into ImageView as a round image using Picasso
                val i = findViewById<ImageView>(R.id.rev_img)
                Picasso.get()
                    .load(response.toString())
                    .placeholder(placeholderDrawable)
                    .transform(Edit_pro.CircleTransform())
                    .into(i)
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error occurred: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id"] = username.toString()
                return params
            }
        }

        Volley.newRequestQueue(this).add(stringRequest)
    }

    fun getCirclePlaceholderDrawable(): Drawable {
        // Create a circular placeholder drawable
        val size = 300 // Set the size of the placeholder circle
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            isAntiAlias = true
            color = Color.LTGRAY // Set the color of the circular placeholder
        }
        val radius = size / 2f
        canvas.drawCircle(radius, radius, radius, paint)
        return BitmapDrawable(resources, bitmap)
    }

    fun end_ac(view: View) {
        val feed_ = findViewById<EditText>(R.id.rev_feedba)
        val star = findViewById<RatingBar>(R.id.ratingBar)

        val urlGetName = "http://192.168.100.146/GetName.php"

        val stringRequestName = object : StringRequest(
            Request.Method.POST, urlGetName,
            Response.Listener<String> { response ->
                val name = response.trim()

                val urlReviews = "http://192.168.100.146/Reviews.php"
                val stringRequestReviews = object : StringRequest(
                    Request.Method.POST, urlReviews,
                    Response.Listener<String> { response ->
                        Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                        finish()
                    },
                    Response.ErrorListener { error ->
                        Toast.makeText(this, "Error occurred: ${error.message}", Toast.LENGTH_SHORT).show()
                    }) {
                    override fun getParams(): MutableMap<String, String> {
                        val params = HashMap<String, String>()
                        params["name"] = name
                        params["review"] = feed_.text.toString()
                        params["stars"] = star.rating.toInt().toString()
                        params["user_from"] = fromus.toString()
                        params["user_to"] = username.toString()
                        return params
                    }
                }

                Volley.newRequestQueue(this@Review_ui).add(stringRequestReviews)
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error occurred: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id"] = fromus.toString()
                return params
            }
        }

        Volley.newRequestQueue(this@Review_ui).add(stringRequestName)

        finish()
    }
}