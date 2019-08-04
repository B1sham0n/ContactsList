package android.example.contactslist.dagger;

import android.example.contactslist.ContactActivity;
import android.example.contactslist.MainActivity;
import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = {RetrofitModule.class, DBModule.class})
public interface Component {
    void inject(MainActivity mainActivity);

    void inject(ContactActivity contactActivity);

/*
    @ApplicationContext
    Context getContext();
*/
}
