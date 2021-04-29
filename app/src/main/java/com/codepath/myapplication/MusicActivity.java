package com.codepath.myapplication;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MusicActivity extends AppCompatActivity {

    private MediaPlayer mMediaPlayer;
    private Button mPlay, mPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        mPause = findViewById(R.id.pause);
        mPlay = findViewById(R.id.play);
        mMediaPlayer = MediaPlayer.create(this, R.raw.owl_city_fireflies);

        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.start();
            }
        });
        mPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mMediaPlayer.pause();
            }
        });
    }

    private void releaseMediaPlayer(){
        if (mMediaPlayer != null){
            mMediaPlayer.release();
            mMediaPlayer = null;

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }
}