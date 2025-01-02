package com.example.messenger

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactRecycleAdapter (context : Context, val contacts : List<User>): RecyclerView.Adapter<ContactRecycleAdapter.ViewHolder>() {

    var layoutInflator = LayoutInflater.from(context)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContactRecycleAdapter.ViewHolder {
        val itemView = layoutInflator.inflate(R.layout.itemlistlayout, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ContactRecycleAdapter.ViewHolder, position: Int) {
        val contact = contacts[position]
        holder.contactNameTV.text = contact.email
        holder.lastMessageTV.text = "Hej"
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var contactNameTV = itemView.findViewById<TextView>(R.id.contactNameTV)
        var lastMessageTV = itemView.findViewById<TextView>(R.id.lastMessageTV)

    }
}