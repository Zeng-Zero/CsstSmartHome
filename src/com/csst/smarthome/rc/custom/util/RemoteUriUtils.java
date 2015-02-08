package com.csst.smarthome.rc.custom.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;

import com.csst.smarthome.rc.custom.provider.RemoteKeys;
import com.csst.smarthome.rc.custom.provider.Remotes;

public class RemoteUriUtils {
    private static final String LOG_TAG = "RemoteUriUtils";

    public static ContentValues getRemoteUriContentValue(Context ctx, Uri uri) {
        ContentResolver cs = ctx.getContentResolver();
        
        ContentValues values = null;
        try {
            Cursor c = cs.query(uri, null, null, null, null);
            if(c.moveToFirst()) {
                values = new ContentValues();
                values.put(Remotes.ID, c.getLong(c.getColumnIndex(Remotes.ID)));
                values.put(Remotes.NAME, c.getString(c.getColumnIndex(Remotes.NAME)));
                values.put(Remotes.ICON_PATH, c.getString(c.getColumnIndex(Remotes.ICON_PATH)));
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }
    
    public static ContentValues getRemoteKeyUriContentValue(Context ctx, Uri uri) {
        ContentResolver cs = ctx.getContentResolver();
        
        ContentValues values = null;
        try {
            Cursor c = cs.query(uri, null, null, null, null);
            if(c.moveToFirst()) {
                values = new ContentValues();
                values.put(RemoteKeys.ID, c.getLong(c.getColumnIndex(RemoteKeys.ID)));
                values.put(RemoteKeys.NAME, c.getString(c.getColumnIndex(RemoteKeys.NAME)));
                values.put(RemoteKeys.DRAWABLE_ID, c.getInt(c.getColumnIndex(RemoteKeys.DRAWABLE_ID)));
                values.put(RemoteKeys.REMOTE_ID, c.getLong(c.getColumnIndex(RemoteKeys.REMOTE_ID)));
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }
    
    public static Cursor getKeysCursorOfRemoteUri(Context ctx, Uri uri) {
        Cursor c = null;
        Builder builder = uri.buildUpon();
        builder.appendPath("keys");
        Uri keysOfRemote = builder.build();
        c = ctx.getContentResolver().query(keysOfRemote, null, null, null, null);
        return c;
    }

    public static boolean deleteRemoteWithId(
            Context ctx, long id) {
        Uri uri = ContentUris.withAppendedId(Remotes.CONTENT_URI, id);
        
        int deletedCount = ctx.getContentResolver().delete(uri, null, null);

        Log.v(LOG_TAG, "deleteRemoteWithId delete " + deletedCount + " remotes.");
        Uri keysUri = uri.buildUpon().appendPath("keys").build();
        deletedCount = ctx.getContentResolver().delete(keysUri, null, null);
        
        Log.v(LOG_TAG, "deleteRemoteWithId delete " + deletedCount + " key in remote:" + id);
        return deletedCount == 1;
    }
    
    
    public static boolean deleteKeyWithId(
            Context ctx, long id) {
        Uri uri = ContentUris.withAppendedId(RemoteKeys.CONTENT_URI, id);
        int deletedCount = ctx.getContentResolver().delete(uri, null, null);
        Log.v(LOG_TAG, "deleteKeyWithId delete " + deletedCount + " keys.");
        return deletedCount == 1;
    }
    
    public static Cursor getCustomRemotes(Context ctx) {
        Cursor c = null;
        c = ctx.getContentResolver().query(Remotes.CONTENT_URI, null, null, null, null);
        return c;
    }
}
