package com.example.noteapp.data;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.noteapp.model.Note;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NoteRepository {

    private final NoteDao noteDao;
    private final LiveData<List<Note>> allNotes;
    private final FirebaseNoteHelper firebaseHelper;
    private final ExecutorService executorService;

    public NoteRepository(Application app) {
        NoteDatabase db = NoteDatabase.getInstance(app);
        noteDao = db.noteDao();
        allNotes = noteDao.getAllNotes();
        firebaseHelper = new FirebaseNoteHelper("default_user"); // Replace with FirebaseAuth UID if needed
        executorService = Executors.newSingleThreadExecutor();
    }

    // ========================= //
    //      Local - Room DB      //
    // ========================= //

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    public void insert(Note note) {
        executorService.execute(() -> {
            noteDao.insert(note);
            firebaseHelper.uploadNote(note);
        });
    }

    public void update(Note note) {
        executorService.execute(() -> {
            noteDao.update(note);
            firebaseHelper.uploadNote(note);
        });
    }

    public void delete(Note note) {
        executorService.execute(() -> {
            noteDao.delete(note);
            firebaseHelper.deleteNote(note);
        });
    }

    public void deleteNote(int id) {
        executorService.execute(() -> noteDao.deleteNote(id));
    }

    // ========================= //
    //         Firebase          //
    // ========================= //

    public void syncFromFirebase() {
        firebaseHelper.syncAllFromFirebase(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Note note = snap.getValue(Note.class);
                    if (note != null) {
                        insert(note); // Lưu về local Room
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseSync", "Sync cancelled: " + error.getMessage());
            }
        });
    }

    // ========================= //
    //       Search / Filter     //
    // ========================= //

    public LiveData<List<Note>> searchNotes(String query) {
        return noteDao.searchNotes("%" + query + "%");
    }

    public LiveData<List<Note>> searchLockedNotes(String query) {
        return noteDao.searchLockedNotes("%" + query + "%");
    }

    public LiveData<List<Note>> getLockedNotes() {
        return noteDao.getLockedNotes();
    }

    public LiveData<List<Note>> searchByTag(String tag) {
        return noteDao.searchByTag("%" + tag + "%");
    }

    public LiveData<List<Note>> searchByCreatedDate(long start, long end) {
        return noteDao.searchByCreatedDate(start, end);
    }

    public LiveData<List<Note>> searchByEditedDate(long start, long end) {
        return noteDao.searchByEditedDate(start, end);
    }

    public LiveData<List<Note>> getPinnedNotes() {
        return noteDao.getPinnedNotes();
    }

    public LiveData<List<Note>> getUnpinnedUnlockedNotes() {
        return noteDao.getUnpinnedUnlockedNotes();
    }

    public LiveData<List<Note>> getPinnedUnlockedNotes(int limit) {
        return noteDao.getPinnedUnlockedNotes(limit);
    }

    public LiveData<List<Note>> getPinnedUnlockedNotesSortedByLastEditedAsc() {
        return noteDao.getPinnedUnlockedNotesSortedByLastEditedAsc();
    }

    public LiveData<List<Note>> getNotesWhereLocked(boolean isLocked) {
        return noteDao.getNotesWhereLocked(isLocked);
    }
    public LiveData<Note> getNoteById(int id) {
        return noteDao.getNoteById(id);
    }

}
