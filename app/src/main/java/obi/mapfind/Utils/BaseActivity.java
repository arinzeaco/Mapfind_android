package obi.mapfind.Utils;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import obi.mapfind.R;
import obi.mapfind.details.ProgressBarClass;

public class BaseActivity extends FragmentActivity {

    SharedPreferences  sp;
    TextView  toolbarTitle, right_text;
    ImageButton backbtn;
    ImageView like;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState);
    }

    public void initToolbarimage(String title){
        toolbarTitle = findViewById(R.id.toolbartitle);
        toolbarTitle.setText(title);
    }
    public void initToolbar(String title, String right_title){
        toolbarTitle = findViewById(R.id.toolbartitle);
        backbtn = findViewById(R.id.backbtn);
        right_text= findViewById(R.id.right_text);
        right_text.setText(right_title);
        toolbarTitle.setText(title);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
    public String  loggedin(){
        sp = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);
        return sp.getString("loggedin", "");
    }
    public String  base_u_id(){
        sp = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);
        return sp.getString("u_id", "");
    }
    public String base_name(){
        sp = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);
        return sp.getString("name", "");
    }
    public String  base_email(){
        sp = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);
        return sp.getString("email", "");
    }
    public String base_avatar(){
        sp = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);
        return sp.getString("avatar", "");
    }
    public String  base_address(){
        sp = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);
        return sp.getString("address", "");
    }
    public String  base_phone(){
        sp = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);
        return sp.getString("phone", "");
    }
    public String  base_longitude(){
        Intent in = getIntent();
        sp = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);

        String longitude;
        if(in.hasExtra("longitude")){
            longitude=sp.getString("longitude", "");
        }else{
            longitude="0";
        }
        return longitude;
    }
    public String  base_latitude(){
        sp = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);
        String latitude;
        Intent in = getIntent();
        if(in.hasExtra("latitude")){
            latitude=sp.getString("latitude", "");
        }else{
            latitude="0";
        }
        return latitude;
    }
    public String base_profession(){
        sp = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);
        return sp.getString("profession", "");
    }

    public String base_interest(){
        sp = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);
        return sp.getString("interest", "");
    }
    public String base_brief(){
        sp = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);
        return sp.getString("brief", "");
    }

    public String base_profession_set(){
        sp = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);
        String profession_set;
        Intent in = getIntent();
        if(in.hasExtra("profession_set")){
            profession_set=sp.getString("profession_set", "");
        }else{
            profession_set="";
        }
        return profession_set;
    }
    public String base_meter(){
        sp = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);
        String met;
        Intent in = getIntent();
        if(in.hasExtra("base_meter")){
            met=sp.getString("base_meter", "");
        }else{
            met="";
        }
        return met;
    }

    public boolean isOnline(Context mContext) {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting())
        {
            return true;
        }
        return false;
    }
    public void ifconnection(View code, String message){
        if(!isOnline(BaseActivity.this)){
            Snackbar snackbar = Snackbar
                    .make(code, message, Snackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.RED);

            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);

            snackbar.show();
            return;
        }
    }
    public String formatNumber(String countryCode, String number) {
        String formatedNumber = countryCode + number;
        if (countryCode.contains("+234")) {
            if (number.charAt(0) == '0') {//If the person still aded 0
                formatedNumber = number.replaceFirst("0", "+234");
            }
        }
        return formatedNumber;
    }
}
