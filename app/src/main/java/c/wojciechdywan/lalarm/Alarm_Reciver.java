package c.wojciechdywan.lalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class Alarm_Reciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
            String[] my_extra = intent.getExtras().getStringArray("extra");
            Intent service_intent = new Intent(context,RingtonePlayingService.class);
            service_intent.putExtra("extra",my_extra);
            context.startService(service_intent);
    }
}
