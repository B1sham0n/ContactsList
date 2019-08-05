package android.example.contactslist.dagger;

import android.example.contactslist.ContactActivity;
import android.example.contactslist.adapters.ContactAdapter;
import android.example.contactslist.fragments.FavoriteFragment;
import android.example.contactslist.fragments.ListFragment;

import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = {DBModule.class})
public interface ComponentDB {
    void inject(ContactActivity contactActivity);

    void inject(ListFragment listFragment);

    void inject(FavoriteFragment favoriteFragment);

    void inject(ContactAdapter.ContactViewHolder contactViewHolder);
}
