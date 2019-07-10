package com.example.mohammad.apufodd;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;


public class ManagerHome extends AppCompatActivity {
    ImageButton bLogout;
    Button bCorders;
    Button bAorders;
    Button bDailySale;
    Button bQr;
    Button bChat;
    Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_home);
        activity=this;
        bLogout=(ImageButton) findViewById(R.id.button6);
        bCorders=(Button) findViewById(R.id.button3);
        bAorders=(Button) findViewById(R.id.button4);
        bDailySale=(Button) findViewById(R.id.button5);
        bQr=(Button) findViewById(R.id.button17);
        bChat=(Button) findViewById(R.id.button19);
        bLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
        bCorders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent adIntent = new Intent(activity, CurrentOrders.class);
                activity.startActivity(adIntent);
            }
        });
        bAorders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent adIntent = new Intent(activity, AllOrders.class);
                activity.startActivity(adIntent);
            }
        });
        bDailySale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent adIntent = new Intent(activity, DailySale.class);
                activity.startActivity(adIntent);
            }
        });
        bQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent adIntent = new Intent(activity, ScanQr.class);
                activity.startActivity(adIntent);
            }
        });
        bChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent adIntent = new Intent(activity, ChatList.class);
                activity.startActivity(adIntent);
            }
        });

    }
}
