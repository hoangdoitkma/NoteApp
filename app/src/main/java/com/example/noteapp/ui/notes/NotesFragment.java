package com.example.noteapp.ui.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.*;

import com.example.noteapp.R;
import com.example.noteapp.data.NoteRepository;
import com.example.noteapp.model.Note;
import com.example.noteapp.ui.addnote.AddNoteActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.noteapp.util.BiometricHelper;

import java.util.List;

public class NotesFragment extends Fragment {
    private NoteRepository repo;
    private NoteAdapter adapter;
    private NoteViewModel noteViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notes, container, false);
        repo = new NoteRepository(getContext());

        adapter = new NoteAdapter();

        adapter.setOnNoteLongClickListener(note -> {
            showNoteOptionsDialog(note);
        });

        adapter.setOnNoteClickListener(note -> {
            Intent intent = new Intent(getContext(), EditNoteActivity.class);
            intent.putExtra("note_id", note.getId());  // Truyền ID ghi chú
            startActivity(intent);
        });

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        noteViewModel.getAllNotes().observe(getViewLifecycleOwner(), notes -> {
            adapter.setNotes(notes);
        });

        RecyclerView recycler = v.findViewById(R.id.recyclerViewNotes);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);

        FloatingActionButton fab = v.findViewById(R.id.fabAddNote);
        fab.setOnClickListener(view -> startActivity(new Intent(getContext(), AddNoteActivity.class)));

        return v;
    }

    private void showNoteOptionsDialog(Note note) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_note_options, null);
        dialog.setContentView(view);

        view.findViewById(R.id.option_delete).setOnClickListener(v -> {
            noteViewModel.deleteNote(note);
            dialog.dismiss();
        });

        view.findViewById(R.id.option_lock).setOnClickListener(v -> {
            if (BiometricHelper.isBiometricAvailable(requireContext())) {
                BiometricHelper.showBiometricPrompt((AppCompatActivity) requireActivity(), success -> {
                    note.setLocked(true);
                    noteViewModel.updateNote(note);
                });
            } else {
                // Xử lý nếu không có biometric
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Nếu dùng LiveData thì không cần gọi thủ công, nhưng để chắc chắn:
        noteViewModel.loadNotes();
    }
}
