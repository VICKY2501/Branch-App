package com.rishi.branchinternational.model.network

import com.example.branchapp.utils.DataStorage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * Interceptor class responsible for adding the authentication token to the request header.
 *
 * @property dataStore DataStore instance for fetching the authentication token.
 */
class AuthInterceptor(val dataStore: DataStorage) : Interceptor {
    /**
     * Intercepts the request chain and adds the authentication token to the header if the request is not a login request.
     *
     * @param chain Interceptor.Chain instance representing the request chain.
     * @return Response object after processing the request with the added authentication token.
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val authToken: String
        val requestWithToken: Request

        // Check if the request is not a login request
        if (!originalRequest.url.toString().contains("/login")) {
            authToken = runBlocking {
                dataStore.authTokenFlow.first()
            }
            // Add the authentication token to the request header
            requestWithToken = originalRequest.newBuilder()
                .url(originalRequest.url)
                .addHeader("X-Branch-Auth-Token", authToken.toString())
                .build()
        } else {
            // If it's a login request, proceed with the original request
            requestWithToken = originalRequest.newBuilder()
                .url(originalRequest.url)
                .build()
        }

        // Proceed with the modified request
        return chain.proceed(requestWithToken)
    }
}
