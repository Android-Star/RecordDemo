package com.example.wilsonhan.recorddemo.activity;

import android.Manifest;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.wilsonhan.recorddemo.R;
import com.example.wilsonhan.recorddemo.adapter.RecordAdapter;
import com.example.wilsonhan.recorddemo.entity.RecordBean;
import com.example.wilsonhan.recorddemo.utils.FileUtil;
import com.example.wilsonhan.recorddemo.utils.MediaManager;
import com.example.wilsonhan.recorddemo.view.RecordButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissionItem;

/**
 * Created by wilsonhan on 2018/2/8.
 */

public class MainActivity
        extends
        AppCompatActivity
        implements
        RecordButton.AudioFinishRecordListener {

    private ListView lvRecord;
    private RecordButton btnRecord;

    private View animView;

    private RecordAdapter mAdapter;
    private List<RecordBean> recordBeans = new ArrayList<>();

    private String dir = Environment.getExternalStorageDirectory() + "/wilson_record_audios";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initPermission();

        lvRecord = findViewById(R.id.lv_record);
        btnRecord = findViewById(R.id.btn_record);
        btnRecord.setmAudioFinishRecordListener(this);
        mAdapter = new RecordAdapter(this, recordBeans);
        lvRecord.setAdapter(mAdapter);

        lvRecord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (animView != null) {
                    animView.setBackgroundResource(R.drawable.adj);
                    animView = null;
                }
                animView = view.findViewById(R.id.iv_record);
                animView.setBackgroundResource(R.drawable.record_anim);
                AnimationDrawable animationDrawable = (AnimationDrawable) animView.getBackground();
                animationDrawable.start();

                RecordBean recordBean = recordBeans.get(i);
                MediaManager.playSound(recordBean.getRecordPath(), new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        animView.setBackgroundResource(R.drawable.adj);
                    }
                });

            }
        });
    }

    private void initPermission() {
        List<PermissionItem> permissions = new ArrayList<PermissionItem>();
        permissions.add(new PermissionItem(Manifest.permission.RECORD_AUDIO, getString(R.string.permission_cus_item_audio), R.drawable.permission_ic_micro_phone));
        permissions.add(new PermissionItem(Manifest.permission.READ_EXTERNAL_STORAGE, getString(R.string.permission_cus_item_read), R.drawable.permission_ic_storage));
        permissions.add(new PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, getString(R.string.permission_cus_item_write), R.drawable.permission_ic_storage));

        HiPermission.create(this)
                .permissions(permissions)
                .checkMutiPermission(new PermissionCallback() {
                    @Override
                    public void onClose() {
                    }

                    @Override
                    public void onFinish() {
                    }

                    @Override
                    public void onDeny(String permisson, int position) {
                    }

                    @Override
                    public void onGuarantee(String permisson, int position) {
                    }
                });
    }

    @Override
    public void onFinishRecord(float seconds, String filePath) {
        RecordBean recordBean = new RecordBean();
        recordBean.setRecordPath(filePath);
        recordBeans.add(recordBean);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaManager.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManager.release();
        File file = new File(dir);
        if (file.isDirectory()) {
            FileUtil.deleteDir(file);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaManager.pause();
    }
}
