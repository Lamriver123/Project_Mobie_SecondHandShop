package com.example.marketplacesecondhand.API;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.marketplacesecondhand.models.UserLoginInfo;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "login_info.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_LOGIN = "login";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_USERID = "user_id";
    public static final String COLUMN_FULLNAME = "full_name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_TOKEN = "token";

    private static final String SQL_CREATE_LOGIN_TABLE =
            "CREATE TABLE " + TABLE_LOGIN + " (" +
                    COLUMN_USERID + " INTEGER, " +
                    COLUMN_USERNAME + " TEXT, " +
                    COLUMN_PASSWORD + " TEXT, " +
                    COLUMN_FULLNAME + " TEXT, " +
                    COLUMN_EMAIL + " TEXT, " +
                    COLUMN_TOKEN + " TEXT)";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_LOGIN_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xử lý nâng cấp cơ sở dữ liệu nếu cần
        // Trong trường hợp đơn giản này, chúng ta có thể xóa bảng cũ và tạo lại
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
        onCreate(db);
    }

    public long saveLoginInfoSQLite(int userId, String username, String password, String token) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOGIN, null, null);

        ContentValues values = new ContentValues();
        values.put(COLUMN_USERID, userId);
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_TOKEN, token);

        // Chèn một hàng mới vào bảng login và trả về ID của hàng vừa được chèn
        long newRowId = db.insert(TABLE_LOGIN, null, values);
        db.close(); // Đóng kết nối cơ sở dữ liệu sau khi hoàn tất
        return newRowId;
    }

    public UserLoginInfo getLoginInfoSQLite() {
        SQLiteDatabase db = this.getReadableDatabase();
        UserLoginInfo loginInfo = null;
        Cursor cursor = db.query(
                TABLE_LOGIN,     // Table name
                null,            // All columns
                null,            // WHERE clause
                null,            // WHERE args
                null,            // GROUP BY
                null,            // HAVING
                null,  // ORDER BY (tuỳ chọn)
                "1"              // LIMIT 1
        );
        if (cursor!=null && cursor.moveToFirst()) {
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USERID));
            String username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
            String password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));
            String fullName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULLNAME));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));
            String token = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TOKEN));
            loginInfo = new UserLoginInfo(userId, username, password, fullName, email, token);
            cursor.close();
            db.close();
        }
        return loginInfo;
    }

    public int updateLoginInfoSQLite(int userId, String fullName, String email, String usernameToUpdate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FULLNAME, fullName);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_USERID, userId);

        // Cập nhật hàng trong bảng login dựa trên username
        int rowsAffected = db.update(
                TABLE_LOGIN,
                values,
                COLUMN_USERNAME + "=?",
                new String[]{usernameToUpdate}
        );
        db.close(); // Đóng kết nối cơ sở dữ liệu sau khi hoàn tất
        return rowsAffected;
    }

    public void clearAllLoginData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOGIN, null, null);
        db.close();
    }
}
