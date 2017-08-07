package com.grantsome.retrofit2test;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;

/**
 * Created by Grantsome on 2017/8/7.
 */

public class ImageUtils {

    public static String parseImageUriString(Intent data){
        String uriString;
        if(Build.VERSION.SDK_INT>=19){
            uriString = handlerImageOnKitKat(data);
        }else {
            uriString = handleImageBeforeKitKat(data);
        }
        return "file://" + uriString;
    }

    private static String handleImageBeforeKitKat(Intent data){
        return getImagePath(data.getData(),null);
    }

    @RequiresApi (api = Build.VERSION_CODES.KITKAT)
    private static String handlerImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(ApplicationContext.getContext(),uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if(DocumentsContract.isDocumentUri(ApplicationContext.getContext(),uri)){
                if("com.android.provider.media.documents".equals(uri.getAuthority())){
                    String id = docId.split(":")[1];
                    String selection = MediaStore.Images.Media._ID + "=" + id;
                    imagePath = getImagePath(uri,selection);
                }
            }else if("com.android.provider.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            imagePath = getImagePath(uri,null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            imagePath = getImagePath(uri,null);
        }
        return imagePath;
    }

    private static String getImagePath(Uri uri,String selection){
        String path = null;
        Cursor cursor = MainActivity.sContext.getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }


}
