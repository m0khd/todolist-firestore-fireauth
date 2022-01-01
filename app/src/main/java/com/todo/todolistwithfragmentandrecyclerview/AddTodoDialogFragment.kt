package com.todo.todolistwithfragmentandrecyclerview

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.todo.todolistwithfragmentandrecyclerview.db.TodoModel
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class AddTodoDialogFragment : DialogFragment() {

    // make class static
    companion object {
        fun newInstance(): AddTodoDialogFragment {
            return AddTodoDialogFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_todo_dialog, container, false)
    }

    // after view created and every view put in right place
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnOk:AppCompatButton = view.findViewById(R.id.btnOk)
        val btnCancel:AppCompatButton = view.findViewById(R.id.btnCancel)

        btnOk.setOnClickListener {
            val tvTodo: AppCompatEditText = view.findViewById(R.id.tvTodo)

            // check if edit text is empty. if it was not empty add it to db
            if (tvTodo.text.toString().isNotEmpty()) {

                val todos:MutableList<TodoModel> = mutableListOf()

                val timestamp = Date()
                val dateToString = timestamp.dateToString("dd-MMM-yyyy")

                // instance of TodoModel
                val todo:TodoModel = TodoModel("", tvTodo.text.toString(), dateToString, false);
                val adapter:TodoAdapter =TodoAdapter(todos, view.context)
                adapter.addTodo(todo);

                // to apply change to list after item added to db, we replace recyclerview
                (activity as MainActivity).getTodoList()

                dismiss()
            }
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    private fun Date.dateToString(format: String): String {
        //simple date formatter
        val dateFormatter = SimpleDateFormat(format, Locale.getDefault())

        //return the formatted date string
        return dateFormatter.format(this)
    }
}