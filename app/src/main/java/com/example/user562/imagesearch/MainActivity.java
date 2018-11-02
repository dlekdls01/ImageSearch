package com.example.user562.imagesearch;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText editSearch = null;
    private ImageView[] imgViews = null;
    private List<HashMap> imgList = new ArrayList();
    private ProgressDialog progressDialog =null;

    private String URL_SEARCH = "https://secure.flickr.com/services/rest/?method=flickr.photos.search";
    private String API_KEY = "&api_key=9188c449ce22997fcdf71ca0872f4924";
    private String PER_PAGE = "&per_page=28";
    private String SORT = "&sort=interestingness-desc";
    private String FORMAT = "&format=json";
    private String CONTECT_TYPE = "&content_type=1";
    private String SEARCH_TEXT = "&text=";
    private String URL_REQUEST = URL_SEARCH + API_KEY + PER_PAGE + SORT + FORMAT + CONTECT_TYPE + SEARCH_TEXT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        editSearch = (EditText)findViewById(R.id.edit_search);
        imgViews = new ImageView[28];
        imgViews[0] = (ImageView)findViewById(R.id.imgV0);
        imgViews[1] = (ImageView)findViewById(R.id.imgV1);
        imgViews[2] = (ImageView)findViewById(R.id.imgV2);
        imgViews[3] = (ImageView)findViewById(R.id.imgV3);
        imgViews[4] = (ImageView)findViewById(R.id.imgV4);
        imgViews[5] = (ImageView)findViewById(R.id.imgV5);
        imgViews[6] = (ImageView)findViewById(R.id.imgV6);
        imgViews[7] = (ImageView)findViewById(R.id.imgV7);
        imgViews[8] = (ImageView)findViewById(R.id.imgV8);
        imgViews[9] = (ImageView)findViewById(R.id.imgV9);
        imgViews[10] = (ImageView)findViewById(R.id.imgV10);
        imgViews[11] = (ImageView)findViewById(R.id.imgV11);
        imgViews[12] = (ImageView)findViewById(R.id.imgV12);
        imgViews[13] = (ImageView)findViewById(R.id.imgV13);
        imgViews[14] = (ImageView)findViewById(R.id.imgV14);
        imgViews[15] = (ImageView)findViewById(R.id.imgV15);
        imgViews[16] = (ImageView)findViewById(R.id.imgV16);
        imgViews[17] = (ImageView)findViewById(R.id.imgV17);
        imgViews[18] = (ImageView)findViewById(R.id.imgV18);
        imgViews[19] = (ImageView)findViewById(R.id.imgV19);
        imgViews[20] = (ImageView)findViewById(R.id.imgV20);
        imgViews[21] = (ImageView)findViewById(R.id.imgV21);
        imgViews[22] = (ImageView)findViewById(R.id.imgV22);
        imgViews[23] = (ImageView)findViewById(R.id.imgV23);
        imgViews[24] = (ImageView)findViewById(R.id.imgV24);
        imgViews[25] = (ImageView)findViewById(R.id.imgV25);
        imgViews[26] = (ImageView)findViewById(R.id.imgV26);
        imgViews[27] = (ImageView)findViewById(R.id.imgV27);

        for(int i=0; i<imgViews.length; i++)
            imgViews[i].setVisibility(View.INVISIBLE);

    }


    public void onClick(View v){

        final String text = editSearch.getText().toString();
        if(text.isEmpty()) return;

        for(int i=0; i<imgViews.length; i++)
            imgViews[i].setVisibility(View.VISIBLE);

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        AsyncTask asyncTask = new AsyncTask() {

            String result;

            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    URL url = new URL(URL_REQUEST+text);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);              //읽기모드
                    conn.setDoOutput(true);             //쓰기모드
                    conn.setUseCaches(false);           //캐시데이터
                    conn.setDefaultUseCaches(false);    //캐시데이터디폴트값

                    InputStream inputStream = conn.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while ((line=bufferedReader.readLine()) != null){
                        stringBuilder.append(line);
                    }

                    bufferedReader.close();
                    conn.disconnect();
                    result = stringBuilder.toString().trim();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                publishProgress(0,null,null);

                //----------
                Parser(result);

                Bitmap bitmap = null;
                try{
                    for(int i=0; i<imgList.size(); i++) {

                        HashMap hashMap = (HashMap) imgList.get(i);
                        String StrUrl = "http://farm" + hashMap.get("farm").toString() + ".staticflickr.com/" + hashMap.get("server").toString()
                                + "/" + hashMap.get("id").toString() + "_" + hashMap.get("secret").toString() + "_t.jpg";

                        bitmap = GetImage(StrUrl);

                        publishProgress(1,i,bitmap);
                    }

                }catch (Exception e){
                    e.getStackTrace();
                    Toast.makeText(MainActivity.this, e.toString(),Toast.LENGTH_LONG).show();
                   // return false;
                }
                return bitmap;
            }

            @Override
            protected void onProgressUpdate(Object[] values) {
                super.onProgressUpdate(values);

                if((int)values[0] == 0){
                    progressDialog.dismiss();
                }
                else if((int)values[0] == 1) {

                    Bitmap b = (Bitmap) values[2];
                    imgViews[(int)values[1]].setImageBitmap(b);
                }
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
            }
        }.execute();
    }


    public boolean Parser(String str){
        if(str.isEmpty()) return false;

        str = str.replace("jsonFlickrApi(", "");
        str = str.replace(")","");

        try{
            JSONObject jsonObject = new JSONObject(str);
            JSONObject photos = jsonObject.getJSONObject("photos");
            JSONArray photo = photos.getJSONArray("photo");
            imgList.clear();

            for(int i=0; i<photo.length(); i++){
                JSONObject photoInfo = photo.getJSONObject(i);

                HashMap hashMap = new HashMap();
                hashMap.put("id", photoInfo.getString("id"));
                hashMap.put("secret", photoInfo.getString("secret"));
                hashMap.put("server", photoInfo.getString("server"));
                hashMap.put("farm", photoInfo.getString("farm"));
                hashMap.put("title", photoInfo.getString("title"));

                imgList.add(hashMap);


            }

        }catch (Exception e){
            e.getStackTrace();
            return  false;
        }
        return true;
    }

    public Bitmap GetImage(String StrUrl){
        Bitmap bitmap = null;
        try{
            URL url = new URL(StrUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoInput(true);
            conn.setRequestMethod("GET");
            conn.setUseCaches(false);
            conn.connect();

            InputStream inputStream = conn.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            bitmap = BitmapFactory.decodeStream(bufferedInputStream);

            bufferedInputStream.close();
            conn.disconnect();

        }catch (Exception e){

        }
        return bitmap;
    }

    // Click imageView
    public void onClickImg(View v){
        int idx = 0;
        for(int i=0; i<imgViews.length; i++){
            if(imgViews[i] == (ImageView)v){
                idx = i;
            }
        }

        AsyncTask asyncTask = new AsyncTask() {
            Bitmap bitmap = null;
            HashMap hashMap = null;
            @Override
            protected Object doInBackground(Object[] objects) {
                hashMap = (HashMap) imgList.get((int)objects[0]);
                String StrUrl = "http://farm" + hashMap.get("farm").toString() + ".staticflickr.com/" + hashMap.get("server").toString()
                        + "/" + hashMap.get("id").toString() + "_" + hashMap.get("secret").toString() + "_b.jpg";

                bitmap = GetImage(StrUrl);

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);

                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.activity_dialog);

                ImageView imgvLarge = (ImageView)dialog.findViewById(R.id.imgvLarge);
                imgvLarge.setImageBitmap(bitmap);
                Button btnSave = (Button)dialog.findViewById(R.id.btnSave);
                Button btnExit = (Button)dialog.findViewById(R.id.btnExit);

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try{
                            // make dir
                            String dir = Environment.getExternalStorageDirectory().getAbsolutePath();
                            new File(dir + "/ImageSearch").mkdirs();

                            // save image
                            String path = dir+ "/ImageSearch/" + editSearch.getText().toString() +System.currentTimeMillis()+ ".png";
                            FileOutputStream fileOutputStream = null;
                            fileOutputStream = new FileOutputStream(path);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 90 , fileOutputStream);
                            Toast.makeText(MainActivity.this,"이미지를 저장하였습니다.",Toast.LENGTH_SHORT).show();
                        }catch (Exception e){
                            Toast.makeText(MainActivity.this, "이미지 저장 실패  "+e.toString(),Toast.LENGTH_LONG).show();
                        }

                    }
                });
                btnExit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        }.execute(idx);


    }
}
