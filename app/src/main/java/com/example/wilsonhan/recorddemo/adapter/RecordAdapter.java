package com.example.wilsonhan.recorddemo.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wilsonhan.recorddemo.R;
import com.example.wilsonhan.recorddemo.entity.RecordBean;
import com.example.wilsonhan.recorddemo.utils.TimeUtil;

import java.io.IOException;
import java.util.List;

/**
 * Created by wilsonhan on 2018/2/8.
 */

public class RecordAdapter extends BaseAdapter {
    private static final int MAX_RECORD_DURATION = Integer.MAX_VALUE;

    private List<RecordBean> recordBeans;
    private Context mContext;
    private LayoutInflater inflater;

    public RecordAdapter(Context context, List<RecordBean> recordBeans) {
        this.mContext = context;
        this.recordBeans = recordBeans;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return recordBeans.size();
    }

    @Override
    public RecordBean getItem(int i) {
        return recordBeans.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.item_record, null);
            viewHolder.ivRecord = view.findViewById(R.id.iv_record);
            viewHolder.llRecord = view.findViewById(R.id.ll_record);
            viewHolder.tvDuration = view.findViewById(R.id.tv_duration);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        RecordBean recordBean = recordBeans.get(i);
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.reset();

        try {
            mediaPlayer.setDataSource(recordBean.getRecordPath());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int duration = mediaPlayer.getDuration();

        //根据语音时长来重置UI长度显示
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int maxWidth = (int) (dm.widthPixels * 0.6);
        int minWidth = (int) (dm.widthPixels * 0.2);
        ViewGroup.LayoutParams layoutParams = viewHolder.llRecord.getLayoutParams();
        layoutParams.width = (int) (minWidth + (maxWidth / MAX_RECORD_DURATION * TimeUtil.convertToSecond(Long.valueOf(duration))));

        viewHolder.tvDuration.setText(TimeUtil.formatDuring(duration));
        return view;
    }

    class ViewHolder {
        TextView tvDuration;
        ImageView ivRecord;
        LinearLayout llRecord;
    }
}
