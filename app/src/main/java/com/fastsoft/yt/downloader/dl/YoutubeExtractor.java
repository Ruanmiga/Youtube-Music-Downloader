package com.fastsoft.yt.downloader.dl;
import android.content.Context;
import android.util.SparseArray;
import com.fastsoft.yt.downloader.dl.models.VideoData;
import com.fastsoft.yt.downloader.dl.models.YtFile;

public class YoutubeExtractor {

    YoutubeParser ytParser;

    public YoutubeExtractor(Context context){
        this.ytParser = new YoutubeParser(context);
    }
    public static YoutubeExtractor with(Context context){
        return new YoutubeExtractor(context);
    }
    public YoutubeExtractor setOnExtractionListener(final YoutubeParser.OnExtractListener listener){
        if(listener != null){
            ytParser.setOnExtractListener(new YoutubeParser.OnExtractListener(){

                    @Override
                    public void onExtractSuccess(SparseArray<YtFile> ytFile, VideoData vData)
                    {
                        listener.onExtractSuccess(ytFile,vData);
                    }

                    @Override
                    public void onError()
                    {
                        listener.onError();
                    }

                });
        }
        return this;
    }
    public YoutubeExtractor extract(String youtubeUrl){
        ytParser.extract(youtubeUrl);
        return this;
    }
    public YoutubeExtractor extractById(String videoId){
        this.extract("https://www.youtube.com/watch?v=" + videoId);
        return this;
    }
}

