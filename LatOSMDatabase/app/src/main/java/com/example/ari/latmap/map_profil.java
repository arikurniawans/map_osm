package com.example.ari.latmap;

import android.app.ProgressDialog;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class map_profil extends AppCompatActivity {
    private String JSON_STRING;
    public final String cari_aja="";
    MapView map;
    EditText txt_cari;
    Button btn_cari;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_profil);
        map = (MapView) findViewById(R.id.map);
        txt_cari = (EditText) findViewById(R.id.txt_lokasi);
        btn_cari = (Button) findViewById(R.id.btn_cari);

        btn_cari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getJSON();
            }
        });
    }


    private void showMap(){
        JSONObject jsonObject = null;
        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(Koneksi.TAG_JSON_ARRAY);

            for(int i = 0; i<result.length(); i++){
                JSONObject jo = result.getJSONObject(i);
                //String id = jo.getString(Koneksi.TAG_ID);
                String name = jo.getString(Koneksi.TAG_NAMA);
                String latt = jo.getString(Koneksi.TAG_LAT);
                String longg = jo.getString(Koneksi.TAG_LONGI);

                if(latt != null &&  longg != null){
                    Double lt = Double.parseDouble(latt);
                    Double lgs = Double.parseDouble(longg);

                    HashMap<String,String> employees = new HashMap<>();
                    //employees.put(Koneksi.TAG_ID,id);
                    employees.put(Koneksi.TAG_NAMA,name);
                    employees.put(Koneksi.TAG_LAT, latt);
                    employees.put(Koneksi.TAG_LONGI, longg);

                    list.add(employees);

                    tampilPeta(lt, lgs, name);
                }else{
                    Toast.makeText(getApplication(), "Data Tidak Ditemukan",Toast.LENGTH_LONG).show();
                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }



        /*ListAdapter adapter = new SimpleAdapter(
                ViewAllEmployee.this, list, R.layout.list_item,
                new String[]{Config.TAG_ID,Config.TAG_NAME},
                new int[]{R.id.id, R.id.name});

        listView.setAdapter(adapter);*/
    }

    private void getJSON(){
        class GetJSON extends AsyncTask<Void,Void,String>{
            final String cari_aja = txt_cari.getText().toString().trim();

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(map_profil.this,"Menampilkan Data","Tunggu Sebentar...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                showMap();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(Koneksi.URL_GET_OSM, cari_aja);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    void tampilPeta(Double lats, Double longs, String marks){
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        GeoPoint startPoint = new GeoPoint(lats, longs);
        IMapController mapController = map.getController();
        mapController.setZoom(12);
        mapController.setCenter(startPoint);

        Marker startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setTitle(marks);
        startMarker.setIcon(getResources().getDrawable(R.drawable.person));
        map.getOverlays().add(startMarker);
    }
}
