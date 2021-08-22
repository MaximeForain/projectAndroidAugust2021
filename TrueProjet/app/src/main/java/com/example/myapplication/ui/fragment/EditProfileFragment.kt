package com.example.myapplication.ui.fragment

import android.content.Context
import com.example.myapplication.model.Error
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
import com.example.myapplication.databinding.FragmentEditProfileBinding
import com.example.myapplication.model.Login
import com.example.myapplication.model.Token
import com.example.myapplication.model.User
import com.example.myapplication.ui.viewModel.EditProfileViewModel

import java.util.*
import java.util.regex.Pattern


// .---------------------------------------------------------------------.
// |                       EditProfileFragment                           |
// '---------------------------------------------------------------------'
class EditProfileFragment : Fragment() {

    lateinit var binding: FragmentEditProfileBinding
    private lateinit var editProfileViewModel : EditProfileViewModel

    //SharedPreferences
    private lateinit var sharedPref : SharedPreferences

    // check form
    private var isValidateEmail : Boolean = false
    private var isValidatePassword : Boolean = true
    private var isValidateUsername : Boolean = false
    private var isValidatePhoneNumber : Boolean = false

    // arguments
    private lateinit var email : String
    private lateinit var username : String
    private lateinit var phonenumber : String
    private lateinit var gender : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        editProfileViewModel = ViewModelProvider(this).get(EditProfileViewModel::class.java)
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        binding.viewModel = editProfileViewModel
        binding.lifecycleOwner = this

        // Get preferences
        sharedPref  = requireActivity().getSharedPreferences(getString(R.string.sharedPref), Context.MODE_PRIVATE)
        val token   = sharedPref.getString(getString(R.string.token), R.string.EMPTY.toString())!!
        val exp     = sharedPref.getLong(getString(R.string.exp_date_payload), 0)
        val expDate = Date(exp * 1000)
        val userId  = sharedPref.getString(getString(R.string.user_id), "")!!

        val jwt =  Token(expDate, token, userId)

        inputsVerifier()
        completeFields()

        editProfileViewModel.error.observe(viewLifecycleOwner){
            error: Error -> this.displayErrorScreen(error)
        }

        // Cancelled button
        binding.cancelledEditProfileButton.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(R.id.action_editProfileFragment_to_profileFragment)
        }

        // edit profile button
        binding.editProfileButton.setOnClickListener {
            if(validateForm()) updateUser(jwt)
            else Toast.makeText(activity, R.string.form_error, Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    // .---------------------------------------------------------------------.
    // |                         Companion d'objet                           |
    // '---------------------------------------------------------------------'
    companion object {
        private const val ARG_EMAIL         = "ARG_EMAIL"
        private const val ARG_USERNAME      = "ARG_FIRSTNAME"
        private const val ARG_PHONENUMBER   = "ARG_PHONE"
        private const val ARG_GENDER        = "ARG_STREET_NUMBER"
        fun newArguments(email: String, username: String, phonenumber: String, gender: String?): Bundle {
            val args = Bundle()
            args.putString(ARG_EMAIL, email)
            args.putString(ARG_USERNAME, username)
            args.putString(ARG_PHONENUMBER, phonenumber)
            args.putString(ARG_GENDER, gender)

            return args
        }
    }

    // .---------------------------------------------------------------------.
    // |                               Error                                 |
    // '---------------------------------------------------------------------'
    private fun displayErrorScreen(error: Error) {
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
            Error.USER_ALREADY_EXIST -> Toast.makeText(
                    activity,
                    R.string.user_already_exist,
                    Toast.LENGTH_SHORT
            ).show()
            else -> {
                Toast.makeText(activity, getString((R.string.toast_profile_modified)), Toast.LENGTH_SHORT).show()
                NavHostFragment.findNavController(this).navigate(R.id.action_editProfileFragment_to_profileFragment)
            }
        }
    }

    private fun completeFields() {
        username    = requireArguments().getString(ARG_USERNAME)!!
        email       = requireArguments().getString(ARG_EMAIL)!!
        phonenumber = requireArguments().getString(ARG_PHONENUMBER)!!
        gender      = requireArguments().getString(ARG_GENDER)!!

        binding.usernameTextInputLayout.editText!!.setText(username)
        binding.emailTextInputLayout.editText!!.setText(email)
        binding.phonenumberTextInputLayout.editText!!.setText(phonenumber)

        binding.radioButtonMan.isChecked = gender == "H"
        binding.radioButtonWommen.isChecked = gender != "H"
    }

    private fun inputsVerifier() {
        emailTextChangedListener()
        usernameTextChangedListener()
        phoneNumberTextChangedListener()
        passwordTextChangedListener()
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
        if (binding.passwordTextInputLayout.editText!!.text.isNotEmpty() && binding.passwordConfirmTextInputLayout.editText!!.text.isEmpty()) {
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
    private fun updateUser(jwt: Token) {
        val email: String       = binding.emailTextInputLayout.editText!!.text.toString()
        val username: String    = binding.usernameTextInputLayout.editText!!.text.toString()
        val phonenumber: String = binding.phonenumberTextInputLayout.editText!!.text.toString()
        val password: String    = binding.passwordConfirmTextInputLayout.editText!!.text.toString()

        val isMan: Boolean = binding.radioButtonMan.isChecked
        val gender: String =  if (isMan) "H" else "F"

        val user = User(email, password, username, phonenumber, gender)

        editProfileViewModel.updateUser(user, jwt)
    }
}