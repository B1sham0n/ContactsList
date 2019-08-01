package android.example.contactslist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.example.contactslist.dagger.Component;


import android.example.contactslist.dagger.DBModule;
import android.example.contactslist.dagger.DaggerComponent;
import android.example.contactslist.dagger.RetrofitModule;
import android.example.contactslist.retrofit.InfoUser;
import android.example.contactslist.retrofit.JsonPlaceHolderApi;
import android.example.contactslist.retrofit.Results;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    @Inject
    Call<InfoUser> infoUser;

    @Inject
    SQLiteDatabase db;

    @Inject
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Component component = DaggerComponent.create();
        Component component = DaggerComponent.builder()
                .retrofitModule(new RetrofitModule(10))
                .dBModule(new DBModule(this, DBHelper.USER_TABLE_NAME, DBHelper.USER_DB_NAME))
                .build();
        component.inject(this);
        findUsers();
        final SwipeRefreshLayout swipe = findViewById(R.id.swipe);
        swipe.setRefreshing(false);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe.setRefreshing(true);
                LinearLayout parent = findViewById(R.id.parentLayout);
                parent.removeAllViews();
                findUsers();
                addUserInList("name", "username", "email", "");
                swipe.setRefreshing(false);
            }
        });
        //System.out.println("size = " + listUser.size() + " first = " + listUser.get(0).getName().getFirst());

        //final Handler handler = new Handler();
        /*Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://randomuser.me/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        //Call<InfoUser>call = jsonPlaceHolderApi.getUser(10);

        //Flowable<InfoUser> call = jsonPlaceHolderApi.getUser(10);
        /*call.toObservable()
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<InfoUser>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }
                    @Override
                    public void onNext(InfoUser infoUser) {
                        //listUser.add(infoUser.getResults().get(0));
                        for(int i = 0; i < 10; i++)
                            System.out.println(infoUser.getResults().get(i).getEmail() + "rx ");
                        int i = 0;

                        /*addUserInList(infoUser.getResults().get(0).getName().getFirst() +  " " + infoUser.getResults().get(0).getName().getLast(),
                                    infoUser.getResults().get(0).getLogin().getUsername(),
                                    infoUser.getResults().get(0).getEmail(),
                                    infoUser.getResults().get(0).getPicture().getLarge());
                        Context mContext = getApplicationContext();
                        View child;
                        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        child = inflater.inflate(R.layout.contact_inflater, null);
                        LinearLayout parent = findViewById(R.id.parentLayout);
                       // View parentView = View.inflate(getApplicationContext(), R.layout.activity_main, null);
                        //LinearLayout parent = findViewById(R.id.parentLayout);
                        System.out.println("count " + parent.getChildCount());
                        TextView tvName = child.findViewById(R.id.tvName);
                        tvName.setText("hello");

                        TextView tvUsername = child.findViewById(R.id.tvUsername);
                        tvUsername.setText("its");

                        TextView tvEmail = child.findViewById(R.id.tvEmail);
                        tvEmail.setText("me");

                        //ImageView ivPhoto = child.findViewById(R.id.userPhoto);
                        //Picasso.get()
                        //        .load(infoUser.getResults().get(0).getPicture().getLarge())
                        //        .into(ivPhoto);
                        parent.addView(child);


                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });*/
            //System.out.println(mails.get(0) + " mails");

        /*
        call.enqueue(new Callback<InfoUser>() {
            @Override
            public void onResponse(Call<InfoUser> call, Response<InfoUser> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Error code: " +  response.code(), Toast.LENGTH_SHORT).show();
                    System.out.println("its error");
                    return;
                }
                System.out.println("its response " + response.body().getResults().get(0).getGender());
            }

            @Override
            public void onFailure(Call<InfoUser> call, Throwable t) {
                System.out.println("its fail " + t.getMessage());
            }
        });*/
    }
    private void findUsers(){
        infoUser.clone().enqueue(new Callback<InfoUser>() {//без .clone нельзя обращаться с одному call много раз
            @Override
            public void onResponse(Call<InfoUser> call, Response<InfoUser> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Error code: " +  response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                //listUser = response.body().getResults();
                for(int i = 0; i < response.body().getResults().size(); i++){
                    System.out.println("its response " + i + " " + response.body().getResults().get(i).getName().getFirst());
                    //System.out.println("its listUser " + i + " " + listUser.get(i).getName().getFirst());
                    View child = View.inflate(getApplicationContext(), R.layout.contact_inflater, null);
                    LinearLayout parent = findViewById(R.id.parentLayout);

                    TextView tvName = child.findViewById(R.id.tvName);
                    tvName.setText(response.body().getResults().get(i).getName().getFirst() + " " + response.body().getResults().get(i).getName().getLast());

                    TextView tvUsername = child.findViewById(R.id.tvUsername);
                    tvUsername.setText(response.body().getResults().get(i).getLogin().getUsername());

                    TextView tvEmail = child.findViewById(R.id.tvEmail);
                    tvEmail.setText(response.body().getResults().get(i).getEmail());

                    ImageView ivPhoto = child.findViewById(R.id.userPhoto);
                    Picasso.get()
                            .load(response.body().getResults().get(i).getPicture().getLarge())
                            .into(ivPhoto);
                    parent.addView(child);

                    ContentValues cv = new ContentValues();
                    cv.put(DBHelper.USER_COLUMN_USER_NAME, response.body().getResults().get(i).getName().getFirst() + " "
                            + response.body().getResults().get(i).getName().getLast());
                    cv.put(DBHelper.USER_COLUMN_USER_USERNAME, response.body().getResults().get(i).getLogin().getUsername());
                    cv.put(DBHelper.USER_COLUMN_USER_EMAIL, response.body().getResults().get(i).getEmail());
                    cv.put(DBHelper.USER_COLUMN_USER_PHOTO, response.body().getResults().get(i).getPicture().getLarge());

                    db.insert(DBHelper.USER_TABLE_NAME, null, cv);
                }
                //dbHelper.deleteDB(db);

                Cursor c = db.query(DBHelper.USER_TABLE_NAME, null,null, null,
                        null, null, null);
                if(c.moveToFirst()) {
                    do{
                        System.out.println("name: "  + c.getString(c.getColumnIndex(DBHelper.USER_COLUMN_USER_NAME)));
                    }while(c.moveToNext());
                }

            }
            @Override
            public void onFailure(Call<InfoUser> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("its fail " + t.getMessage());
            }
        });

    }
    void addUserInList(String name, String username, String email, String photoURL){
        System.out.println("this " + name);

        View child = View.inflate(getApplicationContext(), R.layout.contact_inflater, null);
        View parentView = View.inflate(getApplicationContext(), R.layout.activity_main, null);
        LinearLayout parent = parentView.findViewById(R.id.parentLayout);

        TextView tvName = child.findViewById(R.id.tvName);
        tvName.setText(name);

        TextView tvUsername = child.findViewById(R.id.tvUsername);
        tvUsername.setText(username);

        TextView tvEmail = child.findViewById(R.id.tvEmail);
        tvEmail.setText(email);

        ImageView ivPhoto = child.findViewById(R.id.userPhoto);
        //Picasso.get()
        //        .load(photoURL)
        //        .into(ivPhoto);
        parent.addView(child);

    }
}
