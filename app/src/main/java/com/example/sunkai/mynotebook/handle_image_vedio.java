package com.example.sunkai.mynotebook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sunkai on 2016/12/16.
 */


//此类用于处理用户选择、拍摄的相片或者视频以及得到系统时间
public class handle_image_vedio {


    public static String getTime(){
        return new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒").format(new Date());
    }


    public static Bitmap getImage(String uri, int width, int height){
        Bitmap bitmap=null;
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        bitmap=BitmapFactory.decodeFile(uri,options);
        options.inJustDecodeBounds=false;
        int beWidth=options.outWidth/width;
        int beHeight=options.outHeight/height;
        int be=1;
        if(beWidth<beHeight){
            be=beWidth;
        }else{
            be=beHeight;
        }
        if(be<=0){
            be=1;
        }
        options.inSampleSize = be;
        bitmap=BitmapFactory.decodeFile(uri, options);
        Bitmap newbitmap= ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return newbitmap;
    }
    public static Bitmap getVedio(String uri,int width,int height,int kind){
        Bitmap bitmap=null;
        bitmap=ThumbnailUtils.createVideoThumbnail(uri,kind);
        bitmap=ThumbnailUtils.extractThumbnail(bitmap,width,height,ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }
}
