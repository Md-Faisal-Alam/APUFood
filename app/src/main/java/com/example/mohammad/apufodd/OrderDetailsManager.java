package com.example.mohammad.apufodd;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDetailsManager extends AppCompatActivity {
    String orderId;
    Activity activity;
    ListView lv;
    FirebaseFirestore db;
    TextView tStatus;
    TextView tTotalAmount;
    TextView tDate;
    TextView tName;
    Button bReady;
    public final static int QRcodeWidth = 500 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_manager);
        activity=this;
        lv=(ListView) findViewById(R.id.ListView6);
        orderId= getIntent().getStringExtra("id");
        db = FirebaseFirestore.getInstance();
        tStatus=(TextView) findViewById(R.id.textView20);
        tDate=(TextView) findViewById(R.id.textView19);
        tTotalAmount=(TextView)findViewById(R.id.textView18);
        final SharedPreferences prefs = this.getSharedPreferences(
                "pref", Context.MODE_PRIVATE);

        tName=(TextView)findViewById(R.id.textView21);

        bReady=(Button) findViewById(R.id.button18);
        getOrderDetails();
        bReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> data = new HashMap<>();
                if(bReady.getText().equals("READY"))
                    data.put("status", "Ready");
                else
                    data.put("status", "Delivered");
                db.collection("orders").document(orderId).set(data, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                activity.finish();
                            }
                        });
            }
        });

    }
    public void getOrderDetails()
    {
        DocumentReference docRef=db.collection("orders").document(orderId);
                    docRef.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                        tStatus.setText("Order Status: "+document.getString("status"));
                                        if(document.getString("status").equals("Ordered")) {
                                            bReady.setVisibility(View.VISIBLE);
                                            bReady.setText("READY");
                                        }
                                        else if(document.getString("status").equals("Ready")) {
                                            bReady.setVisibility(View.VISIBLE);
                                            bReady.setText("DELIVERED");
                                        }
                                        else
                                            bReady.setVisibility(View.INVISIBLE);
                                        tTotalAmount.setText("Total Amount:"+"\nRM"+document.getDouble("total"));
                                        String datetime=document.getDate("datetime").toString();
                                        int indextemp=datetime.indexOf(" GMT");
                                        if(indextemp==-1)
                                            indextemp=datetime.indexOf(" UTC");
                                        datetime=datetime.substring(0,indextemp);
                                        tDate.setText(datetime);
                                        tName.setText(document.getString("username"));
                                        Map<String, HashMap> orders= (Map<String, HashMap>) document.get("order");
                                        List<String> food_list = new ArrayList<String>();
                                        for(Map.Entry<String, HashMap> entry : orders.entrySet()) {
                                            String key = entry.getKey();
                                            HashMap value = entry.getValue();
                                            int num=(int) Math.round((Double) value.get("num"));
                                            food_list.add(num+" X  "+value.get("name")+"  /      RM"+value.get("price"));
                                            // In case, another loop.
                                        }
                                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                                                (activity, android.R.layout.simple_list_item_1, food_list);
                                        lv.setAdapter(arrayAdapter);
                            }
                            else {
                                Log.w("aba", "Error getting documents.", task.getException());
                            }
                        }
                    });
    }
}
