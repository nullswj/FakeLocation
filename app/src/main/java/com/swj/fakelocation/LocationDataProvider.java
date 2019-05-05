package com.swj.fakelocation;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class LocationDataProvider extends ContentProvider {
    public static final int Local_DIR = 0;

    public static final int Local_ITEM = 1;

    public static final int real_Local_DIR = 2;

    public static final int real_Local_ITEM = 3;

    public static final String AUTHORITY = "com.swj.fakelocation.provider";

    private static UriMatcher matcher ;

    private LocationDatabaseHelper dbHelper;

    static
    {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY,"Location",Local_DIR);
        matcher.addURI(AUTHORITY,"Location/#",Local_ITEM);
        matcher.addURI(AUTHORITY,"RealLocation",real_Local_DIR);
        matcher.addURI(AUTHORITY,"RealLocation/#",real_Local_ITEM);
    }

    public LocationDataProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        switch (matcher.match(uri))
        {
            case Local_DIR:
                return "vnd.android.cursor.dir/vnd."+AUTHORITY+"Location";
            case Local_ITEM:
                return "vnd.android.cursor.item/vnd."+AUTHORITY+"Location";
            case real_Local_DIR:
                return "vnd.android.cursor.dir/vnd."+AUTHORITY+"RealLocation";
            case real_Local_ITEM:
                return "vnd.android.cursor.item/vnd."+AUTHORITY+"RealLocation";
            default:
                break;
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Uri uriReturn = null;
        switch (matcher.match(uri))
        {
            case Local_DIR:
            case Local_ITEM:
                long newLocationID = db.insert("Location",null,values);
                uriReturn = uri.parse("content://" + AUTHORITY + "/Location/" + newLocationID);
                break;

            case real_Local_DIR:
            case real_Local_ITEM:
                long newrealLocationID = db.insert("RealLocation",null,values);
                uriReturn = uri.parse("content://" + AUTHORITY + "/RealLocation/" + newrealLocationID);
                break;
        }
        return uriReturn;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new LocationDatabaseHelper(getContext(),"LocationStore.db",null,1);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = null;
        switch (matcher.match(uri))
        {
            case Local_DIR:
                cursor = db.query("Location",projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case Local_ITEM:
                String lat_ID = "1";
                String lat_Name = "lat";
                String lon_Name = "lon";
                cursor = db.query("Location",new String[]{lat_Name,lon_Name},"id = ?",new String[]{lat_ID},null,null,sortOrder);
                break;

            case real_Local_DIR:
                cursor = db.query("RealLocation",projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case real_Local_ITEM:
                String real_lat_ID = "1";
                String real_lat_Name = "lat";
                String real_lon_Name = "lon";
                cursor = db.query("RealLocation",new String[]{real_lat_Name,real_lon_Name},"id = ?",new String[]{real_lat_ID},null,null,sortOrder);
                break;
            default:
                break;
        }
        return cursor;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
