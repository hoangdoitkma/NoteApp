package com.example.noteapp.ui.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
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
import java.util.Collections;
import java.util.List;

public class NotesFragment extends Fragment {

    private NoteAdapter adapter;
    private NoteViewModel noteViewModel;
    private Spinner spinnerFilter;

    private boolean biometricVerifiedForLockedNotes = false;
    private int selectedFilterPosition = 0;
    private AutoCompleteTextView autoCompleteTextViewFilter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notes, container, false);



        autoCompleteTextViewFilter = v.findViewById(R.id.autoCompleteTextViewFilter);

        String[] filterOptions = getResources().getStringArray(R.array.note_filter_options);
        ArrayAdapter<String> adapterDropdown = new ArrayAdapter<>(
                getContext(),
                R.layout.spinner_item,
                filterOptions
        );
        autoCompleteTextViewFilter.setAdapter(adapterDropdown);

        // Đặt mặc định chọn option đầu tiên
        autoCompleteTextViewFilter.setText(filterOptions[selectedFilterPosition], false);

        autoCompleteTextViewFilter.setOnItemClickListener((parent, view, position, id) -> {
            selectedFilterPosition = position;

            if (position == 0) {
                biometricVerifiedForLockedNotes = false;
                reloadCurrentFilter();
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
                            autoCompleteTextViewFilter.setText(filterOptions[0], false);
                            reloadCurrentFilter();
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Không hỗ trợ xác thực", Toast.LENGTH_SHORT).show();
                    selectedFilterPosition = 0;
                    autoCompleteTextViewFilter.setText(filterOptions[0], false);
                    reloadCurrentFilter();
                }
            }
        });

        adapter = new NoteAdapter(new ArrayList<>());
        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                if (note.isLocked()) {
                    if (biometricVerifiedForLockedNotes) {
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

    private void reloadCurrentFilter() {
        if (selectedFilterPosition == 0) {
            loadUnlockedNotes();
        } else if (biometricVerifiedForLockedNotes) {
            loadLockedNotes();
        }
    }

    private void loadUnlockedNotes() {
        noteViewModel.getPinnedUnlockedNotes(3).observe(getViewLifecycleOwner(), pinnedNotesOriginal -> {
            List<Note> pinnedNotes = pinnedNotesOriginal != null ? pinnedNotesOriginal : new ArrayList<>();

            noteViewModel.getUnpinnedUnlockedNotes().observe(getViewLifecycleOwner(), unpinnedNotesOriginal -> {
                List<Note> unpinnedNotes = unpinnedNotesOriginal != null ? unpinnedNotesOriginal : new ArrayList<>();

                List<Note> combined = new ArrayList<>(pinnedNotes);
                combined.addAll(unpinnedNotes);

                adapter.setNotes(combined);
            });
        });

    }

    private void pinNoteWithLimit(Note note) {
        if (note.isPinned()) {
            // Nếu note đã ghim, bỏ ghim đi
            note.setPinned(false);
            noteViewModel.updateNote(note);
            reloadCurrentFilter();

        } else {
            // Lấy danh sách note ghim đang có
            noteViewModel.getPinnedUnlockedNotesSortedByLastEditedAsc().observe(getViewLifecycleOwner(), pinnedNotes -> {
                if (pinnedNotes != null && pinnedNotes.size() >= 3) {
                    // Bỏ ghim note ghim lâu nhất (đầu danh sách)
                    Note oldestPinnedNote = pinnedNotes.get(0);
                    oldestPinnedNote.setPinned(false);
                    noteViewModel.updateNote(oldestPinnedNote);
                }
                // Ghim note mới
                note.setPinned(true);
                noteViewModel.updateNote(note);

                // Reload danh sách
                reloadCurrentFilter();

            });
        }
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
            reloadCurrentFilter();

            dialog.dismiss();
        });

        view.findViewById(R.id.option_toggle_lock).setOnClickListener(v -> {
            if (BiometricHelper.isBiometricAvailable(requireContext())) {
                if (getActivity() instanceof AppCompatActivity) {
                    BiometricHelper.showBiometricPrompt((AppCompatActivity) getActivity(), success -> {
                        if (success) {
                            boolean wasLocked = note.isLocked();
                            note.setLocked(!wasLocked);
                            noteViewModel.updateNote(note);

                            if (wasLocked) {
                                Toast.makeText(getContext(), "Đã bỏ khóa ghi chú", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Đã khóa ghi chú", Toast.LENGTH_SHORT).show();
                            }

                            reloadCurrentFilter();

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
            pinNoteWithLimit(note);
            dialog.dismiss();

        });

        dialog.show();
    }

    private boolean showLockedNotes = false;

    @Override
    public void onResume() {
        super.onResume();
        if (selectedFilterPosition == 0) {
            reloadCurrentFilter();
            // reload ghi chú không khóa
        } else if (biometricVerifiedForLockedNotes) {
            loadLockedNotes(); // reload ghi chú đã khóa (nếu đã xác thực)
        }
    }
}
