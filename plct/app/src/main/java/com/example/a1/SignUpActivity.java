package com.example.a1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPhone, etDob, etPassword, etConfirmPassword;
    private Button btnSignUp, btnBackToLogin;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Khởi tạo
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etDob = findViewById(R.id.etDob);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);
        databaseHelper = new DatabaseHelper(this);

        // Xử lý nút đăng ký
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = etFullName.getText().toString();
                String email = etEmail.getText().toString();
                String phone = etPhone.getText().toString();
                String dob = etDob.getText().toString();
                String password = etPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();

                // Kiểm tra dữ liệu nhập
                if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || dob.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(SignUpActivity.this, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Thêm người dùng vào cơ sở dữ liệu
                long userId = databaseHelper.addUser(fullName, email, phone, dob, password); // Giả sử addUser trả về userId

                if (userId != -1) {
                    // Sau khi người dùng được đăng ký, thêm các danh mục mặc định
                    databaseHelper.addDefaultCategories((int) userId);

                    // Lưu userId vào SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("userId", (int) userId);  // Lưu userId
                    editor.apply();  // Áp dụng thay đổi

                    Toast.makeText(SignUpActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                    // Chuyển về trang đăng nhập
                    startActivity(new Intent(SignUpActivity.this, LoginForm.class));
                    finish();
                } else {
                    Toast.makeText(SignUpActivity.this, "Đăng ký thất bại, thử lại!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Xử lý nút quay lại
        btnBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginForm.class));
                finish();
            }
        });
    }
}
