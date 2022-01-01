package com.todo.todolistwithfragmentandrecyclerview

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.todo.todolistwithfragmentandrecyclerview.databinding.ActivityMainBinding
import com.todo.todolistwithfragmentandrecyclerview.db.TodoModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val emailId = intent.getStringExtra("email_id")
        binding.tvMainEmail.text = "Welcome $emailId"

        getTodoList()

        // show dialog fragment
        binding.gotoAddTodoFragment.setOnClickListener {
            AddTodoDialogFragment.newInstance().show(supportFragmentManager, null)
        }

        // Logout
        binding.ivMainLogout.setOnClickListener {
            logout()
        }
    }

    fun getTodoList() {
        val sharedPref = getSharedPreferences("test", Context.MODE_PRIVATE)
        val db = FirebaseFirestore.getInstance()

        db.collection("todos").orderBy("time_stamp", Query.Direction.DESCENDING)
            .addSnapshotListener{ snapshot, e ->
                val todos:MutableList<TodoModel> = mutableListOf()
                snapshot?.documents?.forEach {

                    if (it["user_id"].toString() == sharedPref.getString("user_id", "").toString()) {
                        val todoDB : TodoModel = TodoModel("", "", "", false)
                        todoDB.id = it.id
                        todoDB.title = it["title"] as String
                        todoDB.date = it["date"] as String
                        todoDB.isChecked = it["isChecked"] as Boolean

                        todos.add(todoDB)
                    }
                }
                val recyclerView: RecyclerView = findViewById(R.id.rvTodoItems)
                recyclerView.adapter = null
                recyclerView.layoutManager = null
                recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                val adapter =TodoAdapter(todos, this)
                recyclerView.adapter = adapter

            }
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()

        val sharedPref = getSharedPreferences("test", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("isLoggedIn", false.toString())
                .apply()
        }

        with(sharedPref.edit()) {
            putString("email", "")
                .apply()
        }

        with(sharedPref.edit()) {
            putString("user_id", "")
                .apply()
        }

        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

}