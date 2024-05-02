package com.example.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import java.util.zip.Inflater

class Search : AppCompatActivity() {
    private var recent_sear =  arrayOf("Mentor 1","Mentor 2","Mentor 3")
    private var categories= arrayOf("Education","Entreprenuership","Personal Growth")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val search_check=findViewById<EditText>(R.id.search_tab)

        search_check.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable?) {
                val search_enter=s.toString().trim()
                if (search_enter.isNotEmpty()){
                    screen_switch()
                }
            }
        })


        val recent=findViewById<LinearLayout>(R.id.recent_a)
        for(i in recent_sear.indices)
        {
            val recent_lay=layoutInflater.inflate(R.layout.recent_ui,null)
            val unique_search=recent_lay.findViewById<TextView>(R.id.search_in_text)
            unique_search.text= recent_sear[i]
            recent_lay.setPadding(0,20,0,20)

            recent.addView(recent_lay)
        }


        val caterogyinstan=findViewById<LinearLayout>(R.id.category_a)
        for(i in categories.indices)
        {
            val categorlayout=layoutInflater.inflate(R.layout.catogerui,null)
            val nam=categorlayout.findViewById<TextView>(R.id.naming_cat)
            nam.text=categories[i]

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 20, 0, 20)
            categorlayout.layoutParams = params
            categorlayout.setPadding(0,20,0,20)

            caterogyinstan.addView(categorlayout)
            categorlayout.isClickable=true
            categorlayout.setOnClickListener{
                screen_switch()
            }
        }


    }
    fun screen_switch()
    {
        val intent = Intent(this, Search_results::class.java)
        startActivity(intent)
    }
    fun end_ac(view: View)
    {
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
    fun Chat_screen(view: View)
    {
        val intent=Intent(this,chat_main::class.java)
        startActivity(intent)
        finish()
    }
}