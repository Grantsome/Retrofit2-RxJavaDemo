package com.grantsome.retrofit2test;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.get_girl)
    Button mGirlButton;

    @Bind(R.id.post_bihu)
    Button mBihuButton;

    @Bind(R.id.display_text)
    TextView mDisplayText;

    @Bind(R.id.upload)
    Button mUploadButton;

    @Bind(R.id.download)
    Button mDownloadButton;

    @Bind(R.id.display_image)
    ImageView mDisplayImage;

    private static final int OPEN_ALBUM = 0;

    private static  String path;

    public static Context sContext;

    @OnClick(R.id.get_girl)
    public void ClickOnGetGirl(){
        mDisplayText.setVisibility(View.VISIBLE);
        mDisplayImage.setVisibility(View.GONE);
        HttpUtils.getGirl(mDisplayText);
    }

    @OnClick(R.id.post_bihu)
    public void ClickOnPostHihu(){
        mDisplayText.setVisibility(View.VISIBLE);
        mDisplayImage.setVisibility(View.GONE);
        HttpUtils.checkBihuLogin(mDisplayText);
    }

    @OnClick(R.id.upload)
    public void ClickOnUpLoad(){
        mDisplayText.setVisibility(View.VISIBLE);
        mDisplayImage.setVisibility(View.GONE);
        checkAndOpenAlbum();
    }

    @OnClick(R.id.download)
    public void ClickOnDownload(){
        mDisplayText.setVisibility(View.GONE);
        mDisplayImage.setVisibility(View.VISIBLE);
        HttpUtils.downloadImage(mDisplayImage);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        sContext = this;
    }

    //用户同意/拒绝,activity的onRequestPermissionsResult会被回调来通知结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @Nullable String[] permissions, @Nullable int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1 & grantResults.length > 0 & grantResults[0] == PackageManager.PERMISSION_GRANTED){
            openAlbum();
        }
    }

    public void checkAndOpenAlbum(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},OPEN_ALBUM);
        }else {
            openAlbum();
        }
    }

    public void openAlbum() {
        //选择数据
        Intent intent = new Intent(Intent.ACTION_PICK,null);
        //指定数据类型为图片类型: String IMAGE_UNSPECIFIED = "image/*";
        intent.setType("image/*");
        startActivityForResult(intent,OPEN_ALBUM);
    }

    public void cropImage(String imageUriString,String outputUriString){
        Intent intent = new Intent("com.android.camera.action.CROP");
        //aspectX aspectY 是长宽比
        intent.setDataAndType(Uri.parse(imageUriString),"image/*");
        intent.putExtra("crop","true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("return-data",false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.parse(outputUriString));
        //Bitmap.CompressFormat.JPEG:返回string类型的枚举常量 JPEG
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        upLoadAvatar(intent.getData());
    }

    private void upLoadAvatar(Uri uri){
        if(uri!=null) {
            File file = uri2File(uri);
            if(null!=file){
                HttpUtils.UpLoadImage(mDisplayText,file);
            }
        }
    }

    private File uri2File(Uri uri) {
            String path = null;
            if ("file".equals(uri.getScheme())) {
                path = uri.getEncodedPath();
                if (path != null) {
                    path = Uri.decode(path);
                    ContentResolver cr = this.getContentResolver();
                    StringBuffer buff = new StringBuffer();
                    buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=").append("'" + path + "'").append(")");
                    Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA }, buff.toString(), null, null);
                    int index = 0;
                    int dataIdx = 0;
                    for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                        index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                        index = cur.getInt(index);
                        dataIdx = cur.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                        path = cur.getString(dataIdx);
                    }
                    cur.close();
                    if (index == 0) {
                    } else {
                        Uri u = Uri.parse("content://media/external/images/media/" + index);
                        System.out.println("temp uri is :" + u);
                    }
                }
                if (path != null) {
                    return new File(path);
                }
            } else if ("content".equals(uri.getScheme())) {
                // 4.2.2以后
                String[] proj = { MediaStore.Images.Media.DATA };
                Cursor cursor = this.getContentResolver().query(uri, proj, null, null, null);
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    path = cursor.getString(columnIndex);
                }
                cursor.close();

                return new File(path);
            } else {

            }
            return null;
        }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case OPEN_ALBUM:
                    path = ImageUtils.parseImageUriString(data);
                    cropImage(ImageUtils.parseImageUriString(data), "file://" + getExternalCacheDir() + "/" +System.currentTimeMillis());
                    break;

            }
        }
    }

}
