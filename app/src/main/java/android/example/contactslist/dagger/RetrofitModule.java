package android.example.contactslist.dagger;

import android.example.contactslist.retrofit.InfoUser;
import android.example.contactslist.retrofit.JsonPlaceHolderApi;
import android.example.contactslist.retrofit.Results;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class RetrofitModule {

    private Integer countUsers;
    @Inject
    public RetrofitModule(Integer countUsers){
        this.countUsers = countUsers;
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(){
        return new Retrofit.Builder()
                .baseUrl("https://randomuser.me/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    Call<InfoUser> provideNameList(Retrofit retrofit){
        return retrofit.create(JsonPlaceHolderApi.class)
                .getUser(countUsers);
    }


}
