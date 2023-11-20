package com.example.branchapp.models.repository
import com.example.branchapp.exceptions.Emptytoken
import com.rishi.branchinternational.model.entity.AuthTokenResponse
import com.rishi.branchinternational.model.entity.LoginData
import com.example.branchapp.models.entity.Message
import com.rishi.branchinternational.model.entity.MessagePostRequest
import com.rishi.branchinternational.model.network.MessageApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Repository class responsible for handling API requests related to messaging operations.
 *
 * @param apiService MessageApiService instance for API communication.
 * @param dispatcher CoroutineDispatcher for performing background tasks.
 */
class AppRepository @Inject constructor(
    private val apiService: MessageApiService,
    private val dispatcher: CoroutineDispatcher
) {

    /**
     * Function to authenticate the user and obtain an authentication token.
     *
     * @param username User's username.
     * @param password User's password.
     * @return Result object containing AuthTokenResponse if successful, or EmptyAuthTokenException if unsuccessful.
     */
    suspend fun login(username: String, password: String): Result<AuthTokenResponse> {
        return withContext(dispatcher) {
            try {
                val response = apiService.logInBody(LoginData(username, password))
                val authTokenResponse: AuthTokenResponse? = response.body()

                if (response.isSuccessful && authTokenResponse != null) {
                    Result.success(authTokenResponse)
                } else {
                    Result.failure(Emptytoken())
                }
            } catch (e: IllegalStateException) {
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Function to retrieve all messages from the API.
     *
     * @return Result object containing a list of Message objects if successful, or EmptyAuthTokenException if unsuccessful.
     */
    suspend fun getAllMessages(): Result<MutableList<Message>?> {
        return withContext(dispatcher) {
            return@withContext try {
                val response = apiService.getMessages()
                val messagesResponse: MutableList<Message>? = response.body()

                if (response.isSuccessful) {
                    Result.success(messagesResponse)
                } else {
                    Result.failure(Emptytoken())
                }
            } catch (e: IllegalStateException) {
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Function to post a new message to the API.
     *
     * @param threadId ID of the message thread.
     * @param messageBody Body of the message.
     * @return Result object containing the sent Message if successful, or EmptyAuthTokenException if unsuccessful.
     */
    suspend fun postMessage(threadId: Int, messageBody: String): Result<Message?> {
        return withContext(dispatcher) {
            try {
                val response = apiService.postMessage(MessagePostRequest(threadId, messageBody))
                val messageResponse: Message? = response.body()

                if (response.isSuccessful) {
                    Result.success(messageResponse)
                } else {
                    Result.failure(Emptytoken())
                }
            } catch (e: IllegalStateException) {
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Function to reset all messages through the API.
     *
     * @return Result object containing a list of Empty MutableList<Message> objects after resetting messages if successful,
     *         or EmptyAuthTokenException if unsuccessful.
     */
    suspend fun resetMessages(): Result<MutableList<Message>?> {
        return withContext(dispatcher) {
            return@withContext try {
                val response = apiService.resetMessages()
                val messagesResponse: MutableList<Message>? = response.body()

                if (response.isSuccessful) {
                    Result.success(messagesResponse)
                } else {
                    Result.failure(Emptytoken())
                }
            } catch (e: IllegalStateException) {
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
