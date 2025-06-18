package com.example.noteapp.ui.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class NotesFragment extends Fragment {

    private NoteAdapter adapter;
    private NoteViewModel noteViewModel;
    private AutoCompleteTextView autoCompleteTextViewFilter;
    private boolean biometricVerifiedForLockedNotes = false;
    private int selectedFilterPosition = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notes, container, false);

        // ViewModel
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        // Filter
        autoCompleteTextViewFilter = v.findViewById(R.id.autoCompleteTextViewFilter);
        String[] filterOptions = getResources().getStringArray(R.array.note_filter_options);
        ArrayAdapter<String> adapterDropdown = new ArrayAdapter<>(requireContext(), R.layout.spinner_item, filterOptions);
        autoCompleteTextViewFilter.setAdapter(adapterDropdown);
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
                            biometricVerifiedForLockedNotes = false;
                            selectedFilterPosition = 0;
                            autoCompleteTextViewFilter.setText(filterOptions[0], false);
                            reloadCurrentFilter();
                        }
                    });
                }
            }
        });

        // RecyclerView
        RecyclerView recycler = v.findViewById(R.id.recyclerViewNotes);
        adapter = new NoteAdapter(new ArrayList<>());
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);

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
                                    Toast.makeText(getContext(), "Xác thực thất bại", Toast.LENGTH_SHORT).show();
                                }
                            });
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

        // FAB
        FloatingActionButton fabAddNote = v.findViewById(R.id.fabAddNote);
        fabAddNote.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), AddNoteActivity.class));
        });

        reloadCurrentFilter();

        return v;
    }

    private void reloadCurrentFilter() {
        if (selectedFilterPosition == 0) {
            loadUnlockedNotes();
        } else if (biometricVerifiedForLockedNotes) {
            loadLockedNotes();
        }
    }

    private void loadUnlockedNotes() {
        noteViewModel.getPinnedUnlockedNotes(3).observe(getViewLifecycleOwner(), pinnedNotes -> {
            noteViewModel.getUnpinnedUnlockedNotes().observe(getViewLifecycleOwner(), unpinnedNotes -> {
                List<Note> all = new ArrayList<>();
                if (pinnedNotes != null) all.addAll(pinnedNotes);
                if (unpinnedNotes != null) all.addAll(unpinnedNotes);
                adapter.setNotes(all);
            });
        });
    }

    private void loadLockedNotes() {
        noteViewModel.getLockedNotes().observe(getViewLifecycleOwner(), notes -> {
            if (notes != null) {
                adapter.setNotes(notes);
            }
        });
    }

    private void openEditNoteActivity(Note note) {
        Intent intent = new Intent(getActivity(), AddNoteActivity.class);
        intent.putExtra("noteId", note.getId());
        startActivity(intent);
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
            BiometricHelper.showBiometricPrompt((AppCompatActivity) requireActivity(), success -> {
                if (success) {
                    note.setLocked(!note.isLocked());
                    noteViewModel.update(note);
                    reloadCurrentFilter();
                    Toast.makeText(getContext(), note.isLocked() ? "Đã khóa ghi chú" : "Đã mở khóa ghi chú", Toast.LENGTH_SHORT).show();
                }
            });
            dialog.dismiss();
        });

        view.findViewById(R.id.option_pin).setOnClickListener(v -> {
            pinNoteWithLimit(note);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void pinNoteWithLimit(Note note) {
        if (note.isPinned()) {
            note.setPinned(false);
            noteViewModel.update(note);
            reloadCurrentFilter();
        } else {
            noteViewModel.getPinnedUnlockedNotesSortedByLastEditedAsc().observe(getViewLifecycleOwner(), pinned -> {
                if (pinned != null && pinned.size() >= 3) {
                    Note oldest = pinned.get(0);
                    oldest.setPinned(false);
                    noteViewModel.update(oldest);
                }
                note.setPinned(true);
                noteViewModel.update(note);
                reloadCurrentFilter();
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadCurrentFilter();
    }
}
