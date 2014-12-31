package bulldogs.luistrejo.com.radiobulldogs;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.nfc.Tag;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

import java.io.IOException;

public class GamesFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "ServicesDemo";
    private MediaPlayer player;
    private SeekBar volumeSeekbar = null;
    private AudioManager audioManager = null;
    Button buttonStart, buttonStop;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_games, container, false);
        initControls();
        volumeSeekbar = (SeekBar)rootView.findViewById(R.id.seekBar1);
        audioManager = (AudioManager)this.getActivity().getSystemService(Context.AUDIO_SERVICE);
        buttonStart = (Button) rootView.findViewById(R.id.startPlaying);
        buttonStop = (Button) rootView.findViewById(R.id.buttonStopPlay);
        buttonStart.setOnClickListener(this);
        buttonStop.setOnClickListener(this);
        return rootView;



    }
@Override

    public void onClick(View v){
        switch (v.getId()){
            case R.id.startPlaying:
                Log.d(TAG, "onClick: Starting service");
                getActivity().startService(new Intent(getActivity(), Servicio.class));
                break;
            case R.id.buttonStopPlay:
                Log.d(TAG, "onClick: Deteniendo servicio");
                getActivity().stopService(new Intent(getActivity(), Servicio.class));
                break;
        }
    if (v == buttonStart) {
        buttonStop.setEnabled(true);
        buttonStart.setEnabled(false);
    } else if (v == buttonStop) {
        buttonStop.setEnabled(false);
        buttonStart.setEnabled(true);
    }
    }




    // control de volumen con el seckbar
    private void initControls()
    {
        try
        {
            volumeSeekbar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekbar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
            {
                @Override
                public void onStopTrackingTouch(SeekBar arg0)
                {
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0)
                {
                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2)
                {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress, 0);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

