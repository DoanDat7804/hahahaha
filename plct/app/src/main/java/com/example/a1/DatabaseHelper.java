package com.example.a1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import models.Category;
import models.Expense;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "expense_manager.db";
    private static final int DATABASE_VERSION = 4;

    // Table names
    public static final String TABLE_USER = "users";
    public static final String TABLE_CATEGORY = "categories";
    public static final String TABLE_EXPENSE = "expenses";
    public static final String TABLE_BALANCE = "balance";

    // Common column names
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USER_ID = "user_id";

    // Columns for TABLE_USER
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PHONE = "phone";
    public static final String COLUMN_USER_DOB = "dob";
    public static final String COLUMN_USER_PASSWORD = "password";

    // Columns for TABLE_CATEGORY
    public static final String COLUMN_CATEGORY_NAME = "name";
    public static final String COLUMN_CATEGORY_DEFAULT = "is_default";

    // Columns for TABLE_EXPENSE
    public static final String COLUMN_EXPENSE_CATEGORY_ID = "category_id";
    public static final String COLUMN_EXPENSE_TITLE = "title";
    public static final String COLUMN_EXPENSE_AMOUNT = "amount";
    public static final String COLUMN_EXPENSE_DATE = "date";
    public static final String COLUMN_EXPENSE_DESCRIPTION = "description";

    // Columns for TABLE_BALANCE
//    public static final String COLUMN_BALANCE_ID = "_id";
//    public static final String COLUMN_BALANCE_USER_ID = "user_id";
    public static final String COLUMN_BALANCE_TYPE = "type";
    public static final String COLUMN_BALANCE_TITLE = "title";
    public static final String COLUMN_BALANCE_AMOUNT = "amount";
    public static final String COLUMN_BALANCE_DATE = "date";
    public static final String COLUMN_BALANCE_DESCRIPTION = "description";

    public static final String COLUMN_BALANCE_START_DATE = "start_date";
    public static final String COLUMN_BALANCE_END_DATE = "end_date";

    // SQL Create Statements
    private static final String CREATE_TABLE_USER =
            "CREATE TABLE " + TABLE_USER + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USER_NAME + " TEXT, " +
                    COLUMN_USER_EMAIL + " TEXT UNIQUE, " +
                    COLUMN_USER_PHONE + " TEXT, " +
                    COLUMN_USER_DOB + " TEXT, " +
                    COLUMN_USER_PASSWORD + " TEXT);";

    private static final String CREATE_TABLE_CATEGORY =
            "CREATE TABLE " + TABLE_CATEGORY + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USER_ID + " INTEGER, " +
                    COLUMN_CATEGORY_NAME + " TEXT, " +
                    COLUMN_CATEGORY_DEFAULT + " INTEGER, " +
                    "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_ID + "));";

    private static final String CREATE_TABLE_EXPENSE =
            "CREATE TABLE " + TABLE_EXPENSE + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USER_ID + " INTEGER, " +
                    COLUMN_EXPENSE_CATEGORY_ID + " INTEGER, " +
                    COLUMN_EXPENSE_TITLE + " TEXT, " +
                    COLUMN_EXPENSE_AMOUNT + " REAL, " +
                    COLUMN_EXPENSE_DATE + " TEXT, " +
                    COLUMN_EXPENSE_DESCRIPTION + " TEXT, " +
                    "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_ID + "), " +
                    "FOREIGN KEY(" + COLUMN_EXPENSE_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORY + "(" + COLUMN_ID + "));";

    private static final String CREATE_TABLE_BALANCE =
            "CREATE TABLE " + TABLE_BALANCE + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USER_ID + " INTEGER, " +
                    COLUMN_BALANCE_TYPE + " TEXT, " +
                    COLUMN_BALANCE_TITLE + " TEXT, " +
                    COLUMN_BALANCE_AMOUNT + " REAL, " +
                    COLUMN_BALANCE_DATE + " TEXT, " +
                    COLUMN_BALANCE_DESCRIPTION + " TEXT, " +
                    COLUMN_BALANCE_START_DATE + " TEXT, " + // Thêm cột start_date
                    COLUMN_BALANCE_END_DATE + " TEXT, " + // Thêm cột end_date
                    "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_ID + "));";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTable = "CREATE TABLE User (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL" +
                ")";
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_CATEGORY);
        db.execSQL(CREATE_TABLE_EXPENSE);
        db.execSQL(CREATE_TABLE_BALANCE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
            db.execSQL(CREATE_TABLE_USER);
        }
    }


    // Add Default Categories
    public void addDefaultCategories(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String[] defaultCategories = {"Food", "Transportation", "Medicine"};
            for (String category : defaultCategories) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_USER_ID, userId);
                values.put(COLUMN_CATEGORY_NAME, category);
                values.put(COLUMN_CATEGORY_DEFAULT, 1);
                db.insert(TABLE_CATEGORY, null, values);
            }
        } finally {
            db.close();
        }
    }

    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USER, null, COLUMN_USER_EMAIL + " = ?", new String[]{email}, null, null, null);

    }

    // Add a new user to the database
    public long addUser(String fullName, String email, String phone, String dob, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_USER_NAME, fullName);
        values.put(COLUMN_USER_EMAIL, email);
        values.put(COLUMN_USER_PHONE, phone);
        values.put(COLUMN_USER_DOB, dob);
        values.put(COLUMN_USER_PASSWORD, hashPassword(password));

        long result = db.insert(TABLE_USER, null, values);
        db.close();

        return result;
    }

    public Cursor getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USER, null, COLUMN_ID + " = ?", new String[]{String.valueOf(userId)}, null, null, null);
    }

    public boolean updateUser(int userId, String fullName, String email, String phone, String dob) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_USER_NAME, fullName);
        values.put(COLUMN_USER_EMAIL, email);
        values.put(COLUMN_USER_PHONE, phone);
        values.put(COLUMN_USER_DOB, dob);

        int rowsUpdated = db.update(TABLE_USER, values, COLUMN_ID + " = ?", new String[]{String.valueOf(userId)});
        db.close();
        return rowsUpdated > 0;
    }

    // Get user's password by ID
    public String getUserPasswordById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_USER, new String[]{COLUMN_USER_PASSWORD}, COLUMN_ID + " = ?", new String[]{String.valueOf(userId)}, null, null, null);
            if (cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    // Update user's password
    public boolean updateUserPassword(int userId, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_PASSWORD, hashPassword(newPassword));
        int rows = db.update(TABLE_USER, values, COLUMN_ID + " = ?", new String[]{String.valueOf(userId)});
        db.close();
        return rows > 0;
    }
    public Cursor getExpensesByCategory(int categoryId, String startDate, String endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_EXPENSE +
                " WHERE " + COLUMN_EXPENSE_CATEGORY_ID + " = ?" +
                (startDate.isEmpty() ? "" : " AND " + COLUMN_EXPENSE_DATE + " >= ?") +
                (endDate.isEmpty() ? "" : " AND " + COLUMN_EXPENSE_DATE + " <= ?");

        if (!startDate.isEmpty() && !endDate.isEmpty()) {
            return db.rawQuery(query, new String[]{String.valueOf(categoryId), startDate, endDate});
        } else if (!startDate.isEmpty()) {
            return db.rawQuery(query, new String[]{String.valueOf(categoryId), startDate});
        } else if (!endDate.isEmpty()) {
            return db.rawQuery(query, new String[]{String.valueOf(categoryId), endDate});
        } else {
            return db.rawQuery(query, new String[]{String.valueOf(categoryId)});
        }
    }


    // Hash password
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password; // Return plain password if hashing fails
        }
    }
//------------------------------
    public Cursor getExpenseSummaryByCategory(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_CATEGORY_NAME + ", SUM(" + COLUMN_EXPENSE_AMOUNT + ") AS total_amount " +
                "FROM " + TABLE_EXPENSE + " e INNER JOIN " + TABLE_CATEGORY + " c " +
                "ON e." + COLUMN_EXPENSE_CATEGORY_ID + " = c." + COLUMN_ID +
                " WHERE e." + COLUMN_USER_ID + " = ? " +
                "GROUP BY " + COLUMN_CATEGORY_NAME;
        return db.rawQuery(query, new String[]{String.valueOf(userId)});
    }
    private static final String ALTER_TABLE_CATEGORY_ADD_BUDGET =
            "ALTER TABLE " + TABLE_CATEGORY + " ADD COLUMN budget_limit REAL DEFAULT NULL;";
    public boolean checkBudgetExceed(int categoryId, double newExpenses) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT budget_limit, SUM(" + COLUMN_EXPENSE_AMOUNT + ") AS current_spent " +
                "FROM " + TABLE_CATEGORY + " c LEFT JOIN " + TABLE_EXPENSE + " e " +
                "ON c." + COLUMN_ID + " = e." + COLUMN_EXPENSE_CATEGORY_ID +
                " WHERE c." + COLUMN_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(categoryId)});
        if (cursor.moveToFirst()) {
            double budgetLimit = cursor.getDouble(0);
            double currentSpent = cursor.isNull(1) ? 0 : cursor.getDouble(1);
            return (currentSpent + newExpenses > budgetLimit);

        }
        return false;
    }
    public Cursor getFilteredExpenses(int userId, String startDate, String endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_EXPENSE + " WHERE " + COLUMN_USER_ID + " = ? " +
                (startDate.isEmpty() ? "" : " AND " + COLUMN_EXPENSE_DATE + " >= ?") +
                (endDate.isEmpty() ? "" : " AND " + COLUMN_EXPENSE_DATE + " <= ?");
        if (!startDate.isEmpty() && !endDate.isEmpty()) {
            return db.rawQuery(query, new String[]{String.valueOf(userId), startDate, endDate});
        } else if (!startDate.isEmpty()) {
            return db.rawQuery(query, new String[]{String.valueOf(userId), startDate});
        } else if (!endDate.isEmpty()) {
            return db.rawQuery(query, new String[]{String.valueOf(userId), endDate});
        } else {
            return db.rawQuery(query, new String[]{String.valueOf(userId)});
        }
    }
    public void addCategory(int userId, String categoryName, boolean isDefault, double budgetLimit) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_CATEGORY_NAME, categoryName);
        values.put(COLUMN_CATEGORY_DEFAULT, isDefault ? 1 : 0);
        values.put("budget_limit", budgetLimit);
        db.insert(TABLE_CATEGORY, null, values);
        db.close();
    }

    public Cursor getCategoryById(int categoryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                TABLE_CATEGORY, // Tên bảng
                null, // Trả về tất cả các cột
                COLUMN_ID + " = ?", // Điều kiện WHERE
                new String[]{String.valueOf(categoryId)}, // Giá trị tham số WHERE
                null, // Group By
                null, // Having
                null // Order By
        );
    }
    // Lấy tổng số dư (thu nhập)
    public double getTotalIncome(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COLUMN_BALANCE_AMOUNT + ") FROM " + TABLE_BALANCE + " WHERE " +
                        COLUMN_USER_ID + " = ? AND " + COLUMN_BALANCE_TYPE + " = 'income'",
                new String[]{String.valueOf(userId)}
        );
        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

     //Lấy tổng chi tiêu
     public double getTotalExpense(int userId) {
         SQLiteDatabase db = this.getReadableDatabase();
         double total = 0;
         String query = "SELECT SUM(amount) FROM expenses WHERE user_id = ?";
         Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

         if (cursor.moveToFirst()) {
             total = cursor.getDouble(0); // Lấy giá trị tổng từ cột đầu tiên
         }
         cursor.close();
         return total;
     }
//    public double getTotalExpense(int userId) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        // Truy vấn tính tổng chi tiêu từ bảng expenses
//        Cursor cursor = db.rawQuery(
//                "SELECT SUM(" + COLUMN_EXPENSE_AMOUNT + ") FROM " + TABLE_EXPENSE +
//                        " WHERE " + COLUMN_USER_ID + " = ?",
//                new String[]{String.valueOf(userId)}
//        );
//
//        double total = 0.0;
//        if (cursor.moveToFirst()) {
//            total = cursor.getDouble(0); // Lấy tổng chi tiêu
//        }
//        cursor.close();
//        db.close();
//        return total;
//    }

    public boolean insertIncome(int userId, String title, double amount, String description, String startDate, String endDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_BALANCE_TYPE, "income"); // Hoặc loại thu nhập bạn đang sử dụng
        values.put(COLUMN_BALANCE_TITLE, title);
        values.put(COLUMN_BALANCE_AMOUNT, amount);
        values.put(COLUMN_BALANCE_DATE, String.valueOf(System.currentTimeMillis())); // Lưu thời gian hiện tại
        values.put(COLUMN_BALANCE_DESCRIPTION, description);
        values.put(COLUMN_BALANCE_START_DATE, startDate);  // Lưu start_date
        values.put(COLUMN_BALANCE_END_DATE, endDate);      // Lưu end_date


        long result = db.insert(TABLE_BALANCE, null, values);
        db.close();

        return result != -1; // Nếu `result == -1`, có lỗi xảy ra
    }

    public Cursor getIncomeData(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_BALANCE +
                " WHERE " + COLUMN_USER_ID + " = ? AND " + COLUMN_BALANCE_TYPE + " = 'income'" +
                " ORDER BY " + COLUMN_BALANCE_DATE + " DESC";
        return db.rawQuery(query, new String[]{String.valueOf(userId)});
    }
    public Cursor getIncomeDataByDate(int userId, long startDate, long endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_BALANCE +
                " WHERE " + COLUMN_USER_ID + " = ?" +
                " AND " + COLUMN_BALANCE_TYPE + " = 'income'" +
                " AND " + COLUMN_BALANCE_DATE + " BETWEEN ? AND ?" +
                " ORDER BY " + COLUMN_BALANCE_DATE + " DESC";

        return db.rawQuery(query, new String[]{
                String.valueOf(userId),
                String.valueOf(startDate),
                String.valueOf(endDate)
        });
    }
    public List<Category> getCategoriesByUserId(int userId) {
        List<Category> categoryList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Truy vấn danh sách danh mục theo userId
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CATEGORY + " WHERE " + COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                // Lấy dữ liệu từ cursor và thêm vào danh sách
                Category category = new Category();
                category.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                category.setName(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME)));
                categoryList.add(category);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return categoryList;
    }
    public long addCategory(String categoryName, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_NAME, categoryName);
        values.put(COLUMN_USER_ID, userId);

        long result = db.insert(TABLE_CATEGORY, null, values);
        db.close();
        return result;
    }
    // Hàm lấy userId từ email
    public int getUserIdByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_ID + " FROM " + TABLE_USER + " WHERE " + COLUMN_USER_EMAIL + " = ?", new String[]{email}); // Đúng tên bảng và cột

        if (cursor != null && cursor.moveToFirst()) {
            int userId = cursor.getInt(0);
            cursor.close();
            return userId;
        }

        if (cursor != null) {
            cursor.close();
        }
        return -1; // Trả về -1 nếu không tìm thấy
    }

    public List<String> getUserCategories(int userId) {
        List<String> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_CATEGORY_NAME +
                " FROM " + TABLE_CATEGORY +
                        " WHERE " + COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                categories.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return categories;
    }

    public int getCategoryIdByName(String categoryName, int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_ID + " FROM " + TABLE_CATEGORY +
                        " WHERE " + COLUMN_CATEGORY_NAME + " = ? AND " + COLUMN_USER_ID + " = ?",
                new String[]{categoryName, String.valueOf(userId)}
        );

        int categoryId = -1;
        if (cursor.moveToFirst()) {
            categoryId = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return categoryId;
    }

    public boolean addExpenses(int userId, int categoryId, String title, double amount, String date, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_EXPENSE_CATEGORY_ID, categoryId);
        values.put(COLUMN_EXPENSE_TITLE, title);
        values.put(COLUMN_EXPENSE_AMOUNT, amount);
        values.put(COLUMN_EXPENSE_DATE, date);
        values.put(COLUMN_EXPENSE_DESCRIPTION, description);


        long result = db.insert(TABLE_EXPENSE, null, values);
        db.close();
        return result != -1; // Trả về true nếu chèn thành công
    }
    public List<Expense> getExpenses(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Expense> expenses = new ArrayList<>();
        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_EXPENSE_TITLE + ", " + COLUMN_EXPENSE_AMOUNT + ", " + COLUMN_EXPENSE_DATE +
                        " FROM " + TABLE_EXPENSE +
                        " WHERE " + COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}
        );

        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex("title"));
            double amount = cursor.getDouble(cursor.getColumnIndex("amount"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            expenses.add(new Expense(title, amount, date));
        }
        cursor.close();
        db.close();
        return expenses;
    }

    public List<Expense> getExpensesByCategory1(int userId, int categoryId, String startDate, String endDate) {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " +
                DatabaseHelper.COLUMN_EXPENSE_TITLE + ", " +
                DatabaseHelper.COLUMN_EXPENSE_AMOUNT + ", " +
                DatabaseHelper.COLUMN_EXPENSE_DATE + " " +
                "FROM " + DatabaseHelper.TABLE_EXPENSE + " " +
                "WHERE " + DatabaseHelper.COLUMN_USER_ID + " = ? " +
                "AND " + DatabaseHelper.COLUMN_EXPENSE_CATEGORY_ID + " = ? " +
                "AND " + DatabaseHelper.COLUMN_EXPENSE_DATE + " BETWEEN ? AND ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(categoryId), startDate, endDate});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_TITLE));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_AMOUNT));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_DATE));
                expenses.add(new Expense(title, amount, date));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return expenses;
    }




}