package org.icddrb.callcentersupport;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by SadiqMdAsif on 20-Apr-16.
 */
public class MainScreen extends Activity {
    int icddrb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        Intent intentCC = new Intent(this, CallManagerService.class);
        startService(intentCC);

        Intent intentCom = new Intent(this, AndroidNetCommunicationClientActivity.class);
        startActivity(intentCom);
    }
}
