package android.example.contactslist.fragments;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.example.contactslist.db_helpers.DBHelper;
import android.example.contactslist.adapters.ContactAdapter;
import android.example.contactslist.dagger.ComponentDB;
import android.example.contactslist.dagger.DBModule;
import android.example.contactslist.dagger.DaggerComponentDB;
import android.example.contactslist.entities.Contact;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.example.contactslist.R;

import java.util.ArrayList;

import javax.inject.Inject;

public class ListFragment extends Fragment {

    @Inject
    SQLiteDatabase db;

    @Inject
    DBHelper dbHelper;

    @Inject
    public ListFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private ArrayList<Contact> contactList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_contacts, container, false);
    }
        private void setRecyclerView(){
        recyclerView = getView().findViewById(R.id.recView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);//заранее знаем размер списка
        contactAdapter = new ContactAdapter(contactList.size(), contactList);//get count contacts
        //contactAdapter.setContactsList(contactList);
        recyclerView.setAdapter(contactAdapter);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ComponentDB component = DaggerComponentDB.builder()
                .dBModule(new DBModule(getActivity().getApplicationContext(), DBHelper.USER_TABLE_NAME, DBHelper.USER_DB_NAME))
                .build();
        component.inject(this);
        Cursor c = db.query(DBHelper.USER_TABLE_NAME, null, null, null,
                null, null, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
                contactList = getAll(getActivity());
            }
            else{
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 100);
            }
        }
        setRecyclerView();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        contactList = getAll(getActivity());
    }

    public ArrayList<Contact> getAll(Context context) {
        ArrayList<Contact> contacts = new ArrayList<>();
        Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

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

}
