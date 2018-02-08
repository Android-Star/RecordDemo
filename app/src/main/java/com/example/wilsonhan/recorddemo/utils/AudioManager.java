package com.example.wilsonhan.recorddemo.utils;

import android.media.MediaRecorder;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by wilsonhan on 2018/1/30.
 */

public class AudioManager {

    private MediaRecorder mediaRecorder;
    private String mDir;
    private String mCurPath;

    private static AudioManager mInstans;

    private boolean isPrepared = false;

    public AudioManager(String mDir) {
        this.mDir = mDir;
    }

    private AudioStateListener audioStateListener;

    public String getCurrentFilePath() {
        return mCurPath;
    }

    /**
     * 回调准备完毕
     */
    public interface AudioStateListener {
        void wellPrepared();
    }

    public void setAudioStateListener(AudioStateListener audioStateListener) {
        this.audioStateListener = audioStateListener;
    }

    public static AudioManager getInstence(String dir) {
        if (mInstans == null) {
            synchronized (AudioManager.class) {
                if (mInstans == null)
                    mInstans = new AudioManager(dir);
            }
        }
        return mInstans;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void prepareAudio() {
        try {
            isPrepared = false;
            File dir = new File(mDir);
            if (!dir.exists())
                dir.mkdirs();
            String fileName = generateFileName();
            File file = new File(dir, fileName);
            mCurPath = file.getAbsolutePath();

            mediaRecorder = new MediaRecorder();
            //设置音频源
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //设置输出文件
            mediaRecorder.setOutputFile(mCurPath);
            //设置音频格式
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
            //设置音频编码
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            mediaRecorder.prepare();
            mediaRecorder.start();
            //准备结束
            isPrepared = true;
            if (audioStateListener != null) {
                audioStateListener.wellPrepared();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String generateFileName() {
        return UUID.randomUUID().toString() + ".amr";
    }

    public int getVoiceLevel(int maxLevel) {
        if (isPrepared) {
            try {
                //mediaRecorder.getMaxAmplitude()取值范围1-32767
                return maxLevel * mediaRecorder.getMaxAmplitude() / 32768 + 1;
            } catch (Exception e) {

            }
        }
        return 1;
    }

    public void cancel() {
        release();
        if (!TextUtils.isEmpty(mCurPath)) {
            File file = new File(mCurPath);
            file.delete();
        }
        mCurPath = null;
    }

    public void release() {
        if(mediaRecorder!=null){
            mediaRecorder.stop();
            mediaRecorder.release();
        }
        mediaRecorder = null;
    }


}
