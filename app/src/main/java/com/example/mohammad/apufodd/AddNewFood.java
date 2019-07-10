package com.example.mohammad.apufodd;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.utilities.Base64;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AddNewFood extends AppCompatActivity {


    Activity activity;
    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_food);
        activity=this;
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        final Button bAdd=(Button) findViewById(R.id.button7);
        final EditText fName= (EditText) findViewById(R.id.editText6);
        final EditText fDes=(EditText) findViewById(R.id.editText8);
        final EditText fPrice=(EditText) findViewById(R.id.editText9);
        final EditText fAv=(EditText) findViewById(R.id.editText17);
        final Button bBro=(Button) findViewById(R.id.button20);

        img=(ImageView) findViewById(R.id.imageView4);
        bBro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
            }
        });
        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name=fName.getText().toString();
                final String des=fDes.getText().toString();
                String sPrice=fPrice.getText().toString();
                String sAv=fAv.getText().toString();
                if(sPrice.length()<1) sPrice="0";
                if(sAv.length()<1) sAv="0";
                final Double price= Double.parseDouble(sPrice);
                final int av=Integer.parseInt(sAv);

                BitmapDrawable drawable = (BitmapDrawable) img.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,bos);
                byte[] bb = bos.toByteArray();
                final String image = Base64.encodeBytes(bb);


                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                final boolean[] check = {false};
                if(name.length()<1 || des.length()<1 || price==0)
                {
                    new AlertDialog.Builder(activity)
                            .setTitle("Failed!")
                            .setMessage("Please fill all the fields.")
                            .setPositiveButton("OK", null).show();
                    return;
                }
                db.collection("foods")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    check[0]=false;
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if(document.getString("name").toString().equals(name)) {
                                            check[0]=true;
                                            break;
                                        }
                                    }
                                    if(check[0]==true)
                                    {
                                        new AlertDialog.Builder(activity)
                                                .setTitle("Failed!")
                                                .setMessage("The food already exists.")
                                                .setPositiveButton("OK", null).show();
                                    }
                                    else
                                    {
                                        Map<String, Object> docData = new HashMap<>();
                                        docData.put("name",name);
                                        docData.put("des",des);
                                        docData.put("price",price);
                                        docData.put("av",av);
                                        docData.put("image",image);
                                        db.collection("foods").add(docData)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        new AlertDialog.Builder(activity)
                                                                .setTitle("Success!")
                                                                .setMessage("The new food has been added successfully.")
                                                                .setPositiveButton("OK", null).show();
                                                        activity.finish();
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
    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        final String[] INITIAL_PERMS = {Manifest.permission.READ_EXTERNAL_STORAGE};
        final int REQUEST = 1337;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED) {
                requestPermissions(INITIAL_PERMS, REQUEST);
            }
        }
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                img.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();

            }

        }else {
        }
    }
}
