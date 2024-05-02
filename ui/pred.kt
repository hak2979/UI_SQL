package com.example.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Log.*
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import org.json.JSONException

class pred : AppCompatActivity() {
    private var name_gigs= arrayOf("John Cooper","Martin Wat","Emma","Wills")
    private var price_gigs= arrayOf("$1500/Session","$500/Session","$109/Session","$80/Session",)
    private var Experience_gigs= arrayOf("UX Designer","Lead - Technolgy","Lead Corporation","@Meta")
    private var availible_gigs= arrayOf(true,false,false,false)
    private var categories= arrayOf("All","Education","Entreprenuership","Personal Growth","Caree","Etaaa")
    private var lik= arrayOf(true,true,true,true)
    private lateinit var storageref: FirebaseStorage
    private lateinit var selectedImageUri: Uri
    private lateinit var imgLauncher: ActivityResultLauncher<String>
    private lateinit var bimgLauncher: ActivityResultLauncher<String>
    private lateinit var bselectedImageUri: Uri
    private var username:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pred)
        storageref = FirebaseStorage.getInstance()
        username=intent.getIntExtra("id",0)


        // Register the activity result launcher
        imgLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val i = findViewById<ImageView>(R.id.editprofile_img)
                i.setImageURI(it)
                selectedImageUri = it
            }
        }

        bimgLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val i = findViewById<ImageView>(R.id.wp)
                i.setImageURI(it)
                bselectedImageUri = it
                uploadImage(bselectedImageUri)
            }
        }


        retrieveImageFromFirebase()
        retrieveImageFromFirebase_()
        val gigsLayout = findViewById<LinearLayout>(R.id.liked_card)
        for (i in name_gigs.indices) {

            val gigLayout = layoutInflater.inflate(R.layout.card_fiver,null)
            val nameTextView = gigLayout.findViewById<TextView>(R.id.name_service)
            val rateTextView = gigLayout.findViewById<TextView>(R.id.rate_service)
            val experienceTextView = gigLayout.findViewById<TextView>(R.id.Experience_of_parent)
            val availibilityTextView = gigLayout.findViewById<TextView>(R.id.is_availibil)
            val imageButton = gigLayout.findViewById<ImageButton>(R.id.imageButton_of_parent)


            imageButton.setBackgroundResource(R.drawable.heart_solid)
            nameTextView.text = name_gigs[i]
            rateTextView.text = price_gigs[i]
            experienceTextView.text = Experience_gigs[i]
            if (availible_gigs[i])
            {
                availibilityTextView.text ="Available"
                val greenColor = Color.parseColor("#00FF00")
                availibilityTextView.setTextColor(greenColor)
            } else
            {
                availibilityTextView.text ="Not Availible"
            }

            imageButton.setOnClickListener {
                if(lik[i]==false)
                {
                    imageButton.setBackgroundResource(R.drawable.heart_solid)
                    lik[i]=true
                }
                else{
                    imageButton.setBackgroundResource(R.drawable.heart_regular)
                    lik[i]=false
                }
            }
            gigLayout.setOnClickListener{
                Toast.makeText(this, "Press", Toast.LENGTH_SHORT).show()
                val intent= Intent(this,Detail::class.java)
                startActivity(intent)
            }

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(10,0,10,0)
            gigLayout.layoutParams=params

            gigsLayout.addView(gigLayout)
        }

        val l=findViewById<LinearLayout>(R.id.reve_ar)
        val x=username.toString()
        val url = "http://192.168.100.146/Reviews_fetch.php?id=$x"
        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    // Check if the response is an empty array
                    if (response.length() == 0) {
                        e("FetchData", "No gigs found")
                    }

                    // Iterate through the JSON array
                    for (i in 0 until response.length()) {
                        val gig_r = response.getJSONObject(i)

                        val gig = layoutInflater.inflate(R.layout.revie, null)
                        val name = gig.findViewById<TextView>(R.id.reve_name)
                        val stars = gig.findViewById<RatingBar>(R.id.reve_ratingBar)
                        val feed = gig.findViewById<TextView>(R.id.reve_feed)

                        name.text = gig_r.getString("name")?:""
                        stars.rating = gig_r.getInt("stars").toFloat()?:0f
                        feed.text = gig_r.getString("review")?:""

                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        params.setMargins(10, 0, 10, 0)
                        gig.layoutParams = params
                        l.addView(gig)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                e("FetchData", "Error occurred: ${error.message}")
            })

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(jsonArrayRequest)
    }
    fun edi(view: View)
    {
        val intent=Intent(this,Edit_pro::class.java)
        intent.putExtra("id",username)
        startActivity(intent)
    }
    fun end_ac(view: View)
    {
        finish()
    }
    fun Book(view: View)
    {
        val intent=Intent(this,Bookedd::class.java)
        intent.putExtra("id",username)
        startActivity(intent)
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
    fun retrieveImageFromFirebase() {

        var url = "http://192.168.100.146/Getimg.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> { response ->
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show()

                // Load a circular placeholder image
                val placeholderDrawable = getCirclePlaceholderDrawable()

                // Load the retrieved image into ImageView as a round image using Picasso
                val i = findViewById<ImageView>(R.id.pic)
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
    class CircleTransform : Transformation {
        override fun transform(source: Bitmap): Bitmap {
            val size = Math.min(source.width, source.height)

            val x = (source.width - size) / 2
            val y = (source.height - size) / 2

            val squaredBitmap = Bitmap.createBitmap(source, x, y, size, size)
            if (squaredBitmap != source) {
                source.recycle()
            }

            val bitmap = Bitmap.createBitmap(size, size, source.config)

            val canvas = Canvas(bitmap)
            val paint = Paint()
            val shader = BitmapShader(squaredBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            paint.shader = shader
            paint.isAntiAlias = true

            val radius = size / 2f
            canvas.drawCircle(radius, radius, radius, paint)

            squaredBitmap.recycle()
            return bitmap
        }

        override fun key(): String {
            return "circle"
        }
    }
    fun pic_select(view: View) {
        // Launch the image selection activity
        bimgLauncher.launch("image/*")
    }

    fun uploadImage(imageUri: Uri) {




        val imageRef = storageref.reference.child("images/${imageUri.lastPathSegment}")
        val uploadTask = imageRef.putFile(imageUri)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()

                val url = "http://192.168.100.146/update_img_background.php"

                val stringRequest = object : StringRequest(
                    Request.Method.POST, url,
                    Response.Listener<String> { response ->
                        if (response.trim() == "success") {
                            // Profile update successful
                            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                            val i = findViewById<ImageView>(R.id.wp)
                            Picasso.get()
                                .load(imageUrl)
                                .fit()
                                .centerCrop()
                                .into(i)
                        } else {
                            // Profile update failed
                            Toast.makeText(this, response.toString(), Toast.LENGTH_SHORT).show()
                        }
                    },
                    Response.ErrorListener { error ->
                        // Error occurred during the request
                        Toast.makeText(this, "Error occurred: ${error.message}", Toast.LENGTH_SHORT).show()
                    }) {
                    override fun getParams(): MutableMap<String, String> {
                        val params = HashMap<String, String>()
                        params["uid"] = username.toString()
                        params["img_b"] = imageUrl
                        // Add other profile data as needed
                        return params
                    }
                }

                // Add the request to the RequestQueue
                Volley.newRequestQueue(this).add(stringRequest)

            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Image upload failed: ${exception.message}", Toast.LENGTH_SHORT).show()
        }


    }
    fun retrieveImageFromFirebase_() {
        var url = "http://192.168.100.146/Getimg_b.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> { response ->
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show()

                // Load a circular placeholder image
                val placeholderDrawable = getCirclePlaceholderDrawable()

                // Load the retrieved image into ImageView as a round image using Picasso
                val imageView = findViewById<ImageView>(R.id.wp)
                Picasso.get()
                    .load(response.toString())
                    .fit()
                    .centerCrop()
                    .into(imageView)
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
}