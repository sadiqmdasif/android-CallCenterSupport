package proto.net.client;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.icddrb.callcentersupport.R;

import navigationDrawer.NavigationActivity;

/**
 * Created by SadiqMdAsif on 03-May-16.
 */
public class ChatBox extends NavigationActivity {

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
        getLayoutInflater().inflate(R.layout.chatbox, frameLayout);
        setContentView(R.layout.chatbox);

        chatBox = (TextView) findViewById(R.id.editTextChatBox);
        chatBoxInput = (EditText) findViewById(R.id.editTextChatInput);
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

}
