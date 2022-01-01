package com.todo.todolistwithfragmentandrecyclerview

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.todo.todolistwithfragmentandrecyclerview.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("test", Context.MODE_PRIVATE)

        if (sharedPref.getString("isLoggedIn", "").equals("true")) {
            goToMainActivity(sharedPref)
        }

        binding.btnLoginCreateAccount.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            when{
                TextUtils.isEmpty(binding.etLoginEmail.text.toString().trim(){it <= ' '}) -> {
                    Toast.makeText(this, "Please enter Email.", Toast.LENGTH_SHORT).show()
                }

                TextUtils.isEmpty(binding.etLoginPassword.text.toString().trim(){it <= ' '}) -> {
                    Toast.makeText(this, "Please enter Password.", Toast.LENGTH_SHORT).show()
                }

                !isEmailValid(binding.etLoginEmail.text.toString()) -> {
                    Toast.makeText(this, "Please enter Valid Email.", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    val email : String = binding.etLoginEmail.text.toString().trim(){it <= ' '}
                    val pass : String = binding.etLoginPassword.text.toString().trim(){it <= ' '}

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(OnCompleteListener  {
                            task ->
                            if (task.isSuccessful) {
                                val fireBaseUser: FirebaseUser = task.result!!.user!!

                                val intent = Intent(this, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.putExtra("user_id", fireBaseUser.uid)
                                intent.putExtra("email_id", email)

                                // save user state to prevent from login after close and open app
                                with(sharedPref.edit()) {
                                    putString("isLoggedIn", true.toString())
                                        .apply()
                                }
                                with(sharedPref.edit()) {
                                    putString("email", email)
                                        .apply()
                                }

                                with(sharedPref.edit()) {
                                    putString("user_id", fireBaseUser.uid)
                                        .apply()
                                }

                                startActivity(intent)
                                finish()
                            } else {

                                binding.text.text = task.exception?.message
                            }
                        })
                }
            }
        }


    }

    private fun goToMainActivity(sharedPref: SharedPreferences) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("email_id", sharedPref.getString("email", ""))
        startActivity(intent)
        finish()
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}