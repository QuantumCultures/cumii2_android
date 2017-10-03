package sg.lifecare.jsw.ui.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jsw.sdk.p2p.device.IAVListener;
import com.jsw.sdk.p2p.device.IRecvIOCtrlListener;
import com.jsw.sdk.p2p.device.P2PDev;
import com.jsw.sdk.p2p.device.extend.AUTH_AV_IO_Proto;
import com.jsw.sdk.ui.TouchedTextureView;
import com.jsw.sdk.ui.TouchedView;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import sg.lifecare.cumii.R;
import sg.lifecare.jsw.data.CameraData;
import sg.lifecare.jsw.util.JswUtil;
import timber.log.Timber;

public class CameraView extends ConstraintLayout {




    public CameraView(Context context) {
        this(context, null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {
        Timber.d("init");
        View view = inflate(getContext(), R.layout.item_camera_view, this);
        ButterKnife.bind(view, this);

        //mVideoHardwareView.setVisibility(View.GONE);
       // mVideoView.setVisibility(View.GONE);
    }



}
