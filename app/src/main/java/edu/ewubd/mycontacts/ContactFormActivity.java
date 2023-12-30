package edu.ewubd.mycontacts;
import android.Manifest;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ContactFormActivity extends AppCompatActivity {




    EditText etname, etemail, ethomePhone, etofficePhone;
    ImageView ivphoto;
    TextView tvChoosePhoto, tvTakePhoto;
    Button cancel, save;

    private ContactDB db;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_PICK_IMAGE = 102;
    private static final int REQUEST_CAMERA_PERMISSION = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_form);

        etname = findViewById(R.id.etName);
        etemail = findViewById(R.id.etEmail);
        ethomePhone = findViewById(R.id.etHmPhone);
        etofficePhone = findViewById(R.id.etOfPhone);

        tvChoosePhoto = findViewById(R.id.btnChoose);
        tvTakePhoto = findViewById(R.id.btnTakePh);

        ivphoto =(ImageView) findViewById(R.id.ivProfile);


        cancel = findViewById(R.id.btnCancel);
        save = findViewById(R.id.btnSave);

        db = new ContactDB(this);


        Intent i = getIntent();
        String name = i.getStringExtra("name");
        String email = i.getStringExtra("email");
        String homePhone = i.getStringExtra("homePhone");
        String officePhone = i.getStringExtra("officePhone");
        byte[] photoData = i.getByteArrayExtra("photo");

        etname.setText(name);
        etemail.setText(email);
        ethomePhone.setText(homePhone);
        etofficePhone.setText(officePhone);

        if (photoData != null) {
            Bitmap photoBitmap = BitmapFactory.decodeByteArray(photoData, 0, photoData.length);
            ivphoto.setImageBitmap(photoBitmap);
        }




        tvTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TakePicture();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveContact();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });
        tvChoosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });
    }

    private void choosePicture() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUEST_PICK_IMAGE);

    }

    private void TakePicture() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            }
            else {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePicture.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
                }
            }
        } else {
            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePicture.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
            }
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                Bitmap resizedBitmap = resizeBitmap(originalBitmap, 200, 200);
                ivphoto.setImageBitmap(resizedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Bitmap resizedBitmap = resizeBitmap(imageBitmap, 100, 100);
            ivphoto.setImageBitmap(resizedBitmap);
        }
    }
    private Bitmap resizeBitmap(Bitmap bitmap, int width, int height) {
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }


    private void saveContact() {
        String name = etname.getText().toString();
        String email = etemail.getText().toString();
        String homePhone = ethomePhone.getText().toString();
        String officePhone = etofficePhone.getText().toString();

        SharedPreferences preferences = getSharedPreferences("myPrefsLogin", MODE_PRIVATE);
        String userEmail = preferences.getString("Email", "");

        BitmapDrawable drawable = (BitmapDrawable) ivphoto.getDrawable();
        if (drawable != null) {
            String photo = convertImageToBase64(ivphoto);

            if (validateInput()) {
                if(db.eventExists(homePhone))
                {
                    db.updateContact(userEmail, name, email, homePhone, officePhone, photo);
                    Toast.makeText(ContactFormActivity.this, "Contact updated successfully", Toast.LENGTH_SHORT).show();
                }
                else {
                    db.insertContact(userEmail, name, email, homePhone, officePhone, photo);
                    Toast.makeText(ContactFormActivity.this, "Contact saved successfully", Toast.LENGTH_SHORT).show();
                }

                Intent i = new Intent(ContactFormActivity.this, ContactListActivity.class);
                startActivity(i);
            }

            else {
                Toast.makeText(ContactFormActivity.this, "Invalid database insertion", Toast.LENGTH_SHORT).show();
            }
        }

        else {
            if (validateInput()) {
                db.insertContact(userEmail, name, email, homePhone, officePhone, null);
                Toast.makeText(ContactFormActivity.this, "Contact saved successfully", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(ContactFormActivity.this, ContactListActivity.class);
                startActivity(i);
            }

            else {
                Toast.makeText(ContactFormActivity.this, "Invalid database insertion", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private String convertImageToBase64(ImageView imageView) {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap imageBitmap = drawable.getBitmap();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private boolean isValidPhoneNum(String phoneNumber) {
        return Patterns.PHONE.matcher(phoneNumber).matches();
    }

    private boolean validateInput() {
        String name = etname.getText().toString();
        String email = etemail.getText().toString();
        String homePhone = ethomePhone.getText().toString();
        String officePhone = etofficePhone.getText().toString();

        if (name.isEmpty()) {
            Toast.makeText(ContactFormActivity.this, "Name is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!emailValidator(email)){
            Toast.makeText(ContactFormActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
            return false;

        }
        if (homePhone.isEmpty()) {
            Toast.makeText(ContactFormActivity.this, "Home phone is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isValidPhoneNum(homePhone)) {
            Toast.makeText(ContactFormActivity.this, "Invalid home phone number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (officePhone.isEmpty()) {
            Toast.makeText(ContactFormActivity.this, "Office phone is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isValidPhoneNum(officePhone)) {
            Toast.makeText(ContactFormActivity.this, "Invalid office phone number", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public boolean emailValidator(String email) {

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false;
        }

        return true;
    }


}
