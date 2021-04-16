package myt.music.app_pro;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import myt.music.app_pro.Constans.GlobalData;
import myt.music.app_pro.Services.MusicPlayer;

public class MusicFileActivity extends AppCompatActivity {


    public ListView mylist;

    private MediaPlayer mediaPlayer=new MediaPlayer();
    private SeekBar seekBar;
    ArrayList<String> file_arraylist;
    int kacinci=0;
    TextView tb_author;
    ImageButton bt_play_pause,bt_backward,bt_forward;
    LinearLayout txt_layout;
    String s_media;

    public customAdapterListMusics.Holder lastPlayed;
    public ProgressDialog mydialog;

    public int countAds = 0;


    public ArrayList<String> listMusicName;


    public ArrayList<String> listMusicTime;

    public ArrayList<String> listMusicNameCont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.mp3_file_activity);
        mylist = (ListView) findViewById(R.id.liste);
        //aa mediaPlayer = new MediaPlayer();
        mydialog = new ProgressDialog(MusicFileActivity.this);

        listMusicName = new ArrayList<String>();
        listMusicTime = new ArrayList<String>();
        listMusicNameCont = new ArrayList<String>();
        View view;


        tb_author=(TextView)findViewById(R.id.tb_author);
        bt_backward=(ImageButton)findViewById(R.id.backward);
        bt_forward=(ImageButton)findViewById(R.id.forward);
        bt_play_pause=(ImageButton)findViewById(R.id.play_pause);
        txt_layout=findViewById(R.id.txt_layout);

        getAllMusics(pathControl());
        new GlobalData().setSongList(getAllMusics(pathControl()));

       // ArrayList<String> pathList=new ArrayList<String>(getAllMusics(pathControl()));
       // Collections.reverse(pathList);
        //Collections.reverse(listMusicNameCont);
       // Collections.reverse(listMusicName);
       // Collections.reverse(listMusicTime);


        customAdapterListMusics adapterListMusics = new customAdapterListMusics(getApplication(), getAllMusics(pathControl()), listMusicNameCont, listMusicName, listMusicTime);

        mylist.setAdapter(adapterListMusics);

        view=getWindow().getDecorView().getRootView();

        FacebookAds.facebookLoadBanner(getApplicationContext(),view);
        FacebookAds.facebookInterstitialAd(MusicFileActivity.this);





    }


    class customAdapterListMusics extends BaseAdapter {

        ArrayList<String> listmusicURL;
        ArrayList<String> listmusicname;
        ArrayList<String> listmusicauthor;
        ArrayList<String> listmusictime;
        Context context;


        private LayoutInflater inflater = null;

        public customAdapterListMusics(Context cnt, ArrayList<String> listmusic, ArrayList<String> listmusicname, ArrayList<String> listmusicauthor, ArrayList<String> listmusictime) {
            context = cnt;
            this.listmusicURL = listmusic;
            this.listmusicname = listmusicname;
            this.listmusicauthor = listmusicauthor;
            this.listmusictime = listmusictime;


            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        }




        @Override
        public int getCount() {
            return listmusicURL.size();
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

            TextView txt_music_name;
            TextView txt_music_author;
            TextView txt_music_time;
            ImageButton btn_play_stop,btn_delete;
            LinearLayout txt_layout;



        }





        @Override
        public View getView(final int position, final View convertView, ViewGroup parent) {
            final Holder holder = new Holder();
            final View rowView;
            rowView = inflater.inflate(R.layout.mp3_file_row, null);


            holder.txt_music_name = rowView.findViewById(R.id.songtext);
            holder.txt_music_author =  rowView.findViewById(R.id.authortext);
            holder.txt_music_time = rowView.findViewById(R.id.musictime);
            holder.btn_play_stop =  rowView.findViewById(R.id.playstop);
            holder.btn_delete =  rowView.findViewById(R.id.deleteButton);
            holder.txt_layout=rowView.findViewById(R.id.txt_layout);
            holder.txt_music_name.setText(listmusicname.get(position).trim());
            holder.txt_music_author.setText(listmusicauthor.get(position).trim());
            holder.txt_music_time.setText(listmusictime.get(position).trim());



            holder.txt_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ab=position;
                    String abc=(Uri.parse(listmusicURL.get(position))).toString();
                    new GlobalData().setUri(Uri.parse(listmusicURL.get(position).toString()));
                    new GlobalData().setMusicName(listmusicauthor.get(position).toString().trim());
                    new GlobalData().setSongListIndex(position);
                    Intent ıntent = new Intent(getApplicationContext(), MusicPlayer.class);
                    MusicFileActivity.this.startActivity(ıntent);
                }
            });


         /*   holder.txt_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bt_play_pause.setImageResource(R.drawable.pause);
                    s_media=Uri.parse(listmusicURL.get(position)).toString();
                    String abc="";
                    if(mediaPlayer.isPlaying()){
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }
                    else{

                    }
                    play_media(s_media);

                }
            });

   */


            holder.btn_play_stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    bt_play_pause.setImageResource(R.drawable.pause);
                    s_media=Uri.parse(listmusicURL.get(position)).toString();


                    if(mediaPlayer.isPlaying()){
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }
                    else{

                    }
                    play_media(s_media);



                    countAds++;
                    if (countAds % 5 == 0) {


                       // FacebookAdsMP3.facebookInterstitialAd(MusicFileActivity.this);


                    }


                }
            });

            holder.btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Toast.makeText(getApplicationContext(),"delete tıkland",Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(MusicFileActivity.this);
                    builder.setTitle("Delete");
                    builder.setMessage("Do you want to delete this song !!!");
                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id) {

                            //İptal butonuna basılınca yapılacaklar.Sadece kapanması isteniyorsa boş bırakılacak

                        }
                    });


                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //Tamam butonuna basılınca yapılacaklar

                            try{
                                File file = new File(Uri.parse(listmusicURL.get(position).toString()).getPath());
                                file.exists();
                                file.delete();

                                listMusicName = new ArrayList<String>();
                                listMusicTime = new ArrayList<String>();
                                listMusicNameCont = new ArrayList<String>();
                                getAllMusics(pathControl());
                                customAdapterListMusics adapterListMusics = new customAdapterListMusics(getApplication(), getAllMusics(pathControl()), listMusicNameCont, listMusicName, listMusicTime);
                                mylist.setAdapter(adapterListMusics);
                                new GlobalData().setSongList(getAllMusics(pathControl()));
                                Toast.makeText(getApplicationContext(),"Deleted!",Toast.LENGTH_SHORT).show();
                            }
                            catch(Exception e){
                                Toast.makeText(getApplicationContext(),"An Error Accoured!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                    builder.show();

                }
            });



            return rowView;


        }


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

    public void play_previous(View view){

        mediaPlayer.pause();
        mediaPlayer.release();
        int onceki=findSiralamaByUrl(s_media);

        if(onceki==0){
            onceki=1;
        }

        try{

            play_media(file_arraylist.get(onceki-1));
            s_media=file_arraylist.get(onceki-1);

        }catch (Exception e){

        }

    }


    public void play_next(View view){

        mediaPlayer.pause();
        mediaPlayer.release();
        int onceki=findSiralamaByUrl(s_media);

        if(onceki+1==file_arraylist.size()){
           onceki=-1;
        }

        try{
            play_media(file_arraylist.get(onceki+1));
            s_media=file_arraylist.get(onceki+1);
        }catch (Exception e){

        }

    }






    public ArrayList<String> getAllMusics(String path) {

        file_arraylist = new ArrayList<String>();// list of file paths

        File[] listFile;
        File file = new File(path);

        if (file.isDirectory()) {
            listFile = file.listFiles();

            for (int i = 0; i < listFile.length; i++) {

                file_arraylist.add("file://" + listFile[i].getAbsolutePath());

                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
               try {
                   mmr.setDataSource(listFile[i].getAbsolutePath());
               }catch (Exception e){
                  // Toast.makeText(this, "Error ! ", Toast.LENGTH_SHORT).show();
               }

                String timem = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                String teee = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

                long dur=0;

                try {
                    dur = Long.parseLong(timem);
               } catch (Exception e){
                    //Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
               }

                String seconds = String.valueOf((dur % 60000) / 1000);
                String minutes = String.valueOf(dur / 60000);
                if (seconds.length() == 1) {

                    listMusicTime.add("0" + minutes + ":0" + seconds);

                } else {
                    listMusicTime.add("0" + minutes + ":" + seconds);
                }

                Date lastModDate = new Date(listFile[i].lastModified());
                listMusicName.add(listFile[i].getName());
                listMusicNameCont.add(lastModDate.toString());

            }
        }
        return file_arraylist;
    }

    public String pathControl() {


        String path = Environment.getExternalStorageDirectory() + File.separator + "MusicDownloader/";

        if (path != null) {
            return path;

        }

        else {
            path = getApplication().getFilesDir() + File.separator+ "MusicDownloader/";

            return path;

        }


    }


    public String getMainPath() {


        String path = Environment.getExternalStorageDirectory() + File.separator ;

        if (path != null) {
            return path;

        }

        else {
            path = getApplication().getFilesDir() + File.separator;

            return path;

        }


    }

    public void recursiveScan(File f,ArrayList<File> listFile) {
        File[] file = f.listFiles();
        for (File ff : file) {
            if (ff.isDirectory()) recursiveScan(ff,listFile);
            if (ff.isFile() && ff.getPath().endsWith(".mp3")) {
                Log.d("Debug", ff.toString());
                listFile.add(ff);
            }
        }
    }




    public void play_media(final String audioFile){

        mediaPlayer = new MediaPlayer();

        Integer poss=findSiralamaByUrl(audioFile);
        tb_author.setText(listMusicName.get(poss).toString());

        try {

            mediaPlayer.setDataSource(audioFile);
            mediaPlayer.prepareAsync();

            final ProgressDialog dialog = new ProgressDialog(MusicFileActivity.this);

            dialog.setMessage(("Loading..."));
            dialog.setCancelable(false);
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.show();

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.release();
                    int onceki=findSiralamaByUrl(audioFile);

                    try{

                        play_media(file_arraylist.get(onceki+1));

                    }catch (Exception e){

                    }

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

                //set max value
                int mDuration = mediaPlayer.getDuration();
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
            mHandler.postDelayed(this, 10);

        }
    };







    public void play(View view){

        mediaPlayer.start();

    }



    // seekForward metodu
    public void seekForward(View view){

        //set seek time
        int seekForwardTime = 5000;

        // get current song position
        int currentPosition = mediaPlayer.getCurrentPosition();
        // check if seekForward time is lesser than song duration
        if(currentPosition + seekForwardTime <= mediaPlayer.getDuration()){
            // forward song
            mediaPlayer.seekTo(currentPosition + seekForwardTime);
        }else{
            // forward to end position
            mediaPlayer.seekTo(mediaPlayer.getDuration());
        }

    }

   // seekBackward metodu
    public void seekBackward(View view){

        //set seek time
        int seekBackwardTime = 5000;

        // get current song position
        int currentPosition = mediaPlayer.getCurrentPosition();
        // check if seekBackward time is greater than 0 sec
        if(currentPosition - seekBackwardTime >= 0){
            // forward song
            mediaPlayer.seekTo(currentPosition - seekBackwardTime);
        }else{
            // backward to starting position
            mediaPlayer.seekTo(0);
        }

    }




    private String getTimeString(long millis) {
        StringBuffer buf = new StringBuffer();

        long hours = millis / (1000*60*60);
        long minutes = ( millis % (1000*60*60) ) / (1000*60);
        long seconds = ( ( millis % (1000*60*60) ) % (1000*60) ) / 1000;

        buf
               // .append(String.format("%02d", hours))
              //  .append(":")
                .append(String.format("%02d", minutes))
                .append(":")
                .append(String.format("%02d", seconds));

        return buf.toString();
    }


    private int findSiralamaByUrl(String sarki_url){

        for(int i=0;i<file_arraylist.size();i++){

            if(sarki_url==file_arraylist.get(i).toString()){
                kacinci=i;
            }

        }

        return kacinci;
    }




    @Override
    protected void onPause() {
        super.onPause();

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



        if (id == R.id.rate) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
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

                    .setNegativeButton(R.string.rate_neutral, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.cancel();

                        }
                    })

            ;

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }



        return super.onOptionsItemSelected(item);
    }


}


/*

    /*public void onBackPressed(){
        super.onBackPressed();

        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
       /* Intent i=new Intent(MusicFileActivity.this,MusicSearchActivityDevelopment_tbd_v2.class);
        startActivity(i);  */

// cıkısa basılarak cıkıldıgında calmaya devam etsin diye -- geri dönüşte hata veriyor.
        /*   Intent i = new Intent(Intent.ACTION_MAIN);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);  */

      /* if(mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }  */


     /*   if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            //ss mediaPlayer = null;
        }  */

// finish();

