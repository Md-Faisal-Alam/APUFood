package com.example.mohammad.apufodd;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;



import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;


public class Register extends AppCompatActivity {
    Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        activity=this;

        Button bSignUp = (Button) findViewById(R.id.button2);
        final EditText fName = (EditText)findViewById(R.id.editText3);
        //final EditText email = (EditText)findViewById(R.id.editText6);          //for email
        final EditText fUserName=(EditText)findViewById(R.id.editText5);
        final EditText fPassword=(EditText)findViewById(R.id.editText4);
        final EditText fEmail=(EditText)findViewById(R.id.editText16);
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        closeKeyboard();

        bSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                final String userName=fUserName.getText().toString();
                final String password=fPassword.getText().toString();
                final String Name=fName.getText().toString();
                final String Email=fEmail.getText().toString();
                final boolean[] check = {false};
                if(userName.length()<1 || Name.length()<1 || password.length()<1)
                {
                    new AlertDialog.Builder(activity)
                            .setTitle("Failed!")
                            .setMessage("Please fill all the field.")
                            .setPositiveButton("OK", null).show();
                    return;
                }
                db.collection("users")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    int userType=0;
                                    check[0]=false;
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if(document.getString("username").toString().equals(userName)) {
                                            check[0]=true;
                                            break;
                                        }
                                    }
                                    if(check[0]==true)
                                    {
                                        new AlertDialog.Builder(activity)
                                                .setTitle("Failed!")
                                                .setMessage("The username is already exists.")
                                                .setPositiveButton("OK", null).show();
                                    }
                                    else
                                    {
                                        Map<String, Object> docData = new HashMap<>();
                                        docData.put("name",Name);
                                        docData.put("username",userName);
                                        docData.put("password",password);
                                        docData.put("emai",Email);
                                        docData.put("type","student");
                                        docData.put("credit",0);
                                        db.collection("users").add(docData)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        new AlertDialog.Builder(activity)
                                                                .setTitle("Success!")
                                                                .setMessage("The new account has been created successfully.")
                                                                .setPositiveButton("OK", null).show();

                                                        Thread thread = new Thread(new Runnable(){
                                                            public void run() {
                                                                try {
                                                                    sendmail(Email,userName,password,Name);
                                                                    activity.finish();
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        });
                                                        thread.start();
                                                    }
                                                });
                                    }
                                } else {
                                    Log.w("aba", "Error getting documents.", task.getException());
                                }
                            }
                        });
            }
        });
    }


    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public void sendmail(String mail, String userName,String passw, String name)
    {
        //   final String username = "apufood2018@gmail.com";
        //  final String password = "!QAZxsw2";

        try {
            String from="apufood2018@gmail.com"; // email and password
            String pass="!QAZxsw2";


            Properties props = System.getProperties();
            String host = "smtp.gmail.com";
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.user", from);
            props.put("mail.smtp.password", pass);
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");

            Session session = Session.getDefaultInstance(props);
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(from));
            InternetAddress toAddress = new InternetAddress(mail);


            // To get the array of addresses
            message.addRecipient(Message.RecipientType.TO, toAddress);

            String msg="Dear "+name+","
                    + "\n\n Your account has been created successfully. Thank you for registering your account at APU Cafeteria. Now You can proceed to use all the functions. And below credentials."+
                    "\n\nFull Name: "+name+"\nUserName: "+userName+"\nPassword: "+passw+"\n\nThis message is from APU Cafeteria Food Ordering System. This is a system generated Email. Please do not reply." +
                    "\n\nRegards,"+ "\nMD, Faisal Alma"+ "\nAPU Cafeteria Developer";
            message.setSubject("APU Cafeteria Account");
            message.setText(msg);
            Transport transport = session.getTransport("smtp");
            transport.connect(host, from, pass);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (MessagingException ex) {
            //Logger.getLogger(Student.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
