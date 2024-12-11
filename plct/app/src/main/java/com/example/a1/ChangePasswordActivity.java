package com.example.a1;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etOldPassword, etNewPassword, etConfirmPassword;
    private Button btnChangePassword, btnCancel;
    private DatabaseHelper databaseHelper;
    private int userId; // ID của người dùng hiện tại (truyền từ Profile Activity)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Ánh xạ
        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnCancel = findViewById(R.id.btnCancel);

        // Khởi tạo DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Lấy userId từ Intent
        userId = getIntent().getIntExtra("USER_ID", -1);

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Quay lại trang trước
            }
        });
    }

    private void changePassword() {
        String oldPassword = etOldPassword.getText().toString();
        String newPassword = etNewPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra mật khẩu cũ
        String storedPassword = databaseHelper.getUserPasswordById(userId);
        if (!storedPassword.equals(oldPassword)) {
            Toast.makeText(this, "Mật khẩu cũ không chính xác", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật mật khẩu mới
        boolean isUpdated = databaseHelper.updateUserPassword(userId, newPassword);
        if (isUpdated) {
            Toast.makeText(this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
            finish(); // Quay lại trang trước
        } else {
            Toast.makeText(this, "Có lỗi xảy ra, vui lòng thử lại", Toast.LENGTH_SHORT).show();
        }
    }
}
