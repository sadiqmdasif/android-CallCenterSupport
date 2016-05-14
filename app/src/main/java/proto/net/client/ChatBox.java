package proto.net.client;

import android.app.Activity;
import android.app.NativeActivity;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.method.DateTimeKeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.icddrb.callcentersupport.R;

import navigationDrawer.NavigationActivity;

/**
 * Created by SadiqMdAsif on 03-May-16.
 */
public class ChatBox extends NavigationActivity {

    static EditText chatBox;
    static EditText chatBoxInput;
    Button btnSend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.chatbox, frameLayout);
        setContentView(R.layout.chatbox);

        chatBox=(EditText) findViewById(R.id.editTextChatBox);
        chatBoxInput =(EditText) findViewById(R.id.editTextChatInput);
        chatBoxInput.setText("");
        btnSend = (Button) findViewById(R.id.buttonSendChatMsg);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidNetCommunicationClientActivity.onSendRequest(chatBoxInput.getText().toString());
                writeToChatBox(chatBoxInput.getText().toString());
            }
        });
    }

    public static EditText getChatBoxInput() {
        return chatBoxInput;
    }

    public static void writeToChatBox(String msg) {
        chatBox.append(msg);
    }

}
