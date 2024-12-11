package com.example.a1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.DatePickerDialog;
import android.widget.DatePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class MoneyInActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private int userId; // ID người dùng

    private TextView tvStartDate, tvEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_in_add);

        // Khởi tạo DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Lấy userId từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);  // Lấy userId, nếu không có thì trả về -1

        // Kiểm tra nếu userId không hợp lệ, chuyển hướng về trang đăng nhập
        if (userId == -1) {
            Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            finish(); // Đóng activity nếu người dùng chưa đăng nhập
            return;
        }

        // Ánh xạ các thành phần trong layout
        EditText edtName = findViewById(R.id.edtName);
        EditText edtAmount = findViewById(R.id.edtAmount);
        EditText edtNote = findViewById(R.id.edtNote);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnCancel = findViewById(R.id.btnCancel);

        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);

        // Sự kiện chọn ngày "Từ ngày"
        tvStartDate.setOnClickListener(v -> showDatePickerDialog(tvStartDate));

        // Sự kiện chọn ngày "Đến ngày"
        tvEndDate.setOnClickListener(v -> showDatePickerDialog(tvEndDate));

        // Sự kiện nút Lưu
        btnSave.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String amountStr = edtAmount.getText().toString().trim();
            String note = edtNote.getText().toString().trim();
            String startDate = tvStartDate.getText().toString();
            String endDate = tvEndDate.getText().toString();

            if (name.isEmpty() || amountStr.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
                Toast.makeText(MoneyInActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(MoneyInActivity.this, "Số tiền không hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (amount <= 0) {
                Toast.makeText(MoneyInActivity.this, "Số tiền phải lớn hơn 0!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lưu thông tin vào database
            boolean isInserted = databaseHelper.insertIncome(userId, name, amount, note, startDate, endDate);
            if (isInserted) {
                Toast.makeText(MoneyInActivity.this, "Đã lưu thu nhập!", Toast.LENGTH_SHORT).show();
                finish(); // Đóng activity và trở về trang trước
            } else {
                Toast.makeText(MoneyInActivity.this, "Không thể lưu thu nhập. Vui lòng thử lại!", Toast.LENGTH_LONG).show();
            }
        });

        // Sự kiện nút Hủy Bỏ
        btnCancel.setOnClickListener(v -> finish());
    }

    // Hiển thị DatePickerDialog
    private void showDatePickerDialog(final TextView targetView) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String selectedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
            targetView.setText(selectedDate); // Cập nhật TextView với ngày đã chọn
        }, year, month, dayOfMonth);

        datePickerDialog.show();
    }
}
