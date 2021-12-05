package com.fastsoft.yt.downloader.dl.models;

public class MusicFile {

    String url;
    Format.ACodec format;
    int kbits;

    public MusicFile(String url,Format.ACodec format,int kbits){
        this.url = url;
        this.kbits = kbits;
        this.format = format;
    }
    public String getUrl(){
        return url;
    }
    public String getFormat(){
        switch(format){
            case AAC:
             return "aac";
            case MP3:
                return "aac";
            case VORBIS:
                return "vorbis";
            case OPUS:
                return "opus";
            case NONE:
                default:
                return "none";
        }
    }
    public String getKbits(){
        return kbits + "Kbit/s";
    }

}

