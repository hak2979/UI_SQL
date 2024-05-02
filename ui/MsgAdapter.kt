package com.example.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import java.net.URL

const val MSG_TYPE_LEFT: Int = 0
const val MSG_TYPE_RIGHT: Int = 1

class MsgAdapter(private val mContext: Context, private val mChat: MutableList<chatdet>,private val currentUser:String) :
    RecyclerView.Adapter<MsgAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutRes = if (viewType == MSG_TYPE_LEFT) {
            R.layout.m_chat
        } else {
            R.layout.other_chat
        }
        val view = LayoutInflater.from(mContext).inflate(layoutRes, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = mChat[position]
        if (chat.msg!!.startsWith("http") && chat.msgType.equals("img")) {
            // If message is a URL (presumably an image URL)
            holder.msg.visibility = View.GONE
            holder.pic.visibility = View.VISIBLE
            holder.vid.visibility=View.GONE
            Picasso.get().load(chat.msg).fit().into(holder.pic)
        } else if(chat.msgType.equals("text")){
            // If message is text
            holder.msg.text = chat.msg
            holder.msg.visibility = View.VISIBLE
            holder.pic.visibility = View.GONE
            holder.vid.visibility=View.GONE
        }
        else if (chat.msgType.equals("voice")){
            holder.msg.visibility = View.VISIBLE
            holder.msg.text = "Tap to Play Audio"
            holder.msg.setOnClickListener {
                playAudio(chat.msg!!)
            }
            holder.pic.visibility = View.GONE
            holder.vid.visibility=View.GONE
        }
        else if (chat.msgType.equals("video")){
            holder.msg.visibility = View.GONE
            holder.pic.visibility = View.GONE
            holder.img.visibility=View.VISIBLE
            holder.vid.visibility = View.VISIBLE
            playVideo(chat.msg!!, holder.vid)
        }
        val senderId = chat.sender

        var url = "http://192.168.100.146/Getimg.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> { response ->

                // Load the retrieved image into ImageView as a round image using Picasso
                val placehol=getCirclePlaceholderDrawable(mContext.resources)
                Picasso.get().load(response.toString()).transform(CircleTransform()).placeholder(placehol).fit().into(holder.img)
            },
            Response.ErrorListener { error ->
                Log.e( "Error occurred", error.message.toString())
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id"] = senderId.toString()
                return params
            }
        }

        Volley.newRequestQueue(mContext).add(stringRequest)

    }

    override fun getItemCount(): Int {
        return mChat.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val msg: TextView = itemView.findViewById(R.id.msg_others)
        val img: ImageView = itemView.findViewById(R.id.pic_other)
        val pic:ImageView=itemView.findViewById(R.id.picm_other)
        val vid:VideoView=itemView.findViewById(R.id.vidm_other)


        init {
            msg.setOnLongClickListener {
                showOptionsDialog(adapterPosition)
                true
            }
        }

        private fun showOptionsDialog(position: Int) {
            val options = arrayOf("Edit", "Delete")
            AlertDialog.Builder(mContext)
                .setTitle("Choose Action")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> showEditDialog(position)
                        1 -> deleteMessage(position)
                    }
                }
                .show()
        }

        private fun showEditDialog(position: Int) {
            val editText = EditText(mContext)
            AlertDialog.Builder(mContext)
                .setTitle("Edit Message")
                .setView(editText)
                .setPositiveButton("Update") { _, _ ->
                    val newMessage = editText.text.toString()
                    updateMessage(position, newMessage)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        private fun deleteMessage(position: Int) {
            val messageId = mChat[position].msgid
            if(mChat[position].sender.equals(currentUser)){
                val url = "http://192.168.100.146/Chat_delete.php"

                val stringRequest = object : StringRequest(
                    Request.Method.POST, url,
                    Response.Listener<String> { response ->
                        if (response.trim() == "success") {
                            Log.d("Suc","com")
                            mChat.removeAt(position)
                            notifyItemChanged(position)
                        } else {
                            Log.e("Msgdel",response.toString())
                        }
                    },
                    Response.ErrorListener { error ->
                    }) {
                    override fun getParams(): MutableMap<String, String> {
                        val params = HashMap<String, String>()
                        params["mn"] = messageId.toString()
                        // Add other profile data as needed
                        return params
                    }
                }

                // Add the request to the RequestQueue
                Volley.newRequestQueue(mContext).add(stringRequest)
            }

        }

        private fun updateMessage(position: Int, newMessage: String) {
            val messageId = mChat[position].msgid // Assuming you have a messageId field in your chatdet model
            if (messageId != null) {
                val currentTime = System.currentTimeMillis() // Get current timestamp
                val messageTimestamp = mChat[position].timestamp // Assuming you have a timestamp field in your chatdet model

                if (messageTimestamp != null) {
                    val timeElapsed = currentTime - messageTimestamp
                    val timeElapsedMinutes = timeElapsed / (60 * 1000) // Convert milliseconds to minutes

                    if (timeElapsedMinutes <= 5 && mChat[position].sender!!.equals(currentTime) && mChat[position].msgType.equals("text")) {

                        val url = "http://192.168.100.146/Chat_up.php"

                        val stringRequest = object : StringRequest(
                            Request.Method.POST, url,
                            Response.Listener<String> { response ->
                                if (response.trim() == "success") {
                                    Log.d("Suc","com")
                                    mChat[position].msg=newMessage
                                    notifyItemChanged(position)
                                } else {
                                    Log.e("Msgdel",response.toString())
                                }
                            },
                            Response.ErrorListener { error ->
                            }) {
                            override fun getParams(): MutableMap<String, String> {
                                val params = HashMap<String, String>()
                                params["mn"] = messageId.toString()
                                params["msg"]=newMessage
                                // Add other profile data as needed
                                return params
                            }
                        }

                        // Add the request to the RequestQueue
                        Volley.newRequestQueue(mContext).add(stringRequest)
                    } else {
                        // Message cannot be edited after 5 minutes
                        Toast.makeText(mContext, "Cannot edit message after 5 minutes", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val authenticate = FirebaseAuth.getInstance()
        return if (!mChat[position].sender.equals(currentUser)) MSG_TYPE_RIGHT else MSG_TYPE_LEFT
    }

    private fun playAudio(audioUrl: String) {
        try {
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(audioUrl)
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: Exception) {
            Log.e("MediaPlayer", "Error playing audio: ${e.message}")
            Toast.makeText(mContext, "Error playing audio", Toast.LENGTH_SHORT).show()
        }
    }
    private fun playVideo(videoUrl: String, videoView: VideoView) {
        try {
            val uri = Uri.parse(videoUrl)
            videoView.setVideoURI(uri)
            videoView.setOnPreparedListener { mp -> mp.isLooping = true }
            videoView.setOnErrorListener { mp, what, extra ->
                Log.e("VideoPlayer", "Error occurred while playing video. Error code: $what, extra: $extra")
                Toast.makeText(mContext, "Error playing video", Toast.LENGTH_SHORT).show()
                false
            }
            videoView.requestFocus()
            videoView.start()
        } catch (e: Exception) {
            Log.e("VideoPlayer", "Error playing video: ${e.message}")
            Toast.makeText(mContext, "Error playing video", Toast.LENGTH_SHORT).show()
        }
    }
    fun getCirclePlaceholderDrawable(resources: Resources): Drawable {
        // Create a circular placeholder drawable
        val size = 300 // Set the size of the placeholder circle
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            isAntiAlias = true
            color = Color.LTGRAY
        }
        val radius = size / 2f
        canvas.drawCircle(radius, radius, radius, paint)
        return BitmapDrawable(resources, bitmap)
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