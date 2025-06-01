
package com.example.noteapp.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.noteapp.R;
import com.example.noteapp.ui.notes.NotesFragment;
import com.example.noteapp.ui.todos.TodosFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            if (item.getItemId() == R.id.menu_notes) {
                fragment = new NotesFragment();
            } else if (item.getItemId() == R.id.menu_todos) {
                fragment = new TodosFragment();
            }
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment)
                    .commit();
                return true;
            }
            return false;
        });

        nav.setSelectedItemId(R.id.menu_notes);
    }
}
