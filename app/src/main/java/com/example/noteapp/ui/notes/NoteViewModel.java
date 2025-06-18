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
    private final LiveData<List<Note>> allNotes;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        repository = new NoteRepository(application);
        allNotes = repository.getAllNotes();
    }
    public LiveData<Note> getNoteById(int id) {
        return repository.getNoteById(id);
    }


    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    public LiveData<List<Note>> getPinnedNotes() {
        return repository.getPinnedNotes();
    }

    public LiveData<List<Note>> getUnpinnedUnlockedNotes() {
        return repository.getUnpinnedUnlockedNotes();
    }

    public LiveData<List<Note>> getLockedNotes() {
        return repository.getLockedNotes();
    }

    public LiveData<List<Note>> getPinnedUnlockedNotes(int limit) {
        return repository.getPinnedUnlockedNotes(limit);
    }

    public LiveData<List<Note>> getPinnedUnlockedNotesSortedByLastEditedAsc() {
        return repository.getPinnedUnlockedNotesSortedByLastEditedAsc();
    }

    public LiveData<List<Note>> getNotesWhereLocked(boolean isLocked) {
        return repository.getNotesWhereLocked(isLocked);
    }

    public LiveData<List<Note>> searchNotes(String query) {
        return repository.searchNotes(query);
    }

    public LiveData<List<Note>> searchLockedNotes(String query) {
        return repository.searchLockedNotes(query);
    }

    public LiveData<List<Note>> searchByTag(String tag) {
        return repository.searchByTag(tag);
    }

    public LiveData<List<Note>> searchByCreatedDate(long start, long end) {
        return repository.searchByCreatedDate(start, end);
    }

    public LiveData<List<Note>> searchByEditedDate(long start, long end) {
        return repository.searchByEditedDate(start, end);
    }

    public void insert(Note note) {
        repository.insert(note);
    }

    public void update(Note note) {
        repository.update(note);
    }

    public void delete(Note note) {
        repository.delete(note);
    }

    public void deleteNote(int noteId) {
        repository.deleteNote(noteId);
    }

    public void syncFromFirebase() {
        repository.syncFromFirebase();
    }
}
