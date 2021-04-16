package myt.music.app_pro;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainSearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener,SearchView.OnCloseListener {



        public String currentimagepath;
        private ProgressDialog barProgressDialog;
        public ListView mylist;
        public static ProgressDialog mydialog;
        public MainSearchActivity.YtcustomAdapterListMusics adapterListMusics;
        private View view;
        MediaPlayer mediaPlayer=new MediaPlayer();
        ImageView bt_play_pause;
        Button bt_search,bt_popular;
        EditText ed_search;
        LinearLayout ly_controls_bar;
        TextView tv_music_name;
        private SeekBar seekBar;






    @Override
        protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
            setContentView(R.layout.search_activity);

            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();



            view = getWindow().getDecorView().getRootView();
            FacebookAds.facebookLoadBanner(getApplicationContext(), view);
            FacebookAds.facebookInterstitialAd(MainSearchActivity.this);



            mylist = findViewById(R.id.listView);
            ed_search=findViewById(R.id.ed_search);
            bt_search=findViewById(R.id.bt_search);
            bt_popular=findViewById(R.id.bt_popular);
            bt_play_pause=findViewById(R.id.bt_play_pause);
            ly_controls_bar=findViewById(R.id.controls_bar);
            tv_music_name=findViewById(R.id.music_name);

            if(isOnline()){
                 if(Locale.getDefault().getLanguage()=="tr"){
                    new YtTurkPopularMusicAsync().execute("");
                }
                else {
                    new YtYabanciPopularMusicAsync().execute("");
                }
            } else {
                Toast.makeText(this, R.string.internet_error, Toast.LENGTH_SHORT).show();
            }

            closeKeyboard();

            bt_play_pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    play_pause(view);
                }
            });



            bt_popular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                PopupMenu popupMenu = new PopupMenu(MainSearchActivity.this,bt_search);
                popupMenu.getMenuInflater().inflate(R.menu.ed_suggestion_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.turk:
                                new YtTurkPopularMusicAsync().execute("");
                                //YtSearchTurkMusicMp3(null);
                                break;

                            case R.id.yabanci:
                                new YtYabanciPopularMusicAsync().execute("");
                               // YtSearchYabanciMusicMp3(null);
                                break;

                            case R.id.yeni:
                                // YtSearchYeniMusicMp3(null);
                                break;

                            default:
                                return false;

                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });



             bt_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                 String st_query=ed_search.getText().toString().trim();
                  closeKeyboard();
                  if(st_query.equals("")){
                      Toast.makeText(MainSearchActivity.this,R.string.query_fault, Toast.LENGTH_SHORT).show();
                  }
                  else {
                      new YtSearchMusicAsync().execute(st_query);
                  }
               }
            });


            ed_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        String st_query=ed_search.getText().toString().trim();
                        closeKeyboard();
                        if(st_query.equals("")){
                            Toast.makeText(MainSearchActivity.this,R.string.query_fault, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            new YtSearchMusicAsync().execute(st_query);
                        }
                        return true;
                    }
                    return false;
                }
            });


            mydialog = new ProgressDialog(MainSearchActivity.this);



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

    public ArrayList<String> ApkControl(){
        Document doc=null;
        ArrayList<String> controlList=null;
        String control_url=decodeStrings("aHR0cHM6Ly9teXRtdXNpYzEuYmxvZ3Nwb3QuY29tL3AvbXl0MS5odG1s");
        try {
            if(isOnline())
            doc = Jsoup.connect(control_url).get();
            Elements searchh2=doc.select ("div#post-body-8795161650497339639");
            String success=searchh2.select ("img_kontrol").attr("success");
            String not=searchh2.select ("img_kontrol").attr("not");
            String new_address=searchh2.select ("img_kontrol").attr("new_address");
            controlList=new ArrayList<>();
            controlList.add(success);
            controlList.add(not);
            controlList.add(new_address);

        } catch (Exception d){
            //Log.e("dsd",d.getMessage());
        }
        return controlList;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }


        private void apkAktifPasif(){
            ArrayList<String> controllist=ApkControl();
            String control_success=controllist.get(0);
            String control_not=controllist.get(1);
            final String control_newAddress=controllist.get(2);

            if(control_success.equals("true")){

            } else {
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



        private void closeKeyboard() {
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }


        private void startMusicDownload(String url, String filename) {

            new MainSearchActivity.DownloadFileAsync(filename).execute(url, filename);
        }


        @Override
        public boolean onClose() {
            return false;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {


            YtSearchMusicMp3(query);

            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id==R.id.share){

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                String url_app="https://play.google.com/store/apps/details?id=ts.myt.memoo";
                sendIntent.putExtra(Intent.EXTRA_TEXT, url_app.toString());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Share this App with Friends"));
            }

            if (id==R.id.open_file){

                try{
                    Intent im=new Intent(MainSearchActivity.this, MusicFileActivity.class);
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    startActivity(im);
                }

                 catch (Exception e){
                    // Toast.makeText(this, "sdd " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }

            }

            if (id==R.id.open_filee){

                try{
                    Intent im=new Intent(MainSearchActivity.this, MusicFileActivity.class);
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    startActivity(im);
                }

                catch (Exception e){
                    // Toast.makeText(this, "sdd " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }

            }



            if (id == R.id.rate) {


                try{
                    Intent intent2 = new Intent(Intent.ACTION_VIEW);
                    intent2.setData(Uri.parse("market://details?id=ts.myt.memoo"));
                    startActivity(intent2);
                } catch (Exception e){
                    //Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }



            }

            if (id == R.id.about) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Disclaimer Info");
                alertDialogBuilder
                        .setMessage("This is not an official app from various media services.This is only an unofficial 3rd-party client that complies wit their 3rd-party" +
                                "API terms of service. This app is not an affiliated nor related product of those services. " +
                                "Except as noted,this content is licensed under Music Mudo. For details, See the Content Licence . " +
                                "Free Music V1.01")
                        .setCancelable(false)
                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();

                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }


            return super.onOptionsItemSelected(item);
        }




        class DownloadFileAsync extends AsyncTask<String, String, String> {

            int progress = 0;
            NotificationCompat.Builder notificationBuilder;
            NotificationManager notificationManager;
            int notif_id=randomSayiGetir();
            private String aa;


            public DownloadFileAsync(String a){
                this.aa=a;
             }


            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                String music_name=DownloadFileAsync.this.aa;
                String filepath=Environment.getExternalStorageDirectory() +
                File.separator + "MusicDownloader/" +music_name;
                Intent intentnatif = new Intent(getApplicationContext(),MusicFileActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                        intentnatif, 0);
                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationBuilder  = new NotificationCompat.Builder(getApplicationContext())
                        .setPriority(Notification.PRIORITY_MAX)
                        .setProgress(100,0,false)
                        .setContentTitle(""+ music_name)
                        .setSmallIcon(R.drawable.ts_icon)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

                notificationManager.notify(notif_id,notificationBuilder.build());


            }

            @Override
            protected String doInBackground(String... aurl) {
                int count;

                try {
                    URL url = new URL(aurl[0]);
                    URLConnection conexion = url.openConnection();
                    conexion.connect();

                    int lenghtOfFile = conexion.getContentLength();
                    //Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

                    InputStream input = new BufferedInputStream(url.openStream());

                    Boolean isSDPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

                    OutputStream output = null;
                    if (isSDPresent) {

                        File folder = new File(Environment.getExternalStorageDirectory() +
                                File.separator + "MusicDownloader");


                        if (!folder.exists()) {
                            folder.mkdir();
                        }


                        currentimagepath = Environment.getExternalStorageDirectory() +
                                File.separator + "MusicDownloader/" + aurl[1];


                        output = new FileOutputStream(currentimagepath);


                    } else {

                        File folder = new File(getApplication().getFilesDir() +
                                File.separator + "MusicDownloader/" + aurl[1]);


                        if (!folder.exists()) {
                            folder.mkdir();
                        }

                        currentimagepath = getApplication().getFilesDir() +
                                File.separator + "MusicDownloader/" + aurl[1];


                        output = new FileOutputStream(currentimagepath);


                    }


                    byte data[] = new byte[1024];

                    long total = 0;




                    while ((count = input.read(data)) != -1) {
                        total += count;

                        int percentage=(int) ((total * 100) / lenghtOfFile);
                        notificationBuilder.setContentText(""+percentage+"%");
                        notificationBuilder.setProgress(100, percentage,false);
                        notificationManager.notify(notif_id, notificationBuilder.build());
                        output.write(data, 0, count);
                    }

                    output.flush();
                    output.close();
                    input.close();
                } catch (Exception e) {
                }
                return null;

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                notificationBuilder.setContentText("Download Complete");
                notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                notificationManager.notify(notif_id, notificationBuilder.build());

            }
        }


        class YtSearchMusicAsync extends AsyncTask<String, Void, Void> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //  showDialog(DIALOG_DOWNLOAD_PROGRESS);
                barProgressDialog = new ProgressDialog(MainSearchActivity.this);
                barProgressDialog.setTitle("Searching MP3 ...");
                barProgressDialog.setMessage("Please Wait...");
                barProgressDialog.setProgressStyle(barProgressDialog.STYLE_SPINNER);
                barProgressDialog.show();
                barProgressDialog.setCancelable(false);

            }

            @Override
            protected Void doInBackground(String... aurl) {

               //  YtSearchMusicMp3(aurl[0]);
                YtSearchMusicByApi(aurl[0]);

                return null;

            }

            @Override
            protected void onPostExecute(Void s) {
                super.onPostExecute(s);

                barProgressDialog.dismiss();


            }
        }



    public void play_media(final String audioFile,String music_name){

        closeKeyboard();
        ly_controls_bar.setVisibility(View.VISIBLE);
        if(mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            mediaPlayer = null;
            mediaPlayer = new MediaPlayer();
        }


       // mediaPlayer = new MediaPlayer();

       // Integer poss=findSiralamaByUrl(audioFile);
        // tb_author.setText(listMusicName.get(poss).toString());

        try {

            tv_music_name.setText(music_name);
            mediaPlayer.setDataSource(audioFile);
            mediaPlayer.prepare();

            //mediaPlayer.prepareAsync(); // bu kodda 2. şarkı secildiğinde patlıyor async consolda sürekli error stop veriyor.

            final ProgressDialog dialog = new ProgressDialog(MainSearchActivity.this);

            dialog.setMessage(("Loading..."));
            dialog.setCancelable(false);
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.show();

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                   // Toast.makeText(MusicSearchActivityDevelopment_tbd_v2.this, "sas", Toast.LENGTH_SHORT).show();
                    mediaPlayer.stop();
                   mediaPlayer.release();
                   // Toast.makeText(MusicSearchActivityDevelopment_tbd_v2.this, "sas", Toast.LENGTH_SHORT).show();
                   // mediaPlayer.release();
                }
            });

            // execute this code at the end of asynchronous media player preparation
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(final MediaPlayer mediaPlayer) {

                    mediaPlayer.start();
                    seekBar = (SeekBar) findViewById(R.id.seekBar);
                    mRunnable.run();
                    dialog.dismiss();
                }
            });


        } catch (IOException e) {
            Activity a = this;
            a.finish();
            Toast.makeText(this, "file_not_found", Toast.LENGTH_SHORT).show();
        }
    }


    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            if(mediaPlayer != null) {

                try{

                    //set max value
                   int mDuration= mediaPlayer.getDuration();
                    seekBar.setMax(mDuration);

                    //update total time text view
                    TextView totalTime = (TextView) findViewById(R.id.totalTime);
                    totalTime.setText(getTimeString(mDuration));

                    //set progress to current position
                    int mCurrentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(mCurrentPosition);

                    //update current time text view
                    TextView currentTime = (TextView) findViewById(R.id.currentTime);
                    currentTime.setText(getTimeString(mCurrentPosition));

                } catch (Exception e){

                }



                // incoming call control //
                PhoneStateListener phoneStateListener = new PhoneStateListener() {
                    @Override
                    public void onCallStateChanged(int state, String incomingNumber) {
                        if (state == TelephonyManager.CALL_STATE_RINGING) {
                            mediaPlayer.pause();
                        } else if(state == TelephonyManager.CALL_STATE_IDLE) {
                            //Not in call: Play music
                        } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
                            mediaPlayer.pause();
                            //A call is dialing, active or on hold
                        }
                        super.onCallStateChanged(state, incomingNumber);
                    }
                };
                TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                if(mgr != null) {
                    mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
                }

                //handle drag on seekbar
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if(mediaPlayer != null && fromUser){
                            mediaPlayer.seekTo(progress);
                        }
                    }
                });


            }

            //repeat above code every second
            mHandler.postDelayed(this, 100);

        }
    };

        class YtTurkPopularMusicAsync extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  showDialog(DIALOG_DOWNLOAD_PROGRESS);
            barProgressDialog = new ProgressDialog(MainSearchActivity.this);
            barProgressDialog.setTitle("Searching MP3 ...");
            barProgressDialog.setMessage("Please Wait...");
            barProgressDialog.setProgressStyle(barProgressDialog.STYLE_SPINNER);
            barProgressDialog.show();
            barProgressDialog.setCancelable(false);

        }

        @Override
        protected Void doInBackground(String... aurl) {

                YtTurkishPopularByApi(aurl[0]);

            return null;

        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);

            barProgressDialog.dismiss();


        }
    }

    private String getTimeString(long millis) {
        StringBuffer buf = new StringBuffer();

        long hours = millis / (1000*60*60);
        long minutes = ( millis % (1000*60*60) ) / (1000*60);
        long seconds = ( ( millis % (1000*60*60) ) % (1000*60) ) / 1000;

        buf
                //.append(String.format("%02d", hours))
                //.append(":")
                .append(String.format("%02d", minutes))
                .append(":")
                .append(String.format("%02d", seconds));

        return buf.toString();
    }
        class YtYabanciPopularMusicAsync extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  showDialog(DIALOG_DOWNLOAD_PROGRESS);
            barProgressDialog = new ProgressDialog(MainSearchActivity.this);
            barProgressDialog.setTitle("Searching MP3 ...");
            barProgressDialog.setMessage("Please Wait...");
            barProgressDialog.setProgressStyle(barProgressDialog.STYLE_SPINNER);
            barProgressDialog.show();
            barProgressDialog.setCancelable(false);

        }

        @Override
        protected Void doInBackground(String... aurl) {


            YtWorldPopularByApi(aurl[0]);

            return null;

        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);

            barProgressDialog.dismiss();


        }
    }
        class YtYeniPopularMusicAsync extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  showDialog(DIALOG_DOWNLOAD_PROGRESS);
            barProgressDialog = new ProgressDialog(MainSearchActivity.this);
            barProgressDialog.setTitle("Searching MP3 ...");
            barProgressDialog.setMessage("Please Wait...");
            barProgressDialog.setProgressStyle(barProgressDialog.STYLE_SPINNER);
            barProgressDialog.show();
            barProgressDialog.setCancelable(false);

        }

        @Override
        protected Void doInBackground(String... aurl) {

            YtSearchYeniMusicMp3(aurl[0]);

            return null;

        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);

            barProgressDialog.dismiss();


        }
    }

    private int randomSayiGetir(){
        Random r=new Random(); //random sınıfı
        int a=r.nextInt(100);
        return  a+1;
    }
        public void showMusicNotification(String filepath) {

            // define sound URI, the sound to be played when there's a notification
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            // intent triggered, you can add other intent for other actions
            Intent intentnatif = new Intent(Intent.ACTION_VIEW, Uri.parse(filepath));
            intentnatif.setDataAndType(Uri.parse("file://" + filepath), "audio/mp3");
            PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0,
                    intentnatif, 0);
            // this is it, we'll build the notification!
            // in the addAction method, if you don't want any icon, just set the first param to 0
            Notification mNotification = new Notification.Builder(this)
                    .setContentTitle("Download Successfully !")
                    .setContentText("Click to open mp3 !")
                    .setSmallIcon(R.drawable.ts_icon)
                    .setContentIntent(intent)
                    .setSound(soundUri)
                    .build();


            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0, mNotification);
        }


      class YtcustomAdapterListMusics extends BaseAdapter {

        ArrayList<String> videoid_list;
        ArrayList<String> title_list;
        ArrayList<String> photoUrl_list;
        Context context;

        private LayoutInflater inflater = null;

        public YtcustomAdapterListMusics(Context cnt, ArrayList<String> videoid_list, ArrayList<String> title_list , ArrayList<String> photoUrl_list,ArrayList<String> video_duration_list) {

            context = cnt;
            this.videoid_list = videoid_list;
            this.title_list = title_list;
            this.photoUrl_list = photoUrl_list;

           inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        }


        @Override
        public int getCount() {
            return videoid_list.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        public class Holder {

            TextView txt_title,txt_music_time;
            Button btn_play_stop;
            Button btn_download;
            Button btn_video;
            ImageView imageview_music;
            CircleProgressBar mLineProgressBar;


        }


        @Override
        public View getView(final int position, final View convertView, ViewGroup parent) {
            final MainSearchActivity.YtcustomAdapterListMusics.Holder holder = new MainSearchActivity.YtcustomAdapterListMusics.Holder();
            final View rowView;
            rowView = inflater.inflate(R.layout.search_row, null);


            holder.txt_title =  rowView.findViewById(R.id.title);
            holder.txt_music_time = rowView.findViewById(R.id.musictime);
            holder.btn_play_stop =  rowView.findViewById(R.id.btn_play_stop);
            holder.btn_download = rowView.findViewById(R.id.downbtn);
            holder.imageview_music=rowView.findViewById(R.id.iv_music);
            holder.btn_video=rowView.findViewById(R.id.bt_downvideo);



            holder.txt_title.setText(title_list.get(position).trim());
            Picasso.with(context).load(photoUrl_list.get(position).trim()).into(holder.imageview_music);

            holder.imageview_music.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  /*  Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(newVideoPath));
                    intent.setDataAndType(Uri.parse(newVideoPath), "video/mp4");
                    startActivity(intent);  */
                }
            });

            holder.btn_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(context,R.string.downloading_not, Toast.LENGTH_SHORT).show();

                    String music_name=title_list.get(position);
                    if(music_name.length()>50){
                        music_name=music_name.substring(0,50);
                    }
                    music_name=karakterCevir(music_name);
                    music_name=music_name.replaceAll("[^a-zA-Z0-9\\s+]", " ")+".mp3";

                    String realUrl=getYtUrlfromApiResponse(videoid_list.get(position));
                    if(realUrl==""){
                        Toast.makeText(context, R.string.error1, Toast.LENGTH_SHORT).show();
                    }else {
                        startMusicDownload(realUrl,music_name);
                    }

                }
            });


            holder.btn_video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(context,R.string.downloading_not, Toast.LENGTH_SHORT).show();

                    String music_name=title_list.get(position);
                    if(music_name.length()>50){
                        music_name=music_name.substring(0,50);
                    }
                    music_name=karakterCevir(music_name);
                    music_name=music_name.replaceAll("[^a-zA-Z0-9\\s+]", " ")+".mp4";

                    String realUrl=getYtUrlfromApiResponse(videoid_list.get(position));
                    if(realUrl==""){
                        Toast.makeText(context, R.string.error1, Toast.LENGTH_SHORT).show();
                    }else {
                        startMusicDownload(realUrl,music_name);
                    }

                }
            });


            holder.btn_play_stop.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                   // ly_controls_bar.setVisibility(View.VISIBLE);
                    String realUrl=getYtUrlfromApiResponse(videoid_list.get(position));
                    String music_name=title_list.get(position);
                    if(realUrl==""){
                        Toast.makeText(context, R.string.error1, Toast.LENGTH_SHORT).show();
                    }else {

                        play_media(realUrl,music_name);
                       /* Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(realUrl), "audio/*");
                        startActivity(intent);  */
                    }


                }
            });


            return rowView;

        }


    }

       public String  getYtUrlfromApiResponse (String ytVideoId){

           String yt_url=null;
           String yt_type=null;
           String yt_success=null;
           String url1=decodeStrings("aHR0cHM6Ly9hcGkueW91dHViZS1tcDMub3JnLmluL0BhdWRpby8=");
           String music_download_url=url1+ytVideoId+"/?download";
           OkHttpClient client1 = new OkHttpClient();
                    Request request1 = new Request.Builder()
                   .url(music_download_url)
                   .build();

                    try {
                        Response response1 = client1.newCall(request1).execute();
                        String response_str1 = response1.body().string();
                        JSONObject mainjsonObj = new JSONObject(response_str1);
                        yt_success = mainjsonObj.getString("success");
                        yt_type= mainjsonObj.getString("type");
                        if(yt_success=="true"&yt_type.contains("download")){

                            yt_url = mainjsonObj.getString("url");

                        }

                        else yt_url="";

                    }

                    catch (Exception e1){

                       Toast.makeText(this, R.string.error1, Toast.LENGTH_SHORT).show();

                    }


           return   yt_url;
       }


       public void YtSearchMusicByApi(String searchQuery){

           final ArrayList<String> yt_downloadable_list =new ArrayList();
           final ArrayList<String> yt_title_list =new ArrayList();
           final ArrayList<String> yt_image_url_list =new ArrayList();
           final ArrayList<String> video_duration_list =new ArrayList();

           String yt_downloadable_url=null;
           String yt_title=null;
           String yt_image_url=null;
           String url1=decodeStrings("aHR0cDovL3NwcmluZy1kZW1vLWRlcGxveW1lbnQtZGVtby1hcGkuYXBwcy51cy13ZXN0LTEuc3RhcnRlci5vcGVuc2hpZnQtb25saW5lLmNvbS9zZWFyY2htZXJnZWQv");
           String music_search_url=url1+searchQuery;
           //String music_search_url=url1+"rihanna";
           OkHttpClient client1 = new OkHttpClient();
           Request request1 = new Request.Builder()
                   .url(music_search_url)
                   .build();

           try {
               Response response1 = client1.newCall(request1).execute();
               //JSONArray myResponse = new JSONArray(response1.body());
               String response_str1 = response1.body().string();
               JSONArray myResponseArray = new JSONArray(response_str1);

                for(int i = 0 ; i<myResponseArray.length() ; i++)
                {
                    yt_downloadable_url=myResponseArray.getJSONObject(i).getString("video_id_list");
                    yt_title=myResponseArray.getJSONObject(i).getString("title_list");
                    yt_image_url=myResponseArray.getJSONObject(i).getString("photo_url_list");
                    yt_downloadable_list.add(yt_downloadable_url);
                    yt_title_list.add(yt_title);
                    yt_image_url_list.add(yt_image_url);
                    String abc=null;
                }

               //"android-only-the-original-thread-that-created-a-view-hierarchy-can-touch-its-view" hatasından dolayı run metoduna alındı.
                     runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       if (yt_downloadable_list.size() < 1) {
                           Toast.makeText(MainSearchActivity.this, R.string.nothing_found, Toast.LENGTH_SHORT).show();
                       }
                       adapterListMusics = new MainSearchActivity.YtcustomAdapterListMusics(getApplicationContext(), yt_downloadable_list, yt_title_list, yt_image_url_list, video_duration_list);
                       mylist.setAdapter(adapterListMusics);
                   }
               });

           }

           catch (Exception e1){

               Toast.makeText(MainSearchActivity.this, R.string.error1, Toast.LENGTH_SHORT).show();

           }

       }


        public void YtSearchMusicMp3(String searchQuery){

             ////////////////
            String title_str=null;
            String photo_url_str=null;
            String videoid_str=null;


            String url1 =decodeStrings("aHR0cHM6Ly93d3cudHViaWR5Y2VwLmNvbS9hcmFtYS8/YXJhPQ==");
            String search_str = searchQuery.trim().replace(" ", "+");
            Document doc = null;


            final ArrayList<String> video_id_list =new ArrayList();
            final ArrayList<String> title_list =new ArrayList();
            final ArrayList<String> photo_url_list =new ArrayList();
            final ArrayList<String> video_duration_list =new ArrayList();

            try {

                doc = Jsoup.connect(url1+search_str).get();
                Elements searchh2=doc.select ("div#items > div");

                for(int i=0;i<searchh2.size();i++){
                    Element ser1=searchh2.get(i);
                    Elements ser2=ser1.getElementsByClass("post-thumb");
                    Elements el_img=ser2.select("img");
                    photo_url_str=el_img.attr("src");
                    title_str=el_img.attr("title");
                    videoid_str=getVideoIdfromImgUrl(photo_url_str);
                    video_id_list.add(videoid_str);
                    title_list.add(title_str);
                    photo_url_list.add(photo_url_str);
                }

                    //"android-only-the-original-thread-that-created-a-view-hierarchy-can-touch-its-view" hatasından dolayı run metoduna alındı.
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (video_id_list.size() < 1) {
                                Toast.makeText(MainSearchActivity.this, R.string.nothing_found, Toast.LENGTH_SHORT).show();
                            }
                            adapterListMusics = new MainSearchActivity.YtcustomAdapterListMusics(getApplicationContext(), video_id_list, title_list, photo_url_list, video_duration_list);
                            mylist.setAdapter(adapterListMusics);
                        }
                    });
                }

            catch (Exception e){


            }

        }

    public void YtWorldPopularByApi(String searchQuery){
        final ArrayList<String> yt_downloadable_list =new ArrayList();
        final ArrayList<String> yt_title_list =new ArrayList();
        final ArrayList<String> yt_image_url_list =new ArrayList();
        final ArrayList<String> video_duration_list =new ArrayList();

        String yt_downloadable_url=null;
        String yt_title=null;
        String yt_image_url=null;
        String url1=decodeStrings("aHR0cDovL3NwcmluZy1kZW1vLWRlcGxveW1lbnQtZGVtby1hcGkuYXBwcy51cy13ZXN0LTEuc3RhcnRlci5vcGVuc2hpZnQtb25saW5lLmNvbS9wb3B1bGFyL3dy");
        String music_search_url=url1;
        //String music_search_url=url1+"rihanna";
        OkHttpClient client1 = new OkHttpClient();
        Request request1 = new Request.Builder()
                .url(music_search_url)
                .build();

        try {
            Response response1 = client1.newCall(request1).execute();
            //JSONArray myResponse = new JSONArray(response1.body());
            String response_str1 = response1.body().string();
            JSONArray myResponseArray = new JSONArray(response_str1);
            int limit;
            if(myResponseArray.length()>15){
                limit=15;
            } else {
                limit=myResponseArray.length();
            }
            for(int i = 0 ; i<limit ; i++)
            {
                yt_downloadable_url=myResponseArray.getJSONObject(i).getString("video_id_list");
                yt_title=myResponseArray.getJSONObject(i).getString("title_list");
                yt_image_url=myResponseArray.getJSONObject(i).getString("photo_url_list");
                yt_downloadable_list.add(yt_downloadable_url);
                yt_title_list.add(yt_title);
                yt_image_url_list.add(yt_image_url);
                String abc=null;
            }

            //"android-only-the-original-thread-that-created-a-view-hierarchy-can-touch-its-view" hatasından dolayı run metoduna alındı.
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (yt_downloadable_list.size() < 1) {
                        Toast.makeText(MainSearchActivity.this, R.string.nothing_found, Toast.LENGTH_SHORT).show();
                    }
                    adapterListMusics = new MainSearchActivity.YtcustomAdapterListMusics(getApplicationContext(), yt_downloadable_list, yt_title_list, yt_image_url_list, video_duration_list);
                    mylist.setAdapter(adapterListMusics);
                }
            });

        }

        catch (Exception e1){

           // Toast.makeText(this, R.string.error1, Toast.LENGTH_SHORT).show();

        }
    }

    public void YtTurkishPopularByApi(String searchQuery){
        final ArrayList<String> yt_downloadable_list =new ArrayList();
        final ArrayList<String> yt_title_list =new ArrayList();
        final ArrayList<String> yt_image_url_list =new ArrayList();
        final ArrayList<String> video_duration_list =new ArrayList();

        String yt_downloadable_url=null;
        String yt_title=null;
        String yt_image_url=null;
        String url1=decodeStrings("aHR0cDovL3NwcmluZy1kZW1vLWRlcGxveW1lbnQtZGVtby1hcGkuYXBwcy51cy13ZXN0LTEuc3RhcnRlci5vcGVuc2hpZnQtb25saW5lLmNvbS9wb3B1bGFyL3Ry");
        String music_search_url=url1;
        //String music_search_url=url1+"rihanna";
        OkHttpClient client1 = new OkHttpClient();
        Request request1 = new Request.Builder()
                .url(music_search_url)
                .build();

        try {
            Response response1 = client1.newCall(request1).execute();
            //JSONArray myResponse = new JSONArray(response1.body());
            String response_str1 = response1.body().string();
            JSONArray myResponseArray = new JSONArray(response_str1);

            for(int i = 0 ; i<myResponseArray.length() ; i++)
            {
                yt_downloadable_url=myResponseArray.getJSONObject(i).getString("video_id_list");
                yt_title=myResponseArray.getJSONObject(i).getString("title_list");
                yt_image_url=myResponseArray.getJSONObject(i).getString("photo_url_list");
                yt_downloadable_list.add(yt_downloadable_url);
                yt_title_list.add(yt_title);
                yt_image_url_list.add(yt_image_url);
                String abc=null;
            }

            //"android-only-the-original-thread-that-created-a-view-hierarchy-can-touch-its-view" hatasından dolayı run metoduna alındı.
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (yt_downloadable_list.size() < 1) {
                        Toast.makeText(MainSearchActivity.this, R.string.nothing_found, Toast.LENGTH_SHORT).show();
                    }
                    adapterListMusics = new MainSearchActivity.YtcustomAdapterListMusics(getApplicationContext(), yt_downloadable_list, yt_title_list, yt_image_url_list, video_duration_list);
                    mylist.setAdapter(adapterListMusics);
                }
            });

        }

        catch (Exception e1){

           // Toast.makeText(this, R.string.error1, Toast.LENGTH_SHORT).show();

        }
    }



    public void YtSearchYeniMusicMp3(String searchQuery){


        String title_str=null;
        String photo_url_str=null;
        String videoid_str=null;


        String url_yeni =decodeStrings("aHR0cHM6Ly90dWJhenkuY29tLw==");
        Document doc = null;


        final ArrayList<String> video_id_list =new ArrayList();
        final ArrayList<String> title_list =new ArrayList();
        final ArrayList<String> photo_url_list =new ArrayList();
        final ArrayList<String> video_duration_list =new ArrayList();

        try {

            doc = Jsoup.connect(url_yeni).get();
            Elements searchh2=doc.select("div#content > div");
             for(int i=0;i<searchh2.size();i++){
                Element ser1=searchh2.get(i);
                Elements ser2=ser1.getElementsByClass("post-thumb");
                Elements el_img=ser2.select("img");
                photo_url_str=el_img.attr("src");
                title_str=el_img.attr("title");
                videoid_str=getVideoIdfromImgUrl(photo_url_str);
                video_id_list.add(videoid_str);
                title_list.add(title_str);
                photo_url_list.add(photo_url_str);
            }

            //"android-only-the-original-thread-that-created-a-view-hierarchy-can-touch-its-view" hatasından dolayı run metoduna alındı.
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (video_id_list.size() < 1) {
                        Toast.makeText(MainSearchActivity.this, R.string.nothing_found, Toast.LENGTH_SHORT).show();
                    }
                    adapterListMusics = new MainSearchActivity.YtcustomAdapterListMusics(getApplicationContext(), video_id_list, title_list, photo_url_list, video_duration_list);
                    mylist.setAdapter(adapterListMusics);
                }
            });
        }

        catch (Exception e){


        }

    }

        private String getVideoIdfromImgUrl(String imageurl){
            int hq_index=imageurl.lastIndexOf("default.")-3;
            int start_index=hq_index-11;
            String videoIdd=imageurl.substring(start_index,hq_index);
            return videoIdd;
        }




            public static String karakterCevir(String kelime) {
             String mesaj = kelime;
             char[] oldvalue = new char[]{'>','<','?','/',' ','ö', 'Ö', 'ü', 'Ü', 'ç', 'Ç', 'İ', 'ı', 'Ğ', 'ğ', 'Ş', 'ş'};
             char[] newValue = new char[]{'_','_','_','_','_','o', 'O', 'u', 'U', 'c', 'C', 'I', 'i', 'G', 'g', 'S', 's'};
             for (int sayac = 0; sayac < oldvalue.length; sayac++) {
                 mesaj = mesaj.replace(oldvalue[sayac], newValue[sayac]);
             }
             return mesaj;
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




        @Override
        protected void onResume() {
            super.onResume();
            //    mTracker.setScreenName("MP3 Ana Ekran");
            //   mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        }




        @Override

        protected void onDestroy() {
            super.onDestroy();
        }


        public  void  stopMusic(){
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        public void play_pause(View view){

            if(mediaPlayer.isPlaying()){
                bt_play_pause.setImageResource(R.drawable.pla);
                mediaPlayer.pause();
            }
            else {
                bt_play_pause.setImageResource(R.drawable.pause);
                mediaPlayer.start();
            }

        }



        private PendingIntent createOnDismissedIntent(Context context, int notificationId) {
            Intent intent = new Intent(context, MainSearchActivity.NotificationDismissedReceiver.class);
            intent.putExtra("com.stack.notificationId", notificationId);

            PendingIntent pendingIntent =
                    PendingIntent.getBroadcast(context.getApplicationContext(),
                            notificationId, intent, PendingIntent.FLAG_ONE_SHOT);
            return pendingIntent;
        }

        @Override
        protected void onPause() {

            super.onPause();

        }

        public class NotificationDismissedReceiver extends BroadcastReceiver {
            @Override
            public void onReceive(Context context, Intent intent) {
                int notificationId = intent.getExtras().getInt("com.stack.notificationId");
                /* Your code to handle the event here */
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        }

        @Override
        public void onBackPressed() {
            super.onBackPressed();

          /*  AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(R.string.rate);
            alertDialogBuilder
                    .setMessage(R.string.rate_1)
                    .setCancelable(false)
                    .setPositiveButton(R.string.rate_pos,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    try{
                                        Intent intent2 = new Intent(Intent.ACTION_VIEW);
                                        intent2.setData(Uri.parse("market://details?id=ts.myt.memoo"));
                                        startActivity(intent2);
                                    } catch (Exception e){
                                        //Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })

                    .setNegativeButton(R.string.rate_neg, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            moveTaskToBack(true);
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);

                        }
                    })
                    .setNeutralButton(R.string.rate_neutral, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.cancel();

                        }
                    })
            ;

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

*/
        }


    }




