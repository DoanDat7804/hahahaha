package com.example.a1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a1.adapters.ExpenseAdapter;

import java.util.List;

import models.Expense;

public class HistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ExpenseAdapter adapter;
    private DatabaseHelper databaseHelper;
    private int userId;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Khởi tạo đối tượng DatabaseHelper và lấy userId từ SharedPreferences
        databaseHelper = new DatabaseHelper(this);
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);
        btnBack = findViewById(R.id.btnBack);
        if (userId == -1) {
            startActivity(new Intent(HistoryActivity.this, LoginForm.class));
            finish();
            return;
        }

        recyclerView = findViewById(R.id.recyclerViewExpenseHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnBack.setOnClickListener(v -> onBackPressed());
        // Lấy danh sách chi tiêu từ cơ sở dữ liệu
        List<Expense> expenses = databaseHelper.getExpenses(userId);
        TextView tvEmptyHistory = findViewById(R.id.tvEmptyHistory);
        TextView tvTotalAmount = findViewById(R.id.tvTotalAmount);

        // Nếu không có chi tiêu nào, hiển thị thông báo rỗng
        if (expenses.isEmpty()) {
            tvEmptyHistory.setVisibility(TextView.VISIBLE);
            recyclerView.setVisibility(RecyclerView.GONE);
        } else {
            tvEmptyHistory.setVisibility(TextView.GONE);
            recyclerView.setVisibility(RecyclerView.VISIBLE);

            // Tạo adapter và set vào RecyclerView
            adapter = new ExpenseAdapter(expenses);
            recyclerView.setAdapter(adapter);

            // Cập nhật tổng chi tiêu
            double totalExpense = databaseHelper.getTotalExpense(userId);
            tvTotalAmount.setText(String.format("%.0f đ", totalExpense));  // Hiển thị tổng chi tiêu
        }
    }
}
