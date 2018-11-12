package obi.mapfind;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import static java.lang.System.out;

public class BaseActivity extends FragmentActivity {

    SharedPreferences  sp;
    TextView  toolbarTitle, right_text;
    ImageButton backbtn;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState);
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
        sp = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);
        return sp.getString("longitude", "");
    }
    public String  base_latitude(){
        sp = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);
        return sp.getString("latitude", "");
    }
    public String  base_profession(){
        sp = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);
        return sp.getString("profession", "");
    }

    public String  base_interest(){
        sp = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);
        return sp.getString("interest", "");
    }
    public String  base_brief(){
        sp = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);
        return sp.getString("interest", "");
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
