package android.example.contactslist.retrofit;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JsonPlaceHolderApi {

    @GET("api")
    //Flowable<InfoUser> getUser(@Query("results") Integer results);
    Call<InfoUser> getUser(@Query("results") Integer results);
}
