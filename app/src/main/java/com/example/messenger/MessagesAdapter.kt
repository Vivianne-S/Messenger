package com.example.messenger

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

// MessagesAdapter
class MessagesAdapter(
    context: Context,
    private val messages: MutableList<Messages>
) : RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)
    private val dateFormatter = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val itemView = layoutInflater.inflate(R.layout.message_item, parent, false)
        return MessageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.userTextView.text = message.email
        holder.messageTextView.text = message.message

        // Format the timestamp
        message.timeStamp?.let { timestamp ->
            holder.timeStampTV.text = dateFormatter.format(timestamp.toDate())
        }
    }

    override fun getItemCount(): Int = messages.size

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userTextView: TextView = itemView.findViewById(R.id.senderTV)
        val messageTextView: TextView = itemView.findViewById(R.id.messageTV)
        val timeStampTV: TextView = itemView.findViewById(R.id.timeStampTV)
    }
}
