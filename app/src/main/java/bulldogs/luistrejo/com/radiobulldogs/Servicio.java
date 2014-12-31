package bulldogs.luistrejo.com.radiobulldogs;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Luis Trejo on 30/12/2014.
 */
public class Servicio extends Service{
    private static final String TAG = "MyService";
    MediaPlayer player;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Servicio creado", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onCreate");
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            player.setDataSource("http://192.168.0.109:8000");
            player.prepare();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
     public int onStartCommand (Intent intent,int flags, int startid){
        Toast.makeText(this, "Servicio Iniciado",Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStart");
        player.start();


            NotificationManager notificationManager = (NotificationManager)
                    getSystemService(NOTIFICATION_SERVICE);
        Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,intent1, 0);
        Notification noti = new Notification.Builder(this)
                .setContentTitle("Radio Bulldogs")
                .setContentText("La estacion de radio mas perra!")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();



        startForeground(1337, noti);



        return START_NOT_STICKY;


    }
    @Override
    public void onDestroy() {
        Toast.makeText(this, "Servicio detenido",Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
        player.stop();
        stopForeground(true);
    }

}
