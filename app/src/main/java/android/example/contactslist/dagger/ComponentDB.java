package android.example.contactslist.dagger;

import android.example.contactslist.ContactActivity;

import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = {DBModule.class})
public interface ComponentDB {
    void inject(ContactActivity contactActivity);
}
