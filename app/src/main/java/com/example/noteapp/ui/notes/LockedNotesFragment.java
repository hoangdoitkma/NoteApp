package com.example.noteapp.ui.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.R;
import com.example.noteapp.model.Note;
import com.example.noteapp.ui.addnote.AddNoteActivity;
import com.example.noteapp.util.BiometricHelper;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class LockedNotesFragment extends Fragment {

    private NoteAdapter adapter;
    private NoteViewModel noteViewModel;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_locked_notes, container, false); // Sửa lại đúng layout fragment

        adapter = new NoteAdapter(new ArrayList<>());
        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                if (getActivity() instanceof AppCompatActivity) {
                    BiometricHelper.showBiometricPrompt((AppCompatActivity) getActivity(), success -> {
                        if (success) {
                            openEditNoteActivity(note);
                        } else {
                            Toast.makeText(getContext(), "Xác thực không thành công", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Không hỗ trợ xác thực trên Activity này", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onItemLongClick(Note note, int position) {
                showUnlockOptionsDialog(note, position);
            }
        });

        RecyclerView recycler = v.findViewById(R.id.recyclerViewLockedNotes); // Lúc này v mới đúng
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        loadLockedNotes();

        return v;
    }


    private void openEditNoteActivity(Note note) {
        // Có thể mở Activity xem sửa ghi chú hoặc một Activity chuyên biệt cho ghi chú khóa
        // Tạm dùng AddNoteActivity (hoặc bạn có thể đổi theo ý)
        Intent intent = new Intent(getActivity(), AddNoteActivity.class);
        intent.putExtra("noteId", note.getId());
        startActivity(intent);
    }

    private void loadLockedNotes() {
        noteViewModel.getNotesWhereLocked(true).observe(getViewLifecycleOwner(), notes -> {
            if (notes != null) {
                adapter.setNotes(notes);
            }
        });
    }

    private void showUnlockOptionsDialog(Note note, int position) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_note_options, null);
        dialog.setContentView(view);
        MaterialButton btnLock = view.findViewById(R.id.option_lock);
        MaterialButton btnUnlock = view.findViewById(R.id.option_unlock);
        // Thay đổi tùy chọn từ khóa -> bỏ khóa
        view.findViewById(R.id.option_lock).setVisibility(View.GONE);
        view.findViewById(R.id.option_unlock).setVisibility(View.VISIBLE);

        view.findViewById(R.id.option_delete).setOnClickListener(v -> {
            noteViewModel.deleteNote(note.getId());
            adapter.getNotes().remove(position);
            adapter.notifyItemRemoved(position);
            Toast.makeText(getContext(), "Đã xóa ghi chú", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        view.findViewById(R.id.option_unlock).setOnClickListener(v -> {
            if (getActivity() instanceof AppCompatActivity) {
                BiometricHelper.showBiometricPrompt((AppCompatActivity) getActivity(), success -> {
                    if (success) {
                        note.setLocked(false);
                        noteViewModel.updateNote(note);
                        loadLockedNotes(); // Cập nhật lại danh sách khóa
                        Toast.makeText(getContext(), "Đã bỏ khóa ghi chú", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Xác thực không thành công", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "Không hỗ trợ xác thực trên Activity này", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        view.findViewById(R.id.option_pin).setOnClickListener(v -> {
            note.setPinned(true);
            noteViewModel.updateNote(note);
            Toast.makeText(getContext(), "Đã ghim ghi chú", Toast.LENGTH_SHORT).show();
            loadLockedNotes(); // Cập nhật lại danh sách khóa
            dialog.dismiss();
        });

        dialog.show();
    }
}
