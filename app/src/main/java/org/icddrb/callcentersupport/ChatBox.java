package org.icddrb.callcentersupport;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by SadiqMdAsif on 03-May-16.
 */
public class ChatBox extends Activity {

    static TextView chatBox;
    static EditText chatBoxInput;
    Button btnSend;

    public static EditText getChatBoxInput() {
        return chatBoxInput;
    }

    public static void writeToChatBox(String msg) {
        try {
            chatBox.append(msg);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatbox);

        chatBox = (TextView) findViewById(R.id.editTextChatBox);
        chatBoxInput = (EditText) findViewById(R.id.editTextChatInput);
        chatBoxInput.setText("");
        btnSend = (Button) findViewById(R.id.buttonSendChatMsg);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidNetCommunicationClientActivity.mMsgSendRequest(chatBoxInput.getText().toString());
                writeToChatBox(chatBoxInput.getText().toString());
            }
        });
    }

}
