package myt.music.app_pro.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;


import java.io.IOException;
import java.util.ArrayList;

import myt.music.app_pro.Constans.Constants;
import myt.music.app_pro.Constans.GlobalData;
import myt.music.app_pro.R;


public class MediaPlaybackService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    public static final String MPS_MESSAGE = "com.freemusicdownloader.mp3downloader.Services.MediaPlaybackService.MESSAGE";
    public static final String MPS_RESULT = "com.freemusicdownloader.mp3downloader.Services.MediaPlaybackService.RESULT";
    public static final String MPS_COMPLETED = "com.freemusicdownloader.mp3downloader.Services.MediaPlaybackService.COMPLETED";

    MediaPlayer mMediaPlayer = null;
    Uri file;
    RemoteViews bigViews, views;
    ArrayList<String> gallerySongList;
    int songListIndex;

    public Intent notificationIntent;
    public PendingIntent pendingIntent;

    private NotificationManager notifManager;
    NotificationCompat.Builder mBuilder;
    String NOTIFICATION_CHANNEL_ID = "com.freemusicdownloader.mp3downloader";

    private static final int NOTIF_ID = 1234;

    public class IDBinder extends Binder {

        MediaPlaybackService getService() {
            return MediaPlaybackService.this;
        }
    }

    //deniz

    IDBinder idBinder = new IDBinder();

    LocalBroadcastManager broadcastManager;
    MediaPlaybackService playbackService;

    private NotificationManagerCompat notificationManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        broadcastManager = LocalBroadcastManager.getInstance(this);

        notificationManager = NotificationManagerCompat.from(this);

        super.onCreate();

        Log.i("asdsad", "asdasd");
    }

    @Override
    public void onDestroy() {
        stop();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return idBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("DESTROYED", "unBinded");
        //notificationManager.cancel(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE);
        stop();
        return super.onUnbind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i("TASK_REMOVED", "TRUE");

        mMediaPlayer.pause();
        notificationManager.cancel(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE);

        stopSelf();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void init(Uri file) {
        this.file = file;

        pause();

        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(getApplicationContext(), file);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            stop();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        showNotification(R.drawable.ic_pause_circle_filled_black_24dp);
        Thread updateThread = new Thread(sendUpdates);
        updateThread.start();
    }

    private Runnable sendUpdates = new Runnable() {
        @Override
        public void run() {
            while (mMediaPlayer != null) {
                SystemClock.sleep(200);
                sendElapsedTime();

//                if (isFinished()) {
//
//                    Log.i("FINISHED", "YES");
//
//                    if (GlobalData.isRepeatSong()) {
//                        buttonPlayUpdate();
//
//                    } else {
//                        Log.i("REPEAT", "OFF!!");
//
//                        //NEXT SONG WE COULDN'T DO IT AT THIS VERSION
////                    if(new GlobalData().getClickNext() == null ||!(new GlobalData().getClickNext())) {
////                        gallerySongList = new GlobalData().getSongList();
////                        songListIndex = new GlobalData().getSongListIndex();
////                        new GlobalData().setCounter(songListIndex);
////                        new GlobalData().setClickNext(true);
////                    }
//
//                        //showNotification(R.drawable.ic_pause_circle_filled_black_24dp);
//
//                    }
//
//                }

                try {
                    Thread.sleep(2500);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

//    public boolean isFinished() {
//
//        boolean isFinished = false;
//
//        try {
//            if (((GlobalData.getSongDuration() - mMediaPlayer.getCurrentPosition()) < 700)) {
//                Log.i("FINISHED", "YES!");
//                isFinished = true;
//            }
//        } catch (Exception e) {
//
//            Log.i("ERROR", "" + e.getMessage());
//        }
//        return isFinished;
//    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void pause() {
        if (mMediaPlayer != null)
            mMediaPlayer.pause();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void play() {
        if (mMediaPlayer != null)
            mMediaPlayer.start();

    }

    public void buttonPlayUpdate() {
        play();
        showNotification(R.drawable.ic_pause_circle_filled_black_24dp);
    }

    public void buttonPauseUpdate() {
        pause();
        showNotification(R.drawable.ic_play_circle_filled_black_24dp);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
            file = null;
            showNotification(R.drawable.ic_play_circle_filled_black_24dp);


        }
    }


    public void seekTo(int msec) {
        mMediaPlayer.seekTo(msec);
    }

    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    public Uri getFile() {
        return file;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Intent intent = new Intent(MPS_COMPLETED);
        broadcastManager.sendBroadcast(intent);
        GlobalData.setIsFinished(true);

        //showNotification(R.drawable.ic_play_circle_filled_black_24dp);
    }

    private void sendElapsedTime() {

        Intent intent = new Intent(MPS_RESULT);
        if (mMediaPlayer != null)
            intent.putExtra(MPS_MESSAGE, mMediaPlayer.getCurrentPosition());

        broadcastManager.sendBroadcast(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent.getAction().equals("") || intent.getAction().isEmpty() != true || intent.getAction() != null) {

            if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {

                // showNotification(R.drawable.ic_pause_circle_filled_black_24dp);
                Log.i("click_event", "Clicked NOTIFICATION");

//            if (mMediaPlayer.getDuration() != 0)
//                GlobalData.setSongDuration(mMediaPlayer.getDuration());
            } else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {

                gallerySongList = new GlobalData().getSongList();
                songListIndex = new GlobalData().getSongListIndex();
                new GlobalData().setCounter(songListIndex);
                new GlobalData().setClickPrevious(true);

                Log.i("click_event", "Clicked Prevvvv");

            } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
                play();
                Log.i("click_event", "Clicked Play");
            } else if (intent.getAction().equals(Constants.ACTION.PAUSE_ACTION)) {

                playbackService = new GlobalData().getMediaPlaybackService();


                if (playbackService.isPlaying()) {
                    buttonPauseUpdate();
                } else {
                    buttonPlayUpdate();
                }

                notificationManager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, mBuilder.build());

            } else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {

                gallerySongList = new GlobalData().getSongList();
                songListIndex = new GlobalData().getSongListIndex();
                new GlobalData().setCounter(songListIndex);
                new GlobalData().setClickNext(true);

                Log.i("click_event", "Clicked nextt");


            } else if (intent.getAction().equals(
                    Constants.ACTION.STOPFOREGROUND_ACTION)) {
                Log.i("click_event", "Received Stop Foreground Intent");

                if (mMediaPlayer != null)
                    mMediaPlayer.pause();
                notificationManager.cancel(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE);
            }
        }

        return START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showNotification(int drawable) {

        bigViews = new RemoteViews(getPackageName(), R.layout.status_bar_expanded);
        views = new RemoteViews(getPackageName(), R.layout.status_bar);


        bigViews.setImageViewBitmap(R.drawable.smile_icon,
                Constants.getDefaultAlbumArt(this));
        views.setImageViewBitmap(R.drawable.smile_icon,
                Constants.getDefaultAlbumArt(this));

        bigViews.setImageViewResource(R.id.status_bar_pause, drawable);
        views.setImageViewResource(R.id.status_bar_play, drawable);

        notificationIntent = new Intent(this, MusicPlayer.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent previousIntent = new Intent(this, MediaPlaybackService.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);

        Intent playIntent = new Intent(this, MediaPlaybackService.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent pauseIntent = new Intent(this, MediaPlaybackService.class);
        pauseIntent.setAction(Constants.ACTION.PAUSE_ACTION);
        PendingIntent ppauseIntent = PendingIntent.getService(this, 0,
                pauseIntent, 0);

        Intent nextIntent = new Intent(this, MediaPlaybackService.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);

        Intent closeIntent = new Intent(this, MediaPlaybackService.class);
        closeIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
                closeIntent, 0);

        views.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);

        views.setOnClickPendingIntent(R.id.status_bar_play, pendingIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_play, pendingIntent);

        views.setOnClickPendingIntent(R.id.status_bar_play, ppauseIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_pause, ppauseIntent);

        views.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);

        views.setOnClickPendingIntent(R.id.status_bar_previous, ppreviousIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);

        views.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);

        String songName = new GlobalData().getMusicName();
        views.setTextViewText(R.id.status_bar_track_name, songName);
        bigViews.setTextViewText(R.id.status_bar_track_name, songName);

        gallerySongList = new GlobalData().getSongList();
        songListIndex = new GlobalData().getSongListIndex();

        if (gallerySongList != null || songListIndex != 0) {

            try {
                MediaMetadataRetriever mData = new MediaMetadataRetriever();
                mData.setDataSource(this, Uri.parse(gallerySongList.get(songListIndex)));

                byte art[] = mData.getEmbeddedPicture();
                if (art != null) {
                    Bitmap image = BitmapFactory.decodeByteArray(art, 0, art.length);
                    new GlobalData().setSongPicture(image);
                    bigViews.setImageViewBitmap(R.id.status_bar_album_art, image);
                    views.setImageViewBitmap(R.id.status_bar_album_art, image);
                } else {
                    bigViews.setImageViewBitmap(R.id.status_bar_album_art, Constants.getDefaultAlbumArt(this));
                    views.setImageViewBitmap(R.id.status_bar_album_art, Constants.getDefaultAlbumArt(this));
                }
            }catch (NullPointerException e){}

        }

        views.setTextViewText(R.id.status_bar_artist_name, "Artist Name");
        bigViews.setTextViewText(R.id.status_bar_artist_name, "Artist Name");

        String id = "channel_id";
        String title = "Channel_title";
        Intent intent;

        if (notifManager == null) {
            notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = notifManager.getNotificationChannel(id);
            if (notificationChannel == null) {
                notificationChannel = new NotificationChannel(id, title, importance);
                notificationChannel.setSound(null, null);
                notificationChannel.setLockscreenVisibility(Notification.BADGE_ICON_LARGE);
                notifManager.createNotificationChannel(notificationChannel);

            }
            notificationChannel.setSound(null, null);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            mBuilder = new NotificationCompat.Builder(getApplicationContext(), id);
            mBuilder.setSmallIcon(R.drawable.ic_music_note)
                    .setAutoCancel(true)
                    .setCustomContentView(views)
                    .setCustomBigContentView(bigViews)
                    .setContentIntent(pendingIntent)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setOnlyAlertOnce(true);

            Notification notification = mBuilder.build();
            notification.flags = Notification.FLAG_ONGOING_EVENT;
            notificationManager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);


        } else {

            mBuilder = new NotificationCompat.Builder(this, NOTIFICATION_SERVICE)
                    .setChannelId(NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_music_note)
                    .setCustomContentView(views)
                    .setCustomBigContentView(bigViews)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setOnlyAlertOnce(true);


            Notification notification = mBuilder.build();
            notification.flags = Notification.FLAG_ONGOING_EVENT;
            notificationManager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);

        }


    }


}
