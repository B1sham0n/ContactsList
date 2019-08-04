package android.example.contactslist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.example.contactslist.adapters.ContactAdapter;


import android.example.contactslist.dagger.Component;
import android.example.contactslist.dagger.DBModule;
import android.example.contactslist.dagger.DaggerComponent;
import android.example.contactslist.dagger.RetrofitModule;
import android.example.contactslist.entities.Contact;
import android.example.contactslist.retrofit.InfoUser;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Toast;

import java.util.ArrayList;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private ArrayList<Contact> contactList;

    @Inject
    Call<InfoUser> infoUser;

    @Inject
    SQLiteDatabase db;

    @Inject
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        Component component = DaggerComponent.builder()
                .retrofitModule(new RetrofitModule(10))
                .dBModule(new DBModule(this, DBHelper.USER_TABLE_NAME, DBHelper.USER_DB_NAME))
                .build();
        component.inject(this);

        //readDB();
        //dbHelper.deleteDB(db);
        //findUsers();
        /*final SwipeRefreshLayout swipe = findViewById(R.id.swipe);
        swipe.setRefreshing(false);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe.setRefreshing(true);

                LinearLayout parent = findViewById(R.id.parentLayout);
                parent.removeAllViews();

                dbHelper.deleteDB(db);
                findUsers();

                swipe.setRefreshing(false);
            }
        });
        */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
               contactList = getAll(this);
            }
            else{
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 100);
            }
        }
        setRecyclerView();
    }


    private void setRecyclerView(){
        recyclerView = findViewById(R.id.recView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);//заранее знаем размер списка
        contactAdapter = new ContactAdapter(contactList.size(), contactList);//get count contacts
        //contactAdapter.setContactsList(contactList);
        recyclerView.setAdapter(contactAdapter);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        contactList = getAll(this);
    }

    public ArrayList<Contact> getAll(Context context) {
        ArrayList<Contact> contacts = new ArrayList<>();
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        if(phones.moveToFirst()){
            while (phones.moveToNext()) {
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Integer id = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));

                String photo = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                contacts.add(new Contact(name, phoneNumber, id, photo));
                dbHelper.insertToDB(db, name, photo, phoneNumber);
            }
        }
        else
            return null;

        phones.close();
        return contacts;
    }

    private void findUsers() {
        infoUser.clone().enqueue(new Callback<InfoUser>() {//без .clone нельзя обращаться с одному call много раз
            @Override
            public void onResponse(Call<InfoUser> call, Response<InfoUser> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Error code: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                //listUser = response.body().getResults();
                String name, username, email, photo, phone;
                Integer id;
                for (int i = 0; i < response.body().getResults().size(); i++) {

                    name = response.body().getResults().get(i).getName().getFirst() + " " + response.body().getResults().get(i).getName().getLast();
                    username = response.body().getResults().get(i).getLogin().getUsername();
                    email = response.body().getResults().get(i).getEmail();
                    photo = response.body().getResults().get(i).getPicture().getLarge();
                    phone = response.body().getResults().get(i).getPhone();
                    id = i;

                    addUserInList(name, username, email, photo, phone, id);

                    dbHelper.insertToDB(db, response.body().getResults().get(i).getName().getFirst() + " " + response.body().getResults().get(i).getName().getLast(),
                            response.body().getResults().get(i).getPicture().getLarge(),
                            response.body().getResults().get(i).getPhone()
                    );
                }
                Cursor c = db.query(DBHelper.USER_TABLE_NAME, null, null, null,
                        null, null, null);
                System.out.println(c.getCount());
            }

            @Override
            public void onFailure(Call<InfoUser> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("its fail " + t.getMessage());
            }
        });
    }

    private void readDB() {
        //чтение из БД в отдельном потоке
        Runnable run = new Runnable() {
            @Override
            public void run() {
                Cursor c = db.query(DBHelper.USER_TABLE_NAME, null, null, null,
                        null, null, null);
                System.out.println(c.getCount());
                String name, username, email, photo, phone;
                Integer id;
                if (c.moveToFirst()) {
                    do {
                        name = c.getString(c.getColumnIndex(DBHelper.USER_COLUMN_USER_NAME));
                        username = c.getString(c.getColumnIndex(DBHelper.USER_COLUMN_USER_USERNAME));
                        email = c.getString(c.getColumnIndex(DBHelper.USER_COLUMN_USER_EMAIL));
                        photo = c.getString(c.getColumnIndex(DBHelper.USER_COLUMN_USER_PHOTO));
                        phone = c.getString(c.getColumnIndex(DBHelper.USER_COLUMN_USER_PHONE));
                        id = c.getInt(c.getColumnIndex(DBHelper.USER_COLUMN_USER_ID));
                        addUserInList(name, username, email, photo, phone, id);

                        //System.out.println("name: " + c.getString(c.getColumnIndex(DBHelper.USER_COLUMN_USER_NAME)));
                    } while (c.moveToNext());
                }
            }
        };
        run.run();
    }

    private void addUserInList(String name, String username, String email, String photoURL, String phone, final Integer id) {
        /*
        //System.out.println("this " + name);
        View child = View.inflate(getApplicationContext(), R.layout.contact_inflater, null);
        LinearLayout parent = findViewById(R.id.parentLayout);

        TextView tvName = child.findViewById(R.id.tvName);
        tvName.setText(name);

        TextView tvUsername = child.findViewById(R.id.tvUsername);
        tvUsername.setText(username);

        TextView tvEmail = child.findViewById(R.id.tvEmail);
        tvEmail.setText(email);

        ImageView ivPhoto = child.findViewById(R.id.userPhoto);
        Picasso.get()
                .load(photoURL)
                .into(ivPhoto);

        child.setOnLongClickListener(new View.OnLongClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public boolean onLongClick(View view) {
                System.out.println("long click");
                /*
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phone));

                if (ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(callIntent);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{CALL_PHONE}, 1);
                    }
                }

                Intent intent = new Intent(getApplicationContext(), ContactActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
                return true;
            }
        });
        parent.addView(child);
*/
    }
}
