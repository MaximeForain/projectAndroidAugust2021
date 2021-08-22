package com.example.myapplication.ui.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.R


// .---------------------------------------------------------------------.
// |                          LoginActivity                              |
// '---------------------------------------------------------------------'
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val sharedPref = this.getSharedPreferences(getString(R.string.sharedPref), Context.MODE_PRIVATE)

        // Checks if a token is stored and connects automatically
        val token = sharedPref.getString(getString(R.string.token), "")!!
        if (token.isNotEmpty()) {
            val intent = Intent(this.applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
    }
}