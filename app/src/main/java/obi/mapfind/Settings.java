package obi.mapfind;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import java.util.ArrayList;
import java.util.Arrays;

import obi.mapfind.details.Profile;


public class Settings extends BaseActivity {
     SearchableSpinner profession;
     ArrayAdapter professionAdapter,distanceAdapter;
    Spinner spinner; TextView right_text;
    RelativeLayout coordinatorLayout;
    SharedPreferences.Editor edit;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings);
        initToolbar("Settings","Save");
        sp = PreferenceManager
                .getDefaultSharedPreferences(Settings.this);
        edit = sp.edit();

        profession = findViewById(R.id.profession);
        spinner = findViewById(R.id.spinner);
        professionAdapter = new ArrayAdapter<>(Settings.this, android.R.layout.simple_spinner_dropdown_item, Constant.professionlist);
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
              if(!isOnline(Settings.this)){
                  ifconnection(coordinatorLayout,"No internet connection");
                  return;
              }
              edit.putString("profession_set",profession.getSelectedItem().toString());
              edit.putString("base_meter", spinner.getSelectedItem().toString());

              edit.apply();

              Intent uo = new Intent(getApplicationContext(), MainActivity.class);
              finish();
              startActivity(uo);
              overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

          });
          Toast.makeText(Settings.this,base_profession_set(),Toast.LENGTH_SHORT).show();

          if(!(base_profession_set().isEmpty() ||
                  base_profession_set().contentEquals(""))){
              int pos = new ArrayList<>(Arrays.asList(Constant.professionlist)).
                      indexOf(base_profession_set());
              profession.setSelection(pos);
          }
        //Log.i("vai_vai", String.valueOf(pos));
          if(!(base_meter().isEmpty() ||
                  base_meter().contentEquals(""))){
              int pos = new ArrayList<>(Arrays.asList(Constant.distance)).
                      indexOf(base_meter());
              spinner.setSelection(pos);
          }
      }
}
