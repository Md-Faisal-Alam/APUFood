package com.example.mohammad.apufodd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.WriterException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDetails extends AppCompatActivity {
    String orderId;
    Activity activity;
    ListView lv;
    FirebaseFirestore db;
    TextView tStatus;
    TextView tTotalAmount;
    TextView tDate;
    TextView tName;
    TextView tBalance;
    ImageView imageView;
    public final static int QRcodeWidth = 500 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        activity=this;
        lv=(ListView) findViewById(R.id.ListView6);
        orderId= getIntent().getStringExtra("id");
        db = FirebaseFirestore.getInstance();
        tStatus=(TextView) findViewById(R.id.textView20);
        tDate=(TextView) findViewById(R.id.textView19);
        tTotalAmount=(TextView)findViewById(R.id.textView18);
        final SharedPreferences prefs = this.getSharedPreferences(
                "pref", Context.MODE_PRIVATE);
        String name=prefs.getString("name","");
        tName=(TextView)findViewById(R.id.textView9);
        tName.setText(name.toUpperCase());

        Double balance=Double.parseDouble(prefs.getString("balance","1"));

        tBalance=(TextView) findViewById(R.id.textView10);
        tBalance.setText("RM "+balance);
        getOrderDetails();
        imageView=(ImageView) findViewById(R.id.imageView3);
        try {
            Bitmap bitmap = TextToImageEncode(orderId);

            imageView.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.Black):getResources().getColor(R.color.zxing_transparent);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
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
                                        tTotalAmount.setText("Total Amount: RM"+document.getDouble("total"));
                                        String datetime=document.getDate("datetime").toString();
                                        int indextemp=datetime.indexOf(" GMT");
                                        if(indextemp==-1)
                                            indextemp=datetime.indexOf(" UTC");
                                        datetime=datetime.substring(0,indextemp);
                                        tDate.setText(datetime);
                                        Map<String, HashMap> orders= (Map<String, HashMap>) document.get("order");
                                        List<String> food_list = new ArrayList<String>();
                                        for(Map.Entry<String, HashMap> entry : orders.entrySet()) {
                                            String key = entry.getKey();
                                            HashMap value = entry.getValue();
                                            int num=(int) Math.round((Double) value.get("num"));
                                            food_list.add(num+"X  "+value.get("name")+"  /      RM"+value.get("price"));
                                            // In your case, another loop.
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
