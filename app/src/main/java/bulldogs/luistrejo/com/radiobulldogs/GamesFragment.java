package bulldogs.luistrejo.com.radiobulldogs;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import bulldogs.luistrejo.com.radiobulldogs.cancion.JSONfunctions;
import bulldogs.luistrejo.com.radiobulldogs.cancion.ListViewAdapter;


public class GamesFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "ServicesDemo";
    private SeekBar volumeSeekbar = null;
    private AudioManager audioManager = null;
    Button buttonStart, buttonStop;
    // Declaramos variables para listview
    JSONObject jsonobject;
    JSONArray jsonarray;
    ListView listview1;
    ListViewAdapter adapter;
    //ProgressDialog mProgressDialog;
    ArrayList<HashMap<String, String>> arraylist;
    public static String songtitle = "songtitle";
    public static String nexttitle = "nexttitle";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_games, container, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new JSONCanciones().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new JSONCanciones().execute();
         }

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
    private class JSONCanciones extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            //mProgressDialog = new ProgressDialog(TopRatedFragment.this.getActivity());
            // Set progressdialog message
            //mProgressDialog.setMessage("Cargando comentarios...");
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            //mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create an array
            arraylist = new ArrayList<HashMap<String, String>>();
            // Retrieve JSON Objects from the given URL address
            jsonobject = JSONfunctions.getJSONfromURL("http://192.168.0.109/RadioB/pag.php");

            try {
                // Locate the array name in JSON
                jsonarray = jsonobject.getJSONArray("streams");

                for (int i = 0; 0 < jsonarray.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    jsonobject = jsonarray.getJSONObject(i);
                    // Retrive JSON Objects
                    map.put("songtitle", jsonobject.getString("songtitle"));
                    map.put("nexttitle", jsonobject.getString("nexttitle"));
                    // Set the JSON Objects into the array
                    arraylist.add(map);
                }
            } catch (JSONException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            // Locate the listview in listview_main.xml
            listview1 = (ListView)getActivity().findViewById(R.id.listView1);
            // Pass the results into ListViewAdapter.java
            adapter = new ListViewAdapter(GamesFragment.this.getActivity(), arraylist);
            // Set the adapter to the ListView
            listview1.setAdapter(adapter);
            // Close the progressdialog
            //mProgressDialog.dismiss();
        }
    }

}

