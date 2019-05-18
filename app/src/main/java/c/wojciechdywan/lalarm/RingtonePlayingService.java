package c.wojciechdywan.lalarm;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import static android.media.AudioManager.STREAM_ALARM;

public class RingtonePlayingService extends Service {
    MediaPlayer mediaPlayer;
    private Context context;
    private int startId, vibreId;
    private boolean isRunning;
    private Vibrator vibr;
    private int volume;
    Database myDb;
    private String state, vibre_alarm, timeToAlarm;
    private String ringtone;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("media", "Received start id " + startId + ": " + intent);
        vibr = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);

        String[] my_extra = intent.getExtras().getStringArray("extra");
        Log.e("media", "My_Extra: " + my_extra[0] + "  vibr: " + my_extra[1]+
                " volume: "+my_extra[2]+" time to alarm: "+my_extra[3]);


        state = my_extra[0];
        vibre_alarm = my_extra[1];
        volume = Integer.parseInt(my_extra[2]);
        timeToAlarm = my_extra[3];

        startId =turn_on_off(state);
        vibreId =turn_on_off(vibre_alarm);
        // Run ?
        if(!this.isRunning && startId == 1){
            Log.e("media", "vibre: "+vibre_alarm+" : "+vibreId);
            Log.e("media", "state: "+state+" : "+startId);
           // mediaPlayer = MediaPlayer.create(this,R.raw.mdk);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(STREAM_ALARM);

            // volume
            AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            volume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)*volume/100;
            int maxVolume = (int)volume;

            mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, AudioManager.FLAG_PLAY_SOUND);
            // Song
            String name = ringtone_Switch(my_extra[4]);
            Uri notification =Uri.parse("android.resource://"+getPackageName()+"/raw/"+name);

            try {
                mediaPlayer.setDataSource(this,notification);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e("media", "there is no music and you want start");
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
            // vibres
            if(vibreId == 1){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    vibr.vibrate(VibrationEffect.createWaveform(new long[] {0,200,0},0));
                }else{
                    vibr.vibrate(new long[] { 0, 200, 0 }, 0);
                }
            }
            this.isRunning = true;
            this.startId=0;
            // ADD data
            myDb = new Database(this);
            AddData(timeToAlarm,vibre_alarm,my_extra[2],my_extra[4]);

            Intent alarm_intent = new Intent(RingtonePlayingService.this,AlarmActivity.class);
            startActivity(alarm_intent);
        }
        // if there is music playing and the user pressed alarm off
        else if(this.isRunning && startId == 0){
            Log.e("media", "there is music and you want end");
            mediaPlayer.stop();
            mediaPlayer.reset();
            vibr.cancel();
            this.isRunning=false;
            this.startId =0;
        }
        else if(!this.isRunning && startId == 0){
            Log.e("media", "there is no music and state 0");
            this.isRunning =false;
            this.startId = 0;
        }
        else if(this.isRunning && startId==1){
            Log.e("media", "there is music and start 1");
            this.isRunning =false;
            this.startId = 1;
        }else{
            Log.e("media", "somehow");
            mediaPlayer.stop();
            vibr.cancel();
        }


        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        // Tell the user we stopped.
        super.onDestroy();
        Toast.makeText(this, "On Destroy called", Toast.LENGTH_SHORT).show();
    }

    public int turn_on_off(String check){
        int id;
        if(check == null){
            id = 0;
        }
        else{
            switch (check) {
                case "on":
                    id = 1;
                    break;
                case "off":
                    id = 0;
                    break;
                default:
                    id = 0;
                    break;
            }
        }
        return id;
    }

    public void AddData(String time,String vibre, String volume, String song){
        String rate ="0";
        boolean isInserted = myDb.insertData(time,vibre,volume,rate, song);
    }

    public String ringtone_Switch(String check){
        String name;
        if(check == null){
            name = "alarm_classic";
        }
        else{
            switch (check) {
                case "0":
                    name = "alarm_classic";
                    break;
                case "1":
                    name = "beep";
                    break;
                case "2":
                    name = "big_easy";
                    break;
                case "3":
                    name = "morning";
                    break;
                default:
                    name = "alarm_classic";
                    break;
            }
        }
        return name;
    }
}
