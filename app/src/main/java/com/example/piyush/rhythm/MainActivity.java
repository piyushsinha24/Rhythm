package com.example.piyush.rhythm;

import android.app.SearchManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
    private String sortorder;

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);

        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = (SearchView)menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        songAdapter = new SongAdapter(this,songs);
        recyclerView.setAdapter(songAdapter);
        LinearLayoutManager  linearLayoutManager=new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(recyclerView.getContext(),linearLayoutManager.getOrientation());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);
        sortorder = MediaStore.MediaColumns.DISPLAY_NAME+"";
        songAdapter.setOnItemClickListener(new SongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final Button b, View v, final Songinfo obj, int position) {
                if (b.getText().equals("Stop")) {
                                mp.stop();
                                mp.reset();
                                mp.release();
                                mp = null;
                                b.setText("Play");
                                seekBar.setProgress(0);
                            } else {
                                Runnable runnable =new Runnable() {
                                    @Override
                                    public void run() {
                                        try
                                        {
                                        mp = new MediaPlayer();
                                        mp.setDataSource(obj.getSongurl());
                                        mp.prepareAsync();
                                        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                            @Override
                                            public void onPrepared(MediaPlayer mp) {
                                                mp.start();
                                                seekBar.setProgress(0);
                                                seekBar.setMax(mp.getDuration());
                                                Log.d("Prog","run: "+mp.getDuration());

                                            }
                                        });
                                        b.setText("Stop");



                        } catch (Exception e) {}

                    }
                };
                handler.postDelayed(runnable,100);

            }
            }


        });

        CheckPermission();
        Thread t=new runThread();
        t.start();

    }



    public class runThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (mp != null) {
                    seekBar.post(new Runnable() {
                        @Override
                        public void run() {
                            seekBar.setProgress(mp.getCurrentPosition());

                            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                @Override
                                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                                    if(mp!=null && b){
                                        mp.seekTo(i);
                                    }
                                }

                                @Override
                                public void onStartTrackingTouch(SeekBar seekBar) {

                                }

                                @Override
                                public void onStopTrackingTouch(SeekBar seekBar) {

                                }
                            });
                        }


                    });
                     Log.d("Runwa","run: "+mp.getCurrentPosition());

                }

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
            loadsongs();

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
        String selection = MediaStore.Audio.Media.IS_MUSIC+"!= 0";
        Cursor cursor = getContentResolver().query(uri,null,selection,null,sortorder);
        if(cursor!=null)
        {
            if(cursor.moveToFirst()){
                do {
                    String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    name = name.replaceFirst("^\\s*[0-9]+\\s*", "");
                    name = name.replaceFirst("^\\\\d+\\\\.?\\\\s*-(?:.*?-)?\\\\s*","");
                    name = name.replaceFirst("\'","");
                    name = name.replaceFirst("\"","");
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    Songinfo s= new Songinfo(name,artist,url);
                    songs.add(s);

                }while (cursor.moveToNext());
                Log.d("SongsList","run: "+songs);


            }
            cursor.close();
            songAdapter = new SongAdapter(this,songs);

        }
    }


}
