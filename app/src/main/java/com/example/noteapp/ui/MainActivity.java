package com.example.noteapp.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.noteapp.R;
import com.example.noteapp.ui.expense.ExpenseFragment;
import com.example.noteapp.ui.notes.NotesFragment;
import com.example.noteapp.ui.qr.QrFragment;
import com.example.noteapp.ui.settings.SettingsFragment;
import com.example.noteapp.ui.todos.TodosFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_NoteApp);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }
        // Mặc định mở Notes
        loadFragment(new NotesFragment(), "Ghi chú");
        nav.setSelectedItemId(R.id.navigation_notes);

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_notes) {
                loadFragment(new NotesFragment(), "Ghi chú");
            } else if (id == R.id.navigation_todos) {
                loadFragment(new TodosFragment(), "Việc cần làm");
            } else if (id == R.id.navigation_qr) {
                loadFragment(new QrFragment(), "Quét QR");
            } else if (id == R.id.navigation_expense) {
                loadFragment(new ExpenseFragment(), "Chi tiêu");
            } else if (id == R.id.navigation_settings) {
                loadFragment(new SettingsFragment(), "Cài đặt");
            } else {
                return false;
            }
            return true;
        });
    }

    private void loadFragment(Fragment fragment, String title) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .commit();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }
}
