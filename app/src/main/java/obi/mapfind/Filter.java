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
import android.widget.Spinner;
import android.widget.TextView;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import java.util.ArrayList;
import java.util.Arrays;

import obi.mapfind.Utils.BaseActivity;
import obi.mapfind.Utils.Constant;


public class Filter extends BaseActivity {
     SearchableSpinner profession;
     ArrayAdapter professionAdapter,distanceAdapter;
    Spinner spinner; TextView right_text,dist;
    LinearLayout coordinatorLayout,dis;   Intent in;
    SharedPreferences.Editor edit;     SharedPreferences  sp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings);
        initToolbar("Filter","Save");
        sp = PreferenceManager
                .getDefaultSharedPreferences(Filter.this);
        edit = sp.edit();
        in = getIntent();
        dist = findViewById(R.id.dist);
        profession = findViewById(R.id.profession);
        spinner = findViewById(R.id.spinner);
        dis = findViewById(R.id.dis);
        if(base_address().contentEquals("")){
           dis.setVisibility(View.GONE);

        }else{
            dist.setVisibility(View.GONE);
            dis.setVisibility(View.VISIBLE);
        }
        professionAdapter = new ArrayAdapter<>(Filter.this, android.R.layout.simple_spinner_dropdown_item,Constant.professionlist_settings);
        distanceAdapter = new ArrayAdapter<>(Filter.this, android.R.layout.simple_spinner_dropdown_item, Constant.distance);

        profession.setAdapter(professionAdapter);
        spinner.setAdapter(distanceAdapter);
       details();
    }
      public void details(){
          coordinatorLayout = findViewById(R.id
                  .layouts);
          right_text= findViewById(R.id.right_text);
          right_text.setOnClickListener(v -> {
             // Log.i("filter _details",profession.getSelectedItem().toString()+spinner.getSelectedItem().toString());

              edit.putString("profession_set",profession.getSelectedItem().toString());
              if(!base_address().contentEquals("")){
                  edit.putString("base_meter", spinner.getSelectedItem().toString());
              }
              edit.apply();
              Intent uo = new Intent(Filter.this
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
