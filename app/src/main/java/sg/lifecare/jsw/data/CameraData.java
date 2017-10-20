package sg.lifecare.jsw.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.securepreferences.SecurePreferences;

import java.util.ArrayList;
import java.util.List;

import sg.lifecare.jsw.util.Base64;
import timber.log.Timber;

public class CameraData {

    private static CameraData sInstance;

    private SharedPreferences mPreferences;
    private ArrayList<Camera> mCameras = new ArrayList<>();

    public static CameraData getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new CameraData(context);
        }

        return sInstance;
    }

    private CameraData(final Context context) {
        Timber.d("Camera data");

        mPreferences = new SecurePreferences(context, "jsw", "jsw_prefs.xml");

        Gson gson = new Gson();
        String result = mPreferences.getString("cameras", "");

        if (!TextUtils.isEmpty(result)) {
            Timber.d("CameraData: %s", result);

            mCameras = gson.fromJson(result, new TypeToken<List<Camera>>() {}.getType());

            if (mCameras == null) {
                mCameras = new ArrayList<>();
            } else {
                for (Camera camera : mCameras) {
                    Timber.d("camera: %s", camera.toString());
                }
            }
        }
    }

    public List<Camera> getCameras() {
        return mCameras;
    }

    private static String encode(String s) {
        if (TextUtils.isEmpty(s)) {
            return "";
        }

        return Base64.base64Encode(s.getBytes());
    }

    private static String decode(String s) {
        if (TextUtils.isEmpty(s)) {
            return "";
        }

        byte[] in = s.getBytes();
        byte[] out = Base64.base64Decode(in, 0, in.length);
        return new String(out);
    }

    public void clear() {
        mCameras.clear();
        save();
    }

    public void addCamera(@NonNull String name, @NonNull String did, @NonNull String password) {
        boolean found = false;

        Timber.d("addCamera: name=%s, did=%s, password=%s", name, did, password);

        if (mCameras.size() > 0) {
            for (Camera camera : mCameras) {

                if (camera.getDid().equals(did)) {
                    camera.setName(name);
                    camera.setPassword(password);
                    save();

                    found = true;

                    break;
                }
            }
        }

        if (!found) {
            Camera camera = new Camera();
            camera.setName(name);
            camera.setPassword(password);
            camera.setDid(did);
            mCameras.add(camera);
            save();
        }
    }

    public void save() {
        String result = new Gson().toJson(mCameras, new TypeToken<List<Camera>>() {}.getType());
        Timber.d("save: %s", result);
        mPreferences.edit().putString("cameras", result).apply();
    }

    public class Camera {
        private String name;
        private String did;
        private String password;
        private String model;
        private int systemType;

        public String getName() {
            return name;
        }

        public String getDid() {
            return decode(did);
        }

        public String getPassword() {
            return decode(password);
        }

        public String getModel() {
            return model;
        }

        public int getSystemType() {
            return systemType;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setDid(String did) {
            this.did = encode(did);
        }

        public void setPassword(String password) {
            this.password = encode(password);
        }

        public void setModel(String model) {
            this.model = model;
        }

        public void setSystemType(int type) {
            this.systemType = type;
        }

        @Override
        public String toString() {
            return "Did: " + did + ", name: " + name;
        }

    }
}
