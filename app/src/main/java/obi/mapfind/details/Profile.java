package obi.mapfind.details;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.io.IOException;
import obi.mapfind.BaseActivity;
import obi.mapfind.CircleTransform;
import obi.mapfind.Constant;
import obi.mapfind.MainActivity;
import obi.mapfind.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Profile extends BaseActivity {

    EditText name,email,phone,address,
            interest,brief;
    String urll; ImageView pro;
    TextView  right_text;
    SharedPreferences sp;
    RelativeLayout coordinatorLayout;
    SharedPreferences.Editor edit;
    private SearchableSpinner profession;
    private ArrayAdapter professionAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        sp = PreferenceManager
                .getDefaultSharedPreferences(Profile.this);
        edit = sp.edit();
        Profile_data();
        initToolbar("Profile","Save");
        right_text= findViewById(R.id.right_text);
        right_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isOnline(Profile.this)){
                    ifconnection(coordinatorLayout,"No internet connection");
                    return;
                }
                update_details();
            }
        });
    }

    public void Profile_data(){
        coordinatorLayout = findViewById(R.id
                .layouts);
        pro =  findViewById(R.id.propic);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone= findViewById(R.id.phone);
        address = findViewById(R.id.address);
        profession = findViewById(R.id.profession);
        interest = findViewById(R.id.interest);
        brief = findViewById(R.id.brief);

        email.setText(base_email());
        name.setText(base_name());

        Intent in = getIntent();
        Bundle b = in.getExtras();
        if(in.hasExtra("newphone")){
            phone.setText( b.getString("newphone"));
        }else{
            phone.setText(base_phone());
        }
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Profile.this, Change_Phone_Mysql.class);
                startActivity(in);

            }
        });
        if(in.hasExtra("address")){
            address.setText(b.getString("address"));
        }else{
            address.setText(base_address());
        }
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Profile.this, MapsActivity.class);
                startActivity(in);

            }
        });

//        profession.setText(base_profession());
        interest.setText(base_interest());
        brief.setText(base_brief());
        professionAdapter = new ArrayAdapter<>(Profile.this, android.R.layout.simple_spinner_dropdown_item, Constant.professionlist);
        profession.setAdapter(professionAdapter);
        if(!base_avatar().isEmpty() && !base_avatar().contentEquals("")) {
            if (isOnline(getApplicationContext())) {
                Picasso.with(getApplicationContext())
                        .load(base_avatar())
                        .placeholder(R.drawable.placeholder)

                        .resize(120, 120)
                        .transform(new CircleTransform())
                        .into(pro);
            }
        }
        pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();

                b.putString("urll", urll);
//                Intent in = new Intent(Profile.this, Displaypic.class);
//                in.putExtras(b);
//                startActivity(in);

            }
        });
    }

    public void update_details() {
        Log.i("name",name.getText().toString());
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("u_id", base_u_id())
                .add("name", name.getText().toString())
                .add("profession", base_profession())
                .add("brief",base_brief())
                .add("interest",base_interest())
                .add("phone",base_phone())
                .build();
        Request request = new Request.Builder().url(Constant.ipadress+"update.php").post(body).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Profile.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ifconnection(coordinatorLayout,"Update fail try again later");
                    }}
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String myResponse = response.body().string();

                Profile.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        JSONObject jso;

                        try {
                            jso = new JSONObject(myResponse);


                            if (jso.getString("status").contentEquals("1")) {

                                JSONObject details = jso.getJSONObject("data");
                                String brief = details.getString("brief");
                                String profession = details.getString("profession");
                                String email = details.getString("email");
                                String interest = details.getString("interest");
                                String name = details.getString("name");
                                String avatar = details.getString("avatar");
                                String phone = details.getString("phone");
                                String address = details.getString("address");


                                edit.putString("email", email);
                                edit.putString("name", name);
                                edit.putString("profession", profession);
                                edit.putString("interest", interest);
                                edit.putString("brief", brief);
                                edit.putString("avatar",avatar);
                                edit.putString("phone", phone);
                                edit.putString("address",address);
                                edit.putString("phone", phone);

                                edit.apply();

                                Intent uo = new Intent(getApplicationContext(), MainActivity.class);
                                finish();
                                startActivity(uo);
                                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                            }else{
                                ifconnection(coordinatorLayout,"Update fail try again later");
                                return;

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }}
                );
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
