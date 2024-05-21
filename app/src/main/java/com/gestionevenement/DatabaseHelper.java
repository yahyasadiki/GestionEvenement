package com.gestionevenement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Information
    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 1;

    // Table Information
    private static final String TABLE_TASKS = "tasks";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_DEADLINE = "deadline";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_DONE = "done";

    // Create Table SQL Query
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_TASKS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT NOT NULL, " +
            COLUMN_DESCRIPTION + " TEXT, " +
            COLUMN_DEADLINE + " TEXT, " +
            COLUMN_CATEGORY + " TEXT, " +
            COLUMN_DONE + " INTEGER NOT NULL);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // getTasksForCategory

    public List<TaskModel> getTasksForCategory(String category) {
        List<TaskModel> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, null, COLUMN_CATEGORY + " = ?", new String[]{category}, null, null, COLUMN_DEADLINE + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                TaskModel task = new TaskModel(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        parseDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEADLINE))),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
                );
                task.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                task.setDone(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DONE)) == 1);
                tasks.add(task);
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();
        return tasks;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }

    // Insert a task into the database
    public long insertTask(TaskModel task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, task.getName());
        values.put(COLUMN_DESCRIPTION, task.getDescription());
        values.put(COLUMN_DEADLINE, formatDate(task.getDeadline()));
        values.put(COLUMN_CATEGORY, task.getCategory());
        values.put(COLUMN_DONE, task.isDone() ? 1 : 0);

        long id = db.insert(TABLE_TASKS, null, values);
        db.close();
        return id;
    }

    // Get all tasks from the database
    public List<TaskModel> getAllTasks() {
        List<TaskModel> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, null, null, null, null, null, COLUMN_DEADLINE + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                TaskModel task = new TaskModel(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        parseDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEADLINE))),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
                );
                task.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                task.setDone(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DONE)) == 1);
                tasks.add(task);
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();
        return tasks;
    }

    // Update a task in the database
    public int updateTask(TaskModel task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, task.getName());
        values.put(COLUMN_DESCRIPTION, task.getDescription());
        values.put(COLUMN_DEADLINE, formatDate(task.getDeadline()));
        values.put(COLUMN_CATEGORY, task.getCategory());
        values.put(COLUMN_DONE, task.isDone() ? 1 : 0);

        int rowsAffected = db.update(TABLE_TASKS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(task.getId())});
        db.close();
        return rowsAffected;
    }

    // Delete a task from the database
    public void deleteTask(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Helper methods to handle date conversion
    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(date);
    }

    private Date parseDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
