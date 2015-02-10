package com.csst.smarthome.rc.custom.util;

import com.csst.smarthome.R;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class TitleLayoutUtils {
    public static final void setupTitleLayout(final Activity activity, String title) {
        TextView titleView = (TextView) activity.findViewById(R.id.title_textview);
        titleView.setText(title);
        activity.findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
    }
}
