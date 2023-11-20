package com.example.branchapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.branchapp.utils.DataStorage
import com.rishi.branchinternational.model.database.BranchInternationalRepository
import com.example.branchapp.models.repository.AppRepository
import javax.inject.Inject

class AppViewModelFactory @Inject constructor(
    private val authRepository: AppRepository,
    private val dataStore: DataStorage,
    private val branchInternationalRepository: BranchInternationalRepository
) : ViewModelProvider.Factory {
    // Override the create method to instantiate the appropriate ViewModel
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if the provided ViewModel class is AppViewModel
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            // If it is, create and return an instance of AppViewModel with the required dependencies
            return AppViewModel(authRepository, dataStore, branchInternationalRepository) as T
        }
        // If the provided ViewModel class is unknown, throw an IllegalArgumentException
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
