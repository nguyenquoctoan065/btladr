package com.example.btladr.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.btladr.database.DatabaseHelper;
import com.example.btladr.model.User;
import com.example.btladr.utils.SecurityUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserDAO {
    private static final String TAG = "UserDAO";

    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;

    // Các tên cột trong bảng
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_FULL_NAME = "full_name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_ROLE = "role";
    public static final String COLUMN_PROFILE_IMAGE = "profile_image";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_LAST_LOGIN = "last_login";
    public static final String COLUMN_STATUS = "status";

    public UserDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Mở kết nối đến database
    public void open() {
        db = dbHelper.getWritableDatabase();
        Log.d(TAG, "Database opened");
    }

    // Đóng kết nối
    public void close() {
        if (dbHelper != null) {
            dbHelper.close();
            Log.d(TAG, "Database closed");
        }
    }

    // Đăng ký người dùng mới
    public long registerUser(User user) {
        try {
            open();

            // Kiểm tra xem username đã tồn tại chưa
            if (isUsernameExists(user.getUsername())) {
                Log.d(TAG, "Username already exists: " + user.getUsername());
                return -1; // Trả về -1 nếu username đã tồn tại
            }

            ContentValues values = new ContentValues();
            values.put(COLUMN_USERNAME, user.getUsername());
            // Mã hóa mật khẩu trước khi lưu vào database
            values.put(COLUMN_PASSWORD, SecurityUtils.hashPassword(user.getPassword()));
            values.put(COLUMN_FULL_NAME, user.getFullName());
            values.put(COLUMN_EMAIL, user.getEmail());
            values.put(COLUMN_PHONE, user.getPhone());
            values.put(COLUMN_ADDRESS, user.getAddress());
            values.put(COLUMN_ROLE, "user"); // Mặc định là user thường
            values.put(COLUMN_STATUS, 1); // Mặc định là active

            // Format thời gian hiện tại
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String currentDateTime = dateFormat.format(new Date());
            values.put(COLUMN_CREATED_AT, currentDateTime);

            // Thêm user vào database
            long id = db.insert(TABLE_USERS, null, values);
            Log.d(TAG, "User registered with id: " + id);
            return id;
        } catch (Exception e) {
            Log.e(TAG, "Error registering user", e);
            return -1;
        } finally {
            close();
        }
    }

    // Kiểm tra đăng nhập
    public User login(String username, String password) {
        User user = null;

        try {
            open();

            // Query database để kiểm tra thông tin đăng nhập
            String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{username});

            if (cursor != null && cursor.moveToFirst()) {
                // Lấy mật khẩu đã mã hóa từ database
                String hashedPassword = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));

                // Xác thực mật khẩu
                if (SecurityUtils.checkPassword(password, hashedPassword)) {
                    user = cursorToUser(cursor);

                    // Cập nhật thời gian đăng nhập cuối
                    updateLastLogin(user.getId());
                }

                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during login", e);
        } finally {
            close();
        }

        return user;
    }

    // Cập nhật thời gian đăng nhập cuối
    private void updateLastLogin(int userId) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateTime = dateFormat.format(new Date());

        ContentValues values = new ContentValues();
        values.put(COLUMN_LAST_LOGIN, currentDateTime);

        db.update(TABLE_USERS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(userId)});
    }

    // Kiểm tra username đã tồn tại chưa
    private boolean isUsernameExists(String username) {
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        boolean exists = (cursor != null && cursor.getCount() > 0);

        if (cursor != null) {
            cursor.close();
        }

        return exists;
    }

    // Lấy thông tin người dùng theo ID
    public User getUserById(int id) {
        User user = null;

        try {
            open();

            String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_ID + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

            if (cursor != null && cursor.moveToFirst()) {
                user = cursorToUser(cursor);
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user by id", e);
        } finally {
            close();
        }

        return user;
    }

    // Chuyển đổi từ Cursor sang đối tượng User
    private User cursorToUser(Cursor cursor) {
        User user = new User();

        user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
        user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
        // Không lấy mật khẩu vì lý do bảo mật
        user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULL_NAME)));
        user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
        user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)));

        int addressIndex = cursor.getColumnIndex(COLUMN_ADDRESS);
        if (addressIndex != -1 && !cursor.isNull(addressIndex)) {
            user.setAddress(cursor.getString(addressIndex));
        }

        user.setRole(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE)));

        int profileImageIndex = cursor.getColumnIndex(COLUMN_PROFILE_IMAGE);
        if (profileImageIndex != -1 && !cursor.isNull(profileImageIndex)) {
            user.setProfileImage(cursor.getString(profileImageIndex));
        }

        user.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)));

        int lastLoginIndex = cursor.getColumnIndex(COLUMN_LAST_LOGIN);
        if (lastLoginIndex != -1 && !cursor.isNull(lastLoginIndex)) {
            user.setLastLogin(cursor.getString(lastLoginIndex));
        }

        user.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));

        return user;
    }
}
