package com.example.mohammad.apufodd;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Recharge extends AppCompatActivity {
    Activity activity;
    ListView lv;
    FirebaseFirestore db;
    final String[] docId = {""};
    final double[] userBal={0};
    TextView fbalance;
    TextView fRehc;
    TextView search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        activity=this;
        db = FirebaseFirestore.getInstance();
        lv = (ListView) findViewById(R.id.ListView2);
        fbalance=(TextView) findViewById(R.id.editText11);
        fbalance.setFocusable(false);

        fRehc=(TextView) findViewById(R.id.editText13);
        final Button bRech=(Button) findViewById(R.id.button10);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        search=(TextView) findViewById(R.id.editText15);

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int userType=0;
                            List<String> student_list = new ArrayList<String>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getString("type").equals("student")) {
                                    student_list.add(document.getString("username").toString());
                                }
                            }
                            Collections.sort(student_list);
                            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                                    (activity, android.R.layout.simple_list_item_single_choice, student_list);
                            lv.setAdapter(arrayAdapter);

                            search.addTextChangedListener(new TextWatcher() {//Search option
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                }
                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                }
                                @Override
                                public void afterTextChanged(Editable s) {
                                    String sText=search.getText().toString();
                                    arrayAdapter.getFilter().filter(sText);
                                    lv.setFilterText(sText);

                                }
                            });
                            if(lv.getCount()>0) {
                                lv.setItemChecked(0, true);
                                getBalance();
                            }

                        } else {
                            Log.w("aba", "Error getting documents.", task.getException());
                        }
                    }
                });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getBalance();
            }
        });
        bRech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nsRch=fRehc.getText().toString();
                if(nsRch.length()<1)
                    nsRch="0";
                double rech=Double.parseDouble(nsRch);
                if(rech==0)
                {
                    new AlertDialog.Builder(activity)
                            .setTitle("Failed!")
                            .setMessage("Please insert the amount of recharge.")
                            .setPositiveButton("OK", null).show();
                    return;
                }
                if(docId[0].length()<2)
                {
                    new AlertDialog.Builder(activity)
                            .setTitle("Failed!")
                            .setMessage("Please select the user.")
                            .setPositiveButton("OK", null).show();
                    return;
                }
                double newBalance=userBal[0]+rech;
                Map<String, Object> data = new HashMap<>();
                data.put("credit", newBalance);

                db.collection("users").document(docId[0]).set(data, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                new AlertDialog.Builder(activity)
                                        .setTitle("Success!")
                                        .setMessage("The APU card has been recharged successfully.")
                                        .setPositiveButton("OK", null).show();
                                getBalance();
                                fRehc.setText("");
                            }
                        });
            }
        });
    }
    public void getBalance()
    {
        int p = lv.getCheckedItemPosition();
        if(p!=ListView.INVALID_POSITION) {
            int tindex=lv.getFirstVisiblePosition();
            TextView textView = (TextView) lv.getAdapter().getView(p, null, lv);
            final String userName=textView.getText().toString();
            db.collection("users")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if(document.getString("username").toString().equals(userName)) {
                                        userBal[0]=document.getDouble("credit");
                                        fbalance.setText(document.getDouble("credit").toString());
                                        docId[0]=document.getId();
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
