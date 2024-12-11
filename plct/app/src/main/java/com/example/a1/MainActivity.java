package com.example.a1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a1.adapters.CategoryAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import models.Category;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        // Khởi tạo database
        databaseHelper = new DatabaseHelper(this);

        // Lấy userId từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);  // Lấy userId, nếu không có thì trả về -1



        // Nếu không có userId, chuyển về trang đăng nhập
        if (userId == -1) {
            startActivity(new Intent(MainActivity.this, LoginForm.class));
            finish();
            return;
        }

        // Ánh xạ các thành phần giao diện
        RecyclerView recyclerView = findViewById(R.id.recyclerViewDanhMuc);
        TextView tvSoDuAmount = findViewById(R.id.tvSoDuAmount);
        TextView tvChiTieuAmount = findViewById(R.id.tvChiTieuAmount);
        Button btnHistory = findViewById(R.id.btnHistory);
        ImageButton btnProfile = findViewById(R.id.btnProfile);
        LinearLayout llSoDu = findViewById(R.id.llSoDu); // Ô "Số dư"
        LinearLayout llChiTieu = findViewById(R.id.llChiTieu);

        // Lấy dữ liệu từ database và hiển thị
        double totalIncome = databaseHelper.getTotalIncome(userId); // Tổng thu nhập
        double totalExpense = databaseHelper.getTotalExpense(userId); // Tổng chi tiêu

        // Hiển thị số dư và chi tiêu
        tvSoDuAmount.setText(String.format("%.0f đ", totalIncome)); // Hiển thị số dư
        tvChiTieuAmount.setText(String.format("%.0f đ", totalExpense)); // Hiển thị chi tiêu

        // Sự kiện nút lịch sử chi tiêu
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        // Xử lý khi nhấn vào ô "Chi tiêu"
        llChiTieu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển hướng sang trang AddExpenseActivity
                Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
                intent.putExtra("userId", userId); // Gửi userId nếu cần sử dụng ở AddExpenseActivity
                startActivity(intent);
                // Hiệu ứng chuyển trang
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        // Sự kiện nút profile
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        // Sự kiện khi nhấn vào ô "Số dư"
        llSoDu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến trang ViewMoneyInActivity
                Intent intent = new Intent(MainActivity.this, ViewMoneyInActivity.class);
                startActivity(intent);
                // Áp dụng hiệu ứng chuyển trang
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        // Hiển thị danh sách các danh mục
        List<Category> categoryList = databaseHelper.getCategoriesByUserId(userId);
        CategoryAdapter adapter = new CategoryAdapter(categoryList, new CategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onCategoryClick(Category category) {
                // Truyền dữ liệu trong MainActivity
                Intent intent = new Intent(MainActivity.this, ExpenseCategoryActivity.class);
                intent.putExtra("category_id", category.getId());
                intent.putExtra("user_id", userId);
                startActivity(intent);
            }

        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // FloatingActionButton thêm danh mục mới
        FloatingActionButton fabAddCategory = findViewById(R.id.fabAddCategory);
        fabAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý sự kiện khi nhấn vào FloatingActionButton
                Intent intent = new Intent(MainActivity.this, AddCategoryActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Tải lại dữ liệu khi quay về MainActivity

        // Hiển thị lại số dư và chi tiêu
        double totalIncome = databaseHelper.getTotalIncome(userId);
        double totalExpense = databaseHelper.getTotalExpense(userId);

        TextView tvSoDuAmount = findViewById(R.id.tvSoDuAmount);
        TextView tvChiTieuAmount = findViewById(R.id.tvChiTieuAmount);

        tvSoDuAmount.setText(String.format("%.0f đ", totalIncome)); // Hiển thị số dư
        tvChiTieuAmount.setText(String.format("%.0f đ", totalExpense)); // Hiển thị chi tiêu

        // Hiển thị lại danh sách các danh mục
        List<Category> categoryList = databaseHelper.getCategoriesByUserId(userId);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewDanhMuc);
        CategoryAdapter adapter = new CategoryAdapter(categoryList, new CategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onCategoryClick(Category category) {
                // Xử lý khi người dùng nhấn vào danh mục
                Intent intent = new Intent(MainActivity.this, ExpenseCategoryActivity.class);
                intent.putExtra("category_name", category.getName()); // Gửi tên danh mục sang trang mới
                startActivity(intent);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}

