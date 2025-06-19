package com.example.noteapp.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.noteapp.R;
import com.example.noteapp.data.FirestoreNoteRepository;
import com.example.noteapp.data.NoteDao;
import com.example.noteapp.data.NoteDatabase;
import com.example.noteapp.model.Note;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingsFragment extends Fragment {

    private Switch switchDarkMode;
    private Switch switchAutoSync;
    private Button btnLoginSync;
    private NoteDao noteDao;
    private FirestoreNoteRepository firestoreRepo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        switchDarkMode = view.findViewById(R.id.switchDarkMode);
        switchAutoSync = view.findViewById(R.id.switch_auto_sync);
        btnLoginSync = view.findViewById(R.id.btnLoginSync);
        Button btnSyncNow = view.findViewById(R.id.btnSyncNow);

        // Khởi tạo DAO và Firestore repository
        noteDao = NoteDatabase.getInstance(requireContext()).noteDao();
        firestoreRepo = new FirestoreNoteRepository(requireContext());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());

        // Dark Mode
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);
        switchDarkMode.setChecked(isDarkMode);
        setDarkMode(isDarkMode);

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setDarkMode(isChecked);
            prefs.edit().putBoolean("dark_mode", isChecked).apply();
        });

        // Auto Sync
        boolean isAutoSync = prefs.getBoolean("auto_sync", false);
        switchAutoSync.setChecked(isAutoSync);

        switchAutoSync.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("auto_sync", isChecked).apply();
            Toast.makeText(getContext(), "Đồng bộ tự động " + (isChecked ? "đã bật" : "đã tắt"), Toast.LENGTH_SHORT).show();
        });

        // Placeholder đăng nhập
        btnLoginSync.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Chức năng đăng nhập đang phát triển", Toast.LENGTH_SHORT).show();
        });

        // Nút đồng bộ thủ công
        btnSyncNow.setOnClickListener(v -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                List<Note> notes = noteDao.getAllSync(); // Đảm bảo gọi dưới background thread
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Chưa đăng nhập Google, không thể đồng bộ", Toast.LENGTH_SHORT).show()
                    );
                    return;
                }
                Log.d("SYNC", "Số ghi chú lấy từ Room: " + notes.size());

                firestoreRepo.pushAllNotesToFirestore(notes);

                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Đã đẩy ghi chú lên Firestore", Toast.LENGTH_SHORT).show()
                );
                executor.shutdown();
            });
        });

        return view;
    }

    private void setDarkMode(boolean enabled) {
        AppCompatDelegate.setDefaultNightMode(
                enabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }
}
