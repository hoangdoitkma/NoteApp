package com.example.noteapp.ui.notes;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.noteapp.data.NoteRepository;
import com.example.noteapp.model.Note;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {

    private final NoteRepository repository;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        repository = new NoteRepository(application.getApplicationContext());
    }

    public LiveData<List<Note>> getAllNotes() {
        return repository.getAllNotes();
    }

    public LiveData<List<Note>> getAllLockedNotes() {
        return repository.getNotesWhereLocked(true);
    }

    public LiveData<List<Note>> getAllUnlockedNotes() {
        return repository.getNotesWhereLocked(false);
    }

    public void insertNote(Note note) {
        repository.insertNote(note);
    }

    public void updateNote(Note note) {
        repository.updateNote(note);
    }

    public void deleteNote(int id) {
        repository.deleteNote(id);
    }

    public LiveData<List<Note>> getNotesWhereLocked(boolean locked) {
        return repository.getNotesWhereLocked(locked);
    }

    public LiveData<Note> getNoteById(int id) {
        return repository.getNoteById(id);
    }
}
