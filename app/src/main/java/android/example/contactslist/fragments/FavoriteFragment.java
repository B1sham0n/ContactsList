package android.example.contactslist.fragments;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.example.contactslist.adapters.ContactAdapter;
import android.example.contactslist.dagger.ComponentDB;
import android.example.contactslist.dagger.DBModule;
import android.example.contactslist.dagger.DaggerComponentDB;
import android.example.contactslist.db_helpers.DBHelper;
import android.example.contactslist.db_helpers.DBHelperFavorite;
import android.example.contactslist.entities.Contact;
import android.net.Uri;
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
import android.widget.Toast;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

public class FavoriteFragment extends Fragment {

    @Inject
    @Named("favorite")
    SQLiteDatabase dbFav;

    @Inject
    DBHelperFavorite dbHelperFav;

    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private ArrayList<Contact> contactList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite, null);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ComponentDB component = DaggerComponentDB.builder()
                .dBModule(new DBModule(getActivity().getApplicationContext(), DBHelperFavorite.USER_TABLE_NAME, DBHelperFavorite.USER_DB_NAME))
                .build();
        component.inject(this);
        //dbHelperFav.deleteDB(dbFav);
        contactList = getAll(getActivity());

        try{
            setRecyclerView();
        }
        catch (Exception e){
            Toast.makeText(getActivity(), "Exceptrion: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void setRecyclerView(){
        recyclerView = getView().findViewById(R.id.recView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);//заранее знаем размер списка
        contactAdapter = new ContactAdapter(contactList.size(), contactList, getActivity().getApplicationContext());//get count contacts
        //contactAdapter.setContactsList(contactList);
        recyclerView.setAdapter(contactAdapter);


    }

    public ArrayList<Contact> getAll(Context context) {
        ArrayList<Contact> contacts = new ArrayList<>();
        Cursor c = dbFav.query(DBHelperFavorite.USER_TABLE_NAME, null, null, null,
                null, null, null);
        if(c.moveToFirst()){
            while (c.moveToNext()) {
                String name = c.getString(c.getColumnIndex(dbHelperFav.USER_COLUMN_USER_NAME));
                String phoneNumber = c.getString(c.getColumnIndex(dbHelperFav.USER_COLUMN_USER_PHONE));
                Integer id = c.getInt(c.getColumnIndex(dbHelperFav.USER_COLUMN_USER_ID));

                String photo = c.getString(c.getColumnIndex(dbHelperFav.USER_COLUMN_USER_PHOTO));

                contacts.add(new Contact(name, phoneNumber, id, photo));

                //dbHelperFav.insertToDB(dbFav, name, photo, phoneNumber);
            }
        }
        else
            return null;

        c.close();
        return contacts;
    }
}
