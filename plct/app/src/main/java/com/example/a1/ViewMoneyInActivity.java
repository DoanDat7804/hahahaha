package com.example.a1;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import java.util.Locale;
import java.util.Calendar;
import android.content.Intent;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DecimalFormat;

import androidx.appcompat.app.AppCompatActivity;

public class ViewMoneyInActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private int userId; // ID người dùng
    private ListView listViewIncome;
    private TextView tvTotalIncome;
    private ImageButton btnBack, btnAddIncome;
    private TextView tvStartDate, tvEndDate;
    private Button btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_in);

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

        listViewIncome = findViewById(R.id.listViewIncome);
        tvTotalIncome = findViewById(R.id.tvTotalIncome);
        btnAddIncome = findViewById(R.id.btnAddIncome);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        btnSearch = findViewById(R.id.btnSearch);
        btnBack = findViewById(R.id.btnBack);
        // Tải dữ liệu ban đầu
        loadIncomeData();

        btnAddIncome.setOnClickListener(v -> {
            Intent intent = new Intent(ViewMoneyInActivity.this, MoneyInActivity.class);
            startActivity(intent);
        });

        tvStartDate.setOnClickListener(v -> showDatePickerDialog(tvStartDate));
        tvEndDate.setOnClickListener(v -> showDatePickerDialog(tvEndDate));
        btnBack.setOnClickListener(v -> onBackPressed());
        btnSearch.setOnClickListener(v -> loadFilteredIncomeData());
    }

    private void loadIncomeData() {
        Cursor cursor = databaseHelper.getIncomeData(userId);
        if (cursor != null && cursor.getCount() > 0) {
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                    R.layout.item_income, cursor,
                    new String[]{DatabaseHelper.COLUMN_BALANCE_TITLE, DatabaseHelper.COLUMN_BALANCE_AMOUNT, DatabaseHelper.COLUMN_BALANCE_DATE},
                    new int[]{R.id.tvIncomeTitle, R.id.tvIncomeAmount, R.id.tvIncomeDate}, 0);

            adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                    if (view.getId() == R.id.tvIncomeDate) {
                        // Định dạng ngày tháng
                        long timestamp = cursor.getLong(columnIndex);
                        String formattedDate = formatTimestampToDate(timestamp);
                        ((TextView) view).setText(formattedDate);
                        return true;
                    } else if (view.getId() == R.id.tvIncomeAmount) {
                        // Định dạng số tiền
                        double amount = cursor.getDouble(columnIndex);
                        String formattedAmount = formatCurrency(amount);
                        ((TextView) view).setText(formattedAmount);
                        return true;
                    }
                    return false;
                }
            });

            listViewIncome.setAdapter(adapter);

            // Tính tổng thu nhập
            double totalIncome = calculateTotalIncome(cursor);
            tvTotalIncome.setText("Tổng thu nhập: " + formatCurrency(totalIncome));
        } else {
            tvTotalIncome.setText("Tổng thu nhập: 0 đ");
            listViewIncome.setAdapter(null);
            Toast.makeText(this, "Không có thu nhập nào!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadFilteredIncomeData() {
        String startDate = tvStartDate.getText().toString();
        String endDate = tvEndDate.getText().toString();

        if (startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn cả ngày bắt đầu và ngày kết thúc", Toast.LENGTH_SHORT).show();
            return;
        }

        long startTimestamp = convertDateToTimestamp(startDate);
        long endTimestamp = convertDateToTimestamp(endDate);

        Cursor cursor = databaseHelper.getIncomeDataByDate(userId, startTimestamp, endTimestamp);
        if (cursor != null && cursor.getCount() > 0) {
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                    R.layout.item_income, cursor,
                    new String[]{DatabaseHelper.COLUMN_BALANCE_TITLE, DatabaseHelper.COLUMN_BALANCE_AMOUNT, DatabaseHelper.COLUMN_BALANCE_DATE},
                    new int[]{R.id.tvIncomeTitle, R.id.tvIncomeAmount, R.id.tvIncomeDate}, 0);

            adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                    if (view.getId() == R.id.tvIncomeDate) {
                        long timestamp = cursor.getLong(columnIndex);
                        String formattedDate = formatTimestampToDate(timestamp);
                        ((TextView) view).setText(formattedDate);
                        return true;
                    } else if (view.getId() == R.id.tvIncomeAmount) {
                        double amount = cursor.getDouble(columnIndex);
                        String formattedAmount = formatCurrency(amount);
                        ((TextView) view).setText(formattedAmount);
                        return true;
                    }
                    return false;
                }
            });

            listViewIncome.setAdapter(adapter);
            double totalIncome = calculateTotalIncome(cursor);
            tvTotalIncome.setText("Tổng thu nhập: " + formatCurrency(totalIncome));
        } else {
            tvTotalIncome.setText("Tổng thu nhập: 0 đ");
            listViewIncome.setAdapter(null);
            Toast.makeText(this, "Không có thu nhập trong khoảng thời gian này!", Toast.LENGTH_SHORT).show();
        }
    }


    private long convertDateToTimestamp(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = dateFormat.parse(dateString);
            return date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void showDatePickerDialog(final TextView targetView) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String selectedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
            targetView.setText(selectedDate);
        }, year, month, dayOfMonth);

        datePickerDialog.show();
    }

    private double calculateTotalIncome(Cursor cursor) {
        double total = 0;
        if (cursor != null && cursor.moveToFirst()) {
            int amountIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_BALANCE_AMOUNT);
            do {
                total += cursor.getDouble(amountIndex);
            } while (cursor.moveToNext());
        }
        return total;
    }

    private String formatCurrency(double amount) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
        return decimalFormat.format(amount) + " đ";
    }


    // Phương thức chuyển đổi timestamp thành ngày theo định dạng dd/MM/yyyy
    private String formatTimestampToDate(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return dateFormat.format(date);
    }
}
