package bulldogs.luistrejo.com.radiobulldogs;



import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.AccessControlContext;
import java.util.ArrayList;
import java.util.List;


public class TopRatedFragment extends Fragment {
    private EditText mensaje;
    private ImageButton enviar;
    //variable de usuario, en ella se almacenara el nombre de usuario
    //de la persona para saber de quien es el comentario, mientras se implementa
    //el login y registro usar esta variable, remover despues.
    private String usuario;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);



        //carga de listview con los datos al iniciar
        Thread tr = new Thread(){
            @Override
        public void run(){
                final String Resultado = leer();
                getActivity().runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                cargaListado(obtDatosJSON(Resultado));
                            }
                        });
            }
        };
        tr.start();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_top_rated, container, false);

        //remover esta variable cuando implementes login
        usuario="luistrejo";
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





        return rootView;


    }
    //cargamos la lista
    public void cargaListado (ArrayList<String> datos){
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,datos);
        ListView listado = (ListView)this.getActivity().findViewById(R.id.listview1);
        listado.setAdapter(adaptador);
        }
    //leemos los datos que nos contesta el servidor
    public String leer(){
        HttpClient cliente = new DefaultHttpClient();
        HttpContext contexto = new BasicHttpContext();
        HttpGet httpget = new HttpGet("http://192.168.0.109/listview/GetData.php");
        String resultado=null;
        try {
            HttpResponse response = cliente.execute(httpget, contexto);
            HttpEntity entity = response.getEntity();
            resultado = EntityUtils.toString(entity, "UTF-8");

        }catch (Exception e){
            //TODO: handle exception
        }
        return resultado;
    }
    public ArrayList<String> obtDatosJSON(String response){
        ArrayList<String> listado = new ArrayList<String>();
        try {
            JSONArray json = new JSONArray(response);
            String texto="";
            for (int i=0; i<json.length();i++){
                texto = json.getJSONObject(i).getString("usuario") +" - "+
                        json.getJSONObject(i).getString("comentario");
                listado.add(texto);
            }
        }catch (Exception e){
            //TODO: handle exception
        }
        return listado;
    }

    //enviamos el mensaje escrito al servidor
    private boolean enviar(){
        HttpClient httpclient;
        List<NameValuePair> nameValuePairs;
        HttpPost httppost;
        httpclient = new DefaultHttpClient();
        httppost = new HttpPost("http://192.168.0.109/RadioB/insertarcomentario.php");
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
    @Override
    protected String doInBackground(String... params){
        if (enviar())
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Mensaje enviado.", Toast.LENGTH_SHORT).show();
                    mensaje.setText("");
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

}
}




