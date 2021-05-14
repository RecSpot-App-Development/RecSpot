package com.codepath.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.VideoView;


public class MainActivity extends AppCompatActivity {

    Button record_btn;
    Button music_btn;
    Button edit_btn;
    Button save_btn;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;  // Declare this integer globally
    Uri videoUri;
    private static final int REQUEST_PERMISSIONS = 403;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RESULT_OK && requestCode==1){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            VideoView videoView = new VideoView(this);
            videoView.setVideoURI(data.getData());
            videoView.start();
            builder.setView(videoView).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        record_btn = findViewById(R.id.record_btn);
        music_btn = findViewById(R.id.music_btn);
        edit_btn = findViewById(R.id.edit_btn);
        save_btn = findViewById(R.id.save_btn);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSaveActivity();
            }
        });


        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditActivity();
            }
        });

        record_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {


                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA,
                                    Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_PERMISSIONS);

                }
                else
                {
                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//                    File mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/myvideo.mp4");
//                    videoUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName()+".fileprovider", mediaFile);
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
                    startActivityForResult(intent, 101);


                }
            }
        });


        music_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMusicActivity();
            }
        });
    }


    private void openSaveActivity() {
        Intent intent = new Intent(this, SaveActivity.class);
        startActivity(intent);
    }

    private void openEditActivity() {
        Intent intent = new Intent(this, EditActivity.class);
        startActivity(intent);
    }

    private void openMusicActivity() {
        Intent intent = new Intent(this, MusicActivity.class);
        startActivity(intent);
    }

    private void openVideoActivity() {
        Intent intent = new Intent(this, VideoActivity.class);
        startActivity(intent);
    }
}