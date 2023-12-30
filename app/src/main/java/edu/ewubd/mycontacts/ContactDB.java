package edu.ewubd.mycontacts;

import  android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.content.Context;
public class ContactDB extends SQLiteOpenHelper{
    public ContactDB(Context context) {
        super(context, "ContactDB.db", null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("DB@OnCreate");
        String sql = "CREATE TABLE ContactTable  ("
                + "user_email TEXT,"
                + "name TEXT,"
                + "email TEXT,"
                + "home_phone TEXT,"
                + "office_phone TEXT,"
                + "photo TEXT,"
                + "PRIMARY KEY (home_phone)"
                + ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("Write code to modify database schema here");
    }


    public void insertContact(String userEmail, String name, String email, String homePhone, String officePhone, String photo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cols = new ContentValues();
        cols.put("user_email", userEmail);
        cols.put("name", name);
        cols.put("email", email);
        cols.put("home_phone", homePhone);
        cols.put("office_phone", officePhone);
        cols.put("photo", photo);
        db.insert("ContactTable", null, cols);
        db.close();
    }

    public boolean eventExists(String homePhone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM ContactTable WHERE home_phone=?";
            String[] selectionArgs = {homePhone};
            cursor = db.rawQuery(query, selectionArgs);

            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }


    public void updateContact(String userEmail, String name, String email, String homePhone, String officePhone, String photo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cols = new ContentValues();
        cols.put("user_email", userEmail);
        cols.put("name", name);
        cols.put("email", email);
        cols.put("office_phone", officePhone);
        cols.put("home_phone", homePhone);
        cols.put("photo", photo);

        String whereClause = "home_phone=?";
        String[] whereArgs = {homePhone};

        db.update("ContactTable", cols, whereClause, whereArgs);
        db.close();
    }

    public void deleteContact(String homePhone) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = "home_phone=?";
        db.delete("ContactTable", whereClause, new String[ ] {homePhone} );
        db.close();
    }



}
