package com.system;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;

/**
 * Created by Shahnawaz on 5/6/2016.
 */
public class DummyActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PackageManager p = getPackageManager();
        p.setComponentEnabledSetting(getComponentName(), PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        finish();
    }
}
