package sg.lifecare.cumii.data;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import sg.lifecare.cumii.data.server.response.AssistsedEntityResponse;
import sg.lifecare.cumii.data.server.response.EntityDetailResponse;

public class DataManager {

    private static DataManager sInstance;

    private final CumiiPreferences mPreferences;
    private final CumiiService mCumiiService;

    private EntityDetailResponse.Data mUser;
    private List<AssistsedEntityResponse.Data> mMembers;

    public static DataManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DataManager(context);
        }

        return sInstance;
    }

    private DataManager(Context context) {

        mPreferences = new CumiiPreferences(context);
        mCumiiService = CumiiService.Factory.makeLifecareService(context);
    }

    public CumiiService getCumiiService() {
        return mCumiiService;
    }

    public CumiiPreferences getPreferences() {
        return mPreferences;
    }

    public EntityDetailResponse.Data getUser() {
        return mUser;
    }

    public List<AssistsedEntityResponse.Data> getMembers() {
        if (mMembers == null) {
            return new ArrayList<AssistsedEntityResponse.Data>();
        }

        return mMembers;
    }

    public AssistsedEntityResponse.Data getMemeberById(@NonNull String id) {
        if ((mMembers != null) && (mMembers.size() > 0)) {
            for (AssistsedEntityResponse.Data member : mMembers) {
                if (id.equals(member.getId())) {
                    return member;
                }
            }
        }

        return null;
    }

    public AssistsedEntityResponse.Data getMember(int position) {
        if ((mMembers != null) && (mMembers.size() > position)) {
            return mMembers.get(position);
        }

        return null;
    }

    public void setUser(EntityDetailResponse.Data user) {
        mUser = user;
    }

    public void setMembers(List<AssistsedEntityResponse.Data> members) {
        mMembers = members;
    }
}
