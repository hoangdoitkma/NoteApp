package com.example.noteapp.data;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.noteapp.model.Note;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NoteRepository {

    private final NoteDao noteDao;
    private final ExecutorService executorService;

    public NoteRepository(Context context) {
        noteDao = new NoteDao(context);
        executorService = Executors.newSingleThreadExecutor();
    }

    // LiveData wrapper cho danh sách note theo trạng thái locked
    public LiveData<List<Note>> getNotesWhereLocked(boolean locked) {
        MutableLiveData<List<Note>> liveData = new MutableLiveData<>();
        executorService.execute(() -> {
            List<Note> notes = noteDao.getNotesWhereLocked(locked);
            liveData.postValue(notes);
        });
        return liveData;
    }

    // LiveData wrapper cho tất cả note
    public LiveData<List<Note>> getAllNotes() {
        MutableLiveData<List<Note>> liveData = new MutableLiveData<>();
        executorService.execute(() -> {
            List<Note> notes = noteDao.getAllNotes();
            liveData.postValue(notes);
        });
        return liveData;
    }

    // LiveData wrapper cho Note theo id
    public LiveData<Note> getNoteById(int id) {
        MutableLiveData<Note> liveData = new MutableLiveData<>();
        executorService.execute(() -> {
            Note note = noteDao.getNoteById(id);
            liveData.postValue(note);
        });
        return liveData;
    }
    // Lấy danh sách note theo trạng thái pinned
    public LiveData<List<Note>> getNotesWherePinned(boolean pinned) {
        MutableLiveData<List<Note>> liveData = new MutableLiveData<>();
        executorService.execute(() -> {
            List<Note> notes = noteDao.getNotesWherePinned(pinned);
            liveData.postValue(notes);
        });
        return liveData;
    }

    public LiveData<List<Note>> getPinnedUnlockedNotes(int limit) {
        MutableLiveData<List<Note>> liveData = new MutableLiveData<>();
        executorService.execute(() -> {
            List<Note> pinnedNotes = noteDao.getPinnedUnlockedNotes(limit);
            liveData.postValue(pinnedNotes);
        });
        return liveData;
    }
    public List<Note> getPinnedUnlockedNotesSortedByLastEditedAsc() {
        return noteDao.getPinnedUnlockedNotesSortedByLastEditedAsc();
    }

    public LiveData<List<Note>> getUnpinnedUnlockedNotes() {
        MutableLiveData<List<Note>> liveData = new MutableLiveData<>();
        executorService.execute(() -> {
            List<Note> unpinnedNotes = noteDao.getUnpinnedUnlockedNotes();
            liveData.postValue(unpinnedNotes);
        });
        return liveData;
    }

    // Thêm ghi chú
    public void insertNote(Note note) {
        executorService.execute(() -> {
            noteDao.insertNote(note);
        });
    }

    // Cập nhật ghi chú
    public void updateNote(Note note) {
        executorService.execute(() -> {
            noteDao.updateNote(note);
        });
    }

    // Xóa ghi chú
    public void deleteNote(int id) {
        executorService.execute(() -> {
            noteDao.deleteNote(id);
        });
    }
}
