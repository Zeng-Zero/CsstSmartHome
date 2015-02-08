package com.csst.smarthome.rc.custom.provider;

import android.database.Cursor;

public class RemoteKeyModel {
    public long  id;
    public String name;
    public int drawableId;
    public long remoteId;
    
    public RemoteKeyModel(Cursor c) {
        id = c.getLong(c.getColumnIndex(RemoteKeys.ID));
        name = c.getString(c.getColumnIndex(RemoteKeys.NAME));
        drawableId = c.getInt(c.getColumnIndex(RemoteKeys.DRAWABLE_ID));
        remoteId = c.getLong(c.getColumnIndex(RemoteKeys.REMOTE_ID));
    }
}
