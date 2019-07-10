package com.example.mohammad.apufodd;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.annotations.Nullable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentHomePage extends AppCompatActivity {

    Button bNewOrder;
    Button bOrderHistory;
    Button bChat;
    Activity activity;
    Double balance;
    String sName;
    String userName;
    TextView tBalance;
    TextView tName;
    ListView lv;
    ImageButton bLogout;
    FirebaseFirestore db;
    @Override
    public void onResume(){
        super.onResume();
        init();
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home_page);
        init();
    }
    public void init()
    {
        activity=this;
        db = FirebaseFirestore.getInstance();
        bNewOrder=(Button) findViewById(R.id.button11);
        bOrderHistory=(Button) findViewById(R.id.button15);

        tBalance=(TextView) findViewById(R.id.textView10);
        lv=(ListView) findViewById(R.id.ListView3);
        final SharedPreferences prefs = this.getSharedPreferences(
                "pref", Context.MODE_PRIVATE);
        userName=prefs.getString("username","");
        tName=(TextView)findViewById(R.id.textView9);
        balance=Double.parseDouble(prefs.getString("balance","1"));
        sName=prefs.getString("name","");
        bLogout=(ImageButton) findViewById(R.id.button12);
        tName.setText(sName.toUpperCase());
        tBalance.setText("RM "+balance);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        bChat=(Button) findViewById(R.id.button16);
        bChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent adIntent = new Intent(activity, ChatStudent.class);
                adIntent.putExtra("userName",userName);
                activity.startActivity(adIntent);
            }
        });
        bLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
        bNewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent adIntent = new Intent(activity, NewOrder.class);
                adIntent.putExtra("userName",userName);
                adIntent.putExtra("balance",balance);
                activity.startActivity(adIntent);
            }
        });
        bOrderHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent adIntent = new Intent(activity, OrderHistory.class);

                activity.startActivity(adIntent);
            }
        });
        db.collection("orders")
                .whereEqualTo("username",userName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> order_list = new ArrayList<String>();
                            List<String> docId=new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (!document.getString("status").equals("Delivered"))
                                {
                                    String datetime = document.getDate("datetime").toString();
                                    int indextemp=datetime.indexOf(" GMT");
                                    if(indextemp==-1)
                                        indextemp=datetime.indexOf(" UTC");
                                    datetime = datetime.substring(0, indextemp);
                                    //datetime = datetime.substring(0, datetime.indexOf(" GMT+"));
                                    order_list.add(datetime + "  /   " + document.getString("status"));
                                    docId.add(document.getId());
                                    final DocumentReference docRef = db.collection("orders").document(document.getId());
                                    docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                            @Nullable FirebaseFirestoreException e) {
                                            if (e != null) {
                                                return;
                                            }

                                            if (snapshot != null && snapshot.exists()) {
                                                if (snapshot.get("status").toString().equals("Ready"))
                                                {
                                                    /*NotificationCompat.Builder builder =
                                                            new NotificationCompat.Builder(activity)
                                                                    .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                                                                    .setContentTitle("APU Cafeteria")   //this is the title of notification
                                                                    .setColor(101)
                                                                    .setContentText("Your Food Is Ready To Collect.");
                                                    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                                    manager.notify(0, builder.build());*/
                                                    NotificationCompat.Builder mBuilder =
                                                            new NotificationCompat.Builder(activity.getApplicationContext(), "notify_001");
                                                    Intent ii = new Intent(activity.getApplicationContext(), MainActivity.class);
                                                    PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, ii, 0);

                                                    NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
                                                    bigText.bigText("Your Food Is Ready To Collect.");
                                                    bigText.setBigContentTitle("APU Cafeteria");
                                                    bigText.setSummaryText("Text in detail");

                                                    mBuilder.setContentIntent(pendingIntent);
                                                    mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
                                                    mBuilder.setContentTitle("Your Title");
                                                    mBuilder.setContentText("Your text");
                                                    mBuilder.setPriority(Notification.PRIORITY_MAX);
                                                    mBuilder.setStyle(bigText);

                                                    NotificationManager mNotificationManager =
                                                            (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);


                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                        NotificationChannel channel = new NotificationChannel("notify_001",
                                                                "Channel human readable title",
                                                                NotificationManager.IMPORTANCE_DEFAULT);
                                                        mNotificationManager.createNotificationChannel(channel);
                                                    }

                                                    mNotificationManager.notify(0, mBuilder.build());
                                                }
                                            }
                                        }
                                    });
                                }
                            }

                            Collections.sort(order_list,Collections.reverseOrder());
                            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                                    (activity, android.R.layout.simple_list_item_1, order_list);
                            lv.setAdapter(arrayAdapter);
                            if(lv.getCount()>0) {
                                lv.setItemChecked(0, true);
                            }
                        } else {
                            Log.w("aba", "Error getting documents.", task.getException());
                        }
                    }
                });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getOrderDetails();
            }
        });
    }
    public void getOrderDetails()
    {
        int p = lv.getCheckedItemPosition();
        if(p!=ListView.INVALID_POSITION) {
            TextView textView = (TextView) lv.getAdapter().getView(p, null, lv);
            String dateString=textView.getText().toString();
            final String finalDateString=dateString.substring(0,dateString.indexOf(" /"));
            db.collection("orders")
                    .whereEqualTo("username",userName)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if(document.getDate("datetime").toString().contains(finalDateString)) {
                                        String orderId=document.getId();
                                        Intent adIntent = new Intent(activity, OrderDetails.class);
                                        adIntent.putExtra("id",orderId);
                                        activity.startActivity(adIntent);
                                        break;
                                    }
                                }
                            } else {
                                Log.w("aba", "Error getting documents.", task.getException());
                            }
                        }
                    });

        }else{
            Toast.makeText(activity, "Nothing Selected..", Toast.LENGTH_LONG).show();
        }
    }
}
