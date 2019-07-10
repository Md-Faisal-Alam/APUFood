package com.example.mohammad.apufodd;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditFood extends AppCompatActivity {
    Activity activity;
    ListView lv;
    FirebaseFirestore db;
    final String[] docId = {""};
    TextView fName;
    TextView fDes;
    TextView fPrice;
    TextView av;
    Button br;
    ImageView img;
    List<String> food_list;
    List<String> food_img;
    TextView search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food);
        activity=this;
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        lv = (ListView) findViewById(R.id.ListView1);

        fName=(TextView) findViewById(R.id.editText7);
        fDes=(TextView) findViewById(R.id.editText10);
        fPrice=(TextView) findViewById(R.id.editText12);
        av=(TextView) findViewById(R.id.editText18);
        br=(Button) findViewById(R.id.button20);
        img=(ImageView) findViewById(R.id.imageView4);
        final Button bUpdate=(Button) findViewById(R.id.button8);
        final Button bDelete=(Button) findViewById(R.id.button9);
        final ListAdapter[] listAdapter = new ListAdapter[1];
        br.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
            }
        });
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        search=(TextView) findViewById(R.id.editText77);
        db.collection("foods")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int userType=0;
                            food_list = new ArrayList<String>();
                            food_img=new ArrayList<String>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                food_list.add(document.getString("name").toString());
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

//                            search.addTextChangedListener(new TextWatcher() {
//                                @Override
//                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                                }
//
//                                @Override
//                                public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                                }
//
//                                @Override
//                                public void afterTextChanged(Editable s) {
//                                  //  String sText=search.getText().toString();
//                                   // arrayAdapter.getFilter().filter(sText);
//                                    String sText=search.getText().toString();
//                                    arrayAdapter.getFilter().filter(sText);
//                                    lv.setFilterText(sText);
//
//                                }
//                            });

                            if(lv.getCount()>0) {
                                lv.setItemChecked(0, true);
                                getItemDetails();

                            }

                        } else {
                            Log.w("aba", "Error getting documents.", task.getException());
                        }
                    }
                });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getItemDetails();
            }
        });
        bUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nName=fName.getText().toString();
                String nDes=fDes.getText().toString();
                String nsPrice=fPrice.getText().toString();
                if(nsPrice.length()<1) nsPrice="0";
                double nPrice= Double.parseDouble(nsPrice);
                String sAv=av.getText().toString();
                if(sAv.length()<1) sAv="0";
                final Double av=Double.parseDouble(sAv);

                BitmapDrawable drawable = (BitmapDrawable) img.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,bos);
                byte[] bb = bos.toByteArray();
                final String image = com.firebase.client.utilities.Base64.encodeBytes(bb);

                Map<String, Object> data = new HashMap<>();
                data.put("name", nName);
                data.put("des",nDes);
                data.put("price",nPrice);
                data.put("av",av);
                data.put("image",image);
                db.collection("foods").document(docId[0]).set(data, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                new AlertDialog.Builder(activity)
                                        .setTitle("Success!")
                                        .setMessage("The food information has been updated successfully.")
                                        .setPositiveButton("OK", null).show();
                                updateListView();
                            }
                        });
            }
        });
        bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("foods").document(docId[0]).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                new AlertDialog.Builder(activity)
                                        .setTitle("Success!")
                                        .setMessage("The food has been deleted successfully.")
                                        .setPositiveButton("OK", null).show();
                                updateListView();

                            }
                        });
            }
        });
    }
    public void getItemDetails()
    {
        int p = lv.getCheckedItemPosition();
        if(p!=ListView.INVALID_POSITION) {
            int tindex=lv.getFirstVisiblePosition();
            //final String foodName = ((TextView) lv.getChildAt(p- tindex)).getText().toString();
            //TextView textView = (TextView) lv.getAdapter().getView(p, null, lv);
            final String foodName=food_list.get(p);//textView.getText().toString();
            db.collection("foods")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if(document.getString("name").toString().equals(foodName)) {
                                        fName.setText(foodName);
                                        fDes.setText(document.getString("des"));
                                        fPrice.setText(document.getDouble("price").toString());
                                        if(document.getDouble("av")!=null)
                                            av.setText(document.getDouble("av").toString());
                                        else
                                            av.setText("0");
                                        String sImage=document.getString("image");
                                        if(sImage!=null &&  sImage.length()>0) {
                                            byte[] decodedString = Base64.decode(sImage, Base64.DEFAULT);
                                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                            img.setImageBitmap(decodedByte);
                                        }
                                        else
                                        {
                                            img.setImageResource(R.drawable.apuicon);
                                        }
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
    public void updateListView()
    {
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
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                food_list.add(document.getString("name").toString());
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
                                getItemDetails();
                            }


                        } else {
                            Log.w("aba", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        final String[] INITIAL_PERMS = {android.Manifest.permission.READ_EXTERNAL_STORAGE};
        final int REQUEST = 1337;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
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
