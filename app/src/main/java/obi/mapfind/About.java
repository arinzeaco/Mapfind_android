package obi.mapfind;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import obi.mapfind.Utils.BaseActivity;

public class About extends BaseActivity {
    TextView about;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        initToolbar("About","");
        about= findViewById(R.id.about);
        about.setText("This is the first vision of an application the lets you located peopl" +
                "e around you based on their profession and distance." +
                " The Google map is used when the user inputs his/her" +
                " location and the nearest person can be found. " +
                "Registration is not to find anyone, " +
                "unless you want to find the person by location");

    }
}