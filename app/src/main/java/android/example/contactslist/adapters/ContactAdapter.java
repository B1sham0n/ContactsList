package android.example.contactslist.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.example.contactslist.activities.ContactActivity;
import android.example.contactslist.constants.Constants;
import android.example.contactslist.dagger.ComponentDB;
import android.example.contactslist.dagger.DBModule;
import android.example.contactslist.dagger.DaggerComponentDB;
import android.example.contactslist.db_helpers.DBHelper;
import android.example.contactslist.db_helpers.DBHelperFavorite;
import android.example.contactslist.entities.Contact;
import android.example.contactslist.R;
import android.net.Uri;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;


public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder>{


    private String EMPTY_TAG = "empty";
    private String FAVORITE_TAG = "favorite";

    private Integer numberItems;
    private ArrayList<Contact> contactList;
    private String nameTab;
    private Context context;

    public ContactAdapter(Integer numberItems, ArrayList<Contact> contacts, Context context, String nameTab) {
        this.numberItems = numberItems;
        contactList = new ArrayList<>();
        this.contactList.addAll(contacts);
        this.nameTab = nameTab;
    }
    public void setContactsList(ArrayList<Contact> newList){
        this.contactList.addAll(newList);
        notifyDataSetChanged();
    }
    public void clearContactsList(){
        this.contactList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0 && !nameTab.equals(Constants.Names.getFavoriteDB()))
            return 0;
        else
            return 1;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = null;
        switch (viewType) {
            case 0:
                view = inflater.inflate(R.layout.search_item, parent, false);
                break;
            case 1:
                view = inflater.inflate(R.layout.contact_list_item, parent, false);
                break;
        }
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        holder.bind(position);
        //System.out.println(getItemCount() + " count");
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public void filteredList(ArrayList<Contact> filteredList){
        contactList.clear();
        contactList.addAll(filteredList);
        System.out.println(contactList.size() + " size");
        notifyDataSetChanged();
    }
    public class ContactViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView phone;
        ImageView photo;
        Button btnStar;
        EditText search;
        TextView tvCount;

        @Inject
        DBHelperFavorite dbHelperFavorite;

        @Inject
        DBHelper dbHelper;

        @Inject
        @Named("favorite")
        SQLiteDatabase dbFav;

        @Inject
        @Named("peoples")
        SQLiteDatabase db;

        private Integer id;

        public ContactViewHolder(@NonNull final View itemView) {
            super(itemView);

            ComponentDB component = DaggerComponentDB.builder()
                    .dBModule(new DBModule(itemView.getContext()))
                    .build();
            component.inject(this);

            name = itemView.findViewById(R.id.listName);
            phone = itemView.findViewById(R.id.listPhone);
            photo = itemView.findViewById(R.id.listPhoto);
            btnStar = itemView.findViewById(R.id.listBtnFavorite);
            search = itemView.findViewById(R.id.searchListContacts);
            tvCount = itemView.findViewById(R.id.countOfContacts);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Intent intent = null;// = new Intent(itemView.getContext(), ContactActivity.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if(btnStar.getTag() == EMPTY_TAG){
                            btnStar.setBackground(itemView.getContext().getDrawable(R.drawable.ic_favorite_full));
                            btnStar.setTag(FAVORITE_TAG);

                            setNewFavorite(btnStar.getId());
                        }
                        else {
                            btnStar.setBackground(itemView.getContext().getDrawable(R.drawable.ic_favorite_empty));
                            btnStar.setTag(EMPTY_TAG);

                            dbHelperFavorite.removeFromDB(dbFav, contactList.get(btnStar.getId()).getNameContact());
                        }
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Intent intent = getIntent();
                    itemView.getContext().startActivity(intent);
                    return true;
                }
            });
        }
        private void setNewFavorite(Integer id){
            String name = null, phone = null, photo = null;

            name = contactList.get(id).getNameContact();
            phone = contactList.get(id).getPhoneContact();
            photo = contactList.get(id).getUriPhotoContact();

            if(name != null){
                dbHelperFavorite.insertToDB(dbFav, name, photo, phone);
            }
        }
        private Boolean compareWithFavorite(Integer id){
            String name_contact = contactList.get(id).getNameContact();
            Cursor c = dbFav.query(dbHelperFavorite.USER_TABLE_NAME, null, null, null,
                    null, null, null);

            String name;
            if(c.moveToFirst()){
                do{
                    name = c.getString(c.getColumnIndex(dbHelperFavorite.USER_COLUMN_USER_NAME));
                    if(name_contact.equals(name))
                        return true;
                }while (c.moveToNext());
            }
            return false;
        }
        private Intent getIntent() {
            Intent intent = new Intent(itemView.getContext(), ContactActivity.class);
            intent.putExtra("id", getId() + 1);//в БД индексация с 1
            intent.putExtra("tab", nameTab);
            System.out.println(getId() + " = id; " + nameTab + " = nameTab");
            return intent;
        }
        public void putId(Integer i){
            this.id = i;
        }
        private Integer getId(){
            return id;
        }
        private void filter(String text) {
            ArrayList<Contact> filteredList = new ArrayList<>();
            for(Contact contact : contactList){
                if(contact.getNameContact().toLowerCase().contains(text.toLowerCase())){
                    filteredList.add(contact);
                }
            }
            filteredList(filteredList);
        }
        void bind(Integer id) {
            switch (ContactAdapter.this.getItemViewType(id)) {
                case 0:
                    //searchContact = findViewById(R.id.searchListContacts);
                    search.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            filter(editable.toString());
                        }
                    });
                    tvCount.setText(context.getResources().getString(R.string.count_of_contacts) + " " + getItemCount());
                break;
                case 1:
                    putId(id);
                    name.setText(contactList.get(id).getNameContact());
                    phone.setText(contactList.get(id).getPhoneContact());
                    btnStar.setId(id);//это число потом берем из списка как id контакта при добавлении в избр

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (compareWithFavorite(id)) {
                            btnStar.setBackground(itemView.getContext().getDrawable(R.drawable.ic_favorite_full));
                            btnStar.setTag(FAVORITE_TAG);
                        } else {
                            btnStar.setBackground(itemView.getContext().getDrawable(R.drawable.ic_favorite_empty));
                            btnStar.setTag(EMPTY_TAG);
                        }
                    }

                    String uri = contactList.get(id).getUriPhotoContact();
                    try {
                        if (!uri.contains("content://com.android.contacts/contacts/")) {
                            Uri uri1 = Uri.parse(contactList.get(id).getUriPhotoContact());
                            photo.setImageURI(uri1);
                            System.out.println("uri = " + uri + " id = " + id);
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                photo.setImageDrawable(itemView.getContext().getDrawable(R.drawable.default_photo_contact));
                            }
                        }
                    } catch (Exception e) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            photo.setImageDrawable(itemView.getContext().getDrawable(R.drawable.default_photo_contact));
                        }
                    }
                break;
            }

        }

    }
}
