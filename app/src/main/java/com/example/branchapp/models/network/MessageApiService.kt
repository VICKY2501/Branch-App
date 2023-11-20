package com.rishi.branchinternational.model.network

import com.rishi.branchinternational.model.entity.AuthTokenResponse
import com.rishi.branchinternational.model.entity.LoginData
import com.example.branchapp.models.entity.Message
import com.rishi.branchinternational.model.entity.MessagePostRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface MessageApiService {

    /**
     * POST request to log in with user credentials and obtain an authentication token.
     *
     * @param loginData LoginData object containing user's username and password.
     * @return Response object containing AuthTokenResponse if successful.
     */
    @POST("api/login")
    suspend fun logInBody(
        @Body loginData: LoginData
    ): Response<AuthTokenResponse>

    /**
     * GET request to retrieve a list of messages.
     *
     * @return Response object containing a list of Message objects if successful.
     */
    @GET("api/messages")
    suspend fun getMessages(): Response<MutableList<Message>>

    /**
     * POST request to send a new message.
     *
     * @param messagePostRequest MessagePostRequest object containing thread ID and message body.
     * @return Response object containing the sent Message object if successful.
     */
    @POST("api/messages")
    suspend fun postMessage(
        @Body messagePostRequest: MessagePostRequest
    ): Response<Message>

    /**
     * POST request to reset messages. This endpoint may not need a request body.
     *
     * @return Response object containing a list of Message objects after resetting messages if successful.
     */
    @POST("api/reset")
    suspend fun resetMessages(): Response<MutableList<Message>>
}
