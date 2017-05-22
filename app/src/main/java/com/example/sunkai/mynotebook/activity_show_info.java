package com.example.sunkai.mynotebook;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.lang.reflect.Field;
import java.util.List;

public class activity_show_info extends AppCompatActivity {

    private ImageView d_img;
    private VideoView d_vedio;
    private TextView d_tvtitle;
    private TextView d_tv;
//    private Button d_delete;
//    private Button d_edit;
    private Button d_back;
    private ActionBar actionBar;
    private int id;
    private List<note> list=null;
    String title,content,imgPath,vedioPath,time,loginName;
    private CoordinatorLayout activity_show_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_info);
//        getActionBar().setDisplayShowHomeEnabled(true);
        initView();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initView() {
        d_img = (ImageView) findViewById(R.id.d_img);
        d_vedio = (VideoView) findViewById(R.id.d_vedio);
        d_tvtitle = (TextView) findViewById(R.id.d_tvtitle);
        d_tv = (TextView) findViewById(R.id.d_tv);
//        d_delete = (Button) findViewById(R.id.d_delee);
//        d_edit = (Button) findViewById(R.id.d_edit);
        activity_show_info = (CoordinatorLayout) findViewById(R.id.activity_show_info);
        loginName=getIntent().getStringExtra("loginName");
        actionBar = getSupportActionBar();
        MainActivity.change=0;

//        d_delete.setOnClickListener(this);
//        d_edit.setOnClickListener(this);
    }

    //当用户从编辑Activity返回的时候或者此页面从后台重新启动的时候会执行此方法，用来显示内容
    @Override
    protected void onResume()
    {
        super.onResume();
        id=getIntent().getIntExtra("id",0);
        list=SQL_add_delete_update.findUserNotes(this,loginName,id);
        title=list.get(0).title;
        content=list.get(0).content;
        imgPath=list.get(0).picPath;
        vedioPath=list.get(0).vedioPath;
//        Toast.makeText(this, vedioPath, Toast.LENGTH_SHORT).show();
        time=list.get(0).time;
        if(imgPath.equals("null"))
            d_img.setVisibility(View.GONE);
        else {
            d_img.setVisibility(View.VISIBLE);
            d_img.setImageBitmap(handle_image_vedio.getImage(imgPath, 500, 400));
        }
        if(vedioPath.equals("null")) {
            d_vedio.setVisibility(View.GONE);
        }
        else {
            d_vedio.setVisibility(View.VISIBLE);
            d_vedio.setVideoPath(vedioPath);
            d_vedio.start();
        }
        if(content.equals(""))
            d_tv.setVisibility(View.GONE);
        else
        {
            d_tv.setVisibility(View.VISIBLE);
            d_tv.setText(content);
        }
        if(title.equals(""))
        {
            d_tvtitle.setVisibility(View.GONE);
        }
        else
        {
            d_tvtitle.setVisibility(View.VISIBLE);
            d_tvtitle.setText(title);
        }
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.d_delete:
//                new AlertDialog.Builder(activity_show_info.this).setTitle("确定删除?").setNegativeButton(
//                        "确定", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                SQL_add_delete_update.delete(activity_show_info.this,id);
//                                Toast.makeText(activity_show_info.this,"删除成功！",Toast.LENGTH_LONG).show();
//                                finish();
//                            }
//                        }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                }).show();
//                MainActivity.change=1;
//                break;
//            case R.id.d_edit:
//
//
//                break;
//        }
//    }


    //当用户点击menu时候，通过switch case根据用户点击的按钮id来执行不同的操作
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            //当用户选择编辑的时候，通过intent把当前相信信息页面的内容传递个下一个页面用于编辑
            case R.id.action_edit:
                Intent intent=new Intent(this,activity_show_info_edit.class);
                intent.putExtra("id",id);
                intent.putExtra("title",title);
                intent.putExtra("content",content);
                intent.putExtra("imagePath",imgPath);
                intent.putExtra("vedioPath",vedioPath);
                intent.putExtra("time",time);
                intent.putExtra("loginName",loginName);
                startActivity(intent);
                break;
            case R.id.action_delete:
                new AlertDialog.Builder(activity_show_info.this).setTitle("确定删除?").setNegativeButton(
                        "确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SQL_add_delete_update.delete(activity_show_info.this,id);
                                Toast.makeText(activity_show_info.this,"删除成功！",Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                MainActivity.change=1;
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //重写onCreateOptionsMenu方法，在顶部的bar中显示菜单
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.show_info_menu, menu);
        setIconVisible(menu,true);
        return true;
    }

    //此方法用于在顶部的bar中的menu显示图标和文字
    public void setIconVisible(Menu menu, boolean visable){
        Field field;
        try {
            field = menu.getClass().getDeclaredField("mOptionalIconsVisible");

            Log.d("TAG"," setIconVisible1() field="+field);
            field.setAccessible(true);
            field.set(menu, visable);
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
