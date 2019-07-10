package com.example.mohammad.apufodd;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity=this;
        Button bSignIn = (Button) findViewById(R.id.button);
        final EditText fUserName = (EditText)findViewById(R.id.editText);
        final EditText fPassword=(EditText)findViewById(R.id.editText2);
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        final TextView reg=(TextView)findViewById(R.id.textView2);
        final SharedPreferences prefs = this.getSharedPreferences(
                "pref", Context.MODE_PRIVATE);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(activity, Register.class);
                activity.startActivity(myIntent);
            }
        });
        bSignIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //  handler code here
                final String userName=fUserName.getText().toString();
                final String password=fPassword.getText().toString();
                final boolean[] check = {false};
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    check[0] =true;
                                    int userType=0;
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if(document.getString("username").toString().equals(userName) &&
                                                document.getString("password").toString().equals(password)) {
                                            if (document.getString("type").toString().equals("admin")) {

                                                userType = 1;
                                                Intent adIntent = new Intent(activity, AdminHomePage.class);

                                                activity.startActivity(adIntent);
                                            }else if (document.getString("type").toString().equals("student")) {
                                                userType = 2;
                                                Intent adIntent = new Intent(activity, StudentHomePage.class);
                                                prefs.edit().putString("username", userName).apply();
                                                prefs.edit().putString("name", document.getString("name")).apply();
                                                prefs.edit().putString("balance",document.getDouble("credit").toString()).apply();

                                                activity.startActivity(adIntent);
                                            }
                                            else if (document.getString("type").toString().equals("manager")) {
                                                Intent adIntent = new Intent(activity, ManagerHome.class);
                                                activity.startActivity(adIntent);
                                                userType = 3;
                                            }
                                            fUserName.setText("");
                                            fPassword.setText("");
                                            break;
                                        }
                                    }
                                    if(userType==0)
                                    {
                                        new AlertDialog.Builder(activity)
                                                .setTitle("Failed!")
                                                .setMessage("Wrong username or password.")
                                                .setPositiveButton("OK", null).show();
                                    }
                                } else {
                                    Log.w("aba", "Error getting documents.", task.getException());
                                }
                            }
                        });
            }
        });
    }

}
