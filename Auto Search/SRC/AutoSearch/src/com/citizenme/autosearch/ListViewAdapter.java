package com.citizenme.autosearch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.citizenme.model.Items;

public class ListViewAdapter extends BaseAdapter implements OnClickListener {
    private Context mContext;
    private LayoutInflater mInflater;
    private Items[] mItems;
    private int mListResource;
    ClickEvents mClickEvents;

    public ListViewAdapter(Context context, LayoutInflater mInflater,
            Items[] Items, int listReource, ClickEvents onClickEvents) {
        this.mContext = context;
        this.mInflater = mInflater;
        this.mItems = Items;
        this.mListResource = listReource;
        this.mClickEvents = onClickEvents;

    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(mListResource, null);
            holder = new ViewHolder();

            holder.linkTV = (TextView) convertView.findViewById(R.id.link);

            holder.snippetTv = (TextView) convertView.findViewById(R.id.snipet);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.linkTV.setText(mItems[position].getLink());
        holder.snippetTv.setText(mItems[position].getSnippet());

        holder.linkTV.setOnClickListener(this);
        holder.snippetTv.setOnClickListener(this);

        holder.linkTV.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mClickEvents.getResult(mItems[position].getLink());
            }
        });

        return convertView;

    }

    @Override
    public int getCount() {
        return mItems.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public void onClick(View v) {

        Log.d("Gaurav I am clicked ");

    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    public interface ClickEvents {
        public void getResult(String item);

    }

    static class ViewHolder {
        TextView linkTV;
        TextView snippetTv;
    }

}
