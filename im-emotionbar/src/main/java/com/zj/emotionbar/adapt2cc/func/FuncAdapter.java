package com.zj.emotionbar.adapt2cc.func;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zj.emotionbar.R;

import java.util.ArrayList;

public class FuncAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private ArrayList<AppBean> mData = new ArrayList<>();
    private final OnItemClickListener listener;

    FuncAdapter(Context context, ArrayList<AppBean> data, OnItemClickListener l) {
        this.listener = l;
        this.inflater = LayoutInflater.from(context);
        if (data != null) {
            this.mData = data;
        }
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.app_common_drawable_text, parent, false);
            viewHolder.iv_icon = convertView.findViewById(R.id.app_act_chat_emo_func_item_iv);
            viewHolder.tv_name = convertView.findViewById(R.id.app_act_chat_emo_func_item_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final AppBean appBean = mData.get(position);
        if (appBean != null) {
            viewHolder.iv_icon.setBackgroundResource(appBean.getIcon());
            viewHolder.tv_name.setText(appBean.getFuncName());
            convertView.setOnClickListener(v -> listener.onClick(appBean.getId(), v));
        }
        return convertView;
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
    }

    public interface OnItemClickListener {
        void onClick(int id, View view);
    }
}