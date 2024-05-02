package com.example.ui

import android.Manifest
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.graphics.Bitmap
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*

private const val CAMERA_PERMISSION_CODE = 101

private const val SCREENSHOT_NOTIFICATION_DELAY = 2000

class Chat_p : AppCompatActivity() {
    private lateinit var uid: String
    private lateinit var mchat: MutableList<chatdet>
    private lateinit var recyclerView: RecyclerView

    // Firebase Storage reference
    private lateinit var storageRef: StorageReference

    // Define variables for voice recording
    private var mediaRecorder: MediaRecorder? = null
    private var audioFilePath: String? = null

    private val CAMERA_IMAGE_REQUEST_CODE = 1
    private val CAMERA_VIDEO_REQUEST_CODE = 2
    private var currentUser:Int=0
    private var to_user:Int=0

    private val screenshotObserver = object : ContentObserver(Handler()) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            super.onChange(selfChange, uri)
            uri?.let { screenshotUri ->
                // Check if the screenshot is taken
                if (isScreenshot(screenshotUri)) {
                    // Delay sending the notification to allow the screenshot to be saved
                    Handler().postDelayed({
                        sendNotification("Screenshot")
                    }, SCREENSHOT_NOTIFICATION_DELAY.toLong())
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Register content observer when activity starts
        contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            screenshotObserver
        )
    }

    override fun onStop() {
        super.onStop()
        // Unregister content observer when activity stops
        contentResolver.unregisterContentObserver(screenshotObserver)
    }

    private fun isScreenshot(uri: Uri): Boolean {
        val contentResolver: ContentResolver = this.contentResolver
        val cursor = contentResolver.query(
            uri, arrayOf(MediaStore.Images.Media.DISPLAY_NAME),
            null, null, null
        )
        cursor?.use {
            while (it.moveToNext()) {
                val fileName = it.getString(it.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))
                if (fileName.contains("screenshot", ignoreCase = true)) {
                    return true
                }
            }
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_p)
        FirebaseApp.initializeApp(this)
        //firebase token
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            Log.d("MyToken", token)
        })

        currentUser = intent.getIntExtra("id",0)
        to_user=intent.getIntExtra("to_user",0)






        var url = "http://192.168.100.146/GetName.php"

        val name_chat = findViewById<TextView>(R.id.name_chats)
        val stringRequest = object : StringRequest(
            com.android.volley.Request.Method.POST, url,
            com.android.volley.Response.Listener<String> { response ->
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                name_chat.text = response.toString()
            },
            com.android.volley.Response.ErrorListener { error ->
                Toast.makeText(this, "Error occurred: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id"] = to_user.toString()
                return params
            }
        }

        Volley.newRequestQueue(this).add(stringRequest)

        mchat = mutableListOf()
        recyclerView = findViewById(R.id.msg_recycle)
        recyclerView.layoutManager = LinearLayoutManager(this)
        storageRef = FirebaseStorage.getInstance().reference
        readMessage()
    }

    fun sendMsg(view: View) {
        val msg = findViewById<EditText>(R.id.msg_chat)

        if (msg.text.isNotEmpty()) {

            val url_ = "http://192.168.100.146/Chats_Send.php"
            val stringRequest_in = object : StringRequest(
                com.android.volley.Request.Method.POST, url_,
                com.android.volley.Response.Listener<String> { response ->
                    readMessage()
                },
                com.android.volley.Response.ErrorListener { error ->
                    Toast.makeText(this, "Error occurred: ${error.message}", Toast.LENGTH_SHORT).show()
                }) {
                override fun getParams(): MutableMap<String, String> {
                    val params = HashMap<String, String>()
                    params["msg"] = msg.text.toString()
                    params["msgType"] = "text"
                    params["recv"] = to_user.toString()
                    params["sender"] = currentUser.toString()
                    val currentTime = System.currentTimeMillis()
                    params["timestamp"] = currentTime.toString()
                    return params
                }
            }

            Volley.newRequestQueue(this).add(stringRequest_in)
        }
    }
    private fun readMessage() {

        val currentUser = currentUser.toString()
        val toUser = to_user.toString()
        val url = "http://192.168.100.146/Chats_r.php?currentUser=$currentUser&to_user=$toUser"

        val jsonArrayRequest = JsonArrayRequest(
            com.android.volley.Request.Method.GET, url, null,
            { response ->
                try {
                    val mchat = mutableListOf<chatdet>()
                    // Loop through each JSON object in the response array
                    for (i in 0 until response.length()) {
                        val gig = response.getJSONObject(i)
                        val sender = gig.getString("sender")
                        val receiver = gig.getString("recv")
                        val msg = gig.getString("msg")
                        val msgType = gig.getString("msgType")
                        val msgId = gig.getString("msgid")
                        val timestamp = gig.getString("timestamp").toLong()
                        mchat.add(chatdet(sender, receiver, msg, msgType, msgId, timestamp))
                    }
                    // Update your RecyclerView adapter with the new chat data
                    val adapter_ = MsgAdapter(this@Chat_p, mchat, currentUser)
                    recyclerView.adapter = adapter_
                    adapter_.notifyDataSetChanged()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Log.e("FetchData", "Error occurred: ${error.message}")
            }
        )

// Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(jsonArrayRequest)

    }

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { imageUri ->
            // Handle the selected image URI
            uploadImageToFirebase(imageUri)
        } ?: run {
            // No image URI returned
            Toast.makeText(this, "Failed to retrieve image", Toast.LENGTH_SHORT).show()
        }
    }
    // Function to select an image from device storage
    fun Img_Select(view: View) {
            getContent.launch("image/*")
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        val filename = UUID.randomUUID().toString()
        val ref = storageRef.child("images/$filename")
        ref.putFile(imageUri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    sendMessage("img", uri.toString())
                }
            }
            .addOnFailureListener { e ->
                // Handle failed upload
                Toast.makeText(this, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun sendMessage(messageContent: String,downloadUrl: String) {
        val url_ = "http://192.168.100.146/Chats_Send.php"
        val stringRequest_in = object : StringRequest(
            com.android.volley.Request.Method.POST, url_,
            com.android.volley.Response.Listener<String> { response ->
                readMessage()
            },
            com.android.volley.Response.ErrorListener { error ->
                Toast.makeText(this, "Error occurred: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["msg"] = downloadUrl
                params["msgType"] = "img"
                params["recv"] = to_user.toString()
                params["sender"] = currentUser.toString()
                val currentTime = System.currentTimeMillis()
                params["timestamp"] = currentTime.toString()
                return params
            }
        }

        Volley.newRequestQueue(this).add(stringRequest_in)
        //sendNotification("Media")
    }

    fun Cam(view: View) {
        val intent = Intent(this, Camera_photo::class.java)
        startActivity(intent)
    }

    fun end_ac(view: View) {
        finish()
    }

    fun voice_call(view: View) {
        val intent = Intent(this, Voice_call::class.java)
        intent.putExtra("uid",uid)
        startActivity(intent)
    }

    fun video_call(view: View) {
        val intent = Intent(this, Video_call::class.java)
        intent.putExtra("uid",uid)
        startActivity(intent)
    }

    fun home_screen(view: View) {
        val intent = Intent(this, Main_UI::class.java)
        startActivity(intent)
        finish()
    }

    fun Searc_screen(view: View) {
        val intent = Intent(this, Search::class.java)
        startActivity(intent)
        finish()
    }

    fun Plus_screen(view: View) {
        val intent = Intent(this, Add_t::class.java)
        startActivity(intent)
        finish()
    }

    fun Chat_screen(view: View) {
        val intent = Intent(this, chat_main::class.java)
        startActivity(intent)
        finish()
    }

    private fun voice_sendMessage(messageType: String, messageContent: String) {

        val url_ = "http://192.168.100.146/Chats_Send.php"
        val stringRequest_in = object : StringRequest(
            com.android.volley.Request.Method.POST, url_,
            com.android.volley.Response.Listener<String> { response ->
                readMessage()
            },
            com.android.volley.Response.ErrorListener { error ->
                Toast.makeText(this, "Error occurred: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["msg"] = messageContent
                params["msgType"] = messageType
                params["recv"] = to_user.toString()
                params["sender"] = currentUser.toString()
                val currentTime = System.currentTimeMillis()
                params["timestamp"] = currentTime.toString()
                return params
            }
        }

        Volley.newRequestQueue(this).add(stringRequest_in)
        //sendNotification("Media")
    }

    fun Voice_Select(view: View) {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                101
            )
        } else {
            // Start recording voice
            startRecording()
        }
    }

    // Function to start voice recording
    private fun startRecording() {
        try {
            Toast.makeText(this,"Recording Start",Toast.LENGTH_SHORT).show()
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                audioFilePath = "${externalCacheDir?.absolutePath}/recording.3gp"
                setOutputFile(audioFilePath)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                prepare()
                start()
            }
            // Stop recording after 10 seconds
            Handler().postDelayed({
                stopRecording()
                // Upload recorded voice to Firebase Storage
                uploadVoiceToFirebase()
            }, 10000) // 10 seconds
        } catch (e: IOException) {
            Toast.makeText(this, "Failed to start recording", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to stop voice recording
    private fun stopRecording() {
        mediaRecorder?.apply {
            Toast.makeText(this@Chat_p,"Recording Stop",Toast.LENGTH_SHORT).show()
            stop()
            release()
        }
        mediaRecorder = null
    }

    private fun uploadVoiceToFirebase() {
        val file = Uri.fromFile(File(audioFilePath))
        val filename = UUID.randomUUID().toString()
        val ref = storageRef.child("voice_recordings/$filename")
        ref.putFile(file)
            .addOnSuccessListener { uploadTask ->
                // Get the download URL
                uploadTask.storage.downloadUrl.addOnSuccessListener { downloadUri ->
                    // Voice recording uploaded successfully, send the download URL as a message
                    voice_sendMessage("voice", downloadUri.toString())
                    Toast.makeText(this, "Voice recording uploaded", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener { e ->
                    // Handle failed download URL retrieval
                    Toast.makeText(this, "Failed to get download URL: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                // Handle failed upload
                Toast.makeText(this, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun cameras(view: View) {
        // Check if the app has permission to access the camera
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request camera permission if it's not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            // If permission is granted, show options for capturing image or video
            showCameraOptions()
        }
    }

    private fun showCameraOptions() {
        val options = arrayOf("Capture Photo", "Record Video")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose an option")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> capturePhoto()
                1 -> recordVideo()
            }
        }
        builder.show()
    }

    private fun capturePhoto() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_IMAGE_REQUEST_CODE)
    }

    private fun recordVideo() {
        val videoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(videoIntent, 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> {
                if (resultCode == RESULT_OK) {
                    val photo = data?.extras?.get("data") as Bitmap
                    val imageUri = bitmapToUri(photo)
                    uploadImageToFirebase(imageUri)
                }
            }
            2 -> {
                if (resultCode == RESULT_OK) {
                    val videoUri = data?.data
                    uploadVideoToFirebase(videoUri)
                }
            }
        }
    }

    private fun bitmapToUri(bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            contentResolver,
            bitmap,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    private fun uploadVideoToFirebase(videoUri: Uri?) {
        if (videoUri != null) {
            val filename = UUID.randomUUID().toString()
            val ref = storageRef.child("videos/$filename")
            ref.putFile(videoUri)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener { uri ->
                        voice_sendMessage("video", uri.toString())
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Handle permission request result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                // Permissions granted, start voice recording
                startRecording()
                // Stop recording after 10 seconds
                Handler().postDelayed({
                    stopRecording()
                    // Upload recorded voice to Firebase Storage
                    uploadVoiceToFirebase()
                }, 3000) // 10 seconds
            } else {
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
            }
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
                val db1=FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("UserInfo")
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