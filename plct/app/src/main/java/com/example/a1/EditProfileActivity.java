package com.example.a1;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPhone, etDob;
    private Button btnSave, btnCancel;
    private DatabaseHelper databaseHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize UI components
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etDob = findViewById(R.id.etDob);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Get userId from Intent
        userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId == -1) {
            Toast.makeText(this, "Lỗi: ID người dùng không hợp lệ!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load current user data
        loadUserData();

        // Set button listeners
        btnSave.setOnClickListener(v -> saveChanges());
        btnCancel.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có chắc chắn muốn hủy các thay đổi?")
                    .setPositiveButton("Có", (dialog, which) -> finish())
                    .setNegativeButton("Không", null)
                    .show();
        });
    }

    private void loadUserData() {
        Cursor cursor = databaseHelper.getUserById(userId);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_NAME);
                int emailIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_EMAIL);
                int phoneIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_PHONE);
                int dobIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_DOB);

                if (nameIndex != -1) etFullName.setText(cursor.getString(nameIndex));
                if (emailIndex != -1) etEmail.setText(cursor.getString(emailIndex));
                if (phoneIndex != -1) etPhone.setText(cursor.getString(phoneIndex));
                if (dobIndex != -1) etDob.setText(cursor.getString(dobIndex));
            } else {
                Toast.makeText(this, "Không tìm thấy dữ liệu người dùng!", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        } else {
            Toast.makeText(this, "Lỗi khi truy vấn cơ sở dữ liệu!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveChanges() {
        String fullName = getTextFromEditText(etFullName);
        String email = getTextFromEditText(etEmail);
        String phone = getTextFromEditText(etPhone);
        String dob = getTextFromEditText(etDob);

        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || dob.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!phone.matches("\\d{10,11}")) {
            Toast.makeText(this, "Số điện thoại không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isUpdated = databaseHelper.updateUser(userId, fullName, email, phone, dob);

        if (isUpdated) {
            Toast.makeText(this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Lỗi khi cập nhật thông tin!", Toast.LENGTH_SHORT).show();
        }
    }

    private String getTextFromEditText(EditText editText) {
        return editText.getText().toString().trim();
    }


}
