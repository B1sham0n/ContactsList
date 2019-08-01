package android.example.contactslist.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Login {

    @SerializedName("username")
    @Expose
    private String username;

    public String getUsername() {
        return username;
    }

}
