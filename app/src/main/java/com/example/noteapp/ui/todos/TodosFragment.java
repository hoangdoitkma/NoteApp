package com.example.noteapp.ui.todos;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.noteapp.R;
import com.example.noteapp.data.TaskDatabaseHelper;
import com.example.noteapp.model.Task;

import java.util.List;

public class TodosFragment extends Fragment implements TaskAdapter.TaskActionListener {

    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private TaskDatabaseHelper dbHelper;
    private List<Task> taskList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todos, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        View btnAdd = view.findViewById(R.id.fabAddTask);
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddTaskActivity.class);
            startActivity(intent);
        });

        dbHelper = new TaskDatabaseHelper(getContext());
        loadTasks();

        return view;
    }

    private void loadTasks() {
        taskList = dbHelper.getAllTasks();
        if (adapter == null) {
            adapter = new TaskAdapter(getContext(), taskList, this);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateTasks(taskList);  // bạn cần tự tạo hàm updateTasks trong adapter
        }
    }

    @Override
    public void onEditTask(Task task) {
        Intent intent = new Intent(getContext(), AddTaskActivity.class);
        intent.putExtra("taskId", task.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteTask(Task task) {
        dbHelper.deleteTask(task.getId());
        loadTasks();
    }

    @Override
    public void onCompleteTask(Task task, boolean completed) {
        task.setCompleted(completed);
        dbHelper.updateTask(task);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTasks(); // refresh danh sách khi quay lại fragment
    }
}
