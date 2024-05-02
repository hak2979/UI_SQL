package com.example.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class Pass_resetui : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pass_resetui)
    }

    fun log_(view: View)
    {
        val intent=Intent(this,Login::class.java)
        startActivity(intent)
        finish()
    }
}