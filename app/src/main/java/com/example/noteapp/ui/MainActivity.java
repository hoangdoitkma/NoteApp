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
import com.example.noteapp.data.FirestoreNoteRepository;
import com.example.noteapp.data.NoteDao;
import com.example.noteapp.data.NoteDatabase;
import com.example.noteapp.data.NoteRepository;

import com.example.noteapp.ui.expense.ExpenseFragment;
import com.example.noteapp.ui.notes.NotesFragment;
import com.example.noteapp.ui.qr.QrFragment;
import com.example.noteapp.ui.settings.SettingsFragment;
import com.example.noteapp.ui.todos.TodosFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

        // 🛡️ Yêu cầu quyền thông báo (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
        }

        // ✅ Đồng bộ nếu đã đăng nhập
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            NoteDao noteDao = NoteDatabase.getInstance(this).noteDao();
            FirestoreNoteRepository repo = new FirestoreNoteRepository(this);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                repo.syncNotesFromFirestore(noteDao);
                repo.pushAllNotesToFirestore(noteDao.getAllSync());
            });
        }


        // 🔄 Mặc định mở Ghi chú
        loadFragment(new NotesFragment(), "Ghi chú");
        nav.setSelectedItemId(R.id.navigation_notes);

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_notes) {
                loadFragment(new NotesFragment(), "Ghi chú");
            } else if (id == R.id.navigation_todos) {
                loadFragment(new TodosFragment(), "Việc cần làm");
            } else if (id == R.id.navigation_qr) {
                loadFragment(new QrFragment(), "QR của tôi");
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
