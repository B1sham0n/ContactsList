package android.example.contactslist.dagger;

import android.example.contactslist.ContactActivity;
import android.example.contactslist.fragments.ListFragment;

import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = {DBModule.class})
public interface ComponentDB {
    void inject(ContactActivity contactActivity);

    void inject(ListFragment listFragment);
}
