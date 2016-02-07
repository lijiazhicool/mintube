package com.shapps.ytube;

import android.util.Log;

import com.google.android.youtube.player.YouTubePlayerView;

/**
 * Created by shyam on 2/2/16.
 */
public class Session{

    private static YouTubePlayerView youTubePlayerView;

    public Session() {
    }

    public Session(YouTubePlayerView youTubePlayerView) {

        this.youTubePlayerView = youTubePlayerView;

    }

    public static YouTubePlayerView getYouTubePlayerView() {
        return youTubePlayerView;
    }

    public static void setYouTubePlayerView(YouTubePlayerView View) {
        Log.e("Yo ", " All Set!");
        youTubePlayerView = View;
    }
}
