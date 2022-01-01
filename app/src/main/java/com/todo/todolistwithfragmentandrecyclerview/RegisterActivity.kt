package com.todo.todolistwithfragmentandrecyclerview

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.todo.todolistwithfragmentandrecyclerview.databinding.ActivityRegisterBinding


class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = this.getSharedPreferences(
            "test", Context.MODE_PRIVATE
        )

        binding.btnRegisterCreateAccount.setOnClickListener {
            when{
                TextUtils.isEmpty(binding.etRegisterEmail.text.toString().trim(){it <= ' '}) -> {
                    Toast.makeText(this, "Please enter Email.", Toast.LENGTH_SHORT).show()
                }

                TextUtils.isEmpty(binding.etRegisterPassword1.text.toString().trim(){it <= ' '}) -> {
                    Toast.makeText(this, "Please enter Password.", Toast.LENGTH_SHORT).show()
                }

                TextUtils.isEmpty(binding.etRegisterPassword2.text.toString().trim(){it <= ' '}) -> {
                    Toast.makeText(this, "Please enter Password again.", Toast.LENGTH_SHORT).show()
                }

                binding.etRegisterPassword1.text.toString() != binding.etRegisterPassword2.text.toString() -> {
                    Toast.makeText(this, "passwords not match.", Toast.LENGTH_SHORT).show()
                }

                !isEmailValid(binding.etRegisterEmail.text.toString()) -> {
                    Toast.makeText(this, "Please enter Valid Email.", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    val email : String = binding.etRegisterEmail.text.toString().trim(){it <= ' '}
                    val pass1 : String = binding.etRegisterPassword1.text.toString().trim(){it <= ' '}
                    val pass2 : String = binding.etRegisterPassword2.text.toString().trim(){it <= ' '}

                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass1)
                        .addOnCompleteListener(OnCompleteListener {
                            task ->
                            if (task.isSuccessful) {

                                val fireBaseUser:FirebaseUser = task.result!!.user!!

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
                                Toast.makeText(this, task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                            }
                        })

                }
            }
        }



    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}