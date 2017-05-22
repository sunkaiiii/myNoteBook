package com.example.sunkai.mynotebook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sunkai on 2016/12/18.
 */


//此类用于读取note类当中的内容，并将内容付给一个新的View中用于显示信息，最终view被添加进Adapter里面添加进listview
public class noteAdapter extends BaseAdapter {
    private Context context;
    private LinearLayout layout;
    private List<note> notes;
    public noteAdapter(Context context,List<note> notes){
        this.context=context;
        this.notes=notes;
    }
    public int getCount() {
        return notes.size();
    }
    public Object getItem(int position) {
        return notes.get(position);
    }
    public long getItemId(int position) {
        return position;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder vh;
        View view;
        TextView contentTv,timeTv,titleTv;
        ImageView imgIV,vedioIV;

        //convertView不为空的时候，这个convertview可以重复利用避免的资源浪费，提升程序性能
        if(convertView==null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_view_show_layout, null);
            vh = new Holder();
            vh.v1 = (TextView) convertView.findViewById(R.id.list_text);
            vh.v2 = (TextView) convertView.findViewById(R.id.list_time);
            vh.v3 = (TextView) convertView.findViewById(R.id.list_title);
            vh.v4 = (ImageView) convertView.findViewById(R.id.list_img);
            convertView.setTag(vh);
        }
        else
        {
            vh=(Holder)convertView.getTag();
        }

        note note = notes.get(position);
        String content = note.content;
        String time = note.time;
        String title = note.title;
        String imageurl = note.picPath;
        String vediourl = note.vedioPath;
        vh.v1.setText("        " + content);
        vh.v2.setText(time);
        vh.v3.setText(title);
        if (imageurl.equals("null")&&vediourl.equals("null")) {
                vh.v4.setVisibility(View.GONE);
        } else {
            vh.v4.setVisibility(View.VISIBLE);
            if(vediourl.equals("null"))
                vh.v4.setImageBitmap(note.picturtebitmap);
            else
            {
                vh.v4.setImageBitmap(note.vediobitmap);
            }
        }
        return convertView;
    }
}
