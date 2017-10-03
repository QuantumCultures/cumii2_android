package sg.lifecare.jsw.ui.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import sg.lifecare.cumii.R;

public class DownloadProgressDialog extends AlertDialog.Builder {

    private ProgressBar mDownloadProgress;

    public DownloadProgressDialog(@NonNull Context context) {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.jsw_dialog_download_progress, null);

        setView(view);

        mDownloadProgress = view.findViewById(R.id.download_progress);

    }
}
