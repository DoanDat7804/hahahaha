package com.example.a1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginForm extends AppCompatActivity {

    private Button btnLogin, btnSignUp;
    private EditText edtEmail, edtPassword;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_form);

        // Kết nối với các thành phần giao diện
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);

        // Khởi tạo database helper
        databaseHelper = new DatabaseHelper(this);

        // Xử lý nút Đăng nhập
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                if (validateLogin(email, password)) {
                    int userId = databaseHelper.getUserIdByEmail(email);

                    if (userId != -1) {
                        // Lưu userId vào SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("userId", userId);
                        editor.apply();

                        // Chuyển đến MainActivity
                        Toast.makeText(LoginForm.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginForm.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginForm.this, "Không thể lấy thông tin tài khoản!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginForm.this, "Email hoặc mật khẩu không đúng!", Toast.LENGTH_SHORT).show();
                }
            }


        });

        // Xử lý nút Đăng ký
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến màn hình đăng ký
                startActivity(new Intent(LoginForm.this, SignUpActivity.class));
            }
        });
    }

    // Hàm kiểm tra thông tin đăng nhập
    private boolean validateLogin(String email, String password) {
        Cursor cursor = databaseHelper.getUserByEmail(email);

        if (cursor != null && cursor.moveToFirst()) {
            int passwordIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_PASSWORD);
            if (passwordIndex >= 0) {
                String storedPassword = cursor.getString(passwordIndex);
                cursor.close();
                // So sánh mật khẩu đã mã hóa
                return storedPassword.equals(hashPassword(password));
            } else {
                cursor.close();
                throw new IllegalStateException("Cột password không tồn tại trong kết quả truy vấn.");
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        return false;
    }

    // Hàm mã hóa mật khẩu
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (int i = 0; i < encodedhash.length; i++) {
                String hex = Integer.toHexString(0xff & encodedhash[i]);
                if(hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}