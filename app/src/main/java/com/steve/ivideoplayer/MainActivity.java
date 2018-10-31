package com.steve.ivideoplayer;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {
    VideoView myVideoV;
    private static final String VIDEO_SAMPLE = "tacoma_narrows";
    private static final String VIDEO_SAMPLE2 =
            "https://developers.google.com/training/images/tacoma_narrows.mp4";
    private int mCurrentPosition = 0;
    private static final String PLAYBACK_TIME = "play_time";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(PLAYBACK_TIME);
        }

        setContentView(R.layout.activity_main);
        myVideoV = (VideoView)findViewById(R.id.myVideoView);
        MediaController controller = new MediaController(this); // create a MediaController with this Context.
        controller.setMediaPlayer(myVideoV);  // set videoView to the MediaController.
        myVideoV.setMediaController(controller); // set the MediaController to this videoView.
        initializePlayer();
    }

    private void initializePlayer() {
        DEBUG_LOG("initializePlayer()");
        Uri videoUri = getMedia(VIDEO_SAMPLE2);
        myVideoV.setVideoURI(videoUri);

        myVideoV.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                DEBUG_LOG( "mCurrentPosition=" + mCurrentPosition);

                if (mCurrentPosition > 0) {
                    myVideoV.seekTo(mCurrentPosition);
                } else {
                    // Skipping to 1 shows the first frame of the video.
                    myVideoV.seekTo(1);
                }
                myVideoV.start();
            }
        });

        myVideoV.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(getBaseContext(), "Playback completed", Toast.LENGTH_SHORT).show();
                myVideoV.seekTo(1);
                mCurrentPosition = 1;
            }
        });
    }

    private void DEBUG_LOG(String s) {
        if (BuildConfig.DEBUG)
            Log.d("=============>> DEBUG: ", s);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // mCurrentPosition will be used on onResume() for the video.
        mCurrentPosition = myVideoV.getCurrentPosition(); // global variable will on destroy(). will recover on create().
        DEBUG_LOG("onSaveInstanceState() saved playback_time = " + mCurrentPosition);
        outState.putInt(PLAYBACK_TIME, mCurrentPosition);
        super.onSaveInstanceState(outState);  // this data will be read only in onCreate().
    }

    private void releasePlayer() {
        myVideoV.stopPlayback();
        mCurrentPosition=0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        DEBUG_LOG("onResume()");
        initializePlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        DEBUG_LOG("onStop()");
        releasePlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        DEBUG_LOG("onPause()");

        //nSaveInstanceState(Bundle outState);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            myVideoV.pause();
        }
    }

    private Uri getMedia(String mediaName) {
        if (URLUtil.isValidUrl(mediaName)) {
            // media name is an external URL
            return Uri.parse(mediaName);
        } else
            return Uri.parse("android.resource://" + getPackageName() +
                "/raw/" + mediaName);
    }
}
