package android.example.contactslist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.example.contactslist.dagger.ComponentDB;
import android.example.contactslist.dagger.DBModule;
import android.example.contactslist.dagger.DaggerComponentDB;
import android.example.contactslist.db_helpers.DBHelper;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import static android.Manifest.permission.CALL_PHONE;

public class ContactActivity extends AppCompatActivity {

    @Inject
    SQLiteDatabase db;

    @Inject
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        ComponentDB component = DaggerComponentDB.builder()
                .dBModule(new DBModule(getApplicationContext(), DBHelper.USER_TABLE_NAME, DBHelper.USER_DB_NAME))
                .build();
        component.inject(this);

        TextView tvPhone = findViewById(R.id.tvPhoneNumber);
        TextView tvUsername = findViewById(R.id.tvUsername);
        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvName = findViewById(R.id.tvContactName);

        ImageView ivPhoto = findViewById(R.id.ivContactPhoto);

        Button btnCall = findViewById(R.id.btnCall);

        Integer id = getIntent().getIntExtra("id", 1);
        Integer i = 1;

        Cursor c = db.query(DBHelper.USER_TABLE_NAME, null, null, null,
                null, null, null);
        System.out.println(c.getCount());
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
        System.out.println("its okey");
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
}
