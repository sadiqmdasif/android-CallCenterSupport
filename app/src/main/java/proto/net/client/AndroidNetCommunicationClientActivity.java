package proto.net.client;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.icddrb.callcentersupport.AutoAnswerIntentService;
import org.icddrb.callcentersupport.R;
import org.json.JSONArray;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import eneter.messaging.diagnostic.EneterTrace;
import eneter.messaging.endpoints.stringmessages.DuplexStringMessagesFactory;
import eneter.messaging.endpoints.stringmessages.IDuplexStringMessageSender;
import eneter.messaging.endpoints.stringmessages.IDuplexStringMessagesFactory;
import eneter.messaging.endpoints.stringmessages.StringResponseReceivedEventArgs;
import eneter.messaging.messagingsystems.messagingsystembase.IDuplexOutputChannel;
import eneter.messaging.messagingsystems.messagingsystembase.IMessagingSystemFactory;
import eneter.messaging.messagingsystems.tcpmessagingsystem.TcpMessagingSystemFactory;
import eneter.net.system.EventHandler;
import navigationDrawer.NavigationActivity;

public class AndroidNetCommunicationClientActivity extends NavigationActivity {   // UI controls
    // Sender sending MyRequest and as a response receiving MyResponse.
    private static IDuplexStringMessagesFactory aStringMessagesFactory = new DuplexStringMessagesFactory();
    private static IDuplexStringMessageSender myStringMessageSender;
    Button btnConnect;
    private Handler myRefresh = new Handler();
    private Gson gson;
    private EventHandler<StringResponseReceivedEventArgs> myOnResponseHandler
            = new EventHandler<StringResponseReceivedEventArgs>() {
        @Override
        public void onEvent(Object sender, StringResponseReceivedEventArgs e) {
            onResponseReceived(sender, e);
        }

    };

    public static void onSendRequest(String msg) {
        // Send the request message.
        try {
            myStringMessageSender.sendMessage(msg);

        } catch (Exception err) {
            EneterTrace.error("Sending the message failed.", err);
        }

    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getLayoutInflater().inflate(R.layout.connection, frameLayout);
        setContentView(R.layout.connection);

        gson = new Gson();
        btnConnect = (Button) findViewById(R.id.buttonConnect);
        final Intent intentCom = new Intent(this, ChatBox.class);
        btnConnect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(intentCom);

            }
        });

        // Open the connection in another thread.
        // Note: From Android 3.1 (Honeycomb) or higher
        //       it is not possible to open TCP connection
        //       from the main thread.
        Thread anOpenConnectionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    openConnection();

                } catch (Exception err) {
                    EneterTrace.error("Open connection failed.", err);
                }
            }
        });

        anOpenConnectionThread.start();
    }

    @Override
    public void onDestroy() {
        // Stop the listening to response messages.
        myStringMessageSender.detachDuplexOutputChannel();

        super.onDestroy();
    }

    private void openConnection() throws Exception {

// Create sender of string messages.
        myStringMessageSender = aStringMessagesFactory.createDuplexStringMessageSender();
// Subscribe to receive response messages.
        myStringMessageSender.responseReceived().subscribe(myOnResponseHandler);
// Create factory to create input channel based on Named Pipes
        IMessagingSystemFactory aMessagingSystemFactory = new TcpMessagingSystemFactory();
        IDuplexOutputChannel anOutputChannel = aMessagingSystemFactory.createDuplexOutputChannel("tcp://172.17.137.87:8060/");

// Attach the output channel to the string message sender.
        myStringMessageSender.attachDuplexOutputChannel(anOutputChannel);
        myStringMessageSender.sendMessage("");

    }

    private void onResponseReceived(Object sender,
                                    final StringResponseReceivedEventArgs e) {
        // Display the result - returned number of characters.
        // Note: Marshal displaying to the correct UI thread.
        myRefresh.post(new Runnable() {
            @Override
            public void run() {
                ChatBox.writeToChatBox(e.getResponseMessage());

                if (e.getResponseMessage().equals("commandMsgCallHistory\n")) {
                    try {
                        myStringMessageSender.sendMessage(getCallDetails());
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                } else if (e.getResponseMessage().contains("commandMsgCallDial")) {
                    try {
                            /* USSD CODE
                        String baseUssd = Uri.encode("*") + "121" + Uri.encode("#");
                        StringBuilder builder = new StringBuilder();
                        builder.append(baseUssd);
                        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + builder.toString()));
                            */

                        int commandMsgLength = e.getResponseMessage().length();
                        String phoneNo = e.getResponseMessage().substring(18, commandMsgLength - 1);
                        Uri number = Uri.parse("tel:" + phoneNo);
                        Intent callIntent = new Intent(Intent.ACTION_CALL, number);
                        startActivity(callIntent);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                } else if (e.getResponseMessage().equals("commandMsgCallReceive\n")) {
                    try {
                        Intent intentCallAction = new Intent(getApplicationContext(), AutoAnswerIntentService.class);
                        startService(intentCallAction);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                } else if (e.getResponseMessage().equals("commandMsgCallEnd\n")) {

                    mEndCall();
                } else if (e.getResponseMessage().contains("commandMsgSMSSend")) {
                    try {
                        int commandMsgLength = e.getResponseMessage().length();
                        String phoneNo = e.getResponseMessage().substring(17, 28);
                        String msg = e.getResponseMessage().substring(28, commandMsgLength - 1);
                        sendSMS(phoneNo, msg);
                    } catch (Exception e) {
                    }
                }


            }
        });
    }

    private void mEndCall() {
        try {

            String serviceManagerName = "android.os.ServiceManager";
            String serviceManagerNativeName = "android.os.ServiceManagerNative";
            String telephonyName = "com.android.internal.telephony.ITelephony";

            Class telephonyClass;
            Class telephonyStubClass;
            Class serviceManagerClass;
            Class serviceManagerStubClass;
            Class serviceManagerNativeClass;
            Class serviceManagerNativeStubClass;

            Method telephonyCall;
            Method telephonyEndCall;
            Method telephonyAnswerCall;
            Method getDefault;

            Method[] temps;
            Constructor[] serviceManagerConstructor;

            // Method getService;
            Object telephonyObject;
            Object serviceManagerObject;

            telephonyClass = Class.forName(telephonyName);
            telephonyStubClass = telephonyClass.getClasses()[0];
            serviceManagerClass = Class.forName(serviceManagerName);
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName);

            Method getService = // getDefaults[29];
                    serviceManagerClass.getMethod("getService", String.class);

            Method tempInterfaceMethod = serviceManagerNativeClass.getMethod(
                    "asInterface", IBinder.class);

            Binder tmpBinder = new Binder();
            tmpBinder.attachInterface(null, "fake");

            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
            IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
            Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);

            telephonyObject = serviceMethod.invoke(null, retbinder);
            //telephonyCall = telephonyClass.getMethod("call", String.class);
            telephonyEndCall = telephonyClass.getMethod("endCall");
            //telephonyAnswerCall = telephonyClass.getMethod("answerRingingCall");

            telephonyEndCall.invoke(telephonyObject);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("error",
                    "FATAL ERROR: could not connect to telephony subsystem");

        }
    }

    private String getCallDetails() {

        StringBuffer sb = new StringBuffer();
        Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null,
                null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        sb.append("Call Details :");

        JsonObject jsonObject = null;
        JSONArray jsonArray = new JSONArray();
        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDay = managedCursor.getString(date);
            // Date callDayTime = new Date(Long.valueOf(callDate));

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy");
            String callDate = formatter.format(new Date(Long.valueOf(callDay)));
            formatter = new SimpleDateFormat("HH:mm");
            String callTime = formatter.format(new Date(Long.valueOf(callDay)));

            String callDuration = managedCursor.getString(duration);
            String callDirection = null;
            int callDirCode = Integer.parseInt(callType);
            switch (callDirCode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    callDirection = "OUTGOING";
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    callDirection = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    callDirection = "MISSED";
                    break;
            }

            try {
                jsonObject = new JsonObject();
                jsonObject.addProperty("Phone", phNumber);
                jsonObject.addProperty("Type", callDirection);
                jsonObject.addProperty("Duration", callDuration);
                jsonObject.addProperty("Date", String.valueOf(callDate));
                jsonObject.addProperty("Time", String.valueOf(callTime));


            } catch (Exception e) {
                e.printStackTrace();
            }

            jsonArray.put(jsonObject);

        }

        String json = gson.toJson(jsonArray);
        managedCursor.close();
        return json;

    }

    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
}