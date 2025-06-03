package com.example.noteapp.ui.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.R;
import com.example.noteapp.model.Note;
import com.example.noteapp.util.BiometricHelper;
import com.example.noteapp.ui.addnote.AddNoteActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class NotesFragment extends Fragment {

    private NoteAdapter adapter;
    private NoteViewModel noteViewModel;
    private Spinner spinnerFilter;


    private boolean biometricVerifiedForLockedNotes = false;
    private int selectedFilterPosition = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notes, container, false);

        spinnerFilter = v.findViewById(R.id.spinnerFilter);

        adapter = new NoteAdapter(new ArrayList<>());
        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                if (note.isLocked()) {
                    if (biometricVerifiedForLockedNotes) {
                        // Đã xác thực, mở luôn
                        openEditNoteActivity(note);
                    } else {
                        if (getActivity() instanceof AppCompatActivity) {
                            BiometricHelper.showBiometricPrompt((AppCompatActivity) getActivity(), success -> {
                                if (success) {
                                    biometricVerifiedForLockedNotes = true;
                                    openEditNoteActivity(note);
                                } else {
                                    Toast.makeText(getContext(), "Xác thực không thành công", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(getContext(), "Không hỗ trợ xác thực trên Activity này", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    openEditNoteActivity(note);
                }
            }

            @Override
            public void onItemLongClick(Note note, int position) {
                showNoteOptionsDialog(note, position);
            }
        });

        RecyclerView recycler = v.findViewById(R.id.recyclerViewNotes);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedFilterPosition = position; // lưu lại vị trí được chọn

                if (position == 0) {
                    biometricVerifiedForLockedNotes = false;
                    loadUnlockedNotes();
                } else if (position == 1) {
                    if (getActivity() instanceof AppCompatActivity) {
                        BiometricHelper.showBiometricPrompt((AppCompatActivity) getActivity(), success -> {
                            if (success) {
                                biometricVerifiedForLockedNotes = true;
                                loadLockedNotes();
                            } else {
                                Toast.makeText(getContext(), "Xác thực không thành công", Toast.LENGTH_SHORT).show();
                                biometricVerifiedForLockedNotes = false;
                                selectedFilterPosition = 0;
                                spinnerFilter.setSelection(0);
                                loadUnlockedNotes();
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Không hỗ trợ xác thực", Toast.LENGTH_SHORT).show();
                        selectedFilterPosition = 0;
                        spinnerFilter.setSelection(0);
                        loadUnlockedNotes();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });


        // Mặc định load ghi chú chưa khóa
        spinnerFilter.setSelection(selectedFilterPosition); // Tự trigger lại itemSelected

        FloatingActionButton fabAddNote = v.findViewById(R.id.fabAddNote);
        fabAddNote.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), AddNoteActivity.class);
            startActivity(intent);
        });

        return v;
    }

    private void openEditNoteActivity(Note note) {
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

    private void loadUnlockedNotes() {
        if (spinnerFilter.getSelectedItemPosition() != 0) {
            spinnerFilter.setSelection(0);
        }
        noteViewModel.getNotesWhereLocked(false).observe(getViewLifecycleOwner(), notes -> {
            if (notes != null) {
                adapter.setNotes(notes);
            }
        });
    }


    private void showNoteOptionsDialog(Note note, int position) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_note_options, null);
        dialog.setContentView(view);

        view.findViewById(R.id.option_delete).setOnClickListener(v -> {
            noteViewModel.deleteNote(note.getId());
            adapter.getNotes().remove(position);
            adapter.notifyItemRemoved(position);
            Toast.makeText(getContext(), "Đã xóa ghi chú", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        view.findViewById(R.id.option_lock).setOnClickListener(v -> {
            if (BiometricHelper.isBiometricAvailable(requireContext())) {
                if (getActivity() instanceof AppCompatActivity) {
                    BiometricHelper.showBiometricPrompt((AppCompatActivity) getActivity(), success -> {
                        if (success) {
                            note.setLocked(true);
                            noteViewModel.updateNote(note);
                            loadUnlockedNotes(); // ẩn ghi chú khóa đi
                            Toast.makeText(getContext(), "Đã khóa ghi chú", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Xác thực không thành công", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Không hỗ trợ xác thực trên Activity này", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Thiết bị không hỗ trợ sinh trắc học", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        view.findViewById(R.id.option_pin).setOnClickListener(v -> {
            note.setPinned(true);
            noteViewModel.updateNote(note);
            Toast.makeText(getContext(), "Đã ghim ghi chú", Toast.LENGTH_SHORT).show();
            loadUnlockedNotes(); // Cập nhật lại danh sách
            dialog.dismiss();
        });

        dialog.show();
    }
    private boolean showLockedNotes = false;
    @Override

    public void onResume() {
        super.onResume();
        if (biometricVerifiedForLockedNotes && showLockedNotes) {
            loadLockedNotes(); // Explicitly reload locked notes
        }
    }
}
