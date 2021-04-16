package myt.music.app_pro;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainControlActivity extends AppCompatActivity {


    Button bt_control;
    private ProgressDialog barProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_splash_control);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();


        //view = getWindow().getDecorView().getRootView();
       // FacebookAds.facebookLoadBanner(getApplicationContext(), view);
        // FacebookAds.facebookInterstitialAd(SplashControlActivity.this);

         if(isOnline()){
        //if(false){
            ArrayList<String> controllist= isApkActivePassive();
            final String control_success=controllist.get(0);
            final String control_not=controllist.get(1);
            final String control_newAddress=controllist.get(2);

            //download kısmı true yapılacak.
            if(control_success.equals("true")){
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Intent i=new Intent(MainControlActivity.this, MainSearchActivity.class);
                        startActivity(i);
                    }
                }, 100);
            }
            else
            {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(R.string.crate);
                alertDialogBuilder
                        .setMessage(control_not)
                        .setCancelable(false)
                        .setPositiveButton(R.string.crate_pos,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                            try{
                                                Intent intent2 = new Intent(Intent.ACTION_VIEW);
                                                intent2.setData(Uri.parse(control_newAddress));
                                                startActivity(intent2);
                                            } catch (Exception e){
                                                //Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }



                                })

                        .setNegativeButton(R.string.crate_neg, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);

                            }
                        })
                ;

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }


        }
        else {
            Toast.makeText(this, R.string.internet_error+" Offline Mod Active !", Toast.LENGTH_SHORT).show();
            Intent intent2 = new Intent(MainControlActivity.this,MainSearchActivity.class);
            startActivity(intent2);
        }



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 2909);
            }
        }


        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }


    }


    private String decodeStrings(String encoded) {
        byte[] dataDec = Base64.decode(encoded, Base64.DEFAULT);
        String decodedString = "";
        try {

            decodedString = new String(dataDec, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        } finally {

            return decodedString;
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }


    public ArrayList<String> isApkActivePassive(){

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    String url1 = decodeStrings("aHR0cDovL3NwcmluZy1kZW1vLWRlcGxveW1lbnQtZGVtby1hcGkuYXBwcy51cy13ZXN0LTEuc3RhcnRlci5vcGVuc2hpZnQtb25saW5lLmNvbS9hcGtjb250cm9s");
                    OkHttpClient client1 = new OkHttpClient();
                    Request request1 = new Request.Builder()
                            .url(url1)
                            .build();
                    ArrayList<String> controllist=new ArrayList<>();
                    try {
                        Response response1 = client1.newCall(request1).execute();
                        String response_str1 = response1.body().string();
                        JSONArray mainjsonArray = new JSONArray(response_str1);
                        JSONObject mainjsonObj = mainjsonArray.getJSONObject(0);
                        Boolean success = (Boolean) mainjsonObj.get("apkAktifPasif");
                        String note = String.valueOf(mainjsonObj.get("not"));
                        String yeniApkAdres = String.valueOf(mainjsonObj.get("yeniApkAdres"));

                        controllist.add(success.toString());
                        controllist.add(note);
                        controllist.add(yeniApkAdres);

                        return controllist;
                    } catch (Exception e1) {
                        e1.getMessage();
                    }
                } catch ( RuntimeException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();




    }

}




