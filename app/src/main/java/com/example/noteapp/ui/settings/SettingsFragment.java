package com.example.noteapp.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

public class SettingsFragment extends Fragment {

    private Switch switchDarkMode;
    private Button btnLoginSync;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        switchDarkMode = view.findViewById(R.id.switchDarkMode);
        btnLoginSync = view.findViewById(R.id.btnLoginSync);

        // Load trạng thái dark mode từ SharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);
        switchDarkMode.setChecked(isDarkMode);
        setDarkMode(isDarkMode);

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setDarkMode(isChecked);
            // Lưu trạng thái dark mode
            prefs.edit().putBoolean("dark_mode", isChecked).apply();
        });

        btnLoginSync.setOnClickListener(v -> {
            // TODO: Thêm xử lý đăng nhập ở đây
            Toast.makeText(getContext(), "Chức năng đăng nhập đang phát triển", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void setDarkMode(boolean enabled) {
        if (enabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
