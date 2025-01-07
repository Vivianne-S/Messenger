package com.example.messenger

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import org.w3c.dom.Text

class MessagesAdapter(
    context: Context,
    private val messages: MutableList<Messages>
) : RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)
    lateinit var cardView : CardView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val itemView = layoutInflater.inflate(R.layout.message_item, parent, false)
        return MessageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        val currentUsedId = Firebase.auth.currentUser?.uid

       holder.userTextView.text = message.email
       holder.messageTextView.text = message.message
        holder.timeStampTV.text = message.timeStamp


        //gives access to the cardview
        cardView = holder.itemView.findViewById(R.id.cardView)

        //this changes the color of the user
        //only need to reverse to change the other users color
        if(message.userId == currentUsedId) {
            cardView.setCardBackgroundColor(Color.parseColor("#e75555"))
        }

    }

    override fun getItemCount(): Int {
        return messages.size
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userTextView: TextView = itemView.findViewById(R.id.senderTV)
        val messageTextView: TextView = itemView.findViewById(R.id.messageTV)
        val timeStampTV : TextView = itemView.findViewById(R.id.timeStampTV)
    }
}
