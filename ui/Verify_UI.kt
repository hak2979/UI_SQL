package com.example.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.ui.R.*

class Verify_UI : AppCompatActivity() {
    private lateinit var one: TextView
    private lateinit var two: TextView
    private lateinit var three: TextView
    private lateinit var four: TextView
    private lateinit var five: TextView
    private lateinit var six: TextView
    private lateinit var seven: TextView
    private lateinit var eight: TextView
    private lateinit var nine: TextView
    private lateinit var zero: TextView
    private lateinit var empty: ImageButton
    private lateinit var verify_1: TextView
    private lateinit var verify_2: TextView
    private lateinit var verify_3: TextView
    private lateinit var verify_4: TextView
    var is_first:Boolean = false
    var is_seconed:Boolean = false
    var is_third:Boolean = false
    var is_fourth:Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_verify_ui)
            init()
    }

    fun ver(view: View)
    {
        if(is_first && is_seconed && is_third && is_fourth)
        {
            val intent =Intent(this,Login::class.java)
            startActivity(intent)
            finish()
        }
    }
    fun init ()
    {
        one=findViewById<TextView>(id.no_1)
        two=findViewById<TextView>(id.no_2)
        three=findViewById<TextView>(id.no_3)
        four=findViewById<TextView>(id.no_4)
        five=findViewById<TextView>(id.no_5)
        six=findViewById<TextView>(id.no_6)
        seven=findViewById<TextView>(id.no_7)
        eight=findViewById<TextView>(id.no_8)
        nine=findViewById<TextView>(id.no_9)
        zero=findViewById<TextView>(id.no_0)
        empty=findViewById<ImageButton>(id.no_em)

        verify_1=findViewById(id.verify_no_1)
        verify_2=findViewById(id.verify_no_2)
        verify_3=findViewById(id.verify_no_3)
        verify_4=findViewById(id.verify_no_4)
    }
    fun button_press(view: View)
    {
        if(one.isPressed)
        {
            if(!is_first)
            {
                verify_1.setText("1")
                is_first=true
            }
            else if(!is_seconed)
            {
                verify_2.setText("1")
                is_seconed=true
            }
            else if(!is_third)
            {
                verify_3.setText("1")
                is_third=true
            }
            else if(!is_fourth)
            {
                verify_4.setText("1")
                is_fourth=true
            }
        }
        else if(two.isPressed)
        {
            if(!is_first)
            {
                verify_1.setText("2")
                is_first=true
            }
            else if(!is_seconed)
            {
                verify_2.setText("2")
                is_seconed=true
            }
            else if(!is_third)
            {
                verify_3.setText("2")
                is_third=true
            }
            else if(!is_fourth)
            {
                verify_4.setText("2")
                is_fourth=true
            }
        }
        else if(three.isPressed)
        {
            if(!is_first)
            {
                verify_1.setText("3")
                is_first=true
            }
            else if(!is_seconed)
            {
                verify_2.setText("3")
                is_seconed=true
            }
            else if(!is_third)
            {
                verify_3.setText("3")
                is_third=true
            }
            else if(!is_fourth)
            {
                verify_4.setText("3")
                is_fourth=true
            }
        }
        else if(four.isPressed)
        {
            if(!is_first)
            {
                verify_1.setText("4")
                is_first=true
            }
            else if(!is_seconed)
            {
                verify_2.setText("4")
                is_seconed=true
            }
            else if(!is_third)
            {
                verify_3.setText("4")
                is_third=true
            }
            else if(!is_fourth)
            {
                verify_4.setText("4")
                is_fourth=true
            }
        }
        else if(five .isPressed)
        {
            if(!is_first)
            {
                verify_1.setText("5")
                is_first=true
            }
            else if(!is_seconed)
            {
                verify_2.setText("5")
                is_seconed=true
            }
            else if(!is_third)
            {
                verify_3.setText("5")
                is_third=true
            }
            else if(!is_fourth)
            {
                verify_4.setText("5")
                is_fourth=true
            }
        }
        else if(six.isPressed)
        {
            if(!is_first)
            {
                verify_1.setText("6")
                is_first=true
            }
            else if(!is_seconed)
            {
                verify_2.setText("6")
                is_seconed=true
            }
            else if(!is_third)
            {
                verify_3.setText("6")
                is_third=true
            }
            else if(!is_fourth)
            {
                verify_4.setText("6")
                is_fourth=true
            }
        }
        else if(seven.isPressed)
        {
            if(!is_first)
            {
                verify_1.setText("7")
                is_first=true
            }
            else if(!is_seconed)
            {
                verify_2.setText("7")
                is_seconed=true
            }
            else if(!is_third)
            {
                verify_3.setText("7")
                is_third=true
            }
            else if(!is_fourth)
            {
                verify_4.setText("7")
                is_fourth=true
            }
        }
        else if(eight.isPressed)
        {
            if(!is_first)
            {
                verify_1.setText("8")
                is_first=true
            }
            else if(!is_seconed)
            {
                verify_2.setText("8")
                is_seconed=true
            }
            else if(!is_third)
            {
                verify_3.setText("8")
                is_third=true
            }
            else if(!is_fourth)
            {
                verify_4.setText("8")
                is_fourth=true
            }
        }
        if(zero.isPressed)
        {
            if(!is_first)
            {
                verify_1.setText("0")
                is_first=true
            }
            else if(!is_seconed)
            {
                verify_2.setText("0")
                is_seconed=true
            }
            else if(!is_third)
            {
                verify_3.setText("0")
                is_third=true
            }
            else if(!is_fourth)
            {
                verify_4.setText("0")
                is_fourth=true
            }
        }
        else if(nine.isPressed)
        {
            if(!is_first)
            {
                verify_1.setText("9")
                is_first=true
            }
            else if(!is_seconed)
            {
                verify_2.setText("9")
                is_seconed=true
            }
            else if(!is_third)
            {
                verify_3.setText("9")
                is_third=true
            }
            else if(!is_fourth)
            {
                verify_4.setText("9")
                is_fourth=true
            }
        }
        else if(empty.isPressed)
        {
            if(is_fourth)
            {
                verify_4.setText("")
                is_fourth=false
            }
            else if(is_third)
            {
                verify_3.setText("")
                is_third=false
            }
            else if(is_seconed)
            {
                verify_2.setText("")
                is_seconed=false
            }
            else if(is_first)
            {
                verify_1.setText("")
                is_first=false
            }
        }
    }
}