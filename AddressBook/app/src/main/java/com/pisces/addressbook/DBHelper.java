package com.pisces.addressbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private SQLiteDatabase db;
    ContentValues contentValues = new ContentValues();

    private static final String TAG = "DBhelper";

    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table user(id integer primary key autoincrement,name text,phone text,work text,address text)");
    }

    //添加数据
    public boolean insert(User user) {
        contentValues.put("name", user.getName());
        contentValues.put("phone", user.getPhone());
        contentValues.put("work", user.getWork());
        contentValues.put("address", user.getAddress());
        long result = db.insert("user", null, contentValues);
        return result > 0 ? true : false;
    }

    public boolean delete(String id) {
        int result = db.delete("user", "id=?", new String[]{id});
        return result > 0 ? true : false;
    }

    //修改数据，根据id进行修改
    public boolean update(String id, User user) {
        contentValues.put("name", user.getName());
        contentValues.put("phone", user.getPhone());
        contentValues.put("work", user.getWork());
        contentValues.put("address", user.getAddress());
        int result = db.update("user", contentValues, "id=?", new String[]{id});
        return result > 0 ? true : false;
    }

    //查询数据,查询表中的所有内容，将查询的内容用note的对象属性进行存储，并将该对象存入集合中。
    public List<User> query() {
        List<User> list = new ArrayList<>();
        Cursor result = db.query("user", null, null, null,
                null, null, null, null);
        if (result != null) {
            while (result.moveToNext()) {
                User user = new User();
                user.setId(String.valueOf(result.getInt(0)));
                user.setName(result.getString(1));   //1姓名
                user.setPhone(result.getString(2));  //2电话
                user.setWork(result.getString(3));   //3工作单位
                user.setAddress(result.getString(4));  //4家庭住址
                list.add(user);
            }
            result.close();
        }
        return list;
    }

    //模糊查询所有名字相同的
    public List<User> query(String name) {
        List<User> list = new ArrayList<>();
        Cursor result = db.rawQuery("select * from user where name like '%" + name + "%'",null);
        if (result != null) {
            Log.w(TAG, "out");
            while (result.moveToNext()) {
                Log.w(TAG, "in");
                User user = new User();
                user.setId(String.valueOf(result.getInt(0)));
                user.setName(result.getString(1));   //1姓名
                user.setPhone(result.getString(2));  //2电话
                user.setWork(result.getString(3));   //3工作单位
                user.setAddress(result.getString(4));  //4家庭住址
                list.add(user);
            }
            result.close();
        }
        return list;
    }

    public User getByName(String name) {
        User user = new User();
        Cursor result = db.query("user", null, "name = ?", new String[]{name},
                null, null, null, null);
        if (result.moveToFirst()) {
            user.setId(String.valueOf(result.getInt(0)));
            user.setName(result.getString(1));   //1姓名
            user.setPhone(result.getString(2));  //2电话
            user.setName(result.getString(3));   //3工作单位
            user.setPhone(result.getString(4));  //4家庭住址

        }
        result.close();
        return user;
    }

    public User getById(String id) {
        Log.w(TAG, "用户号：" + id);
        User user = new User();
        Cursor result = db.query("user", null, "id = ?", new String[]{id},
                null, null, null, null);
        Log.w(TAG, "user.getAddress()");
        if (result.getCount() > 0) {
            result.moveToFirst();
            user.setId(String.valueOf(result.getInt(0)));
            user.setName(result.getString(1));   //1姓名
            user.setPhone(result.getString(2));  //2电话
            user.setWork(result.getString(3));   //3工作单位
            user.setAddress(result.getString(4));  //4家庭住址
            Log.w(TAG, user.getAddress());
        }
        result.close();
        return user;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
