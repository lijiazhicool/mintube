package com.shapps.ytube;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YTubeView extends Activity{//extends YouTubeFailureRecoveryActivity {

    //    YouTubePlayerView youTubeView;
    String vId, pId;

    //For Result Activity
    public static int OVERLAY_PERMISSION_REQ = 1234;
    public static int OVERLAY_PERMISSION_REQ_BACKTO_ACT = 2345;
    static SharedPreferences sharedPref;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = this.getSharedPreferences(getString(R.string.FileName), Context.MODE_PRIVATE);
        if (!sharedPref.contains(getString(R.string.init))) {
            Log.e("Initializing ", "Shared Preferences");
            SharedPreferences.Editor editor = sharedPref.edit();
            //init to check if shared preference is initialized
            editor.putBoolean(getString(R.string.init), true);
            //Repeat
            //if repeatType = 0  --> no repeatType
            //if repeatType = 1  --> repeatType complete
            //if repeatType = 2  --> repeatType single
            editor.putInt(getString(R.string.repeat_type), 0);
            editor.putInt(getString(R.string.no_of_repeats), 5);
            //Type of player
            //WebView player = 0
            //Youtube player = 1
            editor.putInt(getString(R.string.player_type), 0);
            editor.commit();
        }
//
//        youTubeView = new YouTubePlayerView(this);
//
//        Session.setYouTubePlayerView(youTubeView);


        final Intent intent = getIntent();
        if(intent.getData() != null || intent.getStringExtra("android.intent.extra.TEXT") != null) {
            String link;
            if (intent.getData() != null) {
                link = intent.getData().toString();
            } else {
                link = intent.getStringExtra("android.intent.extra.TEXT");
            }
            Log.e("Link : ", link.toString());
            vId = "";
            Pattern pattern = Pattern.compile(
                    "^https?://.*(?:youtu.be/|v/|u/\\\\w/|embed/|watch[?]v=)([^#&?]*).*$",
                    Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(link.toString());
            if (matcher.matches()) {
                vId = matcher.group(1);
            }
            Log.e("Video Id : ", vId);
            //Getting Playlist id
            final String listID = link.substring(link.indexOf("http") + 4, link.length());
            Log.e("List ID Is : ", listID);
            pId = null;
            String regex = ".*list=([A-Za-z0-9_-]+).*?";
            pattern = Pattern.compile(regex,
                    Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(link.toString());
            if (matcher.matches()) {
                pId = matcher.group(1);
                Log.e("PID Is : ", pId);
                Constants.linkType = 1;
            }

            if (isServiceRunning(PlayerService.class)) {
                Log.e("Service : ", "Already Running!");
                PlayerService.startVid(vId, pId);
                finish();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                    Intent i = new Intent(this,
                            GetPermission.class);
                    i.putExtra("VID", vId);
                    i.putExtra("PID", pId);
                    startActivityForResult(i, OVERLAY_PERMISSION_REQ);
                    finish();
                }
                else {
                    Intent i = new Intent(this, PlayerService.class);
                    i.putExtra("VID_ID", vId);
                    i.putExtra("PLAYLIST_ID", pId);
                    i.setAction(Constants.ACTION.STARTFOREGROUND_WEB_ACTION);
                    startService(i);
                    finish();
                }
            }
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                Intent i = new Intent(this,
                        GetPermission.class);
                startActivityForResult(i, OVERLAY_PERMISSION_REQ_BACKTO_ACT);
                finish();
            }
            else {
                startActivity(new Intent(this, MainActivity.class));
            }
            finish();
        }

//        //Remove this
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
//            Intent i = new Intent(this,
//                    GetPermission.class);
//            startActivityForResult(i, OVERLAY_PERMISSION_REQ);
//            finish();
//        }
//        else {
//            Intent i = new Intent(this, PlayerService.class);
//            i.putExtra("VID_ID", "nIkFW78x6UA");
//            i.putExtra("PLAYLIST_ID", (String[]) null);
//            i.setAction(Constants.ACTION.STARTFOREGROUND_WEB_ACTION);
//            startService(i);
//            finish();
//        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == OVERLAY_PERMISSION_REQ_BACKTO_ACT){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    finish();
                }else{
                    startActivity(new Intent(this, MainActivity.class));
                }
            }
        }
        if (requestCode == OVERLAY_PERMISSION_REQ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    finish();
                }else{
                    Intent i = new Intent(this, PlayerService.class);
                    i.putExtra("VID_ID", vId);
                    i.putExtra("PLAYLIST_ID", pId);
                    i.setAction(Constants.ACTION.STARTFOREGROUND_WEB_ACTION);
                    startService(i);
                    finish();
                }
            }
        }
    }

    private boolean isServiceRunning(Class<PlayerService> playerServiceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (playerServiceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


//
//    @Override
//    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
//                                        boolean wasRestored) {
//
//    }
//
//
//    @Override
//    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
//        return youTubeView;
//    }
}