package com.example.messenger

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FriendsRecyclerAdapter(
    private val context: Context,
    private val friends: List<User>
) : RecyclerView.Adapter<FriendsRecyclerAdapter.ViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = layoutInflater.inflate(R.layout.friend_list_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend = friends[position]

        holder.friendNameTV.text = friend.userName

        holder.deleteButton.setOnClickListener(){
            //TODO remove from friendList and add to contactList again.
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ChatActivity::class.java)
            intent.putExtra("contactEmail", friend.email)
            intent.putExtra("CONTACT_ID_KEY", friend.id)
            intent.putExtra("USER_NAME", friend.userName)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = friends.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val friendNameTV: TextView = itemView.findViewById(R.id.friendNameTV)
        val deleteButton : ImageView = itemView.findViewById(R.id.deleteButton)
    }
}