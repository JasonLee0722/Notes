package com.base.notes.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * mAdapter = new CommAdapter<Step>(this, new ArrayList<>(),R.layout.list_item){
 *     @Override
 *     public void convert(ViewHolder holder,final Step step) {
 *         holder.setText(R.id.tvContent,step.getProcess_name());
 *     }
 * };
 */
public abstract class CommAdapter<T> extends BaseAdapter {
    protected LayoutInflater mInflater;
    protected List<T> mData;
    protected Context mContext;
    private int layoutId;

    public CommAdapter(Context context, List<T> data, int layoutId) {
        this.mContext = context;
        this.mData = data;
        this.mInflater = LayoutInflater.from(context);
        this.layoutId = layoutId;
    }

    public void addItems(List<T> list){
        mData.addAll(list);
    }

    public void clearItems()
    {
        mData.clear();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(mContext, convertView, parent, layoutId, position);
        convert(holder, getItem(position));
        return holder.getConvertView();
    }

    public abstract void convert(ViewHolder holder, T t);
}
