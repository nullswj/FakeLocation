package com.swj.fakelocation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import org.jetbrains.annotations.Nullable;


public class LocationDatabaseHelper extends SQLiteOpenHelper {

    public static final String CREATE_LOCATION =
            "create table Location (" +
                                    "id INTEGER primary key autoincrement,"+
                                    "lon REAL,"+
                                    "lat REAL)";

    private Context context;

    public LocationDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LOCATION);
        Toast.makeText(context,"Create Successed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
