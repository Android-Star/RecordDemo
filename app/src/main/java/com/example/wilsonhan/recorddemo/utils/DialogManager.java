package com.example.wilsonhan.recorddemo.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wilsonhan.recorddemo.R;

/**
 * Created by wilsonhan on 2018/1/30.
 */

public class DialogManager {

    private static final int DIALOG_RECORDING = 1;
    private static final int DIALOG_CANCEL = 2;
    private static final int DIALOG_TOO_SHORT = 3;

    private Dialog dialog;
    private Context mContext;

    private ImageView ivIcon;
    private ImageView ivCancel;
    private ImageView ivShort;
    private ImageView ivVoice;
    private TextView tvLabel;

    public DialogManager(Context context) {
        this.mContext = context;
    }

    public void showRecordingDialog() {
        dialog = new Dialog(mContext, R.style.dialog_record);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.layout_dialog, null);
        dialog.setContentView(view);

        ivIcon = dialog.findViewById(R.id.iv_icon);
        ivCancel = dialog.findViewById(R.id.iv_cancel);
        ivShort = dialog.findViewById(R.id.iv_short);
        ivVoice = dialog.findViewById(R.id.iv_voice);
        tvLabel = dialog.findViewById(R.id.tv_label);

        dialog.show();

    }

    public void record() {
        if (dialog != null && dialog.isShowing()) {
            ivIcon.setVisibility(View.VISIBLE);
            ivVoice.setVisibility(View.VISIBLE);
            tvLabel.setVisibility(View.VISIBLE);
            ivCancel.setVisibility(View.GONE);
            ivShort.setVisibility(View.GONE);

            tvLabel.setText("手指上滑，取消发送");
        }
    }

    public void wantCancel() {
        if (dialog != null && dialog.isShowing()) {
            ivIcon.setVisibility(View.GONE);
            ivVoice.setVisibility(View.GONE);
            tvLabel.setVisibility(View.VISIBLE);
            ivCancel.setVisibility(View.VISIBLE);
            ivShort.setVisibility(View.GONE);

            tvLabel.setText("松开手指，取消发送");
        }
    }


    public void showTooShort() {
        if (dialog != null && dialog.isShowing()) {
            ivIcon.setVisibility(View.GONE);
            ivVoice.setVisibility(View.GONE);
            tvLabel.setVisibility(View.VISIBLE);
            ivCancel.setVisibility(View.GONE);
            ivShort.setVisibility(View.VISIBLE);

            tvLabel.setText("录音时间过短");
        }
    }

    public void diMissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    public void setLeavel(int leavel) {
        if (dialog != null && dialog.isShowing()) {

            int leavelId = mContext.getResources()
                    .getIdentifier(
                            "v" + leavel,
                            "drawable",
                            mContext.getPackageName()
                    );
            ivVoice.setImageResource(leavelId);
        }
    }


}
