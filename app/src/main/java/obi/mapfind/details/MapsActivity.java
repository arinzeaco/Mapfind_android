package obi.mapfind.details;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import obi.mapfind.Utils.BaseActivity;
import obi.mapfind.R;
import obi.mapfind.Utils.PlaceArrayAdapter;


public class MapsActivity extends BaseActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks{

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private AutoCompleteTextView mAutocompleteTextView;
    private static final String TAG = "MainActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    LinearLayout coordinatorLayout;
    double lat, lon;

    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    ImageButton clear;  String location;     TextView  right_text;

    SharedPreferences sp;
    SharedPreferences.Editor edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        sp = PreferenceManager
                .getDefaultSharedPreferences(MapsActivity.this);
        edit = sp.edit();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
       initToolbar("Change location","Save");
        mapFragment.getMapAsync(this);
        clear=  findViewById(R.id.clear);
        if(!isOnline(MapsActivity.this)){
            ifconnection(coordinatorLayout,"No internet connection");
            return;
        }
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAutocompleteTextView.setText("");
            }
        });
        mAutocompleteTextView = findViewById(R.id.autoCompleteTextView);
        mAutocompleteTextView.setText(base_address());
        mAutocompleteTextView.setThreshold(3);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();

        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);
        coordinatorLayout = findViewById(R.id
                .layouts);
        right_text= findViewById(R.id.right_text);
        right_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isOnline(MapsActivity.this)){
                    ifconnection(coordinatorLayout,"No internet connection");
                    return;
                }

                Log.i("iiiiiiiiiii", String.valueOf(lon)+"   nh "+String.valueOf(lat));
                Bundle b = new Bundle();
                b.putString("address", mAutocompleteTextView.getText().toString());
                b.putString("longitude", String.valueOf(lon));
                b.putString("latitude", String.valueOf(lat));
                Intent uo = new Intent(getApplicationContext(), Profile.class);
                uo.putExtras(b);
                startActivity(uo);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });
    }
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(TAG, "Fetching details for ID: " + item.placeId);

            Log.i("one ", String.valueOf(item.placeId));

        }
    };
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();


           onMapSearch(mAutocompleteTextView.getText().toString());

        }
    };
    public void onMapSearch(String location) {
        mMap.clear();
        List<Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            mMap.setMaxZoomPreference(10);
           // LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
        //    Log.i("two ", String.valueOf(latLng));
           // mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
            //mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

            // Add a marker in Sydney and move the camera
          lat = address.getLatitude();
           lon = address.getLongitude();
            LatLng sydney = new LatLng(lat, lon);
            edit.putString("address", mAutocompleteTextView.getText().toString());
            edit.putString("longitude", String.valueOf(lon));
            edit.putString("latitude", String.valueOf(lat));
            edit.apply();
            mMap.addMarker(new MarkerOptions().position(sydney).title("aut"));

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,4));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(TAG, "Google Places API connected.");

    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(TAG, "Google Places API connection suspended.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.e(TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());
        ifconnection(coordinatorLayout,"Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode());

    }



    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        List<Address> addressList = null;

        if(!mAutocompleteTextView.getText().toString().isEmpty()){
           location=base_address();
        }else{
            location="Nigeria";
        }

            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }

            Address address = addressList.get(0);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(address.getLatitude(),  address.getLongitude());
        mMap.addMarker(new MarkerOptions().position(sydney).title("Africa,Nigeria"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,4));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }
}