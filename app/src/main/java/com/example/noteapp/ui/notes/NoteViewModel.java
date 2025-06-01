package com.example.noteapp.ui.notes;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.noteapp.data.NoteRepository;
import com.example.noteapp.model.Note;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {

    private final NoteRepository repository;
    private final MutableLiveData<List<Note>> allNotes = new MutableLiveData<>();

    public NoteViewModel(@NonNull Application application) {
        super(application);
        repository = new NoteRepository(application);
        loadNotes();
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    public void loadNotes() {
        allNotes.setValue(repository.getAllNotes());
    }

    public void addNote(Note note) {
        repository.addNote(note);
        loadNotes();
    }

    public void updateNote(Note note) {
        repository.updateNote(note);
        loadNotes();
    }

    public void deleteNote(Note note) {
        repository.deleteNote(note);
        loadNotes();
    }
}
