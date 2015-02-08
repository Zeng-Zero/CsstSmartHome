package com.csst.smarthome.rc.custom.provider;

import android.net.Uri;

public class Remotes {

    public static final String TABLE_NAME = "custom_remote";
    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String ICON_PATH = "icon_path";
    
    public static final Uri CONTENT_URI = Uri.parse("content://" + RemoteProvider.AUTHORITY + "/remote");
}
