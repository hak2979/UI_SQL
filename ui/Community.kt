package com.example.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class Community : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community)
    }

    fun voice_call(view: View)
    {
        val intent= Intent(this,Voice_call::class.java)
        startActivity(intent)
    }
    fun video_call(view: View)
    {
        val intent= Intent(this,Video_call::class.java)
        startActivity(intent)
    }
    fun end_ac(view: View)
    {
        finish()
    }
    fun camera_s(view: View)
    {
        val intent=Intent(this,Camera_photo::class.java)
        startActivity(intent)
    }

    fun Chat_press(view: View)
    {
        val intent=Intent(this,chat_main::class.java)
        startActivity(intent)
        finish()
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
    fun profil(view: View)
    {
        val intent=Intent(this,pred::class.java)
        startActivity(intent)
        finish()
    }
}