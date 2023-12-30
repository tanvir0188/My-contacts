package edu.ewubd.mycontacts;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ContactListActivity extends AppCompatActivity {

    Button add;
    private ListView contactList;
    private ContactDB db;
    private ArrayList<ContactInfos> contacts;
    private ContactInfoAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        add = findViewById(R.id.btnAdd);
        contactList =findViewById(R.id.lvContacts);
        contacts = new ArrayList<>();
        db = new ContactDB(this);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ContactListActivity.this, ContactFormActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    public  void onStart(){
        super.onStart();
        loadContacts();
        //loadData();

    }

    private void loadContacts() {
        contacts.clear();
        SharedPreferences preferences = getSharedPreferences("myPrefsLogin", MODE_PRIVATE);
        String userEmail =preferences.getString("Email", "");
        String query = "SELECT * FROM ContactTable WHERE user_email =?";
        String[] selectionArgs = {userEmail};
        ContactDB db = new ContactDB(this);


        Cursor rows = db.getReadableDatabase().rawQuery(query, selectionArgs);

        if (rows != null) {
            if (rows.getCount() > 0) {
                while (rows.moveToNext()) {
                    String UserEmail = rows.getString(0);
                    String name = rows.getString(1);
                    String contactEmail = rows.getString(2);
                    String homePhone= rows.getString(3);
                    String officePhone= rows.getString(4);
                    String photo = rows.getString(5);

                    ContactInfos cs = new ContactInfos (name, UserEmail, contactEmail, homePhone, officePhone, photo);
                    contacts.add(cs);


                }

            }
            rows.close();
        }
        db.close();
        adapter = new ContactInfoAdapter(this, contacts);
        contactList.setAdapter(adapter);

        contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                System.out.println(position);
                Intent i = new Intent(ContactListActivity.this, ContactFormActivity.class);
                i.putExtra("name", contacts.get(position).name);
                i.putExtra("email", contacts.get(position).email);
                i.putExtra("homePhone", contacts.get(position).homePhone);
                i.putExtra("officePhone", contacts.get(position).officePhone);
                String photo = contacts.get(position).photo;
                if (photo != null) {
                    i.putExtra("photo", Base64.decode(photo, Base64.DEFAULT));
                }

                startActivity(i);
            }
        });

        contactList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteDialog(position);
                return true;
            }
        });
    }

    private void showDeleteDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.delete_dialogue, null);
        builder.setView(view);

        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnDelete = view.findViewById(R.id.btnDelete);

        AlertDialog dialog = builder.create();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(position);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void delete(int position) {
        String homePhone = contacts.get(position).homePhone;

        ContactDB db = new ContactDB(this);
        db.deleteContact(homePhone);
        contacts.remove(position);
        adapter.notifyDataSetChanged();
        db.close();
        Toast.makeText(this, "Class-summary deleted successfully", Toast.LENGTH_SHORT).show();
    }
}
