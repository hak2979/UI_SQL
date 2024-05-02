package com.example.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.view.ViewTreeObserver
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import org.json.JSONException


class Detail : AppCompatActivity() {
    private var categories = arrayOf(
        "Career Advice", "User Experience Design", "Leader",
        "Prototyping"
    )
    var break_p:Boolean=false
    private lateinit var auth: FirebaseAuth
    private lateinit var storageref: FirebaseStorage
    private lateinit var selectedImageUri: Uri
    private lateinit var imgLauncher: ActivityResultLauncher<String>
    private lateinit var db:DatabaseReference
    private var username:Int=0
    private var fromu:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        username = intent.getIntExtra("id",0)
        fromu=intent.getIntExtra("from",0)
        storageref = FirebaseStorage.getInstance()

        imgLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val i = findViewById<ImageView>(R.id.detail_img)
                i.setImageURI(it)
                selectedImageUri = it
            }
        }
        retrieveImageFromFirebase()



        var url = "http://192.168.100.146/GetName.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> { response ->
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                val nameTextView = findViewById<TextView>(R.id.mdetail_name)
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

        val x=username.toString()
        Toast.makeText(this,x.toString(),Toast.LENGTH_SHORT).show()
        url = "http://192.168.100.146/Get_o_gig.php?id=$x"

        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                Log.e("Response", "Gig")
                try {
                    Log.e("In", "Gig")
                    // Check if the response is an empty array
                    if (response.length() == 0) {
                        Log.e("FetchData", "No gigs found")
                    } else {
                        // Retrieve the first gig object from the response array
                        val gig = response.getJSONObject(0)
                        val jobTextView = findViewById<TextView>(R.id.mjob)
                        val desTextView = findViewById<TextView>(R.id.mdes)
                        jobTextView.text = gig.getString("field")
                        desTextView.text = gig.getString("des")
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



        val linear_addcontainer = findViewById<LinearLayout>(R.id.expert_o)

        var index = 0
        var previousLin: LinearLayout? = null
        while (index < categories.size) {
            val lin = LinearLayout(this)
            lin.orientation = LinearLayout.HORIZONTAL
            val linParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            lin.layoutParams = linParams

            while (index < categories.size) {
                val button = Button(this)
                button.text = categories[index]
                button.setBackgroundResource(R.drawable.experti)
                val col = android.graphics.Color.parseColor("#157177")
                button.setTextColor(col)
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                button.setSingleLine(true)
                button.ellipsize = TextUtils.TruncateAt.END
                params.setMargins(20, 0, 20, 0)
                button.layoutParams = params
                button.setPadding(20, 0, 20, 0)

                button.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        button.viewTreeObserver.removeOnGlobalLayoutListener(this)

                        val buttonWidth = button.width + resources.getDimensionPixelSize(R.dimen.button_margin)
                        val linearWidth = lin.width + buttonWidth

                        if (linearWidth >= resources.getDimensionPixelSize(R.dimen.ui_margin)) {
                            break_p=true
                        }
                    }
                })
                if(lin.childCount==2||break_p==true)
                {
                    break
                }
                else
                {
                    lin.addView(button)
                    index++
                }
            }

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 20, 0, 20)
            lin.layoutParams = params

            linear_addcontainer.addView(lin)
        }
    }
    fun bookui(view: View)
    {
        val intent=Intent(this,Book_cal::class.java)
        intent.putExtra("to_user",username)
        intent.putExtra("id",fromu)
        startActivity(intent)
        finish()
    }
    fun Revie_sc(view: View)
    {
        val intent=Intent(this,Review_ui::class.java)
        intent.putExtra("id",username)
        intent.putExtra("from",fromu)
        startActivity(intent)
        finish()
    }
    fun Com(view: View)
    {
        val intent=Intent(this,Community::class.java)
        intent.putExtra("uid",username)
        intent.putExtra("from",fromu)
        startActivity(intent)
        finish()
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
    fun retrieveImageFromFirebase() {

        var url = "http://192.168.100.146/Getimg.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> { response ->
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show()

                // Load a circular placeholder image
                val placeholderDrawable = getCirclePlaceholderDrawable()

                // Load the retrieved image into ImageView as a round image using Picasso
                val i = findViewById<ImageView>(R.id.detail_img)
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
}