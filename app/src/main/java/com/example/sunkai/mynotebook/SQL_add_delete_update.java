package com.example.sunkai.mynotebook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunkai on 2016/12/18.
 */

//此类用于数据库的读写操作
public class SQL_add_delete_update {


    
    public static void add(Context context,String loginName,String title,String content,String time,String imgPath,String vedioPath)
    {
        mySqlLite myHelper=new mySqlLite(context, "registe.db", null, 1);
        SQLiteDatabase db=myHelper.getWritableDatabase();
        ContentValues values=null;
        try
        {
            values=new ContentValues();
            values.put("username",loginName);
            values.put("title",title);
            values.put("content",content);
            values.put("time",time);
            values.put("img",imgPath);
            values.put("vedio",vedioPath);
            db.insert("notesbook", null, values);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            values.clear();
            db.close();
            myHelper.close();
        }
    }

    public static void update(Context context,int id,String loginName,String title,String content,String time,String imagePath,String vedioPath)
    {
        mySqlLite myhelper=new mySqlLite(context,"registe.db",null,1);
        ContentValues contentValues=new ContentValues();
        SQLiteDatabase db=myhelper.getWritableDatabase();
        try {
            contentValues.put("id", id);
            contentValues.put("username", loginName);
            contentValues.put("title", title);
            contentValues.put("content", content);
            contentValues.put("time", time);
            contentValues.put("img", imagePath);
            contentValues.put("vedio", vedioPath);
            String[] s = {String.valueOf(id)};
            db.update("notesbook", contentValues, "id=?", s);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            contentValues.clear();
            db.close();
            myhelper.close();
        }
    }

    public static void delete(Context context,int id)
    {
        mySqlLite myhelper=new mySqlLite(context,"registe.db",null,1);
        ContentValues contentValues=new ContentValues();
        SQLiteDatabase db=myhelper.getWritableDatabase();
        try {
            String[] s = {String.valueOf(id)};
            db.delete("notesbook", "id=?", s);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            contentValues.clear();
            db.close();
            myhelper.close();
        }
    }

    public static List<note> findUserNotes(Context context,String loginName,Integer id)
    {
        mySqlLite myHelper=new mySqlLite(context, "registe.db", null, 1);
        SQLiteDatabase db=myHelper.getReadableDatabase();
        ContentValues values=null;
        List<note> list=null;
        try{
            values=new ContentValues();
            StringBuffer stringBuffer=new StringBuffer();
            if(id==null)
                stringBuffer.append("select * from notesbook where username='"+loginName+"' order by time desc");
           else
                stringBuffer.append("select * from notesbook where username='"+loginName+"' and id="+id.toString()+" order by time desc");
            String sql=stringBuffer.toString();
            Cursor cursor=db.rawQuery(sql,null);
            note notes;
            int index=cursor.getColumnIndex("id");
            int title=cursor.getColumnIndex("title");
            int content=cursor.getColumnIndex("content");
            int img=cursor.getColumnIndex("img");
            int vedio=cursor.getColumnIndex("vedio");
            int time=cursor.getColumnIndex("time");
            list=new ArrayList<note>();
            for(cursor.moveToFirst();!(cursor.isAfterLast());cursor.moveToNext())
            {
//                Toast.makeText(context, cursor.getString(title), Toast.LENGTH_SHORT).show();
                notes=new note();
                notes.id=cursor.getInt(index);
                notes.title=cursor.getString(title);
                notes.content=cursor.getString(content);
                notes.picPath=cursor.getString(img);
                notes.vedioPath=cursor.getString(vedio);
                notes.time=cursor.getString(time);
                //在读取数据库的时候便提前预处理了图片，防止在ListView添加adapter的时候重复调用函数绘制图片造成程序卡顿
                //此操作将增加消耗时间
                if(!notes.picPath.equals("null"))
                    notes.picturtebitmap=handle_image_vedio.getImage(notes.picPath,200,260);
                if(!notes.vedioPath.equals("null"))
                    notes.vediobitmap=handle_image_vedio.getVedio(notes.vedioPath,200, 260, MediaStore.Images.Thumbnails.MICRO_KIND);
                list.add(notes);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            db.close();
            myHelper.close();
        }
        return list;
    }

}
