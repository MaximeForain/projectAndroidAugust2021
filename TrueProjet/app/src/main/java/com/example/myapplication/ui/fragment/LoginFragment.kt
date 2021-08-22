package com.example.myapplication.ui.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentLoginBinding
import com.example.myapplication.model.Error
import com.example.myapplication.model.Login
import com.example.myapplication.model.Token
import com.example.myapplication.ui.activity.MainActivity
import com.example.myapplication.ui.viewModel.LoginViewModel


// .---------------------------------------------------------------------.
// |                            LoginFragment                            |
// '---------------------------------------------------------------------'
class LoginFragment: Fragment() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var sharedPref : SharedPreferences
    private lateinit var binding : FragmentLoginBinding
    private lateinit var loginViewModel : LoginViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        loginViewModel =  ViewModelProvider(this).get(LoginViewModel::class.java)
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.viewModel = loginViewModel
        binding.lifecycleOwner = this

        sharedPref = requireActivity().getSharedPreferences(getString(R.string.sharedPref), Context.MODE_PRIVATE)

        emailEditText = binding.emailTextInputLayout.editText!!
        passwordEditText = binding.passwordTextInputLayout.editText!!

        loginViewModel.error.observe(viewLifecycleOwner){
            error: Error -> this.displayErrorScreen(error)
        }

        // OnClickListerner in view
        binding.loginConnectionButton.setOnClickListener {
            loginViewModel.loginUser(Login(emailEditText.text.toString(), passwordEditText.text.toString()))
        }

        loginViewModel.jwt.observe(viewLifecycleOwner){
            token: Token -> this.savePreferedValue(token)
            goToProfileActivity()
        }

        binding.loginRegistration.setOnClickListener{
            NavHostFragment.findNavController(this).navigate(R.id.action_loginFragment_to_RegistrationFragment)
        }

        return binding.root
    }

    // .---------------------------------------------------------------------.
    // |                        SharedPreferences                            |
    // '---------------------------------------------------------------------'
    private fun savePreferedValue(token: Token){
        val editor : SharedPreferences.Editor = sharedPref.edit()
        editor.putString(getString(R.string.user_id), token.userId)
        editor.putString(getString(R.string.token), token.token)
        editor.putLong(getString(R.string.exp_date_payload), token.expDate!!.time).apply()
    }

    // .---------------------------------------------------------------------.
    // |                              Error                                  |
    // '---------------------------------------------------------------------'
    private  fun displayErrorScreen(error: Error){
        when(error){
            Error.TECHNICAL_ERROR -> Toast.makeText(
                activity,
                R.string.technical_error,
                Toast.LENGTH_SHORT
            ).show()
            Error.NO_CONNECTION -> Toast.makeText(
                activity,
                R.string.connectivity_error,
                Toast.LENGTH_SHORT
            ).show()
            Error.REQUEST_ERROR -> Toast.makeText(
                activity,
                R.string.request_error,
                Toast.LENGTH_SHORT
            ).show()
            Error.BAD_CREDENTIALS -> Toast.makeText(
                activity,
                R.string.bad_credentials,
                Toast.LENGTH_SHORT
            ).show()
            else -> { }
        }
    }

    // .---------------------------------------------------------------------.
    // |                         Navigation                                  |
    // '---------------------------------------------------------------------'
    private fun goToProfileActivity(){
        val intent = Intent(requireActivity().applicationContext, MainActivity::class.java)
        startActivity(intent)
    }
}