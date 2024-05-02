package com.example.ui
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation

class Edit_pro : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference
    private lateinit var storageref: FirebaseStorage
    private lateinit var selectedImageUri: Uri
    private lateinit var imgLauncher: ActivityResultLauncher<String>
    private var username:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_pro)

        storageref = FirebaseStorage.getInstance()

        username=intent.getIntExtra("id",0)


        // Register the activity result launcher
        imgLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val i = findViewById<ImageView>(R.id.editprofile_img)
                i.setImageURI(it)
                selectedImageUri = it
                uploadImage(selectedImageUri)
            }
        }

        retrieveImageFromFirebase()

        val countrySpinner: Spinner = findViewById(R.id.editprofile_country)
        val citySpinner: Spinner = findViewById(R.id.editprofile_city)

        ArrayAdapter.createFromResource(
            this,
            R.array.country_show,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            countrySpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.city_showi,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            citySpinner.adapter = adapter
        }
    }

    fun end_ac(view: View) {
        finish()
    }

    fun change_profile_details(view: View) {
        val name = findViewById<EditText>(R.id.editprofile_name)
        val newEmail = findViewById<EditText>(R.id.editprofile_email)
        val contact = findViewById<EditText>(R.id.editprofile_contactno)
        val country = findViewById<Spinner>(R.id.editprofile_country)
        val city = findViewById<Spinner>(R.id.editprofile_city)

        val url = "http://192.168.100.146/email_browse.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> { response ->
                if (response.isEmpty()) {
                    // Email does not exist, proceed with updating email and profile
                    updateProfile(name.text.toString(), contact.text.toString(), newEmail.text.toString(), country.selectedItem.toString(), city.selectedItem.toString())
                    update_email_(newEmail.text.toString())
                } else {
                    // Email already exists
                    Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error occurred: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["email"] = newEmail.text.toString()
                return params
            }
        }

        Volley.newRequestQueue(this).add(stringRequest)
    }

    fun updateProfile(name: String, contact: String, email: String, country: String, city: String) {
        val url = "http://192.168.100.146/update_profile.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> { response ->
                if (response.trim() == "success") {
                    // Profile update successful
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
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
                params["name"] = name
                params["email"] = email
                params["contact"] = contact
                params["country"] = country
                params["city"] = city
                // Add other profile data as needed
                return params
            }
        }

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(stringRequest)
    }
    fun update_email_(email:String){
        val url = "http://192.168.100.146/update_email.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> { response ->
                if (response.trim() == "success") {
                    // Profile update successful
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    // Profile update failed
                    Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                // Error occurred during the request
                Toast.makeText(this, "Error occurred: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["uid"] = username.toString()
                params["email"] = email
                // Add other profile data as needed
                return params
            }
        }

        // Add the request to the RequestQueue
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
                val i = findViewById<ImageView>(R.id.editprofile_img)
                Picasso.get()
                    .load(response.toString())
                    .placeholder(placeholderDrawable)
                    .transform(CircleTransform())
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

    fun pic_select(view: View) {
        // Launch the image selection activity
        imgLauncher.launch("image/*")
    }

    fun uploadImage(imageUri: Uri) {
        val imageRef = storageref.reference.child("images/${imageUri.lastPathSegment}")
        val uploadTask = imageRef.putFile(imageUri)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()

                val url = "http://192.168.100.146/update_img_main.php"

                val stringRequest = object : StringRequest(
                    Request.Method.POST, url,
                    Response.Listener<String> { response ->
                        if (response.trim() == "success") {
                            // Profile update successful
                            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                            val i = findViewById<ImageView>(R.id.editprofile_img)
                            Picasso.get()
                                .load(imageUrl)
                                .fit()
                                .centerCrop()
                                .transform(CircleTransform())
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
}