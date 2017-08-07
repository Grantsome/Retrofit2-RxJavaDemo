package com.grantsome.retrofit2test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Grantsome on 2017/8/6.
 */

public class HttpUtils {

    public static final String BIHU_BASE_URL = "https://api.caoyue.com.cn/bihu/";
                                    //接口说明文档：https://github.com/haruue/bihu_web/blob/master/api.md
                                    //接口示例： https://api.caoyue.com.cn/bihu/login.phpusername=Grantsome&password=wsgsl19980114

    public static final String IMAGE_TOKEN = "e80d720b6a5ed5d5660fb3c5dbe1426a369a216f:wPEyuWVkft5hVPt8n3vrvDNGUUY=:eyJkZWFkbGluZSI6MTUwMjA3MjI0MywiYWN0aW9uIjoiZ2V0IiwidWlkIjoiNjAzNzEwIiwiYWlkIjoiMTMzOTYxNiIsImZyb20iOiJmaWxlIn0=";
                                    //自己的贴图库的Token

    public static final int ID = 1339616;

    public static final String BASE_UP_URL = "http://up.imgapi.com";
                                    //贴图库的开发者文档 http://www.tietuku.com/doc  贴图库图床的首页 http://www.tietuku.com/

    public static final String BASE_DOWBLOAD = "http://i2.tiimg.com/";
                                    //url的真正地址：http://i2.tiimg.com/fed3e1c4dc63d1ab.jpg

    public static final String GANK_GIRL = "http://gank.io/api/data/%E7%A6%8F%E5%88%A9/";//Retrofit2与之前版本的不同，base url 必须要以/结尾
                                 //接口示例：http://gank.io/api/data/%E7%A6%8F%E5%88%A9/10/1
    public static void getGirl(final TextView displayTextView){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GANK_GIRL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GirlApi girlApi = retrofit.create(GirlApi.class);
        Call<GirlList> call = girlApi.getGirl(10,1);
        call.enqueue(new Callback<GirlList>() {
            @Override
            public void onResponse(Call<GirlList> call, final Response<GirlList> response) {
                //讨厌的更新UI的操作:view.post/handler.post/handler.handlerMessage
                displayTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        displayTextView.setText(response.body().toString());
                    }
                });
            }

            @Override
            public void onFailure(Call<GirlList> call, Throwable t) {

            }
        });
    }

    public interface GirlApi{

        @GET("{count}/{page}")
        Call<GirlList> getGirl(@Path("count") int count,
                               @Path("page") int page);
    }

    public static void checkBihuLogin(final TextView displayTextView){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BIHU_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        BihuLoginApi loginApi = retrofit.create(BihuLoginApi.class);
        Call<LoginInfo> call = loginApi.CheckLogin("Grantsome","wsgsl19980114");
        call.enqueue(new Callback<LoginInfo>() {
            @Override
            public void onResponse(Call<LoginInfo> call, final Response<LoginInfo> response) {
                //看着很不爽的更新UI的操作,一定要把它给干掉
                displayTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        displayTextView.setText(response.body().toString());
                    }
                });
            }

            @Override
            public void onFailure(Call<LoginInfo> call, Throwable t) {

            }
        });
    }

    public interface BihuLoginApi{

        @FormUrlEncoded
        @POST("login.php")
        Call<LoginInfo> CheckLogin(@Field("username") String username,
                                   @Field("password") String password);
    }

    public static void UpLoadImage(final TextView displayTextView,File mFile){
        File file = mFile;
        RequestBody requestBodyFile = RequestBody.create(MediaType.parse("image/*"),file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file",file.getName(),requestBodyFile);
        RequestBody token = RequestBody.create(MediaType.parse("multipart-data"),IMAGE_TOKEN);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_UP_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        UpLoadApi loadApi = retrofit.create(UpLoadApi.class);
        Call<ImageInfo> call = loadApi.upload(token,part,(int) getUnixStamp(),ID,"file",2);
        call.enqueue(new Callback<ImageInfo>() {
            @Override
            public void onResponse(Call<ImageInfo> call, final Response<ImageInfo> response) {
                displayTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        displayTextView.setText(response.body().toString());
                    }
                });
            }

            @Override
            public void onFailure(final Call<ImageInfo> call, final Throwable t) {
                displayTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        displayTextView.setText("上传失败"+call.toString()+t.toString());
                    }
                });
            }
        });
    }

    public interface UpLoadApi{

        @Multipart
        @POST("/")
        Call<ImageInfo> upload(@Part("Token") RequestBody token,
                               @Part MultipartBody.Part file,
                               @Query("deadline") int deadline,
                               @Query("aid") int aid,
                               @Query("from") String from,
                               @Query("httptype") int httptype);
    }


    public static long getUnixStamp() {
        return System.currentTimeMillis() / 1000 + 60;
    }

//    public static void downloadImage(final ImageView displayImage){
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(BASE_DOWBLOAD)
//                .build();
//        DownloadApi downloadApi = retrofit.create(DownloadApi.class);
//        Call<ResponseBody> call = downloadApi.startDownload("fed3e1c4dc63d1ab.jpg");
//        call.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    InputStream is = response.body().byteStream();
//                    final Bitmap bitmap = BitmapFactory.decodeStream(is);
//                    displayImage.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            displayImage.setImageBitmap(bitmap);
//                        }
//                    });
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//                }
//        });
//    }

    public static void downloadImage(final ImageView displayImage){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_DOWBLOAD)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        DownloadApi downloadApi = retrofit.create(DownloadApi.class);
        downloadApi.startDownload("fed3e1c4dc63d1ab.jpg")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {
                        Log.d("HttpUtils.download", "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("HttpUtils.download", "onError: ");
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        InputStream is = responseBody.byteStream();
                        final Bitmap bitmap = BitmapFactory.decodeStream(is);
                        displayImage.setImageBitmap(bitmap);
                    }
                });

    }

//    public interface DownloadApi{
//
//        @GET("{photo}")
//        Call<ResponseBody> startDownload(@Path("photo") String photo);
//    }

    public interface DownloadApi{

        @GET("{photo}")
        Observable<ResponseBody> startDownload(@Path("photo") String photo);
    }

}
