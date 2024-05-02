package com.example.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class Camera_photo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_photo)
    }
    fun PTer(view: View)
    {
        finish()
    }
    fun vid_s(view: View)
    {
        val intent=Intent(this,Vid_Screen::class.java)
        startActivity(intent)
        finish()
    }
}