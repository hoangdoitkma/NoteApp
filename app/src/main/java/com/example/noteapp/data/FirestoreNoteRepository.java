package com.example.noteapp.data;

import android.content.Context;
import android.util.Log;

import com.example.noteapp.model.Note;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FirestoreNoteRepository {

    private final FirebaseFirestore firestore;
    private final String userId;
    private static final String TAG = "FirestoreRepo";

    public FirestoreNoteRepository(Context context) {
        this.firestore = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            userId = auth.getCurrentUser().getUid();
        } else {
            userId = null;
            Log.w(TAG, "Người dùng chưa đăng nhập Firebase.");
        }
    }

    public void saveNoteToFirestore(Note note) {
        if (userId == null) {
            Log.w(TAG, "Bỏ qua saveNote vì userId null.");
            return;
        }

        firestore.collection("users")
                .document(userId)
                .collection("notes")
                .document(String.valueOf(note.getId()))
                .set(note)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Đã lưu note lên Firestore"))
                .addOnFailureListener(e -> Log.e(TAG, "Lỗi khi lưu note", e));
    }

    public void deleteNoteFromFirestore(int noteId) {
        if (userId == null) {
            Log.w(TAG, "Bỏ qua deleteNote vì userId null.");
            return;
        }

        firestore.collection("users")
                .document(userId)
                .collection("notes")
                .document(String.valueOf(noteId))
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Đã xóa note khỏi Firestore"))
                .addOnFailureListener(e -> Log.e(TAG, "Lỗi khi xóa note", e));
    }

    public void syncNotesFromFirestore(NoteDao noteDao) {
        if (userId == null) return;

        firestore.collection("users")
                .document(userId)
                .collection("notes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.execute(() -> {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            Note note = doc.toObject(Note.class);
                            if (note != null) {
                                Note existing = noteDao.getNoteByIdSync(note.getId());
                                if (existing == null) {
                                    noteDao.insert(note);
                                }
                            }
                        }
                        executor.shutdown(); // Dọn dẹp
                    });
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Sync failed", e));
    }


    public void pushAllNotesToFirestore(List<Note> notes) {
        if (userId == null) {
            Log.w(TAG, "Bỏ qua pushAllNotes vì userId null.");
            return;
        }

        for (Note note : notes) {
            saveNoteToFirestore(note);
        }

        Log.d(TAG, "Đã đẩy tất cả note hiện tại lên Firestore.");
    }
}
