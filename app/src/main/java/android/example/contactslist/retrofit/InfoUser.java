package android.example.contactslist.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InfoUser {

    @SerializedName("results")
    @Expose
    private List<Results> results = null;

    public List<Results> getResults() {
        return results;
    }

}
