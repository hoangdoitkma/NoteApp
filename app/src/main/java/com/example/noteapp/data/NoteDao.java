package com.example.noteapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.noteapp.model.Note;

import java.util.List;

@Dao
public interface NoteDao {

    // ====== Thao tác cơ bản ======
    @Insert
    void insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    // ====== Lấy tất cả ghi chú (bao gồm cả bị khóa) ======
    @Query("SELECT * FROM notes ORDER BY pinned DESC, lastEdited DESC")
    LiveData<List<Note>> getAllNotes();

    // ====== Ghi chú được ghim ======
    @Query("SELECT * FROM notes WHERE pinned = 1 ORDER BY lastEdited DESC")
    LiveData<List<Note>> getPinnedNotes();

    // ====== Ghi chú chưa ghim và không bị khóa ======
    @Query("SELECT * FROM notes WHERE pinned = 0 AND locked = 0 ORDER BY lastEdited DESC")
    LiveData<List<Note>> getUnpinnedUnlockedNotes();

    // ====== Tìm kiếm ghi chú (chỉ lấy những ghi chú không bị khóa) ======
    @Query("SELECT * FROM notes WHERE locked = 0 AND (title LIKE :query OR content LIKE :query) ORDER BY pinned DESC, lastEdited DESC")
    LiveData<List<Note>> searchNotes(String query);

    // ====== Tìm kiếm ghi chú đã bị khóa ======
    @Query("SELECT * FROM notes WHERE locked = 1 AND (title LIKE :query OR content LIKE :query) ORDER BY lastEdited DESC")
    LiveData<List<Note>> searchLockedNotes(String query);

    // ====== Lấy tất cả ghi chú bị khóa ======
    @Query("SELECT * FROM notes WHERE locked = 1 ORDER BY lastEdited DESC")
    LiveData<List<Note>> getLockedNotes();

    // ====== Tìm kiếm theo tag ======
    @Query("SELECT * FROM notes WHERE content LIKE :tag AND locked = 0 ORDER BY lastEdited DESC")
    LiveData<List<Note>> searchByTag(String tag);

    // ====== Tìm kiếm theo ngày tạo ======
    @Query("SELECT * FROM notes WHERE timestamp BETWEEN :start AND :end AND locked = 0 ORDER BY timestamp DESC")
    LiveData<List<Note>> searchByCreatedDate(long start, long end);

    // ====== Tìm kiếm theo ngày chỉnh sửa ======
    @Query("SELECT * FROM notes WHERE lastEdited BETWEEN :start AND :end AND locked = 0 ORDER BY lastEdited DESC")
    LiveData<List<Note>> searchByEditedDate(long start, long end);

    // ====== Ghi chú được ghim và không bị khóa, giới hạn số lượng ======
    @Query("SELECT * FROM notes WHERE pinned = 1 AND locked = 0 ORDER BY lastEdited DESC LIMIT :limit")
    LiveData<List<Note>> getPinnedUnlockedNotes(int limit);

    // ====== Ghi chú được ghim và không bị khóa, sắp xếp theo ngày sửa tăng dần ======
    @Query("SELECT * FROM notes WHERE pinned = 1 AND locked = 0 ORDER BY lastEdited ASC")
    LiveData<List<Note>> getPinnedUnlockedNotesSortedByLastEditedAsc();

    // ====== Lấy danh sách ghi chú theo trạng thái khóa ======
    @Query("SELECT * FROM notes WHERE locked = :isLocked ORDER BY lastEdited DESC")
    LiveData<List<Note>> getNotesWhereLocked(boolean isLocked);

    // ====== Xóa ghi chú theo ID ======
    @Query("DELETE FROM notes WHERE id = :noteId")
    void deleteNote(int noteId);
    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    Note getNoteByIdNow(int id);
    @Query("SELECT * FROM notes WHERE id = :noteId LIMIT 1")
    LiveData<Note> getNoteById(int noteId);
    @Query("SELECT * FROM notes WHERE id = :noteId LIMIT 1")
    Note getNoteByIdSync(int noteId); // dùng cho đồng bộ

    @Query("SELECT * FROM notes")
    List<Note> getAllSync();

}
