package com.mn.tiger;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;

import com.mn.tiger.app.TGApplicationProxy;
import com.mn.tiger.log.Logger;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Created by peng on 15/8/2.
 */
public class TGAudioPlayer
{
    private static final Logger LOG = Logger.getLogger(TGAudioPlayer.class);

    private volatile static TGAudioPlayer instance;

    private static Handler timeHandler;

    private int playDuration = 0;

    private MediaPlayer mediaPlayer;

    private OnPlayListener onPlayListener;

    private String currentDataSource = "";

    private FileDescriptor currentFileDataSource = null;

    private int PROGRESS_TIME_INTERVAL = 200;

    private Mode mode = Mode.SINGLE;

    private boolean speakerphoneOn = false;

    public enum Mode
    {
        SINGLE,
        SINGLE_CIRCLE
    }

    public static TGAudioPlayer getInstance()
    {
        if (null == instance)
        {
            synchronized (TGAudioPlayer.class)
            {
                if (null == instance)
                {
                    instance = new TGAudioPlayer();
                }
            }
        }
        return instance;
    }

    private TGAudioPlayer()
    {
        mediaPlayer = new MediaPlayer();
        timeHandler = new Handler();
    }

    public void setMode(Mode mode)
    {
        this.mode = mode;
    }

    public void start(FileDescriptor dataSource, OnPlayListener listener)
    {
        start(dataSource, listener, false);
    }

    public void start(FileDescriptor dataSource, OnPlayListener listener, final boolean speakerphoneOn)
    {
        this.currentFileDataSource = dataSource;
        this.currentDataSource = currentFileDataSource.toString();
        this.onPlayListener = listener;
        this.speakerphoneOn = speakerphoneOn;
        //setOnCompletionListener 当前多媒体对象播放完成时发生的事件
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                switch (mode)
                {
                    case SINGLE:
                        if (null != onPlayListener)
                        {
                            onPlayListener.onPlayComplete(currentDataSource);
                        }
                        break;
                    case SINGLE_CIRCLE:
                        //重新播放
                        start(currentFileDataSource, onPlayListener, speakerphoneOn);
                        break;
                    default:
                        break;
                }
            }
        });

        try
        {
            playDuration = 0;
            mediaPlayer.reset(); //重置多媒体
            mediaPlayer.setDataSource(currentFileDataSource);//为多媒体对象设置播放路径
            play(speakerphoneOn);
        }
        catch (Exception e)
        {
            LOG.e(e);
        }
    }

    public void start(String dataSource)
    {
        start(dataSource, null, false);
    }

    public void start(final String dataSource, final OnPlayListener listener)
    {
        start(dataSource, listener, false);
    }

    public void start(final String dataSource, final OnPlayListener listener, boolean speakerphoneOn)
    {
        this.currentDataSource = dataSource;
        this.onPlayListener = listener;
        this.speakerphoneOn = speakerphoneOn;
        //setOnCompletionListener 当前多媒体对象播放完成时发生的事件
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                switch (mode)
                {
                    case SINGLE:
                        if (null != onPlayListener)
                        {
                            onPlayListener.onPlayComplete(currentDataSource);
                        }
                        break;
                    case SINGLE_CIRCLE:
                        //重新播放
                        start(currentDataSource, onPlayListener, TGAudioPlayer.this.speakerphoneOn);
                        break;
                    default:
                        break;
                }
            }
        });

        try
        {
            playDuration = 0;
            mediaPlayer.reset(); //重置多媒体
            mediaPlayer.setDataSource(currentDataSource);//为多媒体对象设置播放路径
            play(speakerphoneOn);
        }
        catch (Exception e)
        {
            LOG.e(e);
        }
    }

    private void play(boolean speakerphoneOn) throws IOException
    {
        AudioManager audioManager = (AudioManager)TGApplicationProxy.getApplication().getSystemService(Context.AUDIO_SERVICE);
        if(speakerphoneOn)
        {
            audioManager.setBluetoothScoOn(false);
            audioManager.setMicrophoneMute(false);
            //设置声音强制从扬声器输出
            audioManager.setSpeakerphoneOn(true);
            audioManager.setMode(AudioManager.STREAM_MUSIC);
        }

        playDuration = 0;
        mediaPlayer.prepare();//准备播放
        mediaPlayer.start();//开始播放

        final int audioDuration = mediaPlayer.getDuration();

        timeHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                playDuration += PROGRESS_TIME_INTERVAL;
                if (null != onPlayListener && playDuration <= audioDuration)
                {
                    onPlayListener.onPlaying(currentDataSource, playDuration, audioDuration);
                }

                if(playDuration < audioDuration)
                {
                    timeHandler.postDelayed(this, PROGRESS_TIME_INTERVAL);
                }
            }
        },  PROGRESS_TIME_INTERVAL);

        if(null != onPlayListener)
        {
            onPlayListener.onPlayStart(currentDataSource);
        }
    }

    public void toggle()
    {
        if (mediaPlayer.isPlaying())
        {
            mediaPlayer.pause();
            if(null != onPlayListener)
            {
                onPlayListener.onPlayPause(currentDataSource);
            }
        }
        else
        {
            mediaPlayer.start();
            if(null != onPlayListener)
            {
                onPlayListener.onPlayStart(currentDataSource);
            }
        }
    }

    public void stop()
    {
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            if(null != onPlayListener)
            {
                onPlayListener.onPlayStop(currentDataSource);
            }
        }
    }

    public boolean isPlaying()
    {
        return mediaPlayer.isPlaying();
    }

    public interface OnPlayListener
    {
        void onPlayStart(String dataSource);

        void onPlaying(String dataSource, int playDuration, int audioDuration);

        void onPlayPause(String dataSource);

        void onPlayStop(String dataSource);

        void onPlayComplete(String dataSource);
    }
}
