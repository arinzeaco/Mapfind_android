package obi.mapfind.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import obi.mapfind.BaseActivity;
import obi.mapfind.Constant;
import obi.mapfind.MainActivity;
import obi.mapfind.R;
import obi.mapfind.details.Login;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Fragment_find extends Fragment implements OnMapReadyCallback,GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks{
    private GoogleMap mMap; SupportMapFragment mapFragment;
    private GoogleApiClient mGoogleApiClient;
    BaseActivity base;
    LinearLayout coordinatorLayout;
    GoogleMap googleMap;
    private static final String TAG = "MainActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        base= new BaseActivity();
        final LinearLayout v = (LinearLayout) inflater.inflate(R.layout.fragment_find, container, false);
        coordinatorLayout= v.findViewById(R.id.layouts);
       mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
if(mapFragment==null){
    FragmentManager fm = getFragmentManager();
    FragmentTransaction ft = fm.beginTransaction();
    mapFragment= SupportMapFragment.newInstance();
    ft.replace(R.id.map, mapFragment).commit();
    onMapReady(googleMap);
}
        mapFragment.getMapAsync(this);

//        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
//                .addApi(Places.GEO_DATA_API)
//                .enableAutoManage(getActivity(), GOOGLE_API_CLIENT_ID, this)
//                .addConnectionCallbacks(this)
//                .build();


        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       // all_map();

        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Menu 1");
    }


    @Override
    public void onResume(){
        super.onResume();
        onMapReady(googleMap);
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void all_map() {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("profession", "any")
                .add("distance","5")
                .add("longitude","80.5")
                .add("latitude","40.5")
                .build();
        Request request = new Request.Builder().url(Constant.ipadress+"all_users.php").post(body).build();
        // Request request = new Request.Builder().url("http://10.0.2.2/better/charticon.php").post(body).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(),
                                "failed",
                                Toast.LENGTH_SHORT).show();
                     //   base.ifconnection(coordinatorLayout,"Map did not load try again later");

                    }}
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String myResponse = response.body().string();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        JSONObject jso;
                        try {
                            jso = new JSONObject(myResponse);


                            if (jso.getString("status").contentEquals("1")) {
                                JSONArray details = jso.getJSONArray("data");
                                for (int i = 0; i < details.length(); i++) {

                                    // String brief = details.getString("brief");
                                    String profession = details.getJSONObject(i).getString("profession");
//                                String email = details.getString("email");
//                                String interest = details.getString("interest");
                                    String name =details.getJSONObject(i).getString("name");
//                                String avatar = details.getString("avatar");
//                                String phone = details.getString("phone");
                                    //        String address = details.getString("address");
                                   Double longitude =details.getJSONObject(i).getDouble("longitude");
                                    Double latitude =details.getJSONObject(i).getDouble("latitude");

                                        LatLng sydney = new LatLng(latitude,longitude);
                                        mMap.addMarker(new MarkerOptions().position(sydney).title("name"));
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 4));

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
                            }else{
                                base.ifconnection(coordinatorLayout,"Failed to find try again later");
                                return;

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }}
                );
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMaxZoomPreference(9);
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("profession", base.base_profession_set())
                .add("distance", base.base_meter())
                .add("longitude",base.base_latitude())
                .add("latitude",base.base_latitude())
                .build();
        Request request = new Request.Builder().url(Constant.ipadress+"all_users.php").post(body).build();
        // Request request = new Request.Builder().url("http://10.0.2.2/better/charticon.php").post(body).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        base.ifconnection(coordinatorLayout,"Map did not load try again later");

                    }}
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String myResponse = response.body().string();

                getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                JSONObject jso;
                                                try {
                                                    jso = new JSONObject(myResponse);


                                                    if (jso.getString("status").contentEquals("1")) {
                                                        JSONArray details = jso.getJSONArray("data");
                                                        for (int i = 0; i < details.length(); i++) {

                                                            // String brief = details.getString("brief");
                                                            String profession = details.getJSONObject(i).getString("profession");
//                                String email = details.getString("email");
//                                String interest = details.getString("interest");
                                                            String name =details.getJSONObject(i).getString("name");
//                                String avatar = details.getString("avatar");
//                                String phone = details.getString("phone");
                                                            //        String address = details.getString("address");
                                                            Double longitude =details.getJSONObject(i).getDouble("longitude");
                                                            Double latitude =details.getJSONObject(i).getDouble("latitude");




                                                           base. ifconnection(coordinatorLayout,"Worked well");
                                                            // Add a marker in Sydney and move the camera
                                                            LatLng sydney = new LatLng(latitude,longitude);
                                                            mMap.addMarker(new MarkerOptions().position(sydney).title("Africa,Nigeria"));
                                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,4));

                                                        }
                                                    }else{
                                                        base.ifconnection(coordinatorLayout,"Failed to find try again later");
                                                        return;

                                                    }

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            }}
                );
            }
        });
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
}