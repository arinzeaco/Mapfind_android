package obi.mapfind;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import java.util.ArrayList;
import java.util.Arrays;

import obi.mapfind.Utils.BaseActivity;
import obi.mapfind.Utils.Constant;


public class Settings extends BaseActivity {
     SearchableSpinner profession;
     ArrayAdapter professionAdapter,distanceAdapter;
    Spinner spinner; TextView right_text,dist;
    LinearLayout coordinatorLayout;   Intent in;
    SharedPreferences.Editor edit;     SharedPreferences  sp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings);
        initToolbar("Filter","Save");
        sp = PreferenceManager
                .getDefaultSharedPreferences(Settings.this);
        edit = sp.edit();
        in = getIntent();
        profession = findViewById(R.id.profession);
        spinner = findViewById(R.id.spinner);
        dist= findViewById(R.id.dist);
        if(base_latitude().contentEquals("")){
            spinner.setVisibility(View.GONE);
        }else{
            dist.setVisibility(View.GONE);
        }
        professionAdapter = new ArrayAdapter<>(Settings.this, android.R.layout.simple_spinner_dropdown_item,Constant.professionlist_settings);
        distanceAdapter = new ArrayAdapter<>(Settings.this, android.R.layout.simple_spinner_dropdown_item, Constant.distance);

        profession.setAdapter(professionAdapter);
        spinner.setAdapter(distanceAdapter);

       details();
    }
      public void details(){
          coordinatorLayout = findViewById(R.id
                  .layouts);
          right_text= findViewById(R.id.right_text);
          right_text.setOnClickListener(v -> {
              Log.i("filter _details",profession.getSelectedItem().toString()+spinner.getSelectedItem().toString());

              edit.putString("profession_set",profession.getSelectedItem().toString());
              edit.putString("base_meter", spinner.getSelectedItem().toString());
              edit.apply();
              Intent uo = new Intent(Settings.this
                      , MainActivity.class);
              finish();
              startActivity(uo);
              overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

          });

          if(!in.hasExtra("profession_set")){
              int pos = new ArrayList<>(Arrays.asList(Constant.professionlist)).
                      indexOf(sp.getString("profession_set", ""));
              profession.setSelection(pos);
          }
          if(!in.hasExtra("base_meter")){
              int pos = new ArrayList<>(Arrays.asList(Constant.distance)).
                      indexOf(sp.getString("base_meter", ""));
             spinner.setSelection(pos);
          }
      }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
