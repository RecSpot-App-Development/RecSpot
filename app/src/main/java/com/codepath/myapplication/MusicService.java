package com.codepath.myapplication;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

// Service class execute music playback continuously, even when the user is not directly interacting with the application
public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private MediaPlayer mediaPlayer;

    private ArrayList<Song> songs;

    private int currentSongPosition;

    private final IBinder musicBinder = new MusicBinder();

    private String songTitle = "";

    private static final int NOTIFICATION_ID = 1;

    private boolean shuffle = false;

    private Random random;

    public void onCreate(){
        super.onCreate();

        random = new Random();

        currentSongPosition = 0;
        mediaPlayer = new MediaPlayer();
        initMediaPlayer();
    }

    public void setShuffle(){
        if (shuffle) shuffle = false;
        else shuffle = true;
    }

    public void initMediaPlayer(){
        // The wake lock will let playback continue when device becomes idle
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        //mediaPlayer.setDataSource(getApplicationContext(),musicUri);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
    }

    public void setList(ArrayList<Song> theSongs){
        songs = theSongs;
    }

    // Binder support the interaction between the Activity and Service classes
    public class MusicBinder extends Binder {
        MusicService getService(){
            return MusicService.this;
        }
    }

    public void playSong(){
        // Since we also use this code when the user is playing subsequent songs
        mediaPlayer.reset();

        Song song = songs.get(currentSongPosition);
        songTitle = song.getTitle();
        long songId = song.getID();

        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId);
        try {
            mediaPlayer.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception ex){
            Log.e("MUSIC SERVICE", "Error setting data source", ex);
        }

        mediaPlayer.prepareAsync();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopForeground(true);
    }

    public void setCurrentSongPosition(int songPosition){
        currentSongPosition = songPosition;
    }

    public int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration(){
        return mediaPlayer.getDuration();
    }

    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }

    public void pausePlayer(){
        mediaPlayer.pause();
    }

    public void seek(int position){
        mediaPlayer.seekTo(position);
    }

    public void go(){
        mediaPlayer.start();
    }

    public void playPrev(){
        currentSongPosition--;
        if(currentSongPosition < 0) currentSongPosition = songs.size() - 1;

        playSong();
    }

    public void playNext(){
        if (shuffle) {
            int newSongPosition = currentSongPosition;
            while (newSongPosition == currentSongPosition){
                newSongPosition = random.nextInt(songs.size());
            }

            currentSongPosition = newSongPosition;
        } else {
            currentSongPosition++;
            if (currentSongPosition >= songs.size()) currentSongPosition = 0;
        }

        playSong();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBinder;
    }

    public boolean onUnbind(Intent intent){
        mediaPlayer.stop();
        mediaPlayer.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (mediaPlayer.getCurrentPosition() > 0){
            mediaPlayer.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        mediaPlayer.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        // start playback
        mediaPlayer.start();

        // NotificationIntent display a notification showing the title of the track beging played
        Intent notificationIntent = new Intent(this, MusicActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // PendingItent take the user back to the main Activity when they select the notification
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.play)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(songTitle);
        Notification notification = builder.build();

        //startForeground(NOTIFICATION_ID, notification);
    }
}