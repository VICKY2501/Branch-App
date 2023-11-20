package com.example.branchapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.branchapp.utils.DataStorage
import com.rishi.branchinternational.model.database.BranchInternationalRepository
import com.rishi.branchinternational.model.entity.AuthTokenResponse
import com.example.branchapp.models.entity.Message
import com.example.branchapp.models.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val authRepository: AppRepository,
    private val dataStore: DataStorage,
    private val branchInternationalRepository: BranchInternationalRepository
) : ViewModel() {

    // MutableLiveData to hold authentication result
    private var _authResult = MutableLiveData<Result<AuthTokenResponse>>()
    val authResult: LiveData<Result<AuthTokenResponse>> get() = _authResult

    // MutableLiveData to hold the list of messages
    private var _messages = MutableLiveData<MutableList<Message>>()
    val messages: LiveData<MutableList<Message>> get() = _messages

    // MutableLiveData to hold new messages
    private var _newMessages = MutableLiveData<Message>()
    val newMessages: LiveData<Message> get() = _newMessages

    // MutableLiveData to indicate if new messages were sent successfully
    private var _newMessagesSentSuccess = MutableLiveData<Boolean>()
    val newMessagesSentSuccess: LiveData<Boolean> get() = _newMessagesSentSuccess

    // MutableLiveData to indicate loading state
    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // Function to handle user authentication with email and password
    fun signInWithEmailAnsPassword(email: String, password: String) {
        viewModelScope.launch(Dispatchers.Main) {
            // Authenticate user with provided credentials
            val authResultToken = authRepository.login(email, password)
            _authResult.postValue(authResultToken)
            // Save authentication token to data store if authentication was successful
            authResultToken.getOrNull()?.let {
                dataStore.saveAuthToken(it.authToken)
            }
        }
    }

    // Function to fetch all messages from the repository
    suspend fun getAllMessages() {
        _isLoading.postValue(true)
        withContext(Dispatchers.IO) {
            // Fetch messages from the repository
            val allMessages = authRepository.getAllMessages()
            allMessages.getOrNull()?.let {
                // Update messages LiveData with the fetched messages
                _messages.postValue(it)
                // Update messageMap LiveData with the grouped messages
            }
            _isLoading.postValue(false)
        }
    }

    // Function to post a message to a specific thread
    suspend fun postMessage(threadId: Int, messageText: String) {
        withContext(Dispatchers.IO) {
            _newMessagesSentSuccess.postValue(false)
            // Post the message to the specified thread
            val postResult = authRepository.postMessage(threadId, messageText)
            if (postResult.isSuccess) {
                postResult.getOrNull()?.let {
                    // Update newMessages LiveData with the posted message
                    _newMessages.postValue(it)
                    // Indicate that the message was sent successfully
                    _newMessagesSentSuccess.postValue(true)
                }
            }
        }
    }

    // Function to reset messages
    suspend fun resetMessages() {
        withContext(Dispatchers.IO) {
            _isLoading.postValue(true)
            // Reset messages in the repository
            val allMessages = authRepository.resetMessages()
            allMessages.getOrNull()?.let {
                // Update messages LiveData with the reset messages
                _messages.postValue(it)
            }
            _isLoading.postValue(false)
        }
    }

    // Function to check if an authentication token exists in the data store
    fun getAuthTokenFromDataStore(): Boolean {
        // Run a blocking coroutine to get the authentication token from the data store
        val authToken: String = runBlocking { dataStore.authTokenFlow.first() }
        // Return true if the token is not empty, indicating that the user is authenticated
        return authToken.isNotEmpty()
    }
}
