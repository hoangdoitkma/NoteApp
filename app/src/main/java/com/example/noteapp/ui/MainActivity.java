package com.example.noteapp.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.noteapp.R;
import com.example.noteapp.ui.expense.ExpenseFragment;
import com.example.noteapp.ui.notes.NotesFragment;
import com.example.noteapp.ui.qr.QrFragment;
import com.example.noteapp.ui.settings.SettingsFragment;
import com.example.noteapp.ui.todos.TodosFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView nav = findViewById(R.id.bottom_navigation);

        // Set fragment mặc định khi app mở
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, new NotesFragment())
                .commit();
        nav.setSelectedItemId(R.id.navigation_notes);

        nav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment;
            int id = item.getItemId();

            if (id == R.id.navigation_notes) {
                selectedFragment = new NotesFragment();
            } else if (id == R.id.navigation_todos) {
                selectedFragment = new TodosFragment();
            } else if (id == R.id.navigation_qr) {
                selectedFragment = new QrFragment();
            } else if (id == R.id.navigation_expense) {
                selectedFragment = new ExpenseFragment();
            } else if (id == R.id.navigation_settings) {
                selectedFragment = new SettingsFragment();
            } else {
                return false;
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, selectedFragment)
                    .commit();

            return true;
        });
    }
}