package com.example.noteapp.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.noteapp.model.Note;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseNoteHelper {

    private static final String TAG = "FirebaseNoteHelper";
    private final DatabaseReference notesRef;

    // Constructor - truyền vào userId (hoặc "default_user" nếu chưa dùng FirebaseAuth)
    public FirebaseNoteHelper(String userId) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        notesRef = db.getReference("users").child(userId).child("notes");
    }

    // Ghi/chèn ghi chú mới lên Firebase
    public void uploadNote(@NonNull Note note) {
        if (note.getId() == 0) {
            Log.w(TAG, "Note ID is 0. Cannot upload.");
            return;
        }

        notesRef.child(String.valueOf(note.getId())).setValue(note)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Note uploaded to Firebase: " + note.getId()))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to upload note", e));
    }

    // Xóa ghi chú khỏi Firebase
    public void deleteNote(@NonNull Note note) {
        if (note.getId() == 0) {
            Log.w(TAG, "Note ID is 0. Cannot delete.");
            return;
        }

        notesRef.child(String.valueOf(note.getId())).removeValue()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Note deleted from Firebase: " + note.getId()))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to delete note", e));
    }

    // Lấy toàn bộ ghi chú từ Firebase (dành cho đồng bộ khi khởi động app)
    public void syncAllFromFirebase(ValueEventListener listener) {
        notesRef.addListenerForSingleValueEvent(listener);
    }
}
