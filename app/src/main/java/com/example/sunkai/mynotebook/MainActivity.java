package com.example.sunkai.mynotebook;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView notelistview;
//    private Button btn_text;
//    private Button btn_image;
//    private Button btn_vedio;
    private Intent intent;
    private String loginUserName;
    private noteAdapter noteAdapter;
    private List<note> list;
    public static int change=1;
    public ProgressDialog myDialog;
    private FloatingActionButton fab,fab_Text,fab_Image,fab_Vedio;
    private Animation float_button_add,float_button_add_disapear;
    private Animation float_button_text,float_button_image,float_button_vedio;
    private Animation float_button_text_disapear,float_button_image_disapear,float_button_vedio_disapear;
    private AlertDialog.Builder builder;
    AlertDialog ad;
    Thread thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fab_Text.getVisibility()==View.GONE) {
                    startAnimation_apear();
                }
                else {
                    startAnimaiton_disapear();
                }
            }
        });
        notelistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                note selectNote=list.get(position);
                Intent intent=new Intent(MainActivity.this,activity_show_info.class);
                intent.putExtra("id",selectNote.id);
                intent.putExtra("loginName",loginUserName);
                startActivity(intent);
            }
        });
    }

    //执行floatBar的出现动画
    private void startAnimation_apear()
    {
        fab.startAnimation(float_button_add);
        fab_Text.startAnimation(float_button_text);
        fab_Image.startAnimation(float_button_image);
        fab_Vedio.startAnimation(float_button_vedio);
        fab_Text.setVisibility(View.VISIBLE);
        fab_Image.setVisibility(View.VISIBLE);
        fab_Vedio.setVisibility(View.VISIBLE);
    }

    //执行floatBar的离开动画
    private void startAnimaiton_disapear()
    {
        fab.startAnimation(float_button_add_disapear);
        fab_Text.startAnimation(float_button_text_disapear);
        fab_Image.startAnimation(float_button_image_disapear);
        fab_Vedio.startAnimation(float_button_vedio_disapear);
        fab_Text.setVisibility(View.GONE);
        fab_Image.setVisibility(View.GONE);
        fab_Vedio.setVisibility(View.GONE);
    }

    //重写onResume方法
    protected void onResume()
    {
        super.onResume();
        //为了避免每次重新读取数据库并且加载数据在listview上，只有当对数据库造成修改的才会对listview进行重新加载
        if(change==1) {
            myDialog=ProgressDialog.show(MainActivity.this,"加载中","");

            //创建一个线程，在后台读取数据库的内容，前台显示的为ProgressDiog提示窗口，当读取完成后，发送一个message，窗口消失，内容呈现
            new Thread(){
                public void run()
                {
                    try
                    {
                            list = SQL_add_delete_update.findUserNotes(MainActivity.this, loginUserName, null);
                            noteAdapter = new noteAdapter(MainActivity.this, list);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    finally {
                        Message message=new Message();
                        message.what=0;
                        handler.sendMessage(message);
                    }
                }
            }.start();
        }
    }

    private Handler handler=new Handler()
    {
        public void handleMessage(Message msg)
        {
            if(msg.what==0)
            {
                myDialog.dismiss();
                notelistview.setAdapter(noteAdapter);
                change=0;
            }
        }
    };


    private void initView() {
        notelistview = (ListView) findViewById(R.id.notelistview);
//        btn_text = (Button) findViewById(R.id.btn_text);
//        btn_image = (Button) findViewById(R.id.btn_image);
//        btn_vedio = (Button) findViewById(R.id.btn_vedio);
        loginUserName=getIntent().getStringExtra("loginName");
        fab=(FloatingActionButton)findViewById(R.id.fab);
        fab_Text=(FloatingActionButton)findViewById(R.id.fab_text);
        fab_Image=(FloatingActionButton)findViewById(R.id.fab_image);
        fab_Vedio=(FloatingActionButton)findViewById(R.id.fab_vedio);
        float_button_text=AnimationUtils.loadAnimation(this,R.anim.floatactionbutton_text_apear);
        float_button_image=AnimationUtils.loadAnimation(this,R.anim.floatactionbutton_image_apear);
        float_button_vedio=AnimationUtils.loadAnimation(this,R.anim.floatactionbutton_vedio_apear);
        float_button_add=AnimationUtils.loadAnimation(this,R.anim.floatactionbuttom_add_rotate);
        float_button_add_disapear=AnimationUtils.loadAnimation(this,R.anim.floatactionbuttom_add_rotate_dispear);
        float_button_text_disapear=AnimationUtils.loadAnimation(this,R.anim.floatactionbutton_text_disapear);
        float_button_image_disapear=AnimationUtils.loadAnimation(this,R.anim.floatactionbutton_text_disapear);
        float_button_vedio_disapear=AnimationUtils.loadAnimation(this,R.anim.floatactionbutton_text_disapear);

//        btn_text.setOnClickListener(this);
//        btn_image.setOnClickListener(this);
//        btn_vedio.setOnClickListener(this);
        fab_Text.setOnClickListener(this);
        fab_Image.setOnClickListener(this);
        fab_Vedio.setOnClickListener(this);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_switch_user:
                builder=new AlertDialog.Builder(MainActivity.this).setTitle("切换用户?").setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ad.dismiss();
                    }
                }).setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                        loginUserName="";
                        startActivity(intent);
                        finish();
                    }
                });
                ad=builder.create();
                ad.show();
                break;
            case R.id.action_change_password:
                final mySqlLite myHelper=new mySqlLite(MainActivity.this, "registe.db", null, 1);
                builder=new AlertDialog.Builder(MainActivity.this).setTitle("修改密码").setView(R.layout.change_password_layout);
                ad=builder.create();
                ad.show();
                final EditText changePasswordUser=(EditText)ad.findViewById(R.id.change_psssword_username);
                final EditText changePasswordPassword=(EditText)ad.findViewById(R.id.change_psssword_input);
                final EditText changePasswordPsswordInsre=(EditText)ad.findViewById(R.id.change_psssword_input_insure);
                final TextView changePasswordCancel=(TextView)ad.findViewById(R.id.change_password_cancel);
                final TextView changepasswordQueding=(TextView)ad.findViewById(R.id.change_password_queding);
                changePasswordUser.setText(loginUserName);
                changepasswordQueding.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (changePasswordPassword.getText().toString().isEmpty() || changePasswordPsswordInsre.getText().toString().isEmpty()) {
                            Toast.makeText(MainActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                        } else if (!changePasswordPassword.getText().toString().equals(changePasswordPsswordInsre.getText().toString())) {
                            Toast.makeText(MainActivity.this, "密码输入不同", Toast.LENGTH_SHORT).show();
                        } else {
                            SQLiteDatabase db = myHelper.getReadableDatabase();
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("password", changePasswordPassword.getText().toString());
                            String[] s = {loginUserName};
                            db.update("registe_info", contentValues, "name=?", s);
                            Toast.makeText(MainActivity.this, "密码修改成功,请重新登陆", Toast.LENGTH_SHORT).show();
                            String sql = "select * from login_info where loginname='" + loginUserName + "'";
                            Cursor cursor = db.rawQuery(sql, null);
                            cursor.moveToFirst();
                            if (cursor.getInt(cursor.getColumnIndex("autologin")) == 1) {
                                contentValues.clear();
                                contentValues.put("loginpassword", changePasswordPassword.getText().toString());
                                db.update("login_info", contentValues, "loginname=?", s);
                                contentValues.clear();
                            }
                            db.close();
                            ad.dismiss();
                            myHelper.close();
                            Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                            loginUserName="";
                            startActivity(intent);
                            finish();
                        }
                    }
                });
                changePasswordCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ad.dismiss();
                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(View v) {
        intent=new Intent(this,activity_add.class);
        startAnimaiton_disapear();
        switch (v.getId()) {
            case R.id.fab_text:
                intent.putExtra("message","text");
                intent.putExtra("loginName",loginUserName);
                startActivity(intent);
                break;
            case R.id.fab_image:
                intent.putExtra("message","image");
                intent.putExtra("loginName",loginUserName);
                startActivity(intent);
                break;
            case R.id.fab_vedio:
                intent.putExtra("message","vedio");
                intent.putExtra("loginName",loginUserName);
                startActivity(intent);
                break;
        }
    }
    //重写onCreateOptionsMenu方法，在顶部的bar中显示菜单
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_user_menu, menu);
        setIconVisible(menu,true);
        return true;
    }

    //重写onKeyDown方法，监听返回键
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode==KeyEvent.KEYCODE_BACK) {
            if (fab_Text.getVisibility() == View.VISIBLE) {
                startAnimaiton_disapear();
                return false;
            }
            else {
                builder = new AlertDialog.Builder(MainActivity.this).setTitle("退出?").setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ad.dismiss();
                        finish();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ad.dismiss();
                    }
                });
                ad = builder.create();
                ad.show();
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
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
