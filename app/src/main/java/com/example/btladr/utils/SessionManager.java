package com.example.btladr.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.btladr.model.User;

public class SessionManager {
    private static final String TAG = "SessionManager";

    // Shared Preferences
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    // Shared Preferences mode
    private static final int PRIVATE_MODE = 0;

    // Tên file Shared Preferences
    private static final String PREF_NAME = "FoodOrderingAppPref";

    // Các khóa Shared Preferences
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_FULL_NAME = "fullName";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role";

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    // Lưu thông tin đăng nhập
    public void createLoginSession(User user) {
        // Lưu trạng thái đăng nhập
        editor.putBoolean(KEY_IS_LOGGED_IN, true);

        // Lưu thông tin người dùng
        editor.putInt(KEY_USER_ID, user.getId());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_FULL_NAME, user.getFullName());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_ROLE, user.getRole());

        // Commit thay đổi
        editor.commit();

        Log.d(TAG, "User login session created for: " + user.getUsername());
    }

    // Kiểm tra trạng thái đăng nhập
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Lấy thông tin người dùng từ session
    public User getUserDetails() {
        if (!isLoggedIn()) {
            return null;
        }

        User user = new User();
        user.setId(pref.getInt(KEY_USER_ID, -1));
        user.setUsername(pref.getString(KEY_USERNAME, null));
        user.setFullName(pref.getString(KEY_FULL_NAME, null));
        user.setEmail(pref.getString(KEY_EMAIL, null));
        user.setRole(pref.getString(KEY_ROLE, "user"));

        return user;
    }

    // Kiểm tra xem người dùng có phải là admin không
    public boolean isAdmin() {
        return isLoggedIn() && "admin".equals(pref.getString(KEY_ROLE, "user"));
    }

    // Đăng xuất người dùng
    public void logout() {
        // Xóa tất cả dữ liệu
        editor.clear();
        editor.commit();

        Log.d(TAG, "User logged out");
    }
}
