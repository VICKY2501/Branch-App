package com.example.branchapp.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.branchapp.databinding.FragmentMessageBinding
import com.example.branchapp.utils.DataStorage
import com.example.branchapp.view.adapters.MessageThreadAdapter
import com.rishi.branchinternational.model.database.BranchInternationalRepository
import com.example.branchapp.models.repository.AppRepository
import com.example.branchapp.viewmodels.AppViewModel
import com.example.branchapp.viewmodels.AppViewModelFactory
import com.example.branchapp.models.entity.Message
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
@AndroidEntryPoint
class MessageFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var fragmentMessageThreadBinding: FragmentMessageBinding

    private lateinit var navController: NavController

    @Inject
    public lateinit var branchInternationalRepository: BranchInternationalRepository

    @Inject
    public lateinit var appRepository: AppRepository

    @Inject
    public lateinit var dataStore: DataStorage
    private lateinit var messageMap: MutableMap<Int, Message>
    private fun updateOrAddMessage(threadId: Int, newMessage: Message) {
        if (!::messageMap.isInitialized) {
            messageMap = mutableMapOf()
        }
        messageMap[threadId]?.let { existingMessage ->
            // If the timestamp of the new message is greater, update the value in the map
            if (newMessage.timestamp > existingMessage.timestamp) {
                messageMap[threadId] = newMessage
            }
        } ?: run {
            // If the key is not present in the map, add the new message
            messageMap[threadId] = newMessage
        }
    }
    private val appViewModel: AppViewModel by viewModels {
        AppViewModelFactory(appRepository, dataStore, branchInternationalRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentMessageThreadBinding =
            FragmentMessageBinding.inflate(inflater, container, false)
        return fragmentMessageThreadBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the title of the action bar
        (activity as AppCompatActivity?)?.supportActionBar?.title = "All Messages"

        // Initialize the navigation controller
        navController = findNavController()

        // Initialize the RecyclerView layout manager and adapter
        fragmentMessageThreadBinding.rvMessageThread.layoutManager =
            LinearLayoutManager(requireActivity())
        // Adapter class is initialized and fragment is passed in the param.
        val messageThreadAdapter = MessageThreadAdapter(this@MessageFragment)
        // adapter instance is set to the recyclerview to inflate the items.
        fragmentMessageThreadBinding.rvMessageThread.adapter = messageThreadAdapter
        appViewModel.messages.observe(viewLifecycleOwner) { messageList ->
            // Sort messages by timestamp in descending order
            messageMap = mutableMapOf()
            if(messageMap.isNotEmpty()) {
                messageMap.clear()
            }
            messageList.sortByDescending {
                it.timestamp
            }
            for(x in messageList)
            {
                val threadId = x.threadId
                updateOrAddMessage(threadId, x)
            }
            val msg: MutableList<Message> = mutableListOf()
            for(x in messageMap)
            {
                msg.add(x.value)
            }
            // Function to update or add a message to the map based on timestamp
            messageThreadAdapter.messages = msg
            messageThreadAdapter.notifyDataSetChanged()
        }
        // Observe the isLoading LiveData to show/hide the progress bar
        appViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                fragmentMessageThreadBinding.progressBar.visibility =
                    View.VISIBLE
            } else {
                fragmentMessageThreadBinding.progressBar.visibility = View.GONE
            }
        }

        // Call methods to fetch messages and handle reset button click
        getAllMessages()
        resetMessages()

    }

    private fun getAllMessages() {
        // Coroutine to fetch all messages from the repository
        CoroutineScope(Dispatchers.Main).launch {
            appViewModel.getAllMessages()
        }
    }

    private fun resetMessages() {
        // Handle reset button click to reset messages
        fragmentMessageThreadBinding.resetButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                appViewModel.resetMessages()
                Toast.makeText(context, "Messages reset", Toast.LENGTH_SHORT).show()
                /*
                * Here We can call this method or we can use the commented while loop logic below to avoid network request
                */
                appViewModel.getAllMessages()

                /*
                * This loop will basically delete all the messages of the recyclerview which are at 0 index as all new messages are
                * at the top of the recyclerview
                * For now I am keeping the network request only but the code below also works
                */

                /*
                while (messageThreadAdapter.messages.size > 100) {
                    messageThreadAdapter.messages.removeAt(0)
                    messageThreadAdapter.notifyItemRemoved(0)
                }
                */
            }
        }
    }

    // Navigation method to navigate to SingleMessageThreadFragment when clicked on the reply button
    fun navigationToSendMessageThreadFragment(threadID: Int) {
        navController.navigate(
            MessageFragmentDirections.actionHiltMessageFragment2ToHiltSingleMessageFragment(
                threadID
            )
//            MessageFragmentDirections.actionMessageThreadFragmentToSingleMessageThreadFragment(
//                threadID
//            )
        )
    }
}