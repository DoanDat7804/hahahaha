package com.example.a1;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvFullName, tvEmail, tvPhone, tvDob;
    private Button btnEditProfile, btnChangePassword, btnLogout;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize UI components
        tvFullName = findViewById(R.id.tvFullName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvDob = findViewById(R.id.tvDob);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnLogout = findViewById(R.id.btnLogout);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Set button listeners
        btnEditProfile.setOnClickListener(v -> {
            // Go to EditProfileActivity
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            intent.putExtra("USER_ID", 1); // Replace 1 with the actual user ID
            startActivity(intent);
        });

        btnChangePassword.setOnClickListener(v -> {
            // Go to ChangePasswordActivity
            Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            // Logout and return to login screen
            Toast.makeText(this, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, LoginForm.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Load user data when activity is resumed
        loadUserData();
    }

    private void loadUserData() {
        // Replace "1" with the actual user ID from shared preferences or login
        int userId = 1;
        Cursor cursor = databaseHelper.getUserById(userId);

        if (cursor != null && cursor.moveToFirst()) {
            tvFullName.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_NAME)));
            tvEmail.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_EMAIL)));
            tvPhone.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_PHONE)));
            tvDob.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_DOB)));
            cursor.close();
        } else {
            Toast.makeText(this, "Không thể tải thông tin người dùng!", Toast.LENGTH_SHORT).show();
        }
    }
}
