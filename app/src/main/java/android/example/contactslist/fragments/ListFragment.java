package android.example.contactslist.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.example.contactslist.activities.MainActivity;
import android.example.contactslist.constants.Constants;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.example.contactslist.R;
import android.widget.EditText;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

public class ListFragment extends Fragment {

    @Inject
    @Named("peoples")
    SQLiteDatabase db;

    @Inject
    DBHelper dbHelper;

    private SwipeRefreshLayout srlListContacts;

    @Inject
    public ListFragment() {
        // Required empty public constructor
    }
    //TODO: в бд нужно сохранять только когда обновляю список, удалить бд
    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private ArrayList<Contact> contactList;
    private EditText searchContact;
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
        contactAdapter = new ContactAdapter(contactList.size(), contactList, getActivity().getApplicationContext(), Constants.Names.getContactsDB());//get count contacts
        //contactAdapter.setContactsList(contactList);
        recyclerView.setAdapter(contactAdapter);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ComponentDB component = DaggerComponentDB.builder()
                .dBModule(new DBModule(getActivity().getApplicationContext()))
                .build();
        component.inject(this);
        Cursor c = db.query(DBHelper.USER_TABLE_NAME, null, null, null,
                null, null, null);
        //dbHelper.deleteDB(db);
        System.out.println("cursor = " + c.getCount());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
                if(c.getCount() == 0)
                    contactList = getAll(getActivity(), true);
                else
                    contactList = getAll(getActivity(), false);
            }
            else{
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 100);
            }
        }
        setRecyclerView();

        srlListContacts = view.findViewById(R.id.srlListContacts);
        srlListContacts.setRefreshing(false);
        srlListContacts.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                contactAdapter.clearContactsList();
                dbHelper.deleteDB(db);
                contactList = getAll(getActivity(), true);
                setRecyclerView();
                srlListContacts.setRefreshing(false);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Cursor c = db.query(DBHelper.USER_TABLE_NAME, null, null, null,
                null, null, null);
        if(c.getCount() == 0)
            contactList = getAll(getActivity(), true);
        else
            contactList = getAll(getActivity(), false);
    }

    public ArrayList<Contact> getAll(Context context, Boolean writeToDB) {
        ArrayList<Contact> contacts = new ArrayList<>();
        Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        if(phones.moveToFirst()){
            while (phones.moveToNext()) {
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Integer id = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));

                String photo = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                contacts.add(new Contact(name, phoneNumber, id, photo));

                if(writeToDB)
                    dbHelper.insertToDB(db, name, photo, phoneNumber);
            }
        }
        else
            return null;

        phones.close();
        return contacts;
    }

}
