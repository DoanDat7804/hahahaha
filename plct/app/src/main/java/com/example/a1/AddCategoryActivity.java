package com.example.a1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddCategoryActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private EditText edtCategoryName;
    private int userId; // Lưu ID của người dùng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_add);

        // Lấy userId từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1); // -1 là giá trị mặc định nếu không tìm thấy

        // Kiểm tra nếu không nhận được userId
        if (userId == -1) {
            Toast.makeText(this, "Lỗi: không tìm thấy userId", Toast.LENGTH_SHORT).show();
            finish(); // Kết thúc Activity nếu không có userId
            return;
        }

        // Khởi tạo DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Ánh xạ các view
        edtCategoryName = findViewById(R.id.edtCategoryName);
        Button btnSaveCategory = findViewById(R.id.btnSaveCategory);

        // Xử lý sự kiện khi nhấn nút "Lưu"
        btnSaveCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categoryName = edtCategoryName.getText().toString().trim();

                if (categoryName.isEmpty()) {
                    Toast.makeText(AddCategoryActivity.this, "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
                } else {
                    // Lưu danh mục vào cơ sở dữ liệu
                    long result = databaseHelper.addCategory(categoryName, userId);

                    if (result != -1) {
                        Toast.makeText(AddCategoryActivity.this, "Danh mục đã được thêm", Toast.LENGTH_SHORT).show();
                        finish(); // Quay lại màn hình trước
                    } else {
                        Toast.makeText(AddCategoryActivity.this, "Lỗi khi thêm danh mục", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Đóng DatabaseHelper để tránh rò rỉ bộ nhớ
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
