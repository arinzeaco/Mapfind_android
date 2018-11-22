package obi.mapfind.home;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import obi.mapfind.BaseActivity;
import obi.mapfind.Constant;
import obi.mapfind.Other_Profile;
import obi.mapfind.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Find extends BaseActivity implements
        OnMapReadyCallback,GoogleMap.OnInfoWindowClickListener,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks{

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "MainActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    LinearLayout coordinatorLayout;
    double lat, lon;



    SharedPreferences sp;
    SharedPreferences.Editor edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_find);
        sp = PreferenceManager
                .getDefaultSharedPreferences(Find.this);
        edit = sp.edit();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();

        coordinatorLayout = findViewById(R.id
                .layouts);
//        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                LatLng position = marker.getPosition();
//
//                Toast.makeText(
//                        Find.this,
//                        "Lat " + marker.getId() + " "
//                                + "Long " + position.longitude,
//                        Toast.LENGTH_LONG).show();
//                return true;
//            }
//        });

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {


        Log.i(TAG, "Google Places API connected.");

    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.e(TAG, "Google Places API connection suspended.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.e(TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();

    }



    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMaxZoomPreference(9);
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("profession", "any")
                .add("distance","5")
                .add("longitude","80.5")
                .add("latitude","40.5")
                .build();

        Request request = new Request.Builder().url(Constant.ipadress+"all_users.php").post(body).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Find.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                     ifconnection(coordinatorLayout,"Map did not load try again later");

                    }}
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String myResponse = response.body().string();

                Find.this.runOnUiThread(new Runnable() {
                    public MarkerOptions Mapss;

                    @Override
                                                public void run() {

                                                    JSONObject jso;
                                                    try {
                                                        jso = new JSONObject(myResponse);


                                                        if (jso.getString("status").contentEquals("1")) {
                                                            JSONArray details = jso.getJSONArray("data");
                                                            for (int i = 0; i < details.length(); i++) {

                                                               String userid = details.getJSONObject(i).getString("u_id");
                                                                String profession = details.getJSONObject(i).getString("profession");
//                                String email = details.getString("email");
//                                String interest = details.getString("interest");
                                                                String name =details.getJSONObject(i).getString("name");
                                                                String avatar = details.getJSONObject(i).getString("avatar");
                                                                String phone = details.getJSONObject(i).getString("phone");
                                                                String address = details.getJSONObject(i).getString("address");
                                                                Double longitude =details.getJSONObject(i).getDouble("longitude");
                                                                Double latitude =details.getJSONObject(i).getDouble("latitude");

                                                                ifconnection(coordinatorLayout,"Worked well");
                                                                // Add a marker in Sydney and move the camera
                                                                LatLng sydney = new LatLng(latitude,longitude);
                                                                MarkerOptions markerOptions = new MarkerOptions();
                                                                markerOptions.position(sydney)
                                                                        .title(name)
                                                                        .snippet("Address:"+address)
                                                                        .icon(BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_BLUE));

                                                                InfoWindowData info = new InfoWindowData();
                                                               // info.setImage("snowqualmie");
                                                                info.setPhone("Phone:"+ phone);
                                                                info.setImage(avatar);
                                                                info.setUserid(userid);

                                                                CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(Find.this);
                                                                mMap.setInfoWindowAdapter(customInfoWindow);

                                                                Marker m = mMap.addMarker(markerOptions);
                                                                m.setTag(info);
                                                                m.showInfoWindow();
                                                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,4));
                                                            }
                                                        }else{
                                                           ifconnection(coordinatorLayout,"Failed to find try again later");
                                                            return;

                                                        }

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                }}
                );
            }
        });
        if (ActivityCompat.checkSelfPermission(Find.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(Find.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
//                                    edit.putString("loggedin", "yes");
//                                    edit.putString("u_id", user.getUid());
//                                    edit.putString("email", email);
//                                    edit.putString("name", name);
//                                    edit.putString("profession", profession);
//                                edit.putString("interest", interest);
//                                edit.putString("brief", brief);
//                                edit.putString("avatar",avatar);
//                                edit.putString("phone", phone);
//                                edit.putString("address",address);
//                                edit.putString("longitude",longitude);
//                                edit.putString("latitude", latitude);
//                                edit.apply();
//
//                                Intent uo = new Intent(getApplicationContext(), MainActivity.class);
//                                finish();
//                                startActivity(uo);
//                                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(Find.this,"fddf",Toast.LENGTH_SHORT).show();
        Bundle b = new Bundle();
      //  b.putString("userid", infoWindowData.getUserid());
        Intent in = new Intent(Find.this, Other_Profile.class);
        in.putExtras(b);
        startActivity(in);
    }
}
