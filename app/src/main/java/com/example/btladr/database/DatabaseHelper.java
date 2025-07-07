package com.example.btladr.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "food_ordering.db";
    private static final int DATABASE_VERSION = 1;

    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            // Đọc file SQL từ assets
            InputStream inputStream = context.getAssets().open("database.sql");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sql = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                // Bỏ qua các dòng comment hoặc dòng trống
                if (line.startsWith("--") || line.trim().isEmpty()) {
                    continue;
                }
                sql.append(line);
                sql.append(" ");

                // Khi gặp dấu chấm phẩy, thực thi câu lệnh SQL
                if (line.trim().endsWith(";")) {
                    try {
                        db.execSQL(sql.toString());
                        Log.d(TAG, "Executed SQL: " + sql.toString());
                    } catch (Exception e) {
                        Log.e(TAG, "Error executing SQL: " + sql.toString(), e);
                    }
                    sql = new StringBuilder();
                }
            }
            reader.close();
            inputStream.close();

            Log.d(TAG, "Database created successfully");
        } catch (IOException e) {
            Log.e(TAG, "Error reading database.sql file", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa bảng cũ và tạo lại khi cập nhật phiên bản
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);

        db.execSQL("DROP TABLE IF EXISTS feedback_replies");
        db.execSQL("DROP TABLE IF EXISTS notifications");
        db.execSQL("DROP TABLE IF EXISTS cart_items");
        db.execSQL("DROP TABLE IF EXISTS reviews");
        db.execSQL("DROP TABLE IF EXISTS system_logs");
        db.execSQL("DROP TABLE IF EXISTS order_status_history");
        db.execSQL("DROP TABLE IF EXISTS order_items");
        db.execSQL("DROP TABLE IF EXISTS orders");
        db.execSQL("DROP TABLE IF EXISTS feedback");
        db.execSQL("DROP TABLE IF EXISTS foods");
        db.execSQL("DROP TABLE IF EXISTS categories");
        db.execSQL("DROP TABLE IF EXISTS users");

        onCreate(db);
    }
}