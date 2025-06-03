package com.example.noteapp.ui.todos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.R;
import com.example.noteapp.model.Task;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public interface TaskActionListener {
        void onEditTask(Task task);
        void onDeleteTask(Task task);
        void onCompleteTask(Task task, boolean completed);
    }

    private final Context context;
    private final List<Task> taskList;
    private final TaskActionListener listener;

    public TaskAdapter(Context context, List<Task> taskList, TaskActionListener listener) {
        this.context = context;
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.txtTitle.setText(task.getTitle());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String timeStr = sdf.format(task.getDueTimeMillis());
        holder.txtTime.setText(timeStr);

        holder.txtDescription.setText(task.getDescription());
        holder.checkCompleted.setChecked(task.isCompleted());

        // Xử lý khi thay đổi trạng thái CheckBox
        holder.checkCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) listener.onCompleteTask(task, isChecked);
        });

        // Xử lý khi nhấn lâu vào item để hiện menu sửa/xóa
        holder.itemView.setOnLongClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.getMenuInflater().inflate(R.menu.menu_task_options, popupMenu.getMenu());

            // Dùng if-else thay vì switch-case
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_edit) {
                    if (listener != null) listener.onEditTask(task);
                    return true;
                } else if (item.getItemId() == R.id.menu_delete) {
                    if (listener != null) listener.onDeleteTask(task);
                    return true;
                } else {
                    return false;
                }
            });

            popupMenu.show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtTime, txtDescription;
        CheckBox checkCompleted;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            checkCompleted = itemView.findViewById(R.id.checkCompleted);
        }
    }
}
