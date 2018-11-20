package obi.mapfind;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import obi.mapfind.details.Login;
import obi.mapfind.details.Profile;
import obi.mapfind.fragment.Fragment_about;
import obi.mapfind.fragment.Fragment_contacts;
import obi.mapfind.fragment.Fragment_invite;
import obi.mapfind.home.Find;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ImageView propic; TextView name;
    //String pro_name,url;
    LinearLayout head;
    private View navHeader;  Intent in;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navHeader = navigationView.getHeaderView(0);
        propic= navHeader.findViewById(R.id.propic);
        name= navHeader.findViewById(R.id.name);
        initToolbar("Find","");


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
                Picasso.with(getApplicationContext())
                        .load(base_avatar())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.error)
                        .resize(150, 150)
                        .transform(new CircleTransform())
                        .into(propic);
            }
            name.setText(base_name());
        }

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
            case R.id.find:
//                fragment = new Fragment_find();
//                initToolbar("Find","");
                Intent inn = new Intent(MainActivity.this, Find.class);
                startActivity(inn);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.contacts:
                fragment = new Fragment_contacts();
                initToolbar("Contacts","");
                break;
            case R.id.invite:
                fragment = new Fragment_invite();
                initToolbar("Invite","");
                break;
            case R.id.settings:
                Intent in = new Intent(MainActivity.this, Settings.class);
                startActivity(in);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                fragment = new Fragment_settings();
//                initToolbar("Settings","");
                break;
            case R.id.about:
                fragment = new Fragment_about();
                initToolbar("About","");
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

}
