package com.example.myapplication.ui.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.example.myapplication.model.Error
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentProfileBinding
import com.example.myapplication.model.Token
import com.example.myapplication.ui.activity.LoginActivity
import com.example.myapplication.ui.viewModel.ProfileViewModel
import java.util.*


// .---------------------------------------------------------------------.
// |                        ProfileFragment                              |
// '---------------------------------------------------------------------'
class ProfileFragment: Fragment() {

    private lateinit var binding : FragmentProfileBinding
    private lateinit var sharedPref : SharedPreferences
    private lateinit var profileViewModel : ProfileViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        profileViewModel =  ViewModelProvider(this).get(ProfileViewModel::class.java)
        binding.viewModel = profileViewModel

        // get preferences
        sharedPref  = requireActivity().getSharedPreferences(getString(R.string.sharedPref), Context.MODE_PRIVATE)
        val token   = sharedPref.getString(getString(R.string.token), "")!!
        val exp     = sharedPref.getLong(getString(R.string.exp_date_payload), 0)
        val expDate = Date(exp * 1000)
        val userId  = sharedPref.getString(getString(R.string.user_id), "")!!

        val jwt =  Token(expDate, token, userId)

        profileViewModel.getUser(jwt)

        profileViewModel.error.observe(viewLifecycleOwner){
            error: Error -> this.displayErrorScreen(error)
        }

        // Initialization of the pending view
        binding.identity.visibility             = View.GONE
        binding.errorLayout.visibility          = View.GONE
        binding.progressBar.visibility          = View.VISIBLE
        binding.confirmationDeletion.visibility = View.GONE

        // --- Button for edit profile ---
        binding.updateProfileButton.setOnClickListener {
            val editProfileFragment : Bundle = EditProfileFragment.newArguments(
                    profileViewModel.user.value!!.email,
                    profileViewModel.user.value!!.username,
                    profileViewModel.user.value!!.phonenumber,
                    profileViewModel.user.value!!.gender
            )
            NavHostFragment.findNavController(this).navigate(R.id.action_profileFragment_to_editProfileFragment, editProfileFragment)
        }

        // --- Button for disconnexion ---
        binding.disconnexionButton.setOnClickListener {
            //Delete the token data from sharedPref for disconnection
            val editor : SharedPreferences.Editor = sharedPref.edit()
            editor.putString(getString(R.string.token), null).apply()

            // Change of activity
            goToProfileActivity()
        }

        // Button for deletation of profile
        binding.deleteProfileButton.setOnClickListener {
            binding.deleteProfileButton.visibility  = View.GONE
            binding.confirmationDeletion.visibility = View.VISIBLE
        }
        binding.confirmationDeletionButton.setOnClickListener {
            profileViewModel.deleteUser(jwt)
            val editor : SharedPreferences.Editor = sharedPref.edit()
            editor.putString(getString(R.string.token), null).apply()
        }
        profileViewModel.isDeleted.observe(viewLifecycleOwner){
            goToProfileActivity()
            Toast.makeText(activity, R.string.validation__suppression, Toast.LENGTH_SHORT).show()
        }
        binding.cancelButton.setOnClickListener {
            binding.deleteProfileButton.visibility  = View.VISIBLE
            binding.confirmationDeletion.visibility = View.GONE
        }

        // Allows you to manage the rollback to the parent fragment
        val callbacks = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                NavHostFragment.findNavController(this@ProfileFragment).navigate(R.id.action_profileFragment_to_homeFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callbacks)

        return binding.root
    }

    // .---------------------------------------------------------------------.
    // |                             Navigation                              |
    // '---------------------------------------------------------------------'
    private fun goToProfileActivity() {
        val intent = Intent(
                requireActivity().applicationContext,
                LoginActivity::class.java
        )
        startActivity(intent)
    }

    // .---------------------------------------------------------------------.
    // |                               Error                                 |
    // '---------------------------------------------------------------------'
    private fun displayErrorScreen(error: Error){
        binding.progressBar.visibility = View.GONE
        when(error){
            Error.NO_ERROR -> {
                assignVisibility(identity = true, updateProfileButton = true, errorLayout = false, connectivityError = false,
                        connectivityErrorImage = false, requestError = false, technicalError = false, errorImage = false)
            }
            Error.REQUEST_ERROR -> {
                assignVisibility(identity = false, updateProfileButton = false, errorLayout = true, connectivityError = false,
                        connectivityErrorImage = false, requestError = true, technicalError = false, errorImage = true)
            }
            Error.TECHNICAL_ERROR -> {
                assignVisibility(identity = false, updateProfileButton = false, errorLayout = true, connectivityError = false,
                        connectivityErrorImage = false, requestError = false, technicalError = true, errorImage = true)
            }
            else -> {
                assignVisibility(identity = false, updateProfileButton = false, errorLayout = true, connectivityError = true,
                        connectivityErrorImage = true, requestError = false, technicalError = false, errorImage = false)
            }
        }
    }

    private fun assignVisibility(
            identity: Boolean, updateProfileButton: Boolean, errorLayout: Boolean, connectivityError: Boolean,
            connectivityErrorImage: Boolean, requestError: Boolean, technicalError: Boolean, errorImage: Boolean
    ) {
        binding.identity.visibility                 = if (identity) View.VISIBLE else View.GONE
        binding.updateProfileButton.visibility      = if (updateProfileButton) View.VISIBLE else View.GONE
        binding.errorLayout.visibility              = if (errorLayout) View.VISIBLE else View.GONE
        binding.connectivityError.visibility        = if (connectivityError) View.VISIBLE else View.GONE
        binding.connectivityErrorImage.visibility   = if (connectivityErrorImage) View.VISIBLE else View.GONE
        binding.requestError.visibility             = if (requestError) View.VISIBLE else View.GONE
        binding.technicalError.visibility           = if (technicalError) View.VISIBLE else View.GONE
        binding.errorImage.visibility               = if (errorImage) View.VISIBLE else View.GONE
    }
}