package com.example.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class chat_main : AppCompatActivity() {
    var arr= arrayOf("@mipmap/rand_img_foreground","@mipmap/rand_img_foreground","@mipmap/rand_img_foreground","@mipmap/rand_img_foreground","@mipmap/rand_img_foreground")
    private lateinit var db: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerview: RecyclerView
    private lateinit var mUser: MutableList<Chat_people_struc>
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_main)

        val clus=findViewById<LinearLayout>(R.id.clustp)
        for(i in arr)
        {
            val Re=RelativeLayout(this)
            val widthInPixels = resources.getDimensionPixelSize(R.dimen.st)
            val pl = RelativeLayout.LayoutParams(
                widthInPixels,
                widthInPixels)
            pl.setMargins(20,20,20,20)

            Re.layoutParams=pl
            val in_border=TextView(this)
            in_border.layoutParams=RelativeLayout.LayoutParams(
                widthInPixels,
                widthInPixels
            )
            in_border.setBackgroundResource(R.drawable.statu)

            val img_st=ImageView(this)
            img_st.setBackgroundResource(R.drawable.user_solid)
            val params = RelativeLayout.LayoutParams(
                resources.getDimensionPixelSize(R.dimen.im),
                resources.getDimensionPixelSize(R.dimen.im),
            )
            params.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE)
            img_st.layoutParams=params

            img_st.isClickable=true
            img_st.setOnClickListener{
                val intent= Intent(this,Community::class.java)
                startActivity(intent)
            }

            val s_o=TextView(this)
            val new_p=RelativeLayout.LayoutParams(
                resources.getDimensionPixelSize(R.dimen.u),
                resources.getDimensionPixelSize(R.dimen.u)
            )
            new_p.setMargins(0,2,5,0)
            new_p.addRule(RelativeLayout.ALIGN_PARENT_END,RelativeLayout.TRUE)
            s_o.layoutParams=new_p
            s_o.setBackgroundResource(R.drawable.sta_switch)
            Re.addView(in_border)
            Re.addView(img_st)
            Re.addView(s_o)

            clus.addView(Re)
        }

        mUser= mutableListOf()
        recyclerview=findViewById(R.id.people_chat_select)
        recyclerview.layoutManager=LinearLayoutManager(this)
        readuser()
    }

    private fun readuser() {
        auth = FirebaseAuth.getInstance()

        val dbChats = FirebaseDatabase.getInstance().getReference().child("Chats")
        dbChats.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val uniqueUsers = HashSet<String>() // Set to store unique user IDs
                for (messageSnapshot in snapshot.children) {
                    val sender = messageSnapshot.child("sender").value.toString()
                    val receiver = messageSnapshot.child("recv").value.toString()

                    // Check if the sender is the current user and if the receiver is not already in mUser
                    if (sender == auth.currentUser!!.uid && !uniqueUsers.contains(receiver)) {
                        addUserFromDatabase(receiver)
                        uniqueUsers.add(receiver) // Add receiver to the set
                    }

                    // Check if the receiver is the current user and if the sender is not already in mUser
                    if (receiver == auth.currentUser!!.uid && !uniqueUsers.contains(sender)) {
                        addUserFromDatabase(sender)
                        uniqueUsers.add(sender) // Add sender to the set
                    }
                }
                val adapter_ = ChatPeopleSelectAdapter(this@chat_main, mUser)
                recyclerview.adapter = adapter_
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled event
            }
        })
    }
        /*
        auth=FirebaseAuth.getInstance()
        db=FirebaseDatabase.getInstance().getReference().child("Users")
        db.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children){
                    if(!(i.key.toString().equals(auth.currentUser!!.uid.toString()))) {
                        val name = i.child("UserInfo").child("name").value.toString()
                        val img = i.child("UserInfo").child("img").value.toString()
                        val uid = i.key.toString()
                        mUser.add(Chat_people_struc(name, img, uid))
                    }
                }
                val adapter_=ChatPeopleSelectAdapter(this@chat_main,mUser)
                recyclerview.adapter=adapter_
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })*/
    private fun isUserAdded(userId: String): Boolean {
        // Check if the user with the given userId is already in mUser list
        return mUser.any { it.uid == userId }
    }

    private fun addUserFromDatabase(userId: String) {
        if (!isUserAdded(userId)) {
            val userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val name = dataSnapshot.child("UserInfo").child("name").value.toString()
                    val img = dataSnapshot.child("UserInfo").child("img").value.toString()
                    val uid = dataSnapshot.key.toString()
                    mUser.add(Chat_people_struc(name, img, uid))

                    // Notify adapter of data change here
                    val adapter_ = ChatPeopleSelectAdapter(this@chat_main, mUser)
                    recyclerview.adapter = adapter_
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle onCancelled event
                }
            })
        }
    }

    fun Chat_ppress(view: View)
    {

    }
    fun end_ac(view: View)
    {
        finish()
    }
}