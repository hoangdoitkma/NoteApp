package com.example.noteapp.model;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.Executors;

public class QrViewModel extends AndroidViewModel {
    private QRCodeDao qrCodeDao;
    private LiveData<List<QRCode>> qrCodeList;

    public QrViewModel(@NonNull Application application) {
        super(application);
        QRCodeDatabase db = QRCodeDatabase.getInstance(application);
        qrCodeDao = db.qrCodeDao();
        qrCodeList = qrCodeDao.getAll();
    }

    public LiveData<List<QRCode>> getQrCodeList() {
        return qrCodeList;
    }

    public void addQrCode(QRCode qrCode) {
        Executors.newSingleThreadExecutor().execute(() -> qrCodeDao.insert(qrCode));
    }

    public void deleteQrCode(QRCode qrCode) {
        Executors.newSingleThreadExecutor().execute(() -> qrCodeDao.delete(qrCode));
    }

    public void updateQrCode(QRCode qrCode) {
        Executors.newSingleThreadExecutor().execute(() -> qrCodeDao.update(qrCode));
    }
}
