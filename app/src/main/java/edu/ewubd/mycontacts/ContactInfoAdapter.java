package edu.ewubd.mycontacts;
import android.content.Context;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class ContactInfoAdapter extends ArrayAdapter<ContactInfos>{
    private final Context context;
    private final ArrayList<ContactInfos> values;

    public ContactInfoAdapter(@NonNull Context context, @NonNull ArrayList<ContactInfos> items) {
        super(context, -1, items);
        this.context = context;
        this.values = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.contact_list_view_layout, parent, false);

        ImageView photo = rowView.findViewById(R.id.ivImage);
        TextView name = rowView.findViewById(R.id.tvName);
        TextView number = rowView.findViewById(R.id.tvNumber);


        ContactInfos e = values.get(position);
        name.setText(e.name);
        number.setText(e.homePhone);

        if (e.photo != null && !e.photo.isEmpty())
        {
            byte[] decodedString = Base64.decode(e.photo, Base64.DEFAULT);
            photo.setImageBitmap(decodeByteArray(decodedString));
        }
        else
        {
            photo.setImageDrawable(null);
        }



        return rowView;
    }
    public static Bitmap decodeByteArray(byte[] byteArray) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }



}
