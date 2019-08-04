package android.example.contactslist.adapters;

import android.content.Context;
import android.content.Intent;
import android.example.contactslist.ContactActivity;
import android.example.contactslist.entities.Contact;
import android.example.contactslist.R;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder>{

    private Integer numberItems;
    private ArrayList<Contact> contactList;

    public ContactAdapter(Integer numberItems, ArrayList<Contact> contacts) {
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

    class ContactViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView phone;
        ImageView photo;

        private Integer id;

        public ContactViewHolder(@NonNull final View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.listName);
            phone = itemView.findViewById(R.id.listPhone);
            photo = itemView.findViewById(R.id.listPhoto);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Intent intent = null;// = new Intent(itemView.getContext(), ContactActivity.class);
                    Intent intent = getIntent(new Intent());
                    itemView.getContext().startActivity(intent);
                }
            });
        }

        private Intent getIntent(Intent intent) {
            intent = new Intent(itemView.getContext(), ContactActivity.class);
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

            }catch (Exception e){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    photo.setImageDrawable(itemView.getContext().getDrawable(R.drawable.default_photo_contact));
                }
            }


            }

    }
}
