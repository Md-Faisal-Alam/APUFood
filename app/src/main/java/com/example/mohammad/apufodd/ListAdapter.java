package com.example.mohammad.apufodd;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ListAdapter extends ArrayAdapter<String> {

    private final Activity Context;
    private final List<String> ListItemsName;
    private final List<String> ImageName;

    public ListAdapter(Activity context,  List<String> content,
                       List<String> ImageName) {

        super(context, R.layout.activity_list_adapter, content);
        // TODO Auto-generated constructor stub

        this.Context = context;
        this.ListItemsName = content;
        this.ImageName = ImageName;
    }
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = Context.getLayoutInflater();
        View ListViewSingle = inflater.inflate(R.layout.activity_list_adapter, null, true);

        TextView ListViewItems = (TextView) ListViewSingle.findViewById(R.id.textView1);
        ImageView ListViewImage = (ImageView) ListViewSingle.findViewById(R.id.imageView1);

        ListViewItems.setText(ListItemsName.get(position));
       // ListViewImage.setImageResource(ImageName.get(position));
        String sImage=ImageName.get(position);
        if(sImage!=null &&  sImage.length()>1) {
            byte[] decodedString = Base64.decode(sImage, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            ListViewImage.setImageBitmap(decodedByte);
        }
        else
        {
            ListViewImage.setImageResource(R.drawable.apuicon);
        }
        return ListViewSingle;

    };

}