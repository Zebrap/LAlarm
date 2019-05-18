package c.wojciechdywan.lalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.util.Arrays;
import java.util.Calendar;

public class ClockActivity extends AppCompatActivity {

    private TimePicker alarmTimePicker;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    private Button btnRate;
    private String vibr;
    private static SeekBar seek_bar;
    private static TextView text_volume;
    private static String[] ringtone = {"alarm classic","beep","big easy","morning"};
    private TensorFlowInferenceInterface inferenceInterface;
    private Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        vibr = "off";
        alarmTimePicker = (TimePicker) findViewById(R.id.timePicker);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        seek_bar = (SeekBar) findViewById(R.id.seekBar2);
        spinner = (Spinner)findViewById(R.id.spinner);
        //model tensorflow
        inferenceInterface = new TensorFlowInferenceInterface(getAssets(), "tensorflow_lite_xor_nn.pb");
        btnRate = findViewById(R.id.btnRate);
        btnRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long time = GetTime(true);
                time = time/(60000);
                int volume_value = seek_bar.getProgress();
                int vib;
                if(vibr.equals("on")){
                    vib = 1;
                }else{
                    vib = 0;
                }
                int sound_id = spinner.getSelectedItemPosition();

                float[] input = {time,vib,volume_value,sound_id};
                float[] output = predict(input);
                Log.d("rate", Arrays.toString(input)+" -> "+Arrays.toString(output));
                AlertDialog.Builder bulder = new AlertDialog.Builder(ClockActivity.this);
                bulder.setMessage("Ocena budzika: "+Arrays.toString(output)+", Skala: 0 - bardzo źle, 1 - Idealny. Zaleca się użycie budzika z oceną wyższą niż 0.6")
                        .setNegativeButton("ok",null)
                        .create()
                        .show();
            }
        });
        seekbarr();
        spinner_alarm();

    }
    public void spinner_alarm(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ringtone);
        final Spinner spinner = (Spinner)findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
    }

    public void seekbarr(){
        AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int maxvalue = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        int value = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        seek_bar = (SeekBar)findViewById(R.id.seekBar2);
        seek_bar.setProgress((value*100)/maxvalue);
        text_volume = (TextView)findViewById(R.id.textVolume);
        text_volume.setText("Głośność : "+seek_bar.getProgress()+" %");

        seek_bar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int progres_value;
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progres_value = progress;
                        text_volume.setText("Głośność : "+progres_value+" %");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        text_volume.setText("Głośność : "+progres_value+" %");
                    }
                }
        );
    }

    public void OnToggleClicked(View view)
    {
        Log.e("media", "Click vibre: "+vibr);
        long time;
        Intent intent = new Intent(this, Alarm_Reciver.class);
        if (((ToggleButton) view).isChecked())
        {
            String volume_value = String.valueOf(seek_bar.getProgress());

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
            calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
            int hour = alarmTimePicker.getCurrentHour();
            int minute = alarmTimePicker.getCurrentMinute();
            time = GetTime(false);
            // time to alarm
            String timeToAlarm = String.valueOf(time-System.currentTimeMillis());
            Log.e("time","Time to alarm: "+timeToAlarm+" ms");

            // song_id
            String sound_id = String.valueOf(spinner.getSelectedItemPosition());

            // Intent Extra
            String[] nextra = new String[]{"on",vibr,volume_value,timeToAlarm,sound_id};
            Log.e("media",nextra[0]+" vibr: "+nextra[1] + " Seek bar: "+nextra[2]+" Time to alarm: "+nextra[3]
            +" Alarm sound: "+sound_id);
            intent.putExtra("extra",nextra);

            pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Toast.makeText(ClockActivity.this, "ALARM ON: "+hour+":"+minute, Toast.LENGTH_SHORT).show();
            alarmManager.set(AlarmManager.RTC_WAKEUP, time,pendingIntent);
        }
        else
        {
            intent.putExtra("extra",new String[] {"off","off","0","0"});
            alarmManager.cancel(pendingIntent);
            sendBroadcast(intent);
            Toast.makeText(ClockActivity.this, "ALARM OFF", Toast.LENGTH_SHORT).show();
        }
    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.check_vibr:
                if (checked){
                    vibr = "on";
                }
            else{
                    vibr = "off";
                }
                break;
        }
        Log.e("media", "Checkbox: "+ vibr);
    }

    public void endAlarm(){
        Intent intent = new Intent(this, Alarm_Reciver.class);
        intent.putExtra("extra",new String[] {"off","off"});
        alarmManager.cancel(pendingIntent);
        sendBroadcast(intent);
    }

    private float[] predict(float[] input){
        // model has only 1 output neuron
        float output[] = new float[1];

        // feed network with input of shape (1,input.length) = (1,2)
        inferenceInterface.feed("dense_1_input", input, 1, input.length);
        inferenceInterface.run(new String[]{"dense_2/BiasAdd"});
        inferenceInterface.fetch("dense_2/BiasAdd", output);
        return output;
    }

    private long GetTime(boolean current){
        long time;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
        calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
        time=calendar.getTimeInMillis();
        time=(calendar.getTimeInMillis()-(calendar.getTimeInMillis()%60000));
        if(System.currentTimeMillis()>time)
        {
            if (calendar.AM_PM == 0)
                time = time + (1000*60*60*12);
            else
                time = time + (1000*60*60*24);
        }
        if(current){
            time = time-System.currentTimeMillis();
        }
        return time;
    }
}
