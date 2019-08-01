package android.example.contactslist.dagger;

import android.example.contactslist.MainActivity;
import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = {RetrofitModule.class, DBModule.class})
public interface Component {
    void inject(MainActivity mainActivity);

/*
    @ApplicationContext
    Context getContext();
*/
}
