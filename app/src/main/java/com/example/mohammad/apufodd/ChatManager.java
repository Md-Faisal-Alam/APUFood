package com.example.mohammad.apufodd;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;

public class ChatManager extends AppCompatActivity {

    LinearLayout layout;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    Firebase reference1, reference2;
    String userName_;
    String manager="manager";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_student);

        layout = (LinearLayout)findViewById(R.id.layout1);
        sendButton = (ImageView)findViewById(R.id.sendButton);
        messageArea = (EditText)findViewById(R.id.messageArea);
        scrollView = (ScrollView)findViewById(R.id.scrollView);

        userName_=getIntent().getStringExtra("userName");
        Firebase.setAndroidContext(this);
        reference1 = new Firebase("https://apufood-bc4bb.firebaseio.com/messages/" + manager + "_" + userName_);
        reference2 = new Firebase("https://apufood-bc4bb.firebaseio.com/messages/" + userName_ + "_" + manager);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("user", manager);
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                }
                messageArea.setText("");
            }
        });

        reference1.addChildEventListener(new com.firebase.client.ChildEventListener() {
            @Override
            public void onChildAdded(com.firebase.client.DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String userName = map.get("user").toString();

                if(userName.equals(manager)){
                    addMessageBox("You:\n" + message, 1);
                }
                else{
                    addMessageBox(userName_ + ":\n" + message, 2);
                }
            }

            @Override
            public void onChildChanged(com.firebase.client.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(com.firebase.client.DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(com.firebase.client.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(com.firebase.client.FirebaseError firebaseError) {

            }
        });
        View lastChild = scrollView.getChildAt(scrollView.getChildCount() - 1);
        int bottom = lastChild.getBottom() + scrollView.getPaddingBottom();
        int sy = scrollView.getScrollY();
        int sh = scrollView.getHeight();
        int delta = bottom - (sy + sh);
        scrollView.smoothScrollBy(0, delta);
    }

    public void addMessageBox(String message, int type){
        TextView textView = new TextView(ChatManager.this);
        textView.setText(message);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(lp);

        if(type == 1) {
            lp.setMargins(5,0,50,10);
            textView.setBackgroundResource(R.drawable.rounded_corner1);
        }
        else{
            lp.setMargins(50,0,5,10);
            textView.setBackgroundResource(R.drawable.rounded_corner2);
        }
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
        View lastChild = scrollView.getChildAt(scrollView.getChildCount() - 1);
        int bottom = lastChild.getBottom() + scrollView.getPaddingBottom();
        int sy = scrollView.getScrollY();
        int sh = scrollView.getHeight();
        int delta = bottom - (sy + sh);
        scrollView.smoothScrollBy(0, delta);
    }
}
