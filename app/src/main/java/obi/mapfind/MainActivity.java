package obi.mapfind;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import obi.mapfind.Utils.BaseActivity;
import obi.mapfind.Utils.CircleTransform;
import obi.mapfind.Utils.Constant;
import obi.mapfind.details.Login;
import obi.mapfind.details.Profile;
import obi.mapfind.home.CustomInfoWindowGoogleMap;
import obi.mapfind.home.InfoWindowData;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {
    ImageView propic; TextView name;
    //String pro_name,url;
    LinearLayout head;
    private View navHeader;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "MainActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    DrawerLayout coordinatorLayout;
    private GoogleSignInClient mGoogleSignInClient;
    SupportMapFragment mapFragment;
    Intent in;   DrawerLayout drawer;

ImageButton menu;
    SharedPreferences sp;
    SharedPreferences.Editor edit;
    ProgressBar  mprogressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        initToolbar("","");
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient =  GoogleSignIn.getClient(this,gso);
        navHeader = navigationView.getHeaderView(0);
        propic= navHeader.findViewById(R.id.propic);
        name= navHeader.findViewById(R.id.name);
        menu= findViewById(R.id.menu);
       drawer = findViewById(R.id.drawer_layout);
        mprogressBar = findViewById(R.id.progressBar);
        mprogressBar.setVisibility(View.VISIBLE);
        sp = PreferenceManager
                .getDefaultSharedPreferences(MainActivity.this);
        edit = sp.edit();
        if (loggedin().contentEquals("yes")) {
            if (!(base_avatar().contentEquals(""))) {
                Picasso.get()
                        .load(base_avatar())
                        .placeholder(R.drawable.profile)
                        .error(R.drawable.placeholder)
                        .resize(90, 90)
                        .transform(new CircleTransform())
                        .into(propic);
            }
            name.setText(base_name());
        }

        in = getIntent();
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });
         mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();


        navHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (loggedin().contentEquals("yes")) {
                    Intent in = new Intent(MainActivity.this, Profile.class);
                    startActivity(in);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    Intent in = new Intent(MainActivity.this, Login.class);
                    startActivity(in);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                }
            }
        });
        navigationView.setNavigationItemSelectedListener(this);

        //add this line to display menu1 when the activity is loaded
        displaySelectedScreen(R.id.content_frame);
    }
    protected void onResume() {
        super.onResume();
        if (loggedin().contentEquals("yes")) {
            if (!(base_avatar().contentEquals(""))) {
                Picasso.get()
                        .load(base_avatar())
                        .placeholder(R.drawable.profile)
                        .error(R.drawable.placeholder)
                        .resize(90, 90)
                        .transform(new CircleTransform())
                        .into(propic);
            }
            name.setText(base_name());
        }
        if (mMap == null) {
            mapFragment.getMapAsync(this);

        } else {
            ifconnection(coordinatorLayout,"Failed to find try again later");
        }
    }
    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        //calling the method displayselectedscreen and passing the id of selected menu
        displaySelectedScreen(item.getItemId());
        //make this method blank
        return true;
    }

    private void displaySelectedScreen(int itemId) {
        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.profile:
                if (loggedin().contentEquals("yes")) {
                    Intent in = new Intent(MainActivity.this, Profile.class);
                    startActivity(in);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    Intent in = new Intent(MainActivity.this, Login.class);
                    startActivity(in);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                }
                break;
            case R.id.contacts:
                if (loggedin().contentEquals("yes")) {
                    Intent in = new Intent(MainActivity.this, User_Contact.class);
                    startActivity(in);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    Intent in = new Intent(MainActivity.this, Login.class);
                    startActivity(in);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;
            case R.id.invite:
                Intent tweetIntent = new Intent(Intent.ACTION_SEND);
                tweetIntent.putExtra(Intent.EXTRA_TEXT, Constant.INVITE_MESSAGE);
                tweetIntent.setType("text/plain");
                startActivity(tweetIntent);
                break;
            case R.id.settings:
                Intent se = new Intent(MainActivity.this, Filter.class);
                startActivity(se);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                fragment = new Fragment_settings();
//                initToolbar("Filter","");
                break;
            case R.id.about:
                Intent ab = new Intent(MainActivity.this, About.class);
                startActivity(ab);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
             case R.id.logout:
               signOut();

                break;
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        ifconnection(coordinatorLayout,"Failed to find try again later Check yout internet connection");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        String profession_set = null;
        if(!in.hasExtra("profession_set")) {
            if (!sp.getString("profession_set", "").contentEquals("")) {
                profession_set = sp.getString("profession_set", "");
            } else {
                profession_set = "any profession";
            }
        }
        String meter = null;
        if(!in.hasExtra("base_meter") ) {
            if (!sp.getString("base_meter", "").contentEquals("")) {
                meter = sp.getString("base_meter", "");
            } else {
                meter = "5";
            }
        }
//        Toast.makeText(MainActivity.this, profession_set+"  "+meter,
//                Toast.LENGTH_SHORT).show();

        mMap = googleMap;
        mMap.setMaxZoomPreference(9);
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("profession", profession_set)
                .add("distance",meter)
                .add("longitude",base_longitude())
                .add("latitude",base_latitude())
                .build();

        Request request = new Request.Builder().url(Constant.ipadress+"all_users.php").post(body).build();
        Call call = client.newCall(request);
        String finalProfession_set = profession_set;
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mprogressBar.setVisibility(View.GONE);
                        ifconnection(coordinatorLayout,"Failed to find try again later Check yout internet connection");

                    }}
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String myResponse = response.body().string();

                MainActivity.this.runOnUiThread(new Runnable() {

                                            public MarkerOptions Mapss;

                                            @Override
                                            public void run() {
                                                mprogressBar.setVisibility(View.GONE);
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
                                                                    .snippet(userid)
                                                                    .icon(BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_BLUE));

                                                            InfoWindowData info = new InfoWindowData();
                                                            // info.setImage("snowqualmie");
                                                            info.setPhone("Phone:"+ phone);
                                                            info.setImage(avatar);
                                                            info.setUserid(userid);
                                                            info.setAddress(address);

                                                            CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(MainActivity.this);
                                                            mMap.setInfoWindowAdapter(customInfoWindow);

                                                            Marker m = mMap.addMarker(markerOptions);
                                                            m.setTag(info);
                                                            m.showInfoWindow();
                                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,4));
                                                            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                                                @Override
                                                                public void onInfoWindowClick(Marker marker) {
                                                                    Bundle b = new Bundle();
                                                                    //   Toast.makeText(Find.this,marker.getSnippet(),Toast.LENGTH_SHORT).show();
                                                                    b.putString("userid", marker.getSnippet());
                                                                    Intent in = new Intent(MainActivity.this, Other_Profile.class);
                                                                    in.putExtras(b);
                                                                    startActivity(in);
                                                                }
                                                            });
                                                        }
                                                    }else  if (jso.getString("status").contentEquals("2")) {
                                                         Toast.makeText(MainActivity.this,"Could not find any "+ finalProfession_set,Toast.LENGTH_SHORT).show();

                                                        ifconnection(coordinatorLayout,"No Result found try a broder filter");
                                                    }

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            }}
                );
            }
        });
        if (ActivityCompat.checkSelfPermission( MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission( MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }
    private void signOut() {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        fAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        PreferenceManager.getDefaultSharedPreferences(getBaseContext()).
                                edit().clear().apply();
                        Intent uo = new Intent(MainActivity.this, MainActivity.class);

                        startActivity(uo);
                    }
                });
    }
}
