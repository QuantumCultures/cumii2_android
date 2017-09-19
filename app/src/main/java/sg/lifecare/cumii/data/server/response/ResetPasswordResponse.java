package sg.lifecare.cumii.data.server.response;

import com.google.gson.annotations.SerializedName;

public class ResetPasswordResponse extends Response {

    @SerializedName("Data")
    private boolean data;

    @Override
    public Boolean getData() {
        return data;
    }

}
