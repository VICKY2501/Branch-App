package com.example.branchapp.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.branchapp.databinding.FragmentSingleMessageBinding
import com.example.branchapp.utils.DataStorage
import com.example.branchapp.view.adapters.MessageThreadAdapter
import com.rishi.branchinternational.model.database.BranchInternationalRepository
import com.example.branchapp.models.entity.Message
import com.example.branchapp.models.repository.AppRepository
import com.example.branchapp.viewmodels.AppViewModel
import com.example.branchapp.viewmodels.AppViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
@AndroidEntryPoint
class SingleMessageFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var fragmentSingleMessageThreadBinding: FragmentSingleMessageBinding

    @Inject
    lateinit var branchInternationalRepository: BranchInternationalRepository

    @Inject
    lateinit var appRepository: AppRepository

    @Inject
    lateinit var dataStore: DataStorage

    private var singleMessageThread: Int? = null

    private val appViewModel: AppViewModel by viewModels {
        AppViewModelFactory(appRepository, dataStore, branchInternationalRepository)
    }

    // Called when the fragment is first created.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment and return the root view.
        fragmentSingleMessageThreadBinding = FragmentSingleMessageBinding.inflate(
            inflater, container, false
        )
        return fragmentSingleMessageThreadBinding.root
    }

    // Called after onCreateView() and ensures that the fragment's view hierarchy is fully created.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve thread ID from the navigation arguments.
        val args: SingleMessageFragmentArgs by navArgs()
        singleMessageThread = args.threadId

        // Set the action bar title to indicate the current thread.
        (activity as AppCompatActivity?)?.supportActionBar?.title =
            "Messages on thread: $singleMessageThread"

        // Set up the RecyclerView and its adapter.
        fragmentSingleMessageThreadBinding.rvMessageThread.layoutManager =
            LinearLayoutManager(requireActivity())
        val messageThreadAdapter = MessageThreadAdapter(this@SingleMessageFragment)
        fragmentSingleMessageThreadBinding.rvMessageThread.adapter = messageThreadAdapter

        // Fetch all messages and observe changes in the message list.
        CoroutineScope(Dispatchers.Main).launch {
            appViewModel.getAllMessages()
        }

        appViewModel.messages.observe(viewLifecycleOwner) { messageList ->
            // Filter messages for the current thread and sort them by timestamp.
            val singleThreadMessage: MutableList<Message> = mutableListOf()
            for (messageThread in messageList) {
                if (messageThread.threadId == singleMessageThread) {
                    singleThreadMessage.add(messageThread)
                }
            }
            singleThreadMessage.sortBy { it.timestamp }

            // Update the adapter with the filtered messages.
            messageThreadAdapter.messages = singleThreadMessage
            messageThreadAdapter.notifyDataSetChanged()

            // Scroll the RecyclerView to the last message.
            fragmentSingleMessageThreadBinding.rvMessageThread.scrollToPosition(singleThreadMessage.size - 1)
        }

        // Scroll the RecyclerView to the last message when new messages are added.
        fragmentSingleMessageThreadBinding.rvMessageThread.scrollToPosition(messageThreadAdapter.itemCount - 1)

        // Observe new messages sent success and add them to the adapter.
        appViewModel.newMessagesSentSuccess.observe(viewLifecycleOwner) {
            if (it) {
                val newMessage: Message? = appViewModel.newMessages.value
                if (newMessage != null) {
                    messageThreadAdapter.messages.add(newMessage)
                    val position = messageThreadAdapter.itemCount - 1
                    messageThreadAdapter.notifyItemInserted(position)
                    fragmentSingleMessageThreadBinding.rvMessageThread.scrollToPosition(position)
                }
            }
        }

        // Observe loading state and show/hide progress bar accordingly.
        appViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            fragmentSingleMessageThreadBinding.progressBar.visibility =
                if (isLoading) View.VISIBLE else View.GONE
        }

        // Set up the click listener for the send button to post a message on the current thread.
        sendMessageOnThread()
    }

    // Function to send a message on the current thread.
    private fun sendMessageOnThread() {
        fragmentSingleMessageThreadBinding.buttonSend.setOnClickListener {
            val messageText = fragmentSingleMessageThreadBinding.editTextMessage.text.toString()
            CoroutineScope(Dispatchers.Main).launch {
                appViewModel.postMessage(singleMessageThread!!, messageText)
                fragmentSingleMessageThreadBinding.editTextMessage.text.clear()
            }
        }
    }
}