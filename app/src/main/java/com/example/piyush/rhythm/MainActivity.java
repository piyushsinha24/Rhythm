package com.example.piyush.rhythm;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Songinfo> songs = new ArrayList<Songinfo>();
    RecyclerView recyclerView;
    SeekBar seekBar;
    SongAdapter songAdapter;
    MediaPlayer mp;
    Handler handler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
       // Songinfo s=new Songinfo("Cheap Thrills","Sia","https://fullgaana.com/siteuploads/files/sfd4/1522/Sia%20-%20Cheap%20Thrills%20(feat.%20Sean%20Paul)-(FullGaana.Com).mp3");
      //  songs.add(s);
        songAdapter = new SongAdapter(this,songs);
        LinearLayoutManager  linearLayoutManager=new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(recyclerView.getContext(),linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(songAdapter);
        songAdapter.setOnItemClickListener(new SongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final Button b, View v, final Songinfo obj, int position) {
                Runnable r=new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (b.getText().toString().equals("Stop")) {
                                b.setText("Play");
                                mp.stop();
                                mp.reset();
                                mp.release();
                                mp = null;
                            } else {
                                mp = new MediaPlayer();
                                mp.setDataSource(obj.getSongurl());
                                mp.prepareAsync();
                                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mediaPlayer) {
                                        mp.start();
                                        seekBar.setProgress(0);
                                        seekBar.setMax(mp.getDuration());

                                    }
                                });
                                b.setText("Stop");
                            }

                        } catch (IOException e) {

                        }

                    }
                };
                handler.postDelayed(r,100);

            }


        });

        CheckPermission();
        Thread t=new Mythread();
        t.start();

    }
    public class Mythread extends Thread{
        @Override
        public void run(){
            try {
                Thread.sleep(1000);
                if(mp!=null) {
                    seekBar.setProgress(mp.getCurrentPosition());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private void CheckPermission(){
        if(Build.VERSION.SDK_INT>=23) {
            if (ActivityCompat.checkSelfPermission(this
                    , android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
                return;
            }
        }
        else {
            loadsongs();
        }

    }
    @Override
    public  void onRequestPermissionsResult(int requestcode, @NonNull String[]permissions,@NonNull int [] grantresults)
    {
        switch (requestcode){
            case 123:
                if(grantresults[0]==PackageManager.PERMISSION_GRANTED){
                    loadsongs();
                }
                else{
                    Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
                    CheckPermission();
                }
                break;
                default:
                    super.onRequestPermissionsResult(requestcode,permissions,grantresults);
        }

    }
    private void loadsongs(){
        Uri uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC+"!=0";
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null)
        {
            if(cursor.moveToFirst()){
                do {
                    String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    Songinfo s= new Songinfo(name,artist,url);
                    songs.add(s);

                }while (cursor.moveToNext());



            }
            cursor.close();
            songAdapter = new SongAdapter(this,songs);

        }
    }


}
