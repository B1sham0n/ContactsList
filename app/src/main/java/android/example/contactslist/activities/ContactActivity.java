package android.example.contactslist.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.example.contactslist.R;
import android.example.contactslist.dagger.ComponentDB;
import android.example.contactslist.dagger.DBModule;

import android.example.contactslist.dagger.DaggerComponentDB;
import android.example.contactslist.db_helpers.DBHelper;
import android.example.contactslist.db_helpers.DBHelperFavorite;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import javax.inject.Inject;
import javax.inject.Named;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.SEND_SMS;

public class ContactActivity extends AppCompatActivity {

    @Inject
    @Named("peoples")
    SQLiteDatabase db;

    @Inject
    @Named("favorite")
    SQLiteDatabase dbFav;

    @Inject
    DBHelper dbHelper;

    @Inject
    DBHelperFavorite dbHelperFavorite;

    private String message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);


        ComponentDB component = DaggerComponentDB.builder()
                .dBModule(new DBModule(getApplicationContext()))
                .build();
        component.inject(this);


        TextView tvPhone = findViewById(R.id.tvPhoneNumber);
        TextView tvUsername = findViewById(R.id.tvUsername);
        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvName = findViewById(R.id.tvContactName);

        ImageView ivPhoto = findViewById(R.id.ivContactPhoto);

        Button btnCall = findViewById(R.id.btnCall);
        Button btnMessage = findViewById(R.id.btnMessage);

        Integer id = getIntent().getIntExtra("id", 1);
        Integer i = 1;

        Cursor c;
        String tabName = getIntent().getStringExtra("tab");
        System.out.println(tabName);


        if(tabName.equals("peoples"))
            c = db.query(DBHelper.USER_TABLE_NAME, null, null, null,
                null, null, null);
        else
            c = dbFav.query(DBHelperFavorite.USER_TABLE_NAME, null, null, null,
                null, null, null);

        String name = "Name", username = "username", email = "email", photo = "", phone = "phone";
        if (c.moveToFirst()) {
            do {
                i = c.getInt(c.getColumnIndex(DBHelper.USER_COLUMN_USER_ID));
                if(i.equals(id)) {
                    name = c.getString(c.getColumnIndex(DBHelper.USER_COLUMN_USER_NAME));
                    //username = c.getString(c.getColumnIndex(DBHelper.USER_COLUMN_USER_USERNAME));
                    //email = c.getString(c.getColumnIndex(DBHelper.USER_COLUMN_USER_EMAIL));
                    photo = c.getString(c.getColumnIndex(DBHelper.USER_COLUMN_USER_PHOTO));
                    phone = c.getString(c.getColumnIndex(DBHelper.USER_COLUMN_USER_PHONE));
                }
                //System.out.println("name: " + c.getString(c.getColumnIndex(DBHelper.USER_COLUMN_USER_NAME)));
            } while (c.moveToNext());
        }
        tvName.setText(name);
        tvPhone.setText(phone);
        ///tvEmail.setText(email);
        //tvUsername.setText(username);

        try {
            if(!photo.contains("content://com.android.contacts/contacts/")) {
                Uri uri1 = Uri.parse(photo);
                ivPhoto.setImageURI(uri1);
                System.out.println("uri = " + photo + " id = " + id);
            }
            else
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ivPhoto.setImageDrawable(getApplicationContext().getDrawable(R.drawable.default_photo_contact));
                }
            }

        }catch (Exception e){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ivPhoto.setImageDrawable(getApplicationContext().getDrawable(R.drawable.default_photo_contact));
            }
        }
        btnCall.setTag(phone);
        tvPhone.setTag(phone);
        btnCall.setOnClickListener(callListener);
        tvPhone.setOnClickListener(callListener);
        btnMessage.setOnClickListener(messageListener);
        btnMessage.setTag(phone);
        System.out.println("its okey");
    }
    private void sendMessage(String sms, String phoneNum){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNum, null, sms, null, null);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{SEND_SMS}, 1);
            }
        }
    }
    View.OnClickListener callListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + view.getTag()));

            if (ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                startActivity(callIntent);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{CALL_PHONE}, 1);
                }
            }
        }
    };
    View.OnClickListener messageListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {

            AlertDialog.Builder builder = new AlertDialog.Builder(ContactActivity.this);
            builder.setTitle("Title");

            View viewInflated = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog, null, false);

            final EditText input = (EditText) viewInflated.findViewById(R.id.etMessage);

            builder.setView(viewInflated);

            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    message = input.getText().toString();
                    sendMessage(message, view.getTag().toString());
                }
            });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
    };
}

