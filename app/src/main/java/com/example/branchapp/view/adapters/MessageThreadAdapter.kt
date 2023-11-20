package com.example.branchapp.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.branchapp.R
import com.example.branchapp.databinding.ItemMessageLayoutBinding
import com.example.branchapp.view.fragments.MessageFragment
import com.example.branchapp.view.fragments.SingleMessageFragment
import com.example.branchapp.models.entity.Message

/**
 * Adapter class for binding message data to RecyclerView in MessageThreadFragment.
 *
 * @param fragment The fragment using this adapter.
 */
class MessageThreadAdapter(private val fragment: Fragment) :
    RecyclerView.Adapter<MessageThreadAdapter.ViewHolder>() {

    // List to hold the messages to be displayed.
    var messages: MutableList<Message> = mutableListOf()

    // Inflates the item layout and returns a ViewHolder object.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemMessageLayoutBinding: ItemMessageLayoutBinding =
            ItemMessageLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemMessageLayoutBinding)
    }

    // Binds the data at the specified position to the ViewHolder.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = messages[position]

        // Bind message data to the ViewHolder's views.
        holder.id.text =   "Message Id: "+ item.id.toString()
        holder.userId.text = item.userId
        holder.threadId.text = "Thread Id: "+item.threadId.toString()
        holder.timestamp.text = "Time Stamp: "+item.timestamp
        holder.messageBody.text = item.messageBody

        // Handles click event for replying to a message.
        holder.threadReplyButton.setOnClickListener {
            val threadID = item.threadId
            (fragment as MessageFragment).navigationToSendMessageThreadFragment(threadID)
        }

        // Checks if the fragment is SingleMessageThreadFragment and hides specific views.
        if (fragment is SingleMessageFragment) {
//            holder.threadId.visibility = View.GONE
            holder.threadReplyButton.visibility = View.GONE
            holder.messageText.visibility = View.GONE
        }
    }

    // Returns the total number of items in the data set.
    override fun getItemCount(): Int {
        return messages.size
    }

    // ViewHolder class representing the UI components of each item in the RecyclerView.
    class ViewHolder(itemView: ItemMessageLayoutBinding) : RecyclerView.ViewHolder(itemView.root) {
        val id = itemView.id
        var threadId = itemView.threadId
        val userId = itemView.userId
        val messageBody = itemView.messageBody
        val timestamp = itemView.timestamp
        val threadReplyButton = itemView.replyButton
        val messageText = itemView.messageText
    }
}
