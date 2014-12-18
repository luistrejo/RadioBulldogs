package bulldogs.luistrejo.com.radiobulldogs;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.util.ArrayList;


public class TopRatedFragment extends Fragment{

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
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
        return rootView;
    }
    public void cargaListado (ArrayList<String> datos){
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,datos);
        ListView listado = (ListView)this.getActivity().findViewById(R.id.listview1);
        listado.setAdapter(adaptador);
        }
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
}




