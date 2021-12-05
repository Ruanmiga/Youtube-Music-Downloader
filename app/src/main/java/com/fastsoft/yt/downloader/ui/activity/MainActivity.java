package com.fastsoft.yt.downloader.ui.activity;

import android.os.Bundle;
import com.fastsoft.yt.downloader.R;
import com.fastsoft.yt.downloader.common.activity.BaseActivity;
import android.content.Context;
import com.fastsoft.yt.downloader.dl.YoutubeExtractor;
import com.fastsoft.yt.downloader.dl.YoutubeParser;
import android.util.SparseArray;
import com.fastsoft.yt.downloader.dl.models.YtFile;
import com.fastsoft.yt.downloader.dl.models.VideoData;
import java.util.ArrayList;
import com.fastsoft.yt.downloader.dl.models.MusicFile;
import android.util.Log;
import com.fastsoft.ios.bottomsheet.BottomSheet;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.GridLayoutManager;
import android.util.DisplayMetrics;
import android.app.DownloadManager;
import java.io.File;
import android.os.Environment;
import android.net.Uri;
import android.widget.Toast;
import android.os.Build;
import android.Manifest;
import android.content.pm.PackageManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.app.AlertDialog;

public class MainActivity extends BaseActivity { 

    ArrayList<MusicFile> mf;
    TextView txtTitle;
	ImageView thumbnail;
    BottomSheet dialog;
    RecyclerView rv;
    MusicAdapter musicAdapter;
    String fileName;
    Bundle savedInstanceState;

    
    int PERMISSION_CODE = 304;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;

        getActionBar().hide();
        
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(CheckAndRequestPermission())
            {
                intializeDialog();
                dialog.show();
                getIntent(getIntent());
            }

        }else
        {
            intializeDialog();
            
            getIntent(getIntent());
        }



    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if(dialog != null)
        {
            dialog.dismiss();
        }
    }


    private void getIntent(Intent intent){
        if (savedInstanceState == null && Intent.ACTION_SEND.equals(intent.getAction())
            && intent.getType() != null && "text/plain".equals(intent.getType())) {

            String url = getIntent().getStringExtra(Intent.EXTRA_TEXT);

            if (url != null
                && (url.contains("youtube.com") || url.contains("youtu.be"))) {

                getMusicInfo(getApplicationContext(),url);
                
            }else{
                Toast.makeText(MainActivity.this,"Esta Url no es Compatible",Toast.LENGTH_LONG).show();
            }
        }
	}
    private void getMusicInfo(Context context,String ytUrl){
        YoutubeExtractor.with(context)
            .setOnExtractionListener(new YoutubeParser.OnExtractListener(){

                @Override
                public void onExtractSuccess(SparseArray<YtFile> ytFile, VideoData vData) {
                    setDatas(vData.getTitle(),vData.getHqImageUrl());
                    mf = parseMusicFiles(ytFile);
                    chargeMusic();
                }
                @Override
                public void onError() {
                }

            }).extract(ytUrl);
    }

    private void setDatas(String title,String thumbnailUrl){
        txtTitle.setText(title);
        Glide.with(getApplicationContext())
        .load(thumbnailUrl)
        .crossFade()
        .into(this.thumbnail);
       
    }
    
    private void chargeMusic(){
        musicAdapter = new MusicAdapter(MainActivity.this, mf, new ItemClickInterface(){

                @Override
                public void itemClick(String format,String kbits, String url) {
                    fileName = txtTitle.getText().toString();
                    if(fileName == null || fileName.equals("")){
                        fileName = kbits + "_" + System.currentTimeMillis();
                    }

                    downloadFile(getApplicationContext(),url,fileName,kbits.toLowerCase(),format.toLowerCase());
                    dialog.dismiss();
                    finish();
                    Toast.makeText(MainActivity.this,"La descarga A Comenzado",Toast.LENGTH_LONG).show();
                }
            });
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new GridLayoutManager(getApplicationContext(),calculateNoOfColumns(getApplicationContext(),100f)));
		rv.setAdapter(musicAdapter);
    }
    private void intializeDialog(){
        BottomSheet.Builder sheet = new BottomSheet.Builder(MainActivity.this);
        sheet.setContentView(intializeView());
        dialog = sheet.build();
        dialog.show();
    }
    private View intializeView(){
        View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_main,(ViewGroup)findViewById(R.id.activity));

        txtTitle = v.findViewById(R.id.video_title);
        thumbnail = v.findViewById(R.id.video_thumbnail);
        rv = v.findViewById(R.id.recyclerView);

        return v;
	}
    
    private ArrayList<MusicFile> parseMusicFiles(SparseArray<YtFile> ytFile){
        ArrayList<MusicFile> tempMusicFile = new ArrayList<>();
        for(int i = 0; i < ytFile.size(); i++){
            int itag = ytFile.keyAt(i);

            YtFile yt = ytFile.get(itag);
            if(yt.getFormat().getHeight() == -1){
                tempMusicFile.add(new MusicFile(yt.getUrl(),yt.getFormat().getAudioCodec(),yt.getFormat().getAudioBitrate()));
            }
        }

        return tempMusicFile;
    }
    public static int calculateNoOfColumns(Context context,float columnWidthDp){
        {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
            return (int)Math.round(screenWidthDp / columnWidthDp +  0.5);
        }
    }
    public static void downloadFile(Context context,String url, String fileName,String kbits,String format)
    {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        File file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() +"/"+ fileName  + "-" + kbits + "." + format);
        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(url))
            .setTitle(fileName)
            .setDescription("Official Youtube Downloader")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationUri(Uri.fromFile(file))
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true);

        downloadManager.enqueue(request);
	}
    
    public boolean CheckAndRequestPermission()
    {
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;

        if(checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED)
        {
            requestPermissions(new String[]{permission}, PERMISSION_CODE);

            return false;
        }else
        if
        (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_CODE)
        {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_DENIED)
            {
                if(shouldShowRequestPermissionRationale(permissions[0]))
                {
                    dialogPerm("", "This app need Storage Permission to downloading files.",
                        "Grant", new DialogInterface.OnClickListener()
                        {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                dialogInterface.dismiss();
                                CheckAndRequestPermission();
                            }


                        },"Deny/Exit", new DialogInterface.OnClickListener()
                        {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                dialogInterface.dismiss();
                                finish();
                            }


                        },false);
                }
                else
                {
                    dialogPerm("","You have denied permission, Allow permission at Application settings > Permissions",
                        "Go to settings",
                        new DialogInterface.OnClickListener()
                        {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                dialogInterface.dismiss();

                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }


                        },
                        "Deny/Exit",
                        new DialogInterface.OnClickListener()
                        {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                dialogInterface.dismiss();
                                finish();
                            }


                        }, false);


                }
            }else{
                intializeDialog();
                dialog.show();
                getIntent(getIntent());
            }

        }
	}
    private AlertDialog dialogPerm(String title, String msg, String posiviteLabel, DialogInterface.OnClickListener positiveClick,
                                   String negativeLabel, DialogInterface.OnClickListener negativeClick,
                                   boolean isCancelAble)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,android.R.style.Theme_Material_Light_Dialog)
            .setTitle(title)
            .setMessage(msg)
            .setCancelable(isCancelAble)
            .setPositiveButton(posiviteLabel, positiveClick)
            .setNegativeButton(negativeLabel, negativeClick);

        AlertDialog alert = builder.create();
        alert.show();
        return alert;
    }
	
}
