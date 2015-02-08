package com.csst.smarthome.rc.custom.provider;

import android.net.Uri;

public class RemoteKeys {

    public static final String TABLE_NAME = "custom_remote_key";
    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String DRAWABLE_ID = "drawable_id";
    public static final String REMOTE_ID = "remote_id";
    public static final String CODE = "code";
    public static final Uri CONTENT_URI = Uri.parse("content://" + RemoteProvider.AUTHORITY + "/key");
}
