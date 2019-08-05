package android.example.contactslist.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.example.contactslist.ContactActivity;
import android.example.contactslist.dagger.ComponentDB;
import android.example.contactslist.dagger.DBModule;
import android.example.contactslist.dagger.DaggerComponentDB;
import android.example.contactslist.db_helpers.DBHelper;
import android.example.contactslist.db_helpers.DBHelperFavorite;
import android.example.contactslist.entities.Contact;
import android.example.contactslist.R;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    public ContactAdapter(Integer numberItems, ArrayList<Contact> contacts, Context context) {
        this.numberItems = numberItems;
        contactList = new ArrayList<>();
        this.contactList.addAll(contacts);
    }
    public void setContactsList(ArrayList<Contact> newList){
        this.contactList.addAll(newList);
        notifyDataSetChanged();
    }
    public void clearContactsList(){
        this.contactList.clear();
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //return null;
        Context context = parent.getContext();
        //Integer layoutIdForListItem = R.layout.contact_list_item;

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.contact_list_item, parent, false);

        //тут переопределить поля?
        //viewHolder.name.setText("name");
        //viewHolder.phone.setText("11-22-33");

        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return numberItems;
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView phone;
        ImageView photo;
        Button btn;

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
                    .dBModule(new DBModule(itemView.getContext(), DBHelperFavorite.USER_TABLE_NAME, DBHelperFavorite.USER_DB_NAME))
                    .build();
            component.inject(this);

            name = itemView.findViewById(R.id.listName);
            phone = itemView.findViewById(R.id.listPhone);
            photo = itemView.findViewById(R.id.listPhoto);
            btn = itemView.findViewById(R.id.listBtnFavorite);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Intent intent = null;// = new Intent(itemView.getContext(), ContactActivity.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if(btn.getTag() == EMPTY_TAG){
                            btn.setBackground(itemView.getContext().getDrawable(R.drawable.ic_favorite_full));
                            btn.setTag(FAVORITE_TAG);

                            setNewFavorite(btn.getId());
                        }
                        else {
                            btn.setBackground(itemView.getContext().getDrawable(R.drawable.ic_favorite_empty));
                            btn.setTag(EMPTY_TAG);

                            dbHelperFavorite.removeFromDB(dbFav, contactList.get(btn.getId()).getNameContact());
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
            System.out.println(getId() + " = id");
            return intent;
        }
        public void putId(Integer i){
            this.id = i;
        }
        private Integer getId(){
            return id;
        }
        void bind(Integer id) {
            putId(id);
            name.setText(contactList.get(id).getNameContact());
            phone.setText(contactList.get(id).getPhoneContact());
            btn.setId(id);//это число потом берем из списка как id контакта при добавлении в избр

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (compareWithFavorite(id)) {
                    btn.setBackground(itemView.getContext().getDrawable(R.drawable.ic_favorite_full));
                    btn.setTag(FAVORITE_TAG);
                }
                else{
                    btn.setBackground(itemView.getContext().getDrawable(R.drawable.ic_favorite_empty));
                    btn.setTag(EMPTY_TAG);
                }
            }

            String uri = contactList.get(id).getUriPhotoContact();
            try {
                if(!uri.contains("content://com.android.contacts/contacts/")) {
                    Uri uri1 = Uri.parse(contactList.get(id).getUriPhotoContact());
                    photo.setImageURI(uri1);
                    System.out.println("uri = " + uri + " id = " + id);
                }
                else
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        photo.setImageDrawable(itemView.getContext().getDrawable(R.drawable.default_photo_contact));
                    }
                }
            } catch (Exception e){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    photo.setImageDrawable(itemView.getContext().getDrawable(R.drawable.default_photo_contact));
                }
            }


        }

    }
}
