package com.example.wilsonhan.recorddemo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.wilsonhan.recorddemo.R;
import com.example.wilsonhan.recorddemo.utils.AudioManager;
import com.example.wilsonhan.recorddemo.utils.DialogManager;


/**
 * Created by wilsonhan on 2018/1/30.
 */

@SuppressLint("AppCompatCustomView")
public class RecordButton extends Button implements AudioManager.AudioStateListener {

    private static final int DEFAULT_SPACE = 50;

    public static final int STATE_NORMAL = 1;
    public static final int STATE_RECORDING = 2;
    public static final int STATE_WANT_TO_CANCEL = 3;

    private int mCurrentState = STATE_NORMAL;

    private boolean isRecording = false;

    private DialogManager mDialogManager;

    private AudioManager audioManager;

    public AudioFinishRecordListener mAudioFinishRecordListener;

    private Context context;


    public RecordButton(Context context) {
        this(context, null, 0);
    }

    public RecordButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        mDialogManager = new DialogManager(getContext());

        String dir = Environment.getExternalStorageDirectory() + "/wilson_record_audios";
        audioManager = new AudioManager(dir);
        audioManager.setAudioStateListener(this);
        setOnLongClickListener(new OnLongClickListener() {
            @SuppressLint("NewApi")
            @Override
            public boolean onLongClick(View view) {
                mReady = true;
                audioManager.prepareAudio();
                return false;
            }
        });

    }

    /**
     * 录音完成后的回调
     */
    public interface AudioFinishRecordListener {
        void onFinishRecord(float seconds, String filePath);
    }

    public void setmAudioFinishRecordListener(AudioFinishRecordListener mAudioFinishRecordListener) {
        this.mAudioFinishRecordListener = mAudioFinishRecordListener;
    }

    private float mTime;
    //是否触发longClick
    private boolean mReady;

    private Runnable mGetVoiceLevelRunnable = new Runnable() {
        @Override
        public void run() {
            while (isRecording) {
                try {
                    Thread.sleep(100);
                    mTime += 0.1f;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(MSG_VOICE_CHANGE);
            }
        }
    };

    private static final int MSG_AUDIO_PREPARED = 0x110;
    private static final int MSG_VOICE_CHANGE = 0x111;
    private static final int MSG_DIALOG_DISMISS = 0x112;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUDIO_PREPARED:
                    isRecording = true;
                    mDialogManager.showRecordingDialog();
                    new Thread(mGetVoiceLevelRunnable).start();
                    break;
                case MSG_VOICE_CHANGE:
                    mDialogManager.setLeavel(audioManager.getVoiceLevel(7));
                    break;
                case MSG_DIALOG_DISMISS:
                    mDialogManager.diMissDialog();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void wellPrepared() {
        handler.sendEmptyMessage(MSG_AUDIO_PREPARED);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                changeState(STATE_RECORDING);

                break;
            case MotionEvent.ACTION_MOVE:
                if (isRecording) {
                    if (wantToCancel(x, y)) {
                        changeState(STATE_WANT_TO_CANCEL);

                    } else {
                        changeState(STATE_RECORDING);

                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                if (!mReady) {
                    reset();
                    return super.onTouchEvent(event);
                }
                if (mTime < 1.0f || !isRecording) {
                    mDialogManager.showTooShort();
                    audioManager.cancel();
                    handler.sendEmptyMessage(MSG_DIALOG_DISMISS);
                    audioManager.release();
                    Toast.makeText(context, "录音时间过短，请重新录入", Toast.LENGTH_SHORT).show();
                } else if (mCurrentState == STATE_RECORDING) {
                    audioManager.release();
                    if (mAudioFinishRecordListener != null) {
                        mAudioFinishRecordListener.onFinishRecord(mTime, audioManager.getCurrentFilePath());
                    }
                }
                if (mCurrentState == STATE_WANT_TO_CANCEL) {
                    audioManager.cancel();
                }
                mDialogManager.diMissDialog();
                reset();
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private void reset() {
        isRecording = false;
        changeState(STATE_NORMAL);
        mTime = 0;
    }

    private boolean wantToCancel(int x, int y) {
        if (x < DEFAULT_SPACE || x > getWidth() + DEFAULT_SPACE) {
            return true;
        }
        if (y < -DEFAULT_SPACE || y > getHeight() + DEFAULT_SPACE) {
            return true;
        }
        return false;
    }

    private void changeState(int state) {
        if (mCurrentState != state) {
            mCurrentState = state;
            switch (state) {
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.bg_btn_normal);
                    setText(R.string.str_btn_normal);
                    break;
                case STATE_RECORDING:
                    setBackgroundResource(R.drawable.bg_btn_cancel);
                    setText(R.string.str_btn_recording);
                    mDialogManager.record();
                    break;
                case STATE_WANT_TO_CANCEL:
                    setBackgroundResource(R.drawable.bg_btn_normal);
                    setText(R.string.str_btn_cancel);
                    mDialogManager.wantCancel();
                    break;
                default:
                    break;
            }
        }
    }


}
