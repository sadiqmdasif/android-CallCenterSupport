package proto.net.client;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import org.icddrb.callcentersupport.R;

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

public class AndroidNetCommunicationClientActivity extends NavigationActivity
{


    // UI controls
    private Handler myRefresh = new Handler();


    // Sender sending MyRequest and as a response receiving MyResponse.
    private static IDuplexStringMessagesFactory aStringMessagesFactory  = new DuplexStringMessagesFactory();
    private static IDuplexStringMessageSender myStringMessageSender;
    Button btnConnect;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // getLayoutInflater().inflate(R.layout.connection, frameLayout);
        setContentView(R.layout.connection);
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
        Thread anOpenConnectionThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    openConnection();
                }
                catch (Exception err)
                {
                    EneterTrace.error("Open connection failed.", err);
                }
            }
        });
        anOpenConnectionThread.start();
    }

    @Override
    public void onDestroy()
    {
        // Stop the listening to response messages.
        myStringMessageSender.detachDuplexOutputChannel();

        super.onDestroy();
    }

    private void openConnection() throws Exception
    {

// Create sender of string messages.
        myStringMessageSender = aStringMessagesFactory.createDuplexStringMessageSender();
// Subscribe to receive response messages.
        myStringMessageSender.responseReceived().subscribe(myOnResponseHandler);
        // Create factory to create input channel based on Named Pipes
        IMessagingSystemFactory aMessagingSystemFactory = new TcpMessagingSystemFactory();
        IDuplexOutputChannel anOutputChannel = aMessagingSystemFactory.createDuplexOutputChannel("tcp://172.16.13.205:8060/");

        // Attach the output channel to the string message sender.
        myStringMessageSender.attachDuplexOutputChannel(anOutputChannel);

        }

    public static void onSendRequest(String msg)
    {
        // Send the request message.
        try
        {
           // mySender.sendRequestMessage(aRequestMsg);
            myStringMessageSender.sendMessage(msg);

        }
        catch (Exception err)
        {
            EneterTrace.error("Sending the message failed.", err);
        }

    }

    private void onResponseReceived(Object sender,
                                    final StringResponseReceivedEventArgs e)
    {
        // Display the result - returned number of characters.
        // Note: Marshal displaying to the correct UI thread.
        myRefresh.post(new Runnable()
        {
            @Override
            public void run()
            {
                ChatBox.writeToChatBox(e.getResponseMessage().toString());

                if(e.getResponseMessage().toString().equals("CallHistory\n")){
                    try {
                        myStringMessageSender.sendMessage(getCallDetails());
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    private EventHandler<StringResponseReceivedEventArgs> myOnResponseHandler
            = new EventHandler<StringResponseReceivedEventArgs>()
    {
        @Override
        public void onEvent(Object sender, StringResponseReceivedEventArgs e) {
            onResponseReceived(sender, e);
        }

    };

    private String getCallDetails() {

        StringBuffer sb = new StringBuffer();
        Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null,
                null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        sb.append("Call Details :");
        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = managedCursor.getString(duration);
            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
            }
            sb.append("\nPhone Number: " + phNumber + " \nCall Type: "
                    + dir + " \nCall Date: " + callDayTime
                    + " \nCall duration in sec : " + callDuration);
            sb.append("\n----------------------------------");
        }
        managedCursor.close();
        return sb.toString();

    }

}