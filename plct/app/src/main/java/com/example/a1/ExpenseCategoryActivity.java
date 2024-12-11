package com.example.a1;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import models.Expense;

public class ExpenseCategoryActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private int userId;
    private int categoryId; // ID của danh mục chi tiêu
    private TextView tvCategoryName, tvStartDate, tvEndDate, tvTotalAmount;
    private Button btnSearch;
    private LinearLayout expenseListContainer;
    private ImageButton btnBack, btnAddExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_details);

        dbHelper = new DatabaseHelper(this);

        // Lấy ID danh mục từ Intent
        categoryId = getIntent().getIntExtra("category_id", -1);

        // Kết nối với các thành phần trong giao diện
        tvCategoryName = findViewById(R.id.tvCategoryTitle);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnSearch = findViewById(R.id.btnSearch);
        btnBack = findViewById(R.id.btnBack);
        btnAddExpense = findViewById(R.id.btnAddExpense);
        expenseListContainer = findViewById(R.id.expenseListContainer);

        // Tải tên danh mục
        loadCategoryName();

        // Tải danh sách chi tiêu
        loadExpenses("", "");

        // Sự kiện cho các nút bấm
        btnBack.setOnClickListener(v -> onBackPressed());
        btnAddExpense.setOnClickListener(v -> {
            // Chuyển đến màn hình thêm chi tiêu (tùy chỉnh thêm)
        });
        btnSearch.setOnClickListener(v -> {
            String startDate = convertDateFormat(tvStartDate.getText().toString());
            String endDate = convertDateFormat(tvEndDate.getText().toString());
            loadExpenses(startDate, endDate);
        });

        // Đặt DatePicker cho chọn ngày
        setDatePicker(tvStartDate);
        setDatePicker(tvEndDate);
    }

    private void loadCategoryName() {
        Cursor cursor = dbHelper.getCategoryById(categoryId);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY_NAME);
            if (columnIndex != -1) {
                String categoryName = cursor.getString(columnIndex);
                tvCategoryName.setText(categoryName);
            } else {
                Log.e("Database", "COLUMN_CATEGORY_NAME không tồn tại");
                tvCategoryName.setText("Không tìm thấy tên danh mục");
            }
        } else {
            tvCategoryName.setText("Danh mục không tồn tại");
        }
    }

    private void loadExpenses(String startDate, String endDate) {
        double totalAmount = 0; // Biến lưu tổng chi tiêu
        expenseListContainer.removeAllViews();

        // Giả sử userId đã được xác định từ đâu đó, ví dụ là một biến toàn cục hoặc có thể lấy từ SharedPreferences
        userId = getIntent().getIntExtra("user_id", -1); // Lấy userId từ Intent

        List<Expense> expenses = dbHelper.getExpensesByCategory1(userId, categoryId, startDate, endDate);
        if (expenses != null && !expenses.isEmpty()) {
            for (Expense expense : expenses) {
                String title = expense.getTitle();
                double amount = expense.getAmount();
                String date = expense.getDate();

                totalAmount += amount; // Cộng số tiền vào tổng chi tiêu

                // Tạo view cho từng chi tiêu
                LinearLayout expenseItem = new LinearLayout(this);
                expenseItem.setOrientation(LinearLayout.VERTICAL);
                expenseItem.setBackgroundResource(R.drawable.rounded_corner);
                expenseItem.setPadding(16, 16, 16, 16);

                TextView tvTitle = new TextView(this);
                tvTitle.setText("Tên chi tiêu: " + title);
                tvTitle.setTextSize(16);
                tvTitle.setTextColor(getResources().getColor(R.color.black));

                TextView tvAmount = new TextView(this);
                NumberFormat currencyFormat = NumberFormat.getInstance();
                tvAmount.setText("Số tiền: " + currencyFormat.format(amount) + " đ");
                tvAmount.setTextSize(16);
                tvAmount.setTextColor(getResources().getColor(R.color.red));

                TextView tvDate = new TextView(this);
                tvDate.setText("Thời gian: " + date);
                tvDate.setTextSize(14);
                tvDate.setTextColor(getResources().getColor(R.color.gray));

                expenseItem.addView(tvTitle);
                expenseItem.addView(tvAmount);
                expenseItem.addView(tvDate);

                expenseListContainer.addView(expenseItem);
            }
        } else {
            displayEmptyMessage();
        }

        tvTotalAmount.setText(String.format("%.0f VNĐ", totalAmount));
    }



    private void displayEmptyMessage() {
        TextView emptyMessage = new TextView(this);
        emptyMessage.setText("Không có chi tiêu nào!");
        emptyMessage.setTextSize(16);
        emptyMessage.setTextColor(getResources().getColor(R.color.gray));
        expenseListContainer.addView(emptyMessage);
        tvTotalAmount.setText("0 VNĐ");
    }

    private void setDatePicker(TextView dateField) {
        dateField.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(ExpenseCategoryActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        dateField.setText(selectedDate);
                    }, year, month, day);

            datePickerDialog.show();
        });
    }

    private String convertDateFormat(String inputDate) {
        if (inputDate.isEmpty()) return "";
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            return outputFormat.format(inputFormat.parse(inputDate));
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
}