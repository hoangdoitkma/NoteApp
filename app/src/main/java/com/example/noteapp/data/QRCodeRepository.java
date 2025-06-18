package com.example.noteapp.data;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.noteapp.model.QRCode;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QRCodeRepository {
    private final QRDatabaseHelper dbHelper;
    private final MutableLiveData<List<QRCode>> qrCodeLiveData = new MutableLiveData<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public QRCodeRepository(Context context) {
        dbHelper = new QRDatabaseHelper(context.getApplicationContext());
        loadQRCodes();
    }

    public LiveData<List<QRCode>> getQrCodeList() {
        return qrCodeLiveData;
    }

    private void loadQRCodes() {
        executor.execute(() -> {
            List<QRCode> data = dbHelper.getAllQRCodes();
            qrCodeLiveData.postValue(data);
        });
    }

    public void addQrCode(QRCode qrCode) {
        executor.execute(() -> {
            dbHelper.insertQRCode(qrCode.getTitle(), qrCode.getImagePath(), System.currentTimeMillis());
            loadQRCodes(); // Reload data after insert
        });
    }

    public void deleteQrCode(QRCode qrCode) {
        executor.execute(() -> {
            dbHelper.deleteQRCode(qrCode.getId());
            loadQRCodes(); // Reload data after delete
        });
    }

    public void updateQrCode(QRCode qrCode) {
        executor.execute(() -> {
            dbHelper.updateQRCode(qrCode);
            loadQRCodes(); // Reload data after update
        });
    }
}
