package com.example.sunkai.mynotebook;

import android.app.Activity;
import java.lang.String;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.sunkai.mynotebook.handle_image_vedio;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class activity_add extends AppCompatActivity {

    private ImageView c_img;
    private VideoView c_vedio;
    private EditText edit_title;
    private EditText edittext;
    private File imageFile,mediaFile;
    private String picturePath;
    private AlertDialog.Builder builder;
    private ActionBar actionBar;
    public final static int Pic_num_capture=1;
    public final static int Pic_num_select=2;
    public final static int Vedio_num=3;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        initView();
        handle_message();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    private void initView() {
        c_img = (ImageView) findViewById(R.id.c_img);
        c_vedio = (VideoView) findViewById(R.id.c_vedio);
        edit_title = (EditText) findViewById(R.id.edit_title);
        edittext = (EditText) findViewById(R.id.edittext);
        actionBar = getSupportActionBar();
    }

    //处理Intent传进来的信息，从而选择对应的操作
    private void handle_message()
    {
        switch (getIntent().getStringExtra("message"))
        {
            case "text":
                c_img.setVisibility(View.GONE);
                c_vedio.setVisibility(View.GONE);
                break;
            case "image":
                builder=new AlertDialog.Builder(this).setTitle("请选择");
                final ListView choice=new ListView(this);
                ArrayAdapter arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
                arrayAdapter.add("拍照");
                arrayAdapter.add("从图库中选择");
                final AlertDialog dialog;
                choice.setAdapter(arrayAdapter);
                builder.setView(choice);
                dialog = builder.show();
                choice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if(position==0)
                        {
                            dialog.dismiss();

                            //调用系统相机，获得图片
                            Intent image_intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            try {
                                imageFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                                        + "/" + handle_image_vedio.getTime() + ".jpg");
                                image_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                                startActivityForResult(image_intent,Pic_num_capture);
                            }
                            catch (Exception e){e.printStackTrace();}
                        }
                        else if(position==1)
                        {
                            dialog.dismiss();
                            //调用系统图库，获取图片
                            Intent image_intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(image_intent,Pic_num_select);
                        }
                    }
                });
                c_img.setVisibility(View.VISIBLE);
                c_vedio.setVisibility(View.GONE);
                break;
            case "vedio":
                c_img.setVisibility(View.GONE );
                c_vedio.setVisibility(View.VISIBLE);
                Intent viedo_intent=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                try{
                    mediaFile=new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                            +"/"+handle_image_vedio.getTime()+".mp4");
                    viedo_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mediaFile));
                    startActivityForResult(viedo_intent, Vedio_num);
                }catch (Exception e){e.printStackTrace();}
                break;
        }
    }



    //当用户选择添加不同功能的内容的时候，用户选择了拍照、选择已有的图片、拍摄视频，他们通过requestCode不同来区分即 Pic_num_capture，Pic_num_select，Vedio_num;
    //重写onActivityResult来对用户选择的额不同的内容进行反应
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Pic_num_capture){
            picturePath=imageFile.getAbsolutePath();
            Bitmap bitmap=handle_image_vedio.getImage(imageFile.getAbsolutePath(), 500, 400);
            c_img.setImageBitmap(bitmap);
        }
        else if(requestCode==Pic_num_select)
        {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap bitmap=handle_image_vedio.getImage(picturePath, 500, 400);
            c_img.setImageBitmap(bitmap);
        }
        else if(requestCode==Vedio_num)
        {
            c_vedio.setVideoURI(Uri.fromFile(mediaFile));
            c_vedio.start();
        }


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                SQL_add_delete_update.add(this, getIntent().getStringExtra("loginName"), edit_title.getText().toString(), edittext.getText().toString(), handle_image_vedio.getTime(), picturePath + "", mediaFile + "");
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                finish();
                MainActivity.change = 1;
                break;
            case android.R.id.home:
                finish();
                MainActivity.change = 0;
                break;
        }
        return super.onOptionsItemSelected(item);
        }
    //重写onCreateOptionsMenu方法，在顶部的bar中显示菜单
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
