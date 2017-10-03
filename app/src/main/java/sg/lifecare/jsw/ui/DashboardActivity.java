package sg.lifecare.jsw.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.jsw.sdk.decoder.HwH264Decoder;
import com.jsw.sdk.p2p.device.P2PDev;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import sg.lifecare.cumii.R;
import sg.lifecare.jsw.data.CameraData;
import sg.lifecare.jsw.data.P2pCamera;
import timber.log.Timber;

public class DashboardActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private List<P2pCamera> mP2pCameras = new ArrayList<>();

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, DashboardActivity.class);

        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);

        ButterKnife.bind(this);

        setupView();

        initP2pDev();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        uninitP2pDev();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupView() {
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        showCameraListFragment();
    }

    private void initP2pDev() {
        HwH264Decoder.setHardwareDecode(false);

        P2PDev.setNetworkAvailable(true);
        P2PDev.initAll();

        List<CameraData.Camera> cameras = CameraData.getInstance(this).getCameras();

        //cameraData.addCamera("Camera 2", "CGXX-001416-RLGWB", "123456");
        //cameraData.addCamera("Camera 3", "CGXX-001411-RKZTT", "123456");

        for (int i = 0; i < cameras.size(); i++) {
            P2PDev p2pDev = P2PDev.getP2PDev(cameras.get(i).getDid());
            p2pDev.set_id(i);
            p2pDev.setCam_name(cameras.get(i).getName());
            p2pDev.setDev_id1(cameras.get(i).getDid());
            p2pDev.setView_pwd(cameras.get(i).getPassword());
            p2pDev.assembleUID();

            mP2pCameras.add(new P2pCamera(p2pDev, this, i));
        }
    }

    private void uninitP2pDev() {
        P2PDev.deinitAll();
    }

    @Override
    public void onBackPressed() {
        Timber.d("onBackPressed: count=%d", getSupportFragmentManager().getBackStackEntryCount());

        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    public List<P2pCamera> getP2pCameras() {
        return mP2pCameras;
    }

    public P2pCamera getP2pCamera(int position) {
        if (position < mP2pCameras.size()) {
            return mP2pCameras.get(position);
        }

        return null;
    }

    public void startAllCameras() {
        for (int i = 0; i < mP2pCameras.size();  i++) {
            mP2pCameras.get(i).connect(i * 500);
        }
    }

    public void pauseAllCameras() {
        for (int i = 0; i < mP2pCameras.size(); i++) {
            mP2pCameras.get(i).pause();
        }
    }

    public void resumeAllCameras() {
        for (int i = 0; i < mP2pCameras.size(); i++) {
            mP2pCameras.get(i).resume();
        }
    }

    public void stopAllCameras() {
        for (int i = 0; i < mP2pCameras.size(); i++) {
            mP2pCameras.get(i).stop();
        }
    }

    private void showCameraListFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_content, CameraListFragment.newInstance(), CameraListFragment.class.getSimpleName())
                .addToBackStack(CameraListFragment.class.getSimpleName())
                .commit();
    }

    public void showEventListFragment(int position) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_content, EventListFragment.newInstance(position), EventListFragment.class.getSimpleName())
                .addToBackStack(EventListFragment.class.getSimpleName())
                .commit();
    }

    public void showVideoFragment(int position) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_content, VideoFragment.newInstance(position), VideoFragment.class.getSimpleName())
                .addToBackStack(VideoFragment.class.getSimpleName())
                .commit();
    }
}
