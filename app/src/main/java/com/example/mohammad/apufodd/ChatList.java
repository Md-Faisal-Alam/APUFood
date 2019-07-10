package com.example.mohammad.apufodd;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class ChatList extends AppCompatActivity {
    Activity activity;
    ListView lv;
    FirebaseFirestore db;
    TextView search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        activity=this;
        db = FirebaseFirestore.getInstance();
        lv = (ListView) findViewById(R.id.ListView9);
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
                                    student_list.add(document.getString("username").toString()+"  /  "+document.getString("name").toString());
                                }
                            }
                            Collections.sort(student_list);
                            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                                    (activity, android.R.layout.simple_list_item_1, student_list);
                            lv.setAdapter(arrayAdapter);

                            search.addTextChangedListener(new TextWatcher() { //search operation
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
                                }
                            });



                        } else {
                            Log.w("aba", "Error getting documents.", task.getException());
                        }
                    }
                });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getUser();
            }
        });


    }
    public void getUser()
    {
        int p = lv.getCheckedItemPosition();
        if(p!=ListView.INVALID_POSITION) {
            TextView textView = (TextView) lv.getAdapter().getView(p, null, lv);
            String textString=textView.getText().toString();
            final String userName=textString.substring(0,textString.indexOf("  /  "));
            Intent adIntent = new Intent(activity, ChatManager.class);
            adIntent.putExtra("userName",userName);
            activity.startActivity(adIntent);
        }else{
            Toast.makeText(activity, "Nothing Selected..", Toast.LENGTH_LONG).show();
        }
    }
}
