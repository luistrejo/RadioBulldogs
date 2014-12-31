package bulldogs.luistrejo.com.radiobulldogs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class Registro extends ActionBarActivity {

    private EditText nombre;
    private EditText paterno;
    private EditText materno;
    private EditText usuario;
    private EditText contrasena;
    private Spinner especialidad;
    private Button insertar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        final Vibrator vibrator =(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        nombre=(EditText)findViewById(R.id.etnombre);
        paterno=(EditText)findViewById(R.id.etpaterno);
        materno=(EditText)findViewById(R.id.etmaterno);
        usuario=(EditText)findViewById(R.id.etusuario);
        contrasena=(EditText)findViewById(R.id.etcontraseña);
        especialidad=(Spinner)findViewById(R.id.spinEspecialidad);
        insertar=(Button)findViewById(R.id.btregistrar);
        insertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!nombre.getText().toString().trim().equalsIgnoreCase("") ||
                        !paterno.getText().toString().trim().equalsIgnoreCase("")||
                        !materno.getText().toString().trim().equalsIgnoreCase("")||
                        !usuario.getText().toString().trim().equalsIgnoreCase("")||
                        !contrasena.getText().toString().trim().equalsIgnoreCase("")||
                        !especialidad.getSelectedItem().toString().trim().equalsIgnoreCase(""))
                    new Insertar(Registro.this).execute();
                else{
                vibrator.vibrate(200);
                Toast.makeText(Registro.this, "Hay informacion por llenar.", Toast.LENGTH_LONG).show();}
            }
        });

        //Composicion del array con las especialidades que estan en arrays.xml
        Spinner sp = (Spinner) findViewById(R.id.spinEspecialidad);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this, R.array.especialidades, android.R.layout.simple_spinner_item);
        sp.setAdapter(adapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //envio de datos al servidor
    private boolean insertar(){
        HttpClient httpclient;
        List<NameValuePair> nameValuePairs;
        HttpPost httppost;
        httpclient = new DefaultHttpClient();
        httppost=new HttpPost("http://192.168.0.109/RadioB/nuevousuario.php");
        //Añadimos los datos
        nameValuePairs = new ArrayList<NameValuePair>(6);
        nameValuePairs.add(new BasicNameValuePair("nombre",nombre.getText().toString().trim()));
        nameValuePairs.add(new BasicNameValuePair("paterno",paterno.getText().toString().trim()));
        nameValuePairs.add(new BasicNameValuePair("materno",materno.getText().toString().trim()));
        nameValuePairs.add(new BasicNameValuePair("usuario",usuario.getText().toString().trim()));
        nameValuePairs.add(new BasicNameValuePair("contrasena",contrasena.getText().toString().trim()));
        nameValuePairs.add(new BasicNameValuePair("especialidad",especialidad.getSelectedItem().toString().trim()));

        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httpclient.execute(httppost);
            return true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    class Insertar extends AsyncTask<String,String,String> {
        private Activity context;

        Insertar(Activity context){
            this.context=context;
        }
        @Override
        protected String doInBackground(String... params){
            if (insertar())
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Registro exitoso.", Toast.LENGTH_LONG).show();
                        nombre.setText("");
                        paterno.setText("");
                        materno.setText("");
                        usuario.setText("");
                        contrasena.setText("");
                        Intent i=new Intent(Registro.this, Login.class);
                        startActivity(i);
                    }
                });
            else
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Algo ha salido mal :(, intentalo de nuevo.", Toast.LENGTH_LONG).show();
                    }
                });
            return null;

        }
    }


}
