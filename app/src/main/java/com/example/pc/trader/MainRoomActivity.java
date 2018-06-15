package com.example.pc.trader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainRoomActivity extends AppCompatActivity{

    //private TextView tvEmail;

    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;
    TextView nome;
    TextView genero;
    TextView localizacao;
    TextView nickname;
    ImageView foto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_room);
        //tvEmail = findViewById(R.id.tvEmailProfile);
        //tvEmail.setText(getIntent().getExtras().getString("Email"));
        dl = (DrawerLayout)findViewById(R.id.dl);
        abdt = new ActionBarDrawerToggle(this,dl,R.string.Aberto,R.string.Fechado);
        abdt.setDrawerIndicatorEnabled(true);

        dl.addDrawerListener(abdt);
        abdt.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView nav_view = (NavigationView)findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if(id == R.id.myprofile){
                    Toast.makeText(MainRoomActivity.this,"Meu perfil",Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.settings){
                    Toast.makeText(MainRoomActivity.this,"Configurações",Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.editprofile){
                    Toast.makeText(MainRoomActivity.this,"Editar perfil",Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });

          nome = (TextView) findViewById(R.id.textView6);
          genero = (TextView) findViewById(R.id.textView7);
          localizacao = (TextView) findViewById(R.id.textView8);
          nickname = (TextView) findViewById(R.id.textView9);
          foto = findViewById(R.id.imageView3);



    }



    @Override
    protected void onStart() {
        super.onStart();


        try {

            new DownloadDados().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void btnPass_Click(View v){
        try {

            new DownloadDados().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return
                abdt.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    public class DownloadDados extends AsyncTask<Void, Void, String> {
        private ProgressDialog load;
        @Override
        protected String doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            StringBuffer buffer = new StringBuffer();
            try {
                URL url = new URL("https://randomuser.me/api/0.7");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(15000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.connect();

                InputStream inputStream;

                int codigoResposta = urlConnection.getResponseCode();
                if(codigoResposta < HttpURLConnection.HTTP_BAD_REQUEST){
                    inputStream = urlConnection.getInputStream();
                }else{
                    inputStream = urlConnection.getErrorStream();
                }



                reader = new BufferedReader(new InputStreamReader(inputStream));
                String linha = "";
                while((linha = reader.readLine()) != null) {
                    buffer.append(linha);

                }
                reader.close();


            } catch (Exception e) {
                e.printStackTrace();
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            try {
                JSONObject jsonObj = new JSONObject(buffer.toString());
                JSONArray array = jsonObj.getJSONArray("results");
                JSONObject objArray = array.getJSONObject(0);
                JSONObject obj = objArray.getJSONObject("user");
                JSONObject objPic = obj.getJSONObject("picture");

                URL url2 = new URL(objPic.getString("large"));

                HttpURLConnection conexao = (HttpURLConnection)
                        url2.openConnection();
                conexao.setRequestMethod("GET");
                urlConnection.setReadTimeout(30000);
                urlConnection.setConnectTimeout(30000);
                conexao.setDoInput(true);
                conexao.connect();

                InputStream is = conexao.getInputStream();
                Bitmap imagem = BitmapFactory.decodeStream(is);
                foto.setImageBitmap(imagem);

            } catch (Exception e){
                e.printStackTrace();
            }

            return buffer.toString();
        }

        @Override
        protected void onPostExecute(String dados) {


            try {
                JSONObject jsonObj = new JSONObject(dados);
                JSONArray array = jsonObj.getJSONArray("results");
                JSONObject objArray = array.getJSONObject(0);
                JSONObject obj = objArray.getJSONObject("user");
                JSONObject objName = obj.getJSONObject("name");
                JSONObject objLoc = obj.getJSONObject("location");


                genero.setText(obj.getString("gender"));
                String nomeComp = objName.getString("title")+" "+objName.getString("first")+" "+objName.getString("last");
                nome.setText(nomeComp);
                localizacao.setText(objLoc.getString("city"));
                nickname.setText(obj.getString("username"));


            } catch (JSONException ex) {
                ex.printStackTrace();
            }



            load.dismiss();
        }
        @Override
        protected void onPreExecute(){
            load = ProgressDialog.show(MainRoomActivity.this, "Por favor Aguarde ...", "Recuperando Informações do Servidor...");
        }
    }
}