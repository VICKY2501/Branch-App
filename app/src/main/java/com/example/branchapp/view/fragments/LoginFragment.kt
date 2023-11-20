package com.example.branchapp.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.branchapp.R
import com.example.branchapp.databinding.FragmentLoginBinding
import com.example.branchapp.utils.DataStorage
import com.rishi.branchinternational.model.database.BranchInternationalRepository
import com.example.branchapp.models.repository.AppRepository
import com.example.branchapp.viewmodels.AppViewModel
import com.example.branchapp.viewmodels.AppViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var fragmentLoginBinding: FragmentLoginBinding

    private lateinit var navController: NavController

    private lateinit var appViewModel: AppViewModel

    @Inject
    lateinit var appRepository: AppRepository

    @Inject
    lateinit var dataStore: DataStorage

    @Inject
    lateinit var branchInternationalRepository: BranchInternationalRepository

    @Inject
    lateinit var appViewModelFactory: AppViewModelFactory

    // Called to have the fragment instantiate its user interface view.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize AppViewModel using ViewModelProvider with the provided factory.
        appViewModel = ViewModelProvider(this, appViewModelFactory).get(AppViewModel::class.java)

        // Inflate the layout for this fragment and return the root view.
        fragmentLoginBinding = FragmentLoginBinding.inflate(inflater, container, false)
        return fragmentLoginBinding.root
    }

    // Called immediately after onCreateView() has returned, but before any saved state has been restored.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the NavController to navigate between destinations in the NavHostFragment.
        navController = findNavController()

        // Get references to the UI elements from the binding object.
        val logUser = fragmentLoginBinding.logUser
        val layLogUser = fragmentLoginBinding.layLogUser
        val logPassword = fragmentLoginBinding.logPassword
        val layLogPassword = fragmentLoginBinding.layLogPassword

        // Check if there is an existing auth token and navigate to message thread if available.
        checkIfHaveAuthToken()

        // Set click listener for the login button.
        fragmentLoginBinding.logBtn.setOnClickListener {
            // Validate the input fields.
            if (logUser.text.toString().trim().isEmpty()) {
                layLogUser.error = "This Field Cannot Be Empty"
            } else {
                layLogUser.error = null
            }
            if (logPassword.text.toString().trim().isEmpty()) {
                layLogPassword.error = "Password Cannot Be Empty"
            } else {
                layLogPassword.error = null
            }
            if(layLogUser.error== null && !android.util.Patterns.EMAIL_ADDRESS.matcher(logUser.text.toString().trim()).matches())
            {
                 layLogUser.error = "This email is not valid"
            }
            if(layLogUser.error==null && layLogPassword.error == null && logUser.text.toString().reversed()!=logPassword.text.toString())
            {
                layLogPassword.error="This password is not valid"
            }
            // If input fields are not empty, attempt to sign in with the provided credentials.
            if (logUser.text.toString().trim().isNotEmpty() &&
                logPassword.text.toString().trim().isNotEmpty()
            ) {
                appViewModel.signInWithEmailAnsPassword(
                    logUser.text.toString(),
                    logPassword.text.toString()
                )

                // Observe the authentication result and navigate accordingly.
                appViewModel.authResult.observe(viewLifecycleOwner) { response ->
                    if (response == null || response.isFailure) {
                        Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
                    } else if (response.isSuccess) {
                        navController.navigate(R.id.action_hilt_LoginFragment_to_hilt_MessageFragment2)
                    } else {
                        Toast.makeText(context, "Some Error Occurred", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // Check if there is an existing auth token in the data store.
    private fun checkIfHaveAuthToken() {
        val ifTokenExists = appViewModel.getAuthTokenFromDataStore()
        if (ifTokenExists) {
            // If auth token exists, navigate to the message thread fragment.
            navController.navigate(R.id.action_hilt_LoginFragment_to_hilt_MessageFragment2)
        }
    }
}