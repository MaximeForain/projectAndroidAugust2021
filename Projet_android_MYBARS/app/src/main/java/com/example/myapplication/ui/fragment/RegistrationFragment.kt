package com.example.myapplication.ui.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentRegistrationBinding
import com.example.myapplication.model.Error
import com.example.myapplication.model.Login
import com.example.myapplication.model.Token
import com.example.myapplication.model.User
import com.example.myapplication.ui.activity.MainActivity
import com.example.myapplication.ui.viewModel.LoginViewModel
import com.example.myapplication.ui.viewModel.RegistrationViewModel
import java.util.regex.Pattern


// .---------------------------------------------------------------------.
// |                       RegistrationFragment                          |
// '---------------------------------------------------------------------'
class RegistrationFragment : Fragment() {

    private lateinit var binding: FragmentRegistrationBinding
    private lateinit var registrationViewModel : RegistrationViewModel
    private lateinit var loginViewModel : LoginViewModel

    //SharedPreferences
    private lateinit var sharedPref : SharedPreferences

    // check form
    private var isValidateEmail : Boolean = false
    private var isValidatePassword : Boolean = false
    private var isValidateUsername : Boolean = false
    private var isValidatePhoneNumber : Boolean = false

    // arguments
    lateinit var email : String
    lateinit var password : String
    lateinit var username : String
    lateinit var phonenumber : String

    private var isRegister : Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        registrationViewModel = ViewModelProvider(this).get(RegistrationViewModel::class.java)
        loginViewModel =  ViewModelProvider(this).get(LoginViewModel::class.java)
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        binding.viewModel = registrationViewModel
        binding.lifecycleOwner = this

        sharedPref = requireActivity().getSharedPreferences(getString(R.string.sharedPref), Context.MODE_PRIVATE)

        inputsVerifier()

        // Cancelled button
        binding.cancelledEditProfileButton.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(R.id.action_RegistrationFragment_to_loginFragment)
        }

        // Registration button
        binding.creatProfileButton.setOnClickListener {
            if(validateForm()) {
                addUser()
            }
            else {
                showFieldsError()
                Toast.makeText(activity, R.string.form_error, Toast.LENGTH_SHORT).show()
            }
        }

        registrationViewModel.error.observe(viewLifecycleOwner){
            error: Error -> this.displayErrorScreen(error)
            if(isRegister) {
                Toast.makeText(activity, getString((R.string.inscription)), Toast.LENGTH_SHORT).show()
                loginViewModel.loginUser(Login(
                        binding.emailTextInputLayout.editText!!.text.toString(),
                        binding.passwordTextInputLayout.editText!!.text.toString())
                )
            }
        }

        loginViewModel.jwt.observe(viewLifecycleOwner){
            token: Token -> this.savePreferedValue(token)
            goToProfileActivity()
        }

        return binding.root
    }

    // .---------------------------------------------------------------------.
    // |                               Error                                 |
    // '---------------------------------------------------------------------'
    private fun displayErrorScreen(error: Error) {
        when(error){
            Error.TECHNICAL_ERROR -> Toast.makeText(
                    activity,
                    R.string.technical_error,
                    Toast.LENGTH_LONG
            ).show()
            Error.NO_CONNECTION -> Toast.makeText(
                    activity,
                    R.string.connectivity_error,
                    Toast.LENGTH_LONG
            ).show()
            Error.REQUEST_ERROR -> Toast.makeText(
                    activity,
                    R.string.request_error,
                    Toast.LENGTH_LONG
            ).show()
            Error.USER_ALREADY_EXIST -> Toast.makeText(
                    activity,
                    R.string.user_already_exist,
                    Toast.LENGTH_LONG
            ).show()
            else -> { isRegister = true }
        }
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
    // |                         Navigation                                  |
    // '---------------------------------------------------------------------'
    private fun goToProfileActivity(){
        val intent = Intent(requireActivity().applicationContext, MainActivity::class.java)
        startActivity(intent)
    }

    // .---------------------------------------------------------------------.
    // |                        Fields validation                            |
    // '---------------------------------------------------------------------'
    private fun inputsVerifier() {
        emailTextChangedListener()
        usernameTextChangedListener()
        phoneNumberTextChangedListener()
        passwordTextChangedListener()
    }

    private fun showFieldsError(){
        emailCheckError()
        usernameCheckError()
        phoneCheckError()
        passwordCheckConfirmError()
    }

    private fun validateForm():Boolean {
        return isValidateEmail && isValidateUsername && isValidatePhoneNumber && isValidatePassword
    }

    private fun emailTextChangedListener() {
        binding.emailTextInputLayout.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { emailCheckError() }
        })
    }

    private fun usernameTextChangedListener() {
        binding.usernameTextInputLayout.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { usernameCheckError() }
        })
    }

    private fun phoneNumberTextChangedListener() {
        binding.phonenumberTextInputLayout.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { phoneCheckError() }
        })
    }

    private fun passwordTextChangedListener() {
        binding.passwordTextInputLayout.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { passwordCheckConfirmError() }
        })

        binding.passwordConfirmTextInputLayout.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { passwordCheckConfirmError() }
        })
    }

    // .---------------------------------------------------------------------.
    // |                         Check Error                                 |
    // '---------------------------------------------------------------------'
    private fun emailCheckError() {
        if (binding.emailTextInputLayout.editText!!.text.isEmpty()) {
            binding.emailTextInputLayout.isErrorEnabled = true
            binding.emailTextInputLayout.error = getString(R.string.email_empty_error)
            isValidateEmail = false
        } else {
            if (!(Patterns.EMAIL_ADDRESS.matcher(binding.emailTextInputLayout.editText!!.text).matches())) {
                binding.emailTextInputLayout.isErrorEnabled = true
                binding.emailTextInputLayout.error =  getString(R.string.email_format_error)
                isValidateEmail = false
            } else {
                binding.emailTextInputLayout.isErrorEnabled = false
                binding.emailTextInputLayout.error = null
                isValidateEmail = true
            }
        }
    }

    private fun usernameCheckError() {
        if (binding.usernameTextInputLayout.editText!!.text.isEmpty()) {
            binding.usernameTextInputLayout.isErrorEnabled = true
            binding.usernameTextInputLayout.error = getString(R.string.username_empty_error)
            isValidateUsername = false
        } else {
            if (!(Pattern.compile("^[a-zéèçàïôëA-Z]{1,50}(-| )?([a-zéèçàïôëA-Z]{1,50})?$")
                            .matcher(binding.usernameTextInputLayout.editText!!.text)
                            .matches())) {
                binding.usernameTextInputLayout.isErrorEnabled = true
                binding.usernameTextInputLayout.error = getString(R.string.username_format_error)
                isValidateUsername = false
            } else {
                binding.usernameTextInputLayout.isErrorEnabled = false
                binding.usernameTextInputLayout.error = null
                isValidateUsername = true
            }
        }
    }

    private fun phoneCheckError() {
        if (binding.phonenumberTextInputLayout.editText!!.text.isEmpty()) {
            binding.phonenumberTextInputLayout.isErrorEnabled = true
            binding.phonenumberTextInputLayout.error = getString(R.string.phonenumber_empty_error)
            isValidatePhoneNumber = false
        } else {
            if (!(Patterns.PHONE.matcher(binding.phonenumberTextInputLayout.editText!!.text).matches())) {
                binding.phonenumberTextInputLayout.isErrorEnabled = true
                binding.phonenumberTextInputLayout.error = getString(R.string.phonenumber_format_error)
                isValidatePhoneNumber = false
            } else {
                binding.phonenumberTextInputLayout.isErrorEnabled = false
                binding.phonenumberTextInputLayout.error = null
                isValidatePhoneNumber = true
            }
        }
    }

    private fun passwordCheckConfirmError() {
        if (binding.passwordTextInputLayout.editText!!.text.isEmpty()) {
            binding.passwordTextInputLayout.isErrorEnabled = true
            binding.passwordTextInputLayout.error = getString(R.string.password__error)
            isValidatePassword = false
            if (binding.passwordConfirmTextInputLayout.editText!!.text.isEmpty())
                binding.passwordConfirmTextInputLayout.isErrorEnabled = false
        }
        else if (binding.passwordTextInputLayout.editText!!.text.isNotEmpty() && binding.passwordConfirmTextInputLayout.editText!!.text.isEmpty()) {
            binding.passwordTextInputLayout.isErrorEnabled = false
            binding.passwordConfirmTextInputLayout.isErrorEnabled = true
            binding.passwordConfirmTextInputLayout.error = getString(R.string.password_no_confirm_error)
            isValidatePassword = false
        }
        else if (binding.passwordTextInputLayout.editText!!.text.isEmpty() && binding.passwordConfirmTextInputLayout.editText!!.text.isNotEmpty()) {
            binding.passwordTextInputLayout.isErrorEnabled = true
            binding.passwordConfirmTextInputLayout.isErrorEnabled = true
            binding.passwordTextInputLayout.error = getString(R.string.password__error)
            binding.passwordConfirmTextInputLayout.error = getString(R.string.password_confirm_error)
            isValidatePassword = false
        }
        else if (binding.passwordTextInputLayout.editText!!.text.toString() != binding.passwordConfirmTextInputLayout.editText!!.text.toString()) {
            binding.passwordTextInputLayout.isErrorEnabled = false
            binding.passwordConfirmTextInputLayout.isErrorEnabled = true
            binding.passwordConfirmTextInputLayout.error = getString(R.string.password_confirm_error)
            isValidatePassword = false
        } else {
            binding.passwordConfirmTextInputLayout.isErrorEnabled = false
            binding.passwordConfirmTextInputLayout.error = null
            binding.passwordTextInputLayout.error = null
            isValidatePassword = true
        }
    }

    // .---------------------------------------------------------------------.
    // |                           Request Async                             |
    // '---------------------------------------------------------------------'
    private fun addUser() {
        val email: String       = binding.emailTextInputLayout.editText!!.text.toString()
        val username: String    = binding.usernameTextInputLayout.editText!!.text.toString()
        val phonenumber: String = binding.phonenumberTextInputLayout.editText!!.text.toString()
        val password: String    = binding.passwordConfirmTextInputLayout.editText!!.text.toString()

        val isMan: Boolean      = binding.radioButtonMan.isChecked
        val isWommen: Boolean   = binding.radioButtonWommen.isChecked

        val gender: String? = if (!isMan && !isWommen) null
        else if (isMan) "H" else "F"

        registrationViewModel.addUser(User(email, password, username, phonenumber, gender))
    }
}