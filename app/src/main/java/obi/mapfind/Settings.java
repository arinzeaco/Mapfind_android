package obi.mapfind;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
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
    RelativeLayout coordinatorLayout;   Intent in;
    SharedPreferences.Editor edit;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings);
        initToolbar("Settings","Save");
        sp = PreferenceManager
                .getDefaultSharedPreferences(Settings.this);
        edit = sp.edit();
        in = getIntent();
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
              Log.i("filter _details",profession.getSelectedItem().toString()+spinner.getSelectedItem().toString());

              edit.putString("profession_set",profession.getSelectedItem().toString());
              edit.putString("base_meter", spinner.getSelectedItem().toString());
              edit.apply();
              Intent uo = new Intent(getApplicationContext(), MainActivity.class);
              finish();
              startActivity(uo);
              overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

          });
       //   Toast.makeText(Settings.this,sp.getString("profession_set", ""),Toast.LENGTH_SHORT).show();

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
}
