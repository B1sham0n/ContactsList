package android.example.contactslist.fragments;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.example.contactslist.adapters.ContactAdapter;
import android.example.contactslist.constants.Constants;
import android.example.contactslist.dagger.ComponentDB;
import android.example.contactslist.dagger.DBModule;
import android.example.contactslist.dagger.DaggerComponentDB;
import android.example.contactslist.db_helpers.DBHelperFavorite;
import android.example.contactslist.entities.Contact;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.example.contactslist.R;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

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
    private SwipeRefreshLayout srlFavorite;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite, null);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ComponentDB component = DaggerComponentDB.builder()
                .dBModule(new DBModule(getActivity().getApplicationContext()))
                .build();
        component.inject(this);

        //dbHelperFav.deleteDB(dbFav);
        contactList = new ArrayList<>();
        try{
            System.out.println("size: " + contactList.size());
            setRecyclerView(view);
        }
        catch (Exception e){
            Toast.makeText(getActivity(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        srlFavorite = view.findViewById(R.id.srlFavorite);
        srlFavorite.setRefreshing(false);
        srlFavorite.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srlFavorite.setRefreshing(true);
                setRecyclerView(view);
                srlFavorite.setRefreshing(false);
            }
        });
    }
    private void setRecyclerView(View view){
        contactList = getAll(getActivity());
        recyclerView = view.findViewById(R.id.recView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);//заранее знаем размер списка
        contactAdapter = new ContactAdapter(contactList.size(), contactList, Objects.requireNonNull(getActivity()).getApplicationContext(), Constants.Names.getFavoriteDB());//get count contacts
        //contactAdapter.setContactsList(contactList);
        recyclerView.setAdapter(contactAdapter);
    }

    public ArrayList<Contact> getAll(Context context) {
        ArrayList<Contact> contacts = new ArrayList<>();
        Cursor c = dbFav.query(DBHelperFavorite.USER_TABLE_NAME, null, null, null,
                null, null, null);

        while (c.moveToNext()) {
            String name = c.getString(c.getColumnIndex(DBHelperFavorite.USER_COLUMN_USER_NAME));
            String phoneNumber = c.getString(c.getColumnIndex(DBHelperFavorite.USER_COLUMN_USER_PHONE));
            Integer id = c.getInt(c.getColumnIndex(DBHelperFavorite.USER_COLUMN_USER_ID));

            String photo = c.getString(c.getColumnIndex(DBHelperFavorite.USER_COLUMN_USER_PHOTO));

            contacts.add(new Contact(name, phoneNumber, id, photo));
            System.out.println(name);
                //dbHelperFav.insertToDB(dbFav, name, photo, phoneNumber);
        }
        c.close();
        return contacts;
    }
}
