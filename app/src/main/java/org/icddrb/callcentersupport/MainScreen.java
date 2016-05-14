package org.icddrb.callcentersupport;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import navigationDrawer.NavigationActivity;
import proto.net.client.AndroidNetCommunicationClientActivity;

/**
 * Created by SadiqMdAsif on 20-Apr-16.
 */
public class MainScreen extends NavigationActivity {
    int icddrb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        Intent intentCC = new Intent(this, CallCenter.class);
        startService(intentCC);

        Intent intentCom = new Intent(this, AndroidNetCommunicationClientActivity.class);
        startActivity(intentCom);
    }
}
