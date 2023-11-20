package com.example.branchapp.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStorage(private val context: Context) {
    private val Context.datastorage by preferencesDataStore(name = "data_store")

    // Constants for preferences keys.
    companion object {
        private val STRING_KEY_AUTH_TOKEN = stringPreferencesKey("login_token")
    }

    /**
     * Function to save the authentication token in DataStore preferences.
     *
     * @param authToken The authentication token to be saved.
     */
    suspend fun saveAuthToken(authToken: String) {
        // Edit the preferences and save the authentication token.
        context.datastorage.edit { preferences ->
            preferences[STRING_KEY_AUTH_TOKEN] = authToken
        }
    }

    /**
     * Flow representing the authentication token saved in DataStore preferences.
     * Emits the saved authentication token whenever it changes.
     */
    val authTokenFlow: Flow<String> =
        context.datastorage.data.map { preferences ->
            preferences[STRING_KEY_AUTH_TOKEN] ?: ""
        }
}