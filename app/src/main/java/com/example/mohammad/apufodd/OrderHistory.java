package com.example.mohammad.apufodd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderHistory extends AppCompatActivity {
    Activity activity;
    TextView tBalance;
    TextView tName;
    ListView lv;
    FirebaseFirestore db;
    String userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        activity=this;
        db = FirebaseFirestore.getInstance();
        tName=(TextView)findViewById(R.id.textView9);
        tBalance=(TextView) findViewById(R.id.textView10);
        final SharedPreferences prefs = this.getSharedPreferences(
                "pref", Context.MODE_PRIVATE);
        double balance=Double.parseDouble(prefs.getString("balance","1"));
        String sName=prefs.getString("name","");
        tName.setText(sName.toUpperCase());
        tBalance.setText("RM "+balance);

        lv=(ListView) findViewById(R.id.ListView7);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


        userName=prefs.getString("username","");
        db.collection("orders")
                .whereEqualTo("username",userName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> order_list = new ArrayList<String>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String datetime=document.getDate("datetime").toString();
                                int indextemp=datetime.indexOf(" GMT");
                                if(indextemp==-1)
                                    indextemp=datetime.indexOf(" UTC");
                                datetime=datetime.substring(0,indextemp);
                                order_list.add(datetime+"  /   "+document.getString("status"));
                            }
                            Collections.sort(order_list,Collections.reverseOrder());
                            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                                    (activity, android.R.layout.simple_list_item_1, order_list);
                            lv.setAdapter(arrayAdapter);

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
