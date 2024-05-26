package com.malenst.sovkom.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.malenst.sovkom.R
import com.malenst.sovkom.model.Message

class ChatAdapter(private var messages: List<Message>) : RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.textViewMessage)

        fun bind(message: Message) {
            textView.text = message.content
            // Установите другие поля, если это необходимо
        }
    }

    fun updateMessages(newMessages: List<Message>) {
        this.messages = newMessages
        notifyDataSetChanged()  // Уведомляем адаптер о том, что данные изменились
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val layout = if (viewType == TYPE_OUTGOING) R.layout.item_message_outgoing else R.layout.item_message_incoming
        val view = layoutInflater.inflate(layout, parent, false)  // Теперь должно работать без ошибок
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount() = messages.size

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderId == myUserId) TYPE_OUTGOING else TYPE_INCOMING
    }

    companion object {
        const val TYPE_OUTGOING = 1
        const val TYPE_INCOMING = 0
        var myUserId: Long = 0 // Здесь нужно установить ID текущего пользователя
    }
}