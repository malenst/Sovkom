package com.malenst.sovkom.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.malenst.sovkom.R
import com.malenst.sovkom.model.Message

class ChatAdapter(private var messages: MutableList<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class OutgoingMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.textViewMessage)

        fun bind(message: Message) {
            textView.text = message.content
        }
    }

    class IncomingMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.textViewMessage)

        fun bind(message: Message) {
            textView.text = message.content
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_OUTGOING) {
            val view = layoutInflater.inflate(R.layout.item_message_outgoing, parent, false)
            OutgoingMessageViewHolder(view)
        } else {
            val view = layoutInflater.inflate(R.layout.item_message_incoming, parent, false)
            IncomingMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is OutgoingMessageViewHolder) {
            holder.bind(message)
        } else if (holder is IncomingMessageViewHolder) {
            holder.bind(message)
        }
    }

    override fun getItemCount() = messages.size

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderId == myUserId) TYPE_OUTGOING else TYPE_INCOMING
    }

    fun addMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    fun updateMessages(newMessages: List<Message>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    companion object {
        const val TYPE_OUTGOING = 1
        const val TYPE_INCOMING = 0
        var myUserId: Long = 0
    }
}