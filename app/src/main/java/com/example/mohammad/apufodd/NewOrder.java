package com.example.mohammad.apufodd;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewOrder extends AppCompatActivity {
    class orderStruct {
        public String Name;
        public Double Price;
        public Double num;
    }
    List<orderStruct> order_list = new ArrayList<orderStruct>();
    ListView lv;
    ListView lv2;
    FirebaseFirestore db;
    Activity activity;
    TextView tNum;
    TextView tFoodTot;
    TextView tTotalAmount;
    TextView tBalance;
    Button bAdd;
    Button bPay;
    String SelectedFoodName;
    Double SelectedFoodPrice;
    Double numberOfSelectedFood;
    Double SelectedFoodAv;
    String userName;
    Double balance;
    Double totalAmount;
    TextView tName;
    ImageButton delete;
    List<String> food_list;
    List<String> food_img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);
        final SharedPreferences prefs= this.getSharedPreferences(
                "pref", Context.MODE_PRIVATE);
        lv = (ListView) findViewById(R.id.ListView4);
        lv2=(ListView) findViewById(R.id.ListView5);
        tNum=(TextView) findViewById(R.id.editText14);
        tName=(TextView) findViewById(R.id.textView12);
        tFoodTot=(TextView) findViewById(R.id.textView15);
        bAdd=(Button) findViewById(R.id.button13);
        bPay=(Button) findViewById(R.id.button14);
        tTotalAmount=(TextView) findViewById(R.id.textView16);
        tBalance=(TextView) findViewById(R.id.textView13);
        activity =this;
        db = FirebaseFirestore.getInstance();
        userName=prefs.getString("username","");
        balance=Double.parseDouble(prefs.getString("balance","1"));
        tBalance.setText("RM "+balance);
        String sName=prefs.getString("name","");
        tName.setText(sName.toUpperCase());
        delete=(ImageButton) findViewById(R.id.imageButton);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv2.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        final ListAdapter[] listAdapter = new ListAdapter[1];
        db.collection("foods")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int userType=0;
                            food_list = new ArrayList<String>();
                            food_img=new ArrayList<String>();
                            // List<String> food_list = new ArrayList<String>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Double tav=0.0;
                                if(document.getDouble("av")!=null)
                                    tav=document.getDouble("av");
                                food_list.add(document.getString("name").toString()+"  /  RM"+document.getDouble("price")+"  /  X"+tav);
                                if(document.getString("image")!=null)
                                    food_img.add(document.getString("image"));
                                else
                                    food_img.add(" ");
                            }
                            //Collections.sort(food_list);
                            listAdapter[0] = new ListAdapter(activity , food_list, food_img);
                            /*final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                                    (activity, android.R.layout.simple_list_item_single_choice, food_list);*/

                            lv.setAdapter(listAdapter[0]);

                            if(lv.getCount()>0) {
                                lv.setItemChecked(0, true);
                                calculateSelectedPrice();
                            }

                        } else {
                            Log.w("aba", "Error getting documents.", task.getException());
                        }
                    }
                });
        tNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                calculateSelectedPrice();
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.getFocusables(position);
                view.setSelected(true);
                calculateSelectedPrice();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int p = lv2.getCheckedItemPosition();
                if(p!=ListView.INVALID_POSITION) {

                    TextView textView = (TextView) lv2.getAdapter().getView(p, null, lv);
                    String rowText=textView.getText().toString();
                    int index=rowText.indexOf("  /  ");
                    String FoodName=rowText.substring(0,index);
                    List<String> food_list = new ArrayList<String>();
                    Double totalAmount_=0.0;
                    for(int i=0;i<order_list.size();i++)
                    {
                        if(order_list.get(i).Name.equals(FoodName))
                            order_list.remove(i);
                    }
                    for(int i=0;i<order_list.size();i++)
                    {
                        totalAmount_+=(order_list.get(i).num*order_list.get(i).Price);
                        food_list.add(order_list.get(i).Name+"  /  "+order_list.get(i).num+"  /  RM"+order_list.get(i).num*order_list.get(i).Price);
                    }
                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                            (activity, android.R.layout.simple_list_item_single_choice,food_list);
                    lv2.setAdapter(arrayAdapter);
                    totalAmount=totalAmount_;
                    tTotalAmount.setText("Total Amount: RM "+totalAmount_);

                }else{
                    Toast.makeText(activity, "Nothing Selected..", Toast.LENGTH_LONG).show();
                }
            }
        });
        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(numberOfSelectedFood==0)
                {
                    new AlertDialog.Builder(activity)
                            .setTitle("Failed!")
                            .setMessage("Please insert the number of order.")
                            .setPositiveButton("OK", null).show();
                    return;
                }
                orderStruct temp= new orderStruct();
                temp.Name=SelectedFoodName;
                temp.Price=SelectedFoodPrice;
                temp.num=numberOfSelectedFood;
                order_list.add(temp);
                Double totalAmount_=0.0;
                List<String> food_list = new ArrayList<String>();
                for(int i=0;i<order_list.size();i++)
                {
                    totalAmount_+=(order_list.get(i).num*order_list.get(i).Price);
                    food_list.add(order_list.get(i).Name+"  /  "+order_list.get(i).num+"  /  RM"+order_list.get(i).num*order_list.get(i).Price);
                }
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                        (activity, android.R.layout.simple_list_item_single_choice,food_list);
                lv2.setAdapter(arrayAdapter);
                totalAmount=totalAmount_;
                tTotalAmount.setText("Total Amount: RM"+totalAmount_);

            }
        });
        bPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(totalAmount>balance)
                {
                    new AlertDialog.Builder(activity)
                            .setTitle("Failed!")
                            .setMessage("Not enough credit.")
                            .setPositiveButton("OK", null).show();
                    return;
                }
                Date curr= new Date();
                Timestamp now= new Timestamp(curr);



                Map<String, Object> docData = new HashMap<>();
                docData.put("username",userName);
                docData.put("total",totalAmount);
                docData.put("datetime",now);
                docData.put("status","Ordered");
                Map<String, Object> nestedOrder = new HashMap<>();
                for(int i=0;i<order_list.size();i++)
                {
                    Map<String, Object> temp = new HashMap<>();
                    temp.put("name",order_list.get(i).Name);
                    temp.put("num",order_list.get(i).num);
                    temp.put("price",order_list.get(i).Price);
                    nestedOrder.put("Item"+(i+1),temp);
                    updateAv(order_list.get(i).Name,order_list.get(i).num);
                }
                docData.put("order",nestedOrder);
                db.collection("orders").add(docData)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                updateBalance();
                                new AlertDialog.Builder(activity)
                                        .setTitle("Success!")
                                        .setMessage("The order has been submited successfully.")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                activity.finish();
                                            }
                                        }).show();

                            }
                        });
            }
        });

    }
    public void updateBalance()
    {
        final SharedPreferences prefs= this.getSharedPreferences(
                "pref", Context.MODE_PRIVATE);
        final Double newBalance=balance-totalAmount;
        prefs.edit().putString("balance",newBalance.toString()).apply();
        final String[] docId = {""};
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getString("username").equals(userName)) {
                                    docId[0] =document.getId();
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("credit", newBalance);
                                    db.collection("users").document(docId[0]).set(data, SetOptions.merge())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {


                                                }
                                            });
                                }
                            }

                        } else {
                            Log.w("aba", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
    public void updateAv(final String name, final Double num)
    {
        db.collection("foods")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int userType=0;
                            food_list = new ArrayList<String>();
                            food_img=new ArrayList<String>();
                            // List<String> food_list = new ArrayList<String>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.get("name").toString().equals(name)) {
                                    Double tav = 0.0;
                                    if (document.getDouble("av") != null)
                                        tav = document.getDouble("av");
                                    Double nAv=tav-num;
                                    String docId=document.getId();
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("av", nAv);
                                    db.collection("foods").document(docId).set(data, SetOptions.merge())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {


                                                }
                                            });
                                }
                            }


                        } else {
                            Log.w("aba", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
    public void calculateSelectedPrice()
    {
        int p = lv.getCheckedItemPosition();
        if(p!=ListView.INVALID_POSITION) {
            int tindex=lv.getFirstVisiblePosition();
            //TextView textView = (TextView) lv.getAdapter().getView(p, null, lv);
            String rowText=food_list.get(p);//textView.getText().toString();
            int index=rowText.indexOf("  /  RM");
            SelectedFoodName=rowText.substring(0,index);
            int index2=rowText.indexOf("  /  X");
            String sPrice=rowText.substring(index+7,index2);
            SelectedFoodPrice=Double.parseDouble(sPrice);
            String sNum=tNum.getText().toString();
            String sAv=rowText.substring(index2+6,rowText.length());
            if(sNum.length()<1)
                sNum="0";
            if(sAv.length()<1)
                SelectedFoodAv=0.0;
            else
                SelectedFoodAv=Double.parseDouble(sAv);

            numberOfSelectedFood=Double.parseDouble(sNum);
            if(numberOfSelectedFood>SelectedFoodAv)
            {
                new AlertDialog.Builder(activity)
                        .setTitle("Warning!")
                        .setMessage("Only "+SelectedFoodAv + " Is Available.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                numberOfSelectedFood=SelectedFoodAv;
                tNum.setText(SelectedFoodAv+"");
            }
            tFoodTot.setText("Total: RM "+numberOfSelectedFood*SelectedFoodPrice);
        }else{
            Toast.makeText(activity, "Nothing Selected..", Toast.LENGTH_LONG).show();
        }
    }

}
