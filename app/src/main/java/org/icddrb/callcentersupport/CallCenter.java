package org.icddrb.callcentersupport;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipProfile;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import java.io.File;

public class CallCenter extends Service {
    int mStartMode;       // indicates how to behave if the service is killed
    IBinder mBinder;      // interface for clients that bind
    boolean mAllowRebind; // indicates whether onRebind should be used

    public static String number;
    public static String CallID;
    TelephonyManager telephonyManager;

    @Override
    public void onCreate() {

        telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        PhoneStateListener callStateListener = new PhoneStateListener() {

            public void onCallStateChanged(int state, String incomingNumber)
            {
                // TODO React to incoming call.

                try
                {
                    if(state== TelephonyManager.CALL_STATE_RINGING)
                    {
                        Toast.makeText(getApplicationContext(),"Phone Is Ringing"+ incomingNumber, Toast.LENGTH_LONG).show();
                        number=incomingNumber;


                        //g.setPhoneNo(incomingNumber);
                    }

                    if(state== TelephonyManager.CALL_STATE_OFFHOOK)
                    {
                        Toast.makeText(getApplicationContext(),"Phone is Currently in A call"+incomingNumber, Toast.LENGTH_LONG).show();
                        //number = mIntent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                        number=incomingNumber;


                    }
                    if(state== TelephonyManager.DATA_CONNECTING)
                    {
                        Toast.makeText(getApplicationContext(),"Phone is Connecting", Toast.LENGTH_LONG).show();
                        number=incomingNumber;
                    }
                    if(state== TelephonyManager.DATA_CONNECTED)
                    {
                        number=incomingNumber;
                    }
                    if(state== TelephonyManager.DATA_DISCONNECTED)
                    {
                        number="";
                        CallID="";
                    }

                    if(state== TelephonyManager.CALL_STATE_IDLE)
                    {
                        Toast.makeText(getApplicationContext(),"phone is neither ringing nor in a call", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    //conn.MessageBox(MainActivity.this, e.getMessage());
                    e.printStackTrace();
                }
                super.onCallStateChanged(state, incomingNumber);

            }
        };
        telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()

        return mStartMode;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
        return mBinder;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        return mAllowRebind;
    }
    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }
    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
    }

    public class OutgoingReceiver extends BroadcastReceiver {
        public OutgoingReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        }
    }

    public String PhoneNo()
    {
        return number;
    }

}