package obi.mapfind.details;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import obi.mapfind.BaseActivity;
import obi.mapfind.CircleTransform;
import obi.mapfind.R;

public class View_image extends BaseActivity {
    ImageView propic;
    Bundle b;   Intent in;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_image);
        initToolbar("","");
        propic= (ImageView) findViewById(R.id.propic);
        in = getIntent();
        b = in.getExtras();
        if(!b.getString("url").contentEquals("")) {
            if (isOnline(View_image.this)) {
                Picasso.get()
                        .load(b.getString("url"))
                        .placeholder(R.drawable.placeholder)
                        .transform(new CircleTransform())
                        .into(propic);
            }
        }



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

}