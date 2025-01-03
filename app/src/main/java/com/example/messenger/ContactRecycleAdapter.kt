package com.example.messenger

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactRecycleAdapter(
    private val context: Context,
    private val contacts: List<User>
) : RecyclerView.Adapter<ContactRecycleAdapter.ViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = layoutInflater.inflate(R.layout.itemlistlayout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = contacts[position]

       //Users email will be displayed in the textView
        holder.contactNameTV.text = contact.email

       //Will eventually hold last message. Now just a "Hej".
        holder.lastMessageTV.text = "Hej"

        //Email, id and userName is sent to ChatActivity.
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ChatActivity::class.java)
            intent.putExtra("contactEmail", contact.email)
            intent.putExtra("CONTACT_ID_KEY", contact.id)
            intent.putExtra("USER_NAME", contact.userName)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = contacts.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactNameTV: TextView = itemView.findViewById(R.id.contactNameTV)
        val lastMessageTV: TextView = itemView.findViewById(R.id.lastMessageTV)
    }
}
