package com.example.a1;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.List;

public class AddExpenseActivity extends AppCompatActivity {

    private Spinner spinnerCategory;
    private TextView selectedDate;
    private EditText inputTitle, inputAmount, inputDescription;
    private Button btnSelectDate, btnSave ,btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // Khởi tạo view
        inputTitle = findViewById(R.id.input_expense_title);
        spinnerCategory = findViewById(R.id.spinner_category);
        inputAmount = findViewById(R.id.input_expense_amount);
        selectedDate = findViewById(R.id.selected_date);
        inputDescription = findViewById(R.id.input_expense_description);
        btnSelectDate = findViewById(R.id.btn_select_date);
        btnSave = findViewById(R.id.btn_save_expense);
        btnBack = findViewById(R.id.btn_back);
        // Load danh mục
        loadCategories();
        btnBack.setOnClickListener(v -> onBackPressed());        // Chọn ngày
        btnSelectDate.setOnClickListener(view -> showDatePicker());

        // Lưu chi phí
        btnSave.setOnClickListener(view -> new Thread(() -> saveExpenses()).start());
    }

    private void loadCategories() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<String> categories = dbHelper.getUserCategories(getCurrentUserId());

        if (categories == null || categories.isEmpty()) {
            categories.add("Không có danh mục");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                    selectedDate.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void saveExpenses() {
        String title = inputTitle.getText().toString();
        String category = spinnerCategory.getSelectedItem().toString();
        String amountStr = inputAmount.getText().toString();
        String date = selectedDate.getText().toString();
        String description = inputDescription.getText().toString();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(amountStr) || TextUtils.isEmpty(date)) {
            runOnUiThread(() -> Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show());
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            runOnUiThread(() -> Toast.makeText(this, "Số tiền không hợp lệ!", Toast.LENGTH_SHORT).show());
            return;
        }

        try (DatabaseHelper dbHelper = new DatabaseHelper(this)) {
            int userId = getCurrentUserId();
            int categoryId = dbHelper.getCategoryIdByName(category, userId);

            if (categoryId == -1) {
                runOnUiThread(() -> Toast.makeText(this, "Danh mục không hợp lệ!", Toast.LENGTH_SHORT).show());
                return;
            }

            boolean isSaved = dbHelper.addExpenses(userId, categoryId, title, amount, date, description);

            runOnUiThread(() -> {
                if (isSaved) {
                    Toast.makeText(this, "Chi phí đã được lưu!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Đã xảy ra lỗi, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private int getCurrentUserId() {
        return 1; // Dummy user ID
    }
}
