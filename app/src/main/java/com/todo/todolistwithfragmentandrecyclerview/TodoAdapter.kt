package com.todo.todolistwithfragmentandrecyclerview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.todo.todolistwithfragmentandrecyclerview.db.TodoModel
import java.time.Instant
import java.time.format.DateTimeFormatter
import kotlin.collections.HashMap


    class TodoAdapter (private var todos:MutableList<TodoModel>, private val context: Context)
    : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val sharedPref = context.getSharedPreferences("test", Context.MODE_PRIVATE)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view:View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_todo, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val curTodo = todos[position]
        holder.bindItems(curTodo)
    }

    override fun getItemCount(): Int {
        return todos.size
    }

    fun addTodo(todo: TodoModel) {

        val map = HashMap<String, Any>()
        map["title"] = todo.title
        map["date"] = todo.date
        map["isChecked"] = todo.isChecked as Boolean
        map["user_id"] = sharedPref.getString("user_id", "").toString()
        map["time_stamp"] = System.currentTimeMillis()

        db.collection("todos")
            .add(map)
        notifyItemInserted(0)
    }

    private fun toggleStrikeThrough(title:TextView, isChecked:Boolean) {
        if(isChecked) {
            title.paintFlags = title.paintFlags or STRIKE_THRU_TEXT_FLAG
        } else {
            title.paintFlags = title.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    inner class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val tvTitle:TextView = itemView.findViewById(R.id.tvTodoTitle)
        private val tvDate:TextView = itemView.findViewById(R.id.tvTodoDate)
        private val cbDone:CheckBox = itemView.findViewById(R.id.cbDone)
        private val ivDelete = itemView.findViewById<ImageView>(R.id.ivDelete)

        fun bindItems(todo:TodoModel) {

            tvTitle.text = todo.title
            tvDate.text = todo.date
            cbDone.isChecked = todo.isChecked

            toggleStrikeThrough(tvTitle, cbDone.isChecked)

            cbDone.setOnClickListener {
                toggleStrikeThrough(tvTitle, cbDone.isChecked)
                updateIsChecked(todo)
            }

            ivDelete.setOnClickListener {
                deleteTodo(todo.id)
            }

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteTodo(position: String) {
        db.collection("todos").document(position)
            .delete()
    }

    fun updateIsChecked(todo:TodoModel) {
        db.collection("todos").document(todo.id)
            .update("isChecked", !todo.isChecked)
    }

    private fun getTodos() {
        val docRef = db.collection("todos")
/*        docRef.orderBy("time_stamp", Query.Direction.DESCENDING).get().addOnCompleteListener(OnCompleteListener {
                task ->
            if (task.isSuccessful) {
                task.result?.forEach {
                    if (it["user_id"].toString() == sharedPref.getString("user_id", "").toString()) {
                        val todoDB: TodoModel = TodoModel("", "", "", false);
                        todoDB.id = it.id
                        todoDB.title = it["title"] as String
                        todoDB.date = it["date"] as String
                        todoDB.isChecked = it["isChecked"] as Boolean

                        todos.add(todoDB)
                    }
                }
            }
        })*/

        docRef.orderBy("time_stamp", Query.Direction.DESCENDING)
            .addSnapshotListener{ snapshot, e ->

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

            }

    }


}