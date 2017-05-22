package com.example.sunkai.mynotebook;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;

public class activity_show_info_edit extends AppCompatActivity {
    private ImageView de_img;
    private ImageView de_vedio;
    private EditText de_tvtitle;
    private File imageFile,mediaFile;
    private EditText de_tv;
    private CoordinatorLayout activity_show_info;
    private ActionBar actionBar;
    private int id;
    private AlertDialog.Builder builder;
    private String title,content,imagePath,vedioPath,time,loginName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_info_edit);
        initView();
        actionBar.setDisplayHomeAsUpEnabled(true);
        if(de_vedio.getVisibility()==View.VISIBLE)
        {
            de_vedio.setClickable(true);
            de_vedio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    de_img.setVisibility(View.GONE );
                    de_vedio.setVisibility(View.VISIBLE);
                    Intent viedo_intent=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    try{
                        mediaFile=new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                                +"/"+handle_image_vedio.getTime()+".mp4");
                        viedo_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mediaFile));
                        startActivityForResult(viedo_intent, activity_add.Vedio_num);
                    }catch (Exception e){e.printStackTrace();}
                }
            });
        }
        if(de_img.getVisibility()==View.VISIBLE)
        {
            de_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder=new AlertDialog.Builder(activity_show_info_edit.this).setTitle("请选择");
                    final ListView choice=new ListView(activity_show_info_edit.this);
                    ArrayAdapter arrayAdapter=new ArrayAdapter<String>(activity_show_info_edit.this,android.R.layout.simple_list_item_1);
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
                                    startActivityForResult(image_intent,activity_add.Pic_num_capture);
                                }
                                catch (Exception e){e.printStackTrace();}
                            }
                            else if(position==1)
                            {
                                dialog.dismiss();
                                //调用系统图库，获取图片
                                Intent image_intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(image_intent,activity_add.Pic_num_select);
                            }
                        }
                    });
                    de_img.setVisibility(View.VISIBLE);
                }
            });
        }

    }

    //将详细信息页面传进来的Intent内容读取出来
    private void initView() {
        de_img = (ImageView) findViewById(R.id.de_img);
        de_vedio = (ImageView) findViewById(R.id.de_vedio);
        de_tvtitle = (EditText) findViewById(R.id.de_tvtitle);
        de_tv = (EditText) findViewById(R.id.de_tv);
        activity_show_info = (CoordinatorLayout) findViewById(R.id.activity_show_info);
        actionBar = getSupportActionBar();

        id=getIntent().getIntExtra("id",0);
        title=getIntent().getStringExtra("title");
        content=getIntent().getStringExtra("content");
        imagePath=getIntent().getStringExtra("imagePath");
        vedioPath=getIntent().getStringExtra("vedioPath");
        time=getIntent().getStringExtra("time");
        loginName=getIntent().getStringExtra("loginName");


        if(imagePath.equals("null"))
        {
            de_img.setVisibility(View.GONE);
        }
        else
        {
            de_img.setVisibility(View.VISIBLE);
            de_img.setImageBitmap(handle_image_vedio.getImage(imagePath,500,400));
        }
        if(vedioPath.equals("null"))
        {
            de_vedio.setVisibility(View.GONE);
        }
        else
        {
            de_vedio.setVisibility(View.VISIBLE);
            de_vedio.setImageBitmap(handle_image_vedio.getVedio(vedioPath,500, 400, MediaStore.Images.Thumbnails.MICRO_KIND));
        }
        de_tvtitle.setText(title);
        de_tv.setText(content);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                MainActivity.change=0;
                break;
            case R.id.action_save:
                SQL_add_delete_update.update(this,id,loginName,de_tvtitle.getText().toString(),de_tv.getText().toString(),handle_image_vedio.getTime(),imagePath,vedioPath);
                finish();
                Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
                MainActivity.change=1;
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    //当用户选择添加不同功能的内容的时候，用户选择了拍照、选择已有的图片、拍摄视频，他们通过requestCode不同来区分即 Pic_num_capture，Pic_num_select，Vedio_num;
    //重写onActivityResult来对用户选择的额不同的内容进行反应
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==activity_add.Pic_num_capture){
            imagePath=imageFile.getAbsolutePath();
            Bitmap bitmap=handle_image_vedio.getImage(imageFile.getAbsolutePath(), 500, 400);
            de_img.setImageBitmap(bitmap);
        }
        else if(requestCode==activity_add.Pic_num_select)
        {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imagePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap bitmap=handle_image_vedio.getImage(imagePath, 500, 400);
            de_img.setImageBitmap(bitmap);
        }
        else if(requestCode==activity_add.Vedio_num)
        {
            vedioPath=mediaFile.toString();
            de_vedio.setImageBitmap(handle_image_vedio.getVedio(vedioPath, 500, 400,MediaStore.Images.Thumbnails.MICRO_KIND));
        }


    }
}
