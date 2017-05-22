package com.example.sunkai.mynotebook;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
    private Button loginButton;
    private LinearLayout l1;
    private mySqlLite myHelper;
    private AutoCompleteTextView email;
    private EditText password;
    private ArrayAdapter loginName;
    private ArrayAdapter loginPassword;
    private CheckBox rememberPsswordCheckBox;
    private String rememberPassword;
    private TextView forget_password_textView;
    int ok=0;
    private AlertDialog.Builder builder;
    AlertDialog ad;
    int[] error={0,0,0,1,1};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                submit();
            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(isRememberPassWord())
                {
                    rememberPsswordCheckBox.setChecked(true);
                    password.setText(rememberPassword);
                }
                else
                {
                    password.setText("");
                    rememberPsswordCheckBox.setChecked(false);
                }
            }
        });
        forget_password_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(LoginActivity.this).setTitle("密码找回").setView(R.layout.find_password_layout);
                ad = builder.create();
                ad.show();
                final EditText find_password_user=(EditText)ad.findViewById(R.id.find_password_username);
                final EditText find_password_question=(EditText)ad.findViewById(R.id.find_password_question);
                final EditText find_password_answer=(EditText)ad.findViewById(R.id.find_password_answer);
                final TextView queding=(TextView)ad.findViewById(R.id.find_password_queding);
                final TextView cancel=(TextView)ad.findViewById(R.id.find_password_cancel);
                find_password_user.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(!find_replicate_name(find_password_user.getText().toString()))
                        {
                            SQLiteDatabase db = myHelper.getReadableDatabase();
                            try {
                                String sql = "select * from registe_info where name='" + find_password_user.getText().toString() + "'";
                                Cursor cursor = db.rawQuery(sql, null);
                                cursor.moveToFirst();
                                int findPasswordQuestion = cursor.getColumnIndex("findPasswordQuestion");
                                int findPasswordAnswer = cursor.getColumnIndex("findPasswordAnswer");
                                find_password_question.setText(cursor.getString(findPasswordQuestion));
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            finally {
                                db.close();
                            }
                        }
                    }
                });
                queding.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!find_replicate_name(find_password_user.getText().toString()))
                        {
                            SQLiteDatabase db = myHelper.getReadableDatabase();
                            try{
                                String sql = "select * from registe_info where name='" + find_password_user.getText().toString() + "'";
                                Cursor cursor = db.rawQuery(sql, null);
                                cursor.moveToFirst();
                                if(find_password_answer.getText().toString().equals(cursor.getString(cursor.getColumnIndex("findPasswordAnswer"))))
                                {
                                    final String username;
                                    username=find_password_user.getText().toString();
                                    ad.dismiss();
                                    builder.setTitle("修改密码");
                                    builder.setView(R.layout.change_password_layout);
                                    ad=builder.create();
                                    ad.show();
                                    final EditText changePasswordUser=(EditText)ad.findViewById(R.id.change_psssword_username);
                                    final EditText changePasswordPassword=(EditText)ad.findViewById(R.id.change_psssword_input);
                                    final EditText changePasswordPsswordInsre=(EditText)ad.findViewById(R.id.change_psssword_input_insure);
                                    final TextView changePasswordCancel=(TextView)ad.findViewById(R.id.change_password_cancel);
                                    final TextView changepasswordQueding=(TextView)ad.findViewById(R.id.change_password_queding);
                                    changePasswordUser.setText(username);
                                    changepasswordQueding.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if(changePasswordPassword.getText().toString().isEmpty()||changePasswordPsswordInsre.getText().toString().isEmpty())
                                            {
                                                Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                                            }
                                            else if(!changePasswordPassword.getText().toString().equals(changePasswordPsswordInsre.getText().toString()))
                                            {
                                                Toast.makeText(LoginActivity.this, "密码输入不同", Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                            {
                                                SQLiteDatabase db = myHelper.getReadableDatabase();
                                                ContentValues contentValues=new ContentValues();
                                                contentValues.put("password",changePasswordPassword.getText().toString());
                                                String[] s = {username};
                                                db.update("registe_info",contentValues,"name=?",s);
                                                Toast.makeText(LoginActivity.this, "密码修改成功", Toast.LENGTH_SHORT).show();
                                                String sql = "select * from login_info where loginname='" + username + "'";
                                                Cursor cursor = db.rawQuery(sql, null);
                                                cursor.moveToFirst();
                                                if(cursor.getInt(cursor.getColumnIndex("autologin"))==1)
                                                {
                                                    contentValues.clear();
                                                    contentValues.put("loginpassword",changePasswordPassword.getText().toString());
                                                    db.update("login_info",contentValues,"loginname=?",s);
                                                    contentValues.clear();
                                                }
                                                db.close();
                                                ad.dismiss();
                                            }
                                        }
                                    });
                                    changePasswordCancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ad.dismiss();
                                        }
                                    });
                                }
                                else
                                {
                                    Toast.makeText(LoginActivity.this, "密码找回答案有误", Toast.LENGTH_SHORT).show();
                                }
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            finally {
                                db.close();
                            }
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "错误", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ad.dismiss();
                    }
                });
            }
        });
    }
    private void initView() {
        l1 = (LinearLayout) findViewById(R.id.line1);
        loginButton = (Button) findViewById(R.id.sign_in_button);
        email = (AutoCompleteTextView) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        loginName=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        loginPassword=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        rememberPsswordCheckBox=(CheckBox)findViewById(R.id.rememberPasswordCheckBox);
        forget_password_textView=(TextView)findViewById(R.id.forget_password_textView);
        myHelper = new mySqlLite(this, "registe.db", null, 1);
        readAutoTextString();
        email.setAdapter(loginName);
    }

    //读取数据库中已有的用户名，AutoCompleteTextView控件获得arrayAdpter从而可以自动提示用户名
    private void readAutoTextString()
    {
        SQLiteDatabase db=myHelper.getReadableDatabase();
        Cursor cursor=db.query("login_info",null,null,null,null,null,"id");
        int nameIndex=cursor.getColumnIndex("loginname");
        int passwordIndex=cursor.getColumnIndex("loginpassword");
        int AutoLoginIndex=cursor.getColumnIndex("autologin");
        for(cursor.moveToFirst();!(cursor.isAfterLast());cursor.moveToNext())
        {
            loginName.add(cursor.getString(nameIndex));
            loginPassword.add(cursor.getString(passwordIndex));
        }
    }

    //如果这个用户名记住了密码，就让密码EditText自动填充输入
    private boolean isRememberPassWord()
    {
        SQLiteDatabase db=myHelper.getReadableDatabase();
        Cursor cursor=db.query("login_info",null,null,null,null,null,"id");
        int nameIndex=cursor.getColumnIndex("loginname");
        int passwordIndex=cursor.getColumnIndex("loginpassword");
        int AutoLoginIndex=cursor.getColumnIndex("autologin");
        for(cursor.moveToFirst();!(cursor.isAfterLast());cursor.moveToNext())
        {
            if(cursor.getString(nameIndex).equals(email.getText().toString()))
            {
                if(cursor.getInt(AutoLoginIndex)==1)
                {
                    rememberPassword=cursor.getString(passwordIndex);
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
        return  false;
    }


    //隐藏键盘
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    //显示键盘
    private void showKeyboard()
    {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    showSoftInput(view,1);
        }
    }

    //查找是否有重名的存在
    private boolean find_replicate_name(String changeName)
    {
        SQLiteDatabase db=myHelper.getReadableDatabase();
        Cursor cursor=db.query("registe_info",null,null,null,null,null,"id");
        int nameIndex=cursor.getColumnIndex("name");
        for(cursor.moveToFirst();!(cursor.isAfterLast());cursor.moveToNext()) {
            if (cursor.getString(nameIndex).equals(changeName)) {
                return false;
            }
        }
        return true;
    }

    //添加用户
    void addUser(String name,String password,String findPasswordQuestion,String findPasswordAnswer)
    {
        SQLiteDatabase db=myHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("name",name);
        values.put("password",password);
        values.put("findPasswordQuestion",findPasswordQuestion);
        values.put("findPasswordAnswer",findPasswordAnswer);
        if(db.insert("registe_info",null,values)>0)
            Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
        values.clear();
        values.put("loginname",name);
        values.put("loginpassword",password);
        db.insert("login_info",null,values);
        ContentValues contentValues=new ContentValues();
        contentValues.put("autologin",1);
        String[] s={name};
        if(rememberPsswordCheckBox.isChecked())
        {
            db.update("login_info",contentValues,"loginname=?",s);
        }
    }


    private void registe_or_login()
    {
        String userNameString=email.getText().toString().trim();
        String passwordString = password.getText().toString().trim();
        SQLiteDatabase db=myHelper.getReadableDatabase();
        Cursor cursor=db.query("registe_info",null,null,null,null,null,"id");
        int nameIndex=cursor.getColumnIndex("name");
        int passwordIndex=cursor.getColumnIndex("password");
        int position=cursor.getColumnIndex("category");
        //查找是否有相同的登录名，如果有则检验密码，如果没有则进入注册页面
        for(cursor.moveToFirst();!(cursor.isAfterLast());cursor.moveToNext()) {
            if (cursor.getString(nameIndex).equals(userNameString)) {
                if(cursor.getString(passwordIndex).equals(passwordString)) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    ok=1;
                    ContentValues contentValues=new ContentValues();
                    contentValues.put("autologin",1);
                    String[] s={userNameString};
                    if(rememberPsswordCheckBox.isChecked())
                    {
                        db.update("login_info",contentValues,"loginname=?",s);
                    }
                    else
                    {
                        contentValues.clear();
                        contentValues.put("autologin",0);
                        db.update("login_info",contentValues,"loginname=?",s);
                    }
//                    intent.putExtra()
                    finish();
                    intent.putExtra("loginName",userNameString);
                    MainActivity.change=1;
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
        //没有用户名，则进入登陆页面
        if(ok==0) {
            builder = new AlertDialog.Builder(this).setTitle("注册").setView(R.layout.registe_layout);
            ad = builder.create();
            ad.show();
//        showKeyboard();
            final AutoCompleteTextView registeName = (AutoCompleteTextView) ad.findViewById(R.id.registeEditView);
            final EditText registePassword = (EditText) ad.findViewById(R.id.registepassword);
            final EditText registePasswordConfirm = (EditText) ad.findViewById(R.id.registepassword_confirm);
            final EditText registeFindPasswordQuestion = (EditText) ad.findViewById(R.id.registe_find_password_question);
            final EditText registeFindPasswordAnswer = (EditText) ad.findViewById(R.id.registe_find_password_answer);
            final LinearLayout registeLinerLayout = (LinearLayout) ad.findViewById(R.id.registe_LinerLayout);
            final TextView t1 = (TextView) ad.findViewById(R.id.t1);
            final TextView t2 = (TextView) ad.findViewById(R.id.t2);
            final TextView t3 = (TextView) ad.findViewById(R.id.t3);
            final TextView t4 = (TextView) ad.findViewById(R.id.t4);
            final TextView t5 = (TextView) ad.findViewById(R.id.t5);
            final TextView t6 = (TextView) ad.findViewById(R.id.t6);
            final TextView t7 = (TextView) ad.findViewById(R.id.t7);
            t4.setText("不能为空");
            t5.setText("不能为空");
            t6.setClickable(true);
            //在用户点击注册之后对所有注册信息空间进行输入正确性检查，如果通过则注册，如果没有通过则返回错误
            t6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ok = 1;
                    for (int i = 0; i < error.length; i++) {
                        if (error[i] == 1) {
                            ok = 0;
                            Toast.makeText(LoginActivity.this, "部分内容输入有误", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                    addUser(registeName.getText().toString(), registePassword.getText().toString(), registeFindPasswordQuestion.getText().toString(), registeFindPasswordAnswer.getText().toString());
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("loginName",registeName.getText().toString());
                    ad.hide();
                    MainActivity.change=1;
                    finish();
                    startActivity(intent);
                }
            });
            t7.setClickable(true);
            t7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ad.hide();
                }
            });
            registeLinerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideKeyboard();
                }
            });

            //以下均为各个控件的输入完整性检查
            ad.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    showKeyboard();
                    registePasswordConfirm.setFocusable(true);
                }
            });
            registeName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!find_replicate_name(registeName.getText().toString())) {
                        t1.setText("已有该用户名!");
                        error[0] = 1;
                    } else {
                        t1.setText("");
                        error[0] = 0;
                    }
                }
            });
            registePassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String Password = registePassword.getText().toString();
                    if (Password.isEmpty()) {
                        t2.setText("密码不能为空");
                        error[1] = 1;
                    } else if (!Password.equals(registePasswordConfirm.getText().toString())) {
                        t2.setText("");
                        t3.setText("密码不一致");
                        error[1] = 1;
                    } else {
                        t2.setText("");
                        t3.setText("");
                        error[1] = 0;
                        error[2] = 0;
                    }
                }
            });
            registePasswordConfirm.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String confirmPassword = registePasswordConfirm.getText().toString();
                    if (confirmPassword.isEmpty()) {
                        t3.setText("密码不能为空");
                        error[2] = 1;
                    } else if (!confirmPassword.equals(registePassword.getText().toString())) {
                        t3.setText("密码不一致");
                        error[2] = 1;
                    } else {
                        t3.setText("");
                        error[2] = 0;
                        error[1] = 0;
                    }
                }
            });
            registeFindPasswordQuestion.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    {
                    }

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (registeFindPasswordQuestion.getText().toString().isEmpty()) {
                        t4.setText("不能为空");
                        error[3] = 1;
                    } else {
                        t4.setText("");
                        error[3] = 0;
                    }
                }
            });
            registeFindPasswordAnswer.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (registeFindPasswordAnswer.getText().toString().isEmpty()) {
                        t5.setText("不能为空");
                        error[4] = 1;
                    } else {
                        t5.setText("");
                        error[4] = 0;
                    }
                }
            });
            registeName.setText(userNameString);
            registePassword.setText(passwordString);
//        registePasswordConfirm.setFocusable(true);
            return;
        }
    }

    private void submit() {
        // validate
        String userNameString=email.getText().toString().trim();
        String passwordString = password.getText().toString().trim();
        if(TextUtils.isEmpty(userNameString)&&TextUtils.isEmpty(passwordString))
        {
            Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(userNameString))
        {
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(passwordString)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        registe_or_login();
    }
}
