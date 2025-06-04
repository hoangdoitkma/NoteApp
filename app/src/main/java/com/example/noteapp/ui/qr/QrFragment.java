package com.example.noteapp.ui.qr;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.R;
import com.example.noteapp.model.QRCode;
import com.example.noteapp.model.QrViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

public class QrFragment extends Fragment {
    private static final int REQUEST_CODE_PICK_IMAGE = 101;
    private RecyclerView recyclerView;
    private QRCodeAdapter adapter;
    private QrViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewQr);
// Hiển thị 2 cột
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        adapter = new QRCodeAdapter(requireContext(), new ArrayList<>());
        adapter.setOnItemClickListener(this::showQRDialog);
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(QrViewModel.class);
        viewModel.getQrCodeList().observe(getViewLifecycleOwner(), list -> adapter.setQrCodeList(list));

        view.findViewById(R.id.fabAddQr).setOnClickListener(v -> openImagePicker());

        return view;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            String imagePath = saveImageToInternalStorage(selectedImageUri);

            if (imagePath != null) {
                QRCode qrCode = new QRCode("QR từ ảnh", imagePath);
                viewModel.addQrCode(qrCode);
            } else {
                Toast.makeText(requireContext(), "Không thể lưu ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String saveImageToInternalStorage(Uri uri) {
        try {
            ContentResolver resolver = requireContext().getContentResolver();
            InputStream inputStream = resolver.openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap == null) return null;

            File file = new File(requireContext().getFilesDir(), "qr_" + UUID.randomUUID() + ".png");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            inputStream.close();

            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showQRDialog(QRCode qrCode) {
        String imagePath = qrCode.getImagePath();
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        if (bitmap == null) {
            Toast.makeText(requireContext(), "Không load được ảnh từ đường dẫn", Toast.LENGTH_SHORT).show();
            return;
        }

        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_qr_detail);

        ImageView imageView = dialog.findViewById(R.id.imageQrDetail);
        TextView titleView = dialog.findViewById(R.id.textQrTitle);
        Button btnShare = dialog.findViewById(R.id.btnShare);
        Button btnDelete = dialog.findViewById(R.id.btnDelete);
        Button btnEdit = dialog.findViewById(R.id.btnEdit);

        imageView.setImageBitmap(bitmap);
        titleView.setText(qrCode.getTitle());

        btnShare.setOnClickListener(v -> shareImage(qrCode.getImagePath()));

        btnDelete.setOnClickListener(v -> {
            viewModel.deleteQrCode(qrCode);
            dialog.dismiss();
        });

        btnEdit.setOnClickListener(v -> showEditDialog(qrCode, dialog));

        dialog.show();
    }

    private void showEditDialog(QRCode qrCode, Dialog parentDialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Chỉnh sửa tiêu đề QR");

        View viewInflated = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_qr, null);
        final EditText input = viewInflated.findViewById(R.id.editTextTitle);
        input.setText(qrCode.getTitle());

        builder.setView(viewInflated);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newTitle = input.getText().toString().trim();
            if (!newTitle.isEmpty()) {
                qrCode.setTitle(newTitle);
                viewModel.updateQrCode(qrCode);
                Toast.makeText(requireContext(), "Đã cập nhật tiêu đề", Toast.LENGTH_SHORT).show();
                parentDialog.dismiss();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void shareImage(String imagePath) {
        File imageFile = new File(imagePath);
        Uri uri = FileProvider.getUriForFile(requireContext(),
                requireContext().getPackageName() + ".fileprovider", imageFile);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(intent, "Chia sẻ ảnh QR"));
    }
}
