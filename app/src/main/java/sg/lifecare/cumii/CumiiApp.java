package sg.lifecare.cumii;

import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;

import io.fabric.sdk.android.Fabric;
import sg.lifecare.cumii.data.DataManager;
import timber.log.Timber;

public class CumiiApp extends MultiDexApplication {

    private DataManager mDataManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        Timber.plant(new Timber.DebugTree());

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }

        mDataManager = DataManager.getInstance(this);
    }

    public DataManager getDataManager() {
        return mDataManager;
    }
}
