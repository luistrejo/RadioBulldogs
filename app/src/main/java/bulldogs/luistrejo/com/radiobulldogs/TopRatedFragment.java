package bulldogs.luistrejo.com.radiobulldogs;



import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bulldogs.luistrejo.com.radiobulldogs.Listviewcomentarios.JSONfunctions;
import bulldogs.luistrejo.com.radiobulldogs.Listviewcomentarios.ListViewAdapter;


public class TopRatedFragment extends Fragment {

    private EditText mensaje;
    private ImageButton enviar;
    private ProgressDialog pDialog;



    // Declaramos variables para listview
    JSONObject jsonobject;
    JSONArray jsonarray;
    ListView listview;
    ListViewAdapter adapter;
    ProgressDialog mProgressDialog;
    ArrayList<HashMap<String, String>> arraylist;
    public static String usuario = "usuario";
    public static String comentario = "comentario";
    Handler mHandler = new Handler();
    String TAG = "Comentarios";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_top_rated, container, false);
        //corremos metodo para que carge listview

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new DownloadJSON().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new DownloadJSON().execute();
        }
        mensaje=(EditText)rootView.findViewById(R.id.etMensaje);
        enviar=(ImageButton)rootView.findViewById(R.id.imbEnviar);
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if (!mensaje.getText().toString().trim().equalsIgnoreCase(""))
                    new Enviar(TopRatedFragment.this.getActivity()).execute();
                else
                    Toast.makeText(TopRatedFragment.this.getActivity(), "Ups! Tal parece que no has escrito nada.",Toast.LENGTH_LONG).show();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (true) {
                    try {
                        Thread.sleep(10000);
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                    new DownloadJSON().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                } else {
                                    new DownloadJSON().execute();
                                }                            }
                        });
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }).start();


        return rootView;
    }

    //enviamos el mensaje escrito al servidor
    private boolean enviar(){
        HttpClient httpclient;
        List<NameValuePair> nameValuePairs;
        HttpPost httppost;
        httpclient = new DefaultHttpClient();
        httppost = new HttpPost("http://192.168.0.109/RadioB/insertarcomentario.php");

        //Consultamos valor usuario del shared preferences
        SharedPreferences settings = getActivity().getSharedPreferences("usuario",Context.MODE_PRIVATE);
        String usuario = settings.getString("usuario", "?");

        //Añadimos los datos que vamos a enviar
        nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("usuario", usuario.toString().trim()));
        nameValuePairs.add(new BasicNameValuePair("comentario", mensaje.getText().toString().trim()));

        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httpclient.execute(httppost);
            
            return true;
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }catch (ClientProtocolException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }



    private class Enviar extends AsyncTask<String,String,String>{
    private Activity context;

    Enviar(Activity context) { this.context=context; }

        protected void onPreExecute() {
            //para el progress dialog
            pDialog = new ProgressDialog(TopRatedFragment.this.getActivity());
            pDialog.setMessage("Enviando mensaje....");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

    protected String doInBackground(String... params){
        if (enviar())
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Mensaje enviado.", Toast.LENGTH_SHORT).show();
                    mensaje.setText("");
                    //Actualizamos la lista de comentarios cada vez que se envie un mensaje
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        new DownloadJSON().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        new DownloadJSON().execute();
                    }
                }

        });
        else
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Mensaje no enviado, intenta de nuevo en unos segundos.", Toast.LENGTH_LONG).show();
                }
            });
        return null;

    }
        protected void onPostExecute(String result) {

            pDialog.dismiss();//ocultamos progess dialog.


        }

    }

    // Clase que carga el listview con los datos del servidor
    private class DownloadJSON extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            //mProgressDialog = new ProgressDialog(TopRatedFragment.this.getActivity());
            // Set progressdialog message
           // mProgressDialog.setMessage("Cargando comentarios...");
            //mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            //mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create an array
            arraylist = new ArrayList<HashMap<String, String>>();
            // Retrieve JSON Objects from the given URL address
            jsonobject = JSONfunctions.getJSONfromURL("http://192.168.0.109/RadioB/GetData.php");

            try {
                // Locate the array name in JSON
                jsonarray = jsonobject.getJSONArray("comentarios");

                for (int i = 0; i < jsonarray.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    jsonobject = jsonarray.getJSONObject(i);
                    // Retrive JSON Objects
                    map.put("usuario", jsonobject.getString("usuario"));
                    map.put("comentario", jsonobject.getString("comentario"));
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
           try {
               listview = (ListView)getActivity().findViewById(R.id.listview);
               // Pass the results into ListViewAdapter.java
               adapter = new ListViewAdapter(TopRatedFragment.this.getActivity(), arraylist);
               // Set the adapter to the ListView
               listview.setAdapter(adapter);
           }catch (Exception e ) {
               Log.d(TAG, "Error al cargar lista de comentarios");
           }

            // Close the progressdialog
           // mProgressDialog.dismiss();
        }
    }

}




