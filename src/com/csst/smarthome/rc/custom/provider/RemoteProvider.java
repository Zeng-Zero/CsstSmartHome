package com.csst.smarthome.rc.custom.provider;

import java.util.List;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class RemoteProvider extends ContentProvider {

    public static final String      AUTHORITY            = "com.example.smarthomelayout.provider.remote";

    private static final String     DATABASE_NAME        = "CustomRemote.db";
    private static final int        DATABASE_VERSION     = 3;

    private static final int        CUSTOM_REMOTE        = 1;
    private static final int        CUSTOM_REMOTE_ID     = 2;
    private static final int        CUSTOM_REMOTE_KEYS   = 3;

    private static final int        CUSTOM_REMOTE_KEY    = 10;
    private static final int        CUSTOM_REMOTE_KEY_ID = 11;
    private static final UriMatcher URIMatcher           = new UriMatcher(
                                                                 UriMatcher.NO_MATCH);
    static {
        URIMatcher.addURI(AUTHORITY, "remote", CUSTOM_REMOTE);
        URIMatcher.addURI(AUTHORITY, "remote/#", CUSTOM_REMOTE_ID);
        URIMatcher.addURI(AUTHORITY, "remote/#/keys", CUSTOM_REMOTE_KEYS);
        URIMatcher.addURI(AUTHORITY, "key", CUSTOM_REMOTE_KEY);
        URIMatcher.addURI(AUTHORITY, "key/#", CUSTOM_REMOTE_KEY_ID);
    }

    private DatabaseHelper          mDbHelper;

    public static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // create table for remote
            db.execSQL("CREATE TABLE " + Remotes.TABLE_NAME + 
                    "("
                    + Remotes.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + Remotes.NAME + " TEXT UNIQUE NOT NULL,"
                    + Remotes.ICON_PATH + " TEXT" + ");");
            // create table for remote key
            db.execSQL("CREATE TABLE " + RemoteKeys.TABLE_NAME + "("
                    + RemoteKeys.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + RemoteKeys.NAME + " TEXT UNIQUE NOT NULL,"
                    + RemoteKeys.DRAWABLE_ID + " INTEGER NOT NULL,"
                    + RemoteKeys.REMOTE_ID + " INTEGER NOT NULL," 
                    + RemoteKeys.CODE + " TEXT "
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + Remotes.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + RemoteKeys.TABLE_NAME);
            onCreate(db);
        }

    }

    @Override
    public boolean onCreate() {
        mDbHelper = new DatabaseHelper(getContext());
        return (mDbHelper == null) ? false : true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = null;
        int type = URIMatcher.match(uri);
        switch (type) {
        case CUSTOM_REMOTE:
            c = db.query(Remotes.TABLE_NAME, projection, selection,
                    selectionArgs, null, null, sortOrder);
            break;
        case CUSTOM_REMOTE_ID:
            // long id = ContentUris.parseId(uri);
            selection = selection == null ? "_id = " + uri.getLastPathSegment() : selection + " AND _id = " + uri.getLastPathSegment();
            c = db.query(Remotes.TABLE_NAME, projection, selection,
                    selectionArgs, null, null, sortOrder);
            break;
        case CUSTOM_REMOTE_KEYS:
            List<String> segments = uri.getPathSegments();
            String idStr = segments.get(1);
            selection = selection == null ?  "remote_id=" + idStr : selection + " AND remote_id = " + idStr;
            c = db.query(RemoteKeys.TABLE_NAME, projection, selection,
                    selectionArgs, null, null, sortOrder);
            break;
        case CUSTOM_REMOTE_KEY:
            c = db.query(RemoteKeys.TABLE_NAME, projection, selection,
                    selectionArgs, null, null, sortOrder);
            break;
        case CUSTOM_REMOTE_KEY_ID:
            selection = selection == null ? "_id = " + uri.getLastPathSegment() : selection + " AND _id = " + uri.getLastPathSegment();
            c = db.query(RemoteKeys.TABLE_NAME, projection, selection,
                    selectionArgs, null, null, sortOrder);
            break;

        default:
            throw new IllegalArgumentException("unknown uri: " + uri.toString());
        }
        return c;
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = -1;
        int type = URIMatcher.match(uri);
        switch (type) {
        case CUSTOM_REMOTE:
            id = db.insertOrThrow(Remotes.TABLE_NAME, null, values);
            break;
        case CUSTOM_REMOTE_KEY:
            id = db.insertOrThrow(RemoteKeys.TABLE_NAME, null, values);
            break;
        default:
            throw new IllegalArgumentException("unkown uri:" + uri);
        }
        if (id != -1) {
            return ContentUris.withAppendedId(type == CUSTOM_REMOTE ? 
                    Remotes.CONTENT_URI :RemoteKeys.CONTENT_URI, id);
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int type = URIMatcher.match(uri);
        switch (type) {
        case CUSTOM_REMOTE:
            return db.delete(Remotes.TABLE_NAME, selection, selectionArgs);
        case CUSTOM_REMOTE_ID:
            String remoteIdStr = uri.getLastPathSegment();
            selection = selection == null ? "_id=" + remoteIdStr : selection + " AND _id=" + remoteIdStr;
            return db.delete(Remotes.TABLE_NAME, selection, selectionArgs);
        case CUSTOM_REMOTE_KEYS:
            remoteIdStr = uri.getPathSegments().get(1);
            selection = selection == null ? "remote_id=" + remoteIdStr : selection + " AND remote_id=" + remoteIdStr;
            return db.delete(RemoteKeys.TABLE_NAME, selection, selectionArgs);
        case CUSTOM_REMOTE_KEY:
            return db.delete(RemoteKeys.TABLE_NAME, selection, selectionArgs);
        case CUSTOM_REMOTE_KEY_ID:
            String keyIdStr = uri.getLastPathSegment();
            selection = selection == null ? "_id=" + keyIdStr : selection + " AND _id=" + keyIdStr;
            return db.delete(RemoteKeys.TABLE_NAME, selection, selectionArgs);
        default:

            throw new IllegalArgumentException("unknown uri:" + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int type = URIMatcher.match(uri);
        switch (type) {
        case CUSTOM_REMOTE_ID:
            String remoteIdStr = uri.getLastPathSegment();
            if (selection == null) {
                selection = "_id=" + remoteIdStr;
            } else {
                selection = selection + " AND _id=" + remoteIdStr;
            }
            return db.update(Remotes.TABLE_NAME, values, selection,
                    selectionArgs);
        case CUSTOM_REMOTE_KEY_ID:
            String keyIdStr = uri.getLastPathSegment();
            if (selection == null) {
                selection = "_id=" + keyIdStr;
            } else {
                selection = selection + " AND _id=" + keyIdStr;
            }
            return db.update(RemoteKeys.TABLE_NAME, values, selection,
                    selectionArgs);
            default:
                throw new IllegalArgumentException("unknown uri:" + uri);
        }
    }

}
