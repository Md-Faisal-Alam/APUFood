package com.example.mohammad.apufodd;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class AdminHomePage extends AppCompatActivity {
    Activity activity;
    Button bDailySaleAdmin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home_page);
        Button bNewFood=(Button)findViewById(R.id.button3);
        Button bEditFood=(Button) findViewById(R.id.button4);
        Button bRecharge=(Button) findViewById(R.id.button5);
        bDailySaleAdmin=(Button) findViewById(R.id.button50);
        ImageButton bLogout=(ImageButton) findViewById(R.id.button6);
        activity=this;
        bLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
        bNewFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(activity, AddNewFood.class);
                activity.startActivity(myIntent);
            }
        });
        bEditFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(activity, EditFood.class);
                 activity.startActivity(myIntent);
            }
        });
        bRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(activity, Recharge.class);
                activity.startActivity(myIntent);
            }
        });
        bDailySaleAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent adIntent = new Intent(activity, DailySaleAdmin.class);
                activity.startActivity(adIntent);
            }
        });


    }
}
