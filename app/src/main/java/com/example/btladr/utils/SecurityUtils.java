package com.example.btladr.utils;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SecurityUtils {
    private static final String TAG = "SecurityUtils";
    private static final String ALGORITHM = "SHA-256";

    // Mã hóa mật khẩu bằng SHA-256 với salt
    public static String hashPassword(String password) {
        try {
            // Tạo salt ngẫu nhiên
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);

            // Tạo hash
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());

            // Kết hợp salt và hash để lưu trữ
            byte[] combined = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashedPassword, 0, combined, salt.length, hashedPassword.length);

            // Chuyển đổi kết quả thành chuỗi Base64
            return android.util.Base64.encodeToString(combined, android.util.Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Error hashing password", e);
            return null;
        }
    }

    // Kiểm tra mật khẩu
    public static boolean checkPassword(String password, String storedHash) {
        try {
            // Giải mã chuỗi Base64
            byte[] combined = android.util.Base64.decode(storedHash, android.util.Base64.DEFAULT);

            // Tách salt và hash
            byte[] salt = new byte[16];
            byte[] hash = new byte[combined.length - 16];
            System.arraycopy(combined, 0, salt, 0, 16);
            System.arraycopy(combined, 16, hash, 0, hash.length);

            // Tạo hash từ mật khẩu nhập vào với salt đã lưu
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] newHash = md.digest(password.getBytes());

            // So sánh hash mới với hash đã lưu
            if (hash.length != newHash.length) {
                return false;
            }

            for (int i = 0; i < hash.length; i++) {
                if (hash[i] != newHash[i]) {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error checking password", e);
            return false;
        }
    }
}