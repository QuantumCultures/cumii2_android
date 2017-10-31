package sg.lifecare.cumii;

import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.tencent.bugly.crashreport.CrashReport;

import io.fabric.sdk.android.Fabric;
import sg.lifecare.cumii.data.DataManager;
import timber.log.Timber;

public class CumiiApp extends MultiDexApplication {

    private DataManager mDataManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        CrashReport.initCrashReport(getApplicationContext(), "7cce9349f7", false);

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
