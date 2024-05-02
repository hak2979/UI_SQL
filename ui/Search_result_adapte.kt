package com.example.ui

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import java.util.Locale

class Search_result_adapte(private val mContext: Context, private var gigs: List<gig_d>) :
    RecyclerView.Adapter<Search_result_adapte.ViewHolder>() {

    private var gigsFull: List<gig_d> = ArrayList(gigs) // Copy of the original list

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.name_service)
        val rateTextView: TextView = itemView.findViewById(R.id.rate_service)
        val experienceTextView: TextView = itemView.findViewById(R.id.Experience_of_parent)
        val availibilityTextView: TextView = itemView.findViewById(R.id.is_availibil)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.inside_car, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val gig = gigs[position]
        holder.nameTextView.text = gig.name
        holder.rateTextView.text = gig.price
        holder.experienceTextView.text = gig.experience
        if (gig.isAvailable) {
            holder.availibilityTextView.text = "Available"
        } else {
            holder.availibilityTextView.text = "Not Available"
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, Detail::class.java)
            val uid = gig.uid
                intent.putExtra("id", uid?.toInt())
                mContext.startActivity(intent)
        }
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(40, 20, 20, 20)
        params.gravity = Gravity.CENTER_HORIZONTAL
        holder.itemView.layoutParams = params
    }

    override fun getItemCount(): Int {
        return gigs.size
    }

    fun filter(text: String) {
        val searchText = text.toLowerCase(Locale.getDefault())
        gigs = if (searchText.isEmpty()) {
            gigsFull
        } else {
            gigsFull.filter { it.name.toLowerCase(Locale.getDefault()).contains(searchText) }
        }
        notifyDataSetChanged()
    }
}