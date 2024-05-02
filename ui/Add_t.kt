package com.example.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

class Add_t : AppCompatActivity() {
    private var currentUser = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_t)
        this.currentUser = intent.getIntExtra("id", 0)
        Toast.makeText(this,currentUser.toString(),Toast.LENGTH_SHORT).show()
    }
    fun end_ac(view: View)
    {
        finish()
    }
    fun Chat_press(view: View)
    {
        val intent= Intent(this,chat_main::class.java)
        startActivity(intent)
    }


    fun home_screen(view: View)
    {
        val intent= Intent(this,Main_UI::class.java)
        startActivity(intent)
    }
    fun Searc_screen(view: View)
    {
        val intent= Intent(this,Search::class.java)
        startActivity(intent)
    }
    fun Plus_screen(view: View)
    {
        val intent= Intent(this,Add_t::class.java)
        startActivity(intent)
    }
    fun book(view: View)
    {
        val intent=Intent(this,Mentor::class.java)
        intent.putExtra("id",this.currentUser)
        startActivity(intent)
    }
}