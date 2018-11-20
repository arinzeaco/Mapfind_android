package obi.mapfind;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Other_Profile extends BaseActivity {

    TextView text_name,text_email,text_phone,text_address,
            text_profession ,text_brief;
    String urll; ImageView pro;

    TextView right_text;
    SharedPreferences sp;   String likevalue;
    Bitmap bitmap;  BitmapDrawable background;
    RelativeLayout coordinatorLayout;
    SharedPreferences.Editor edit;
    Button change_avatar;
    Intent in;
    Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_profile);
        sp = PreferenceManager
                .getDefaultSharedPreferences(Other_Profile.this);
        edit = sp.edit();
        in = getIntent();
        b = in.getExtras();
        initToolbarimage("");
        pro =  findViewById(R.id.propic);
        text_name = findViewById(R.id.name);
        text_email = findViewById(R.id.email);
        text_phone= findViewById(R.id.phone);
        text_address = findViewById(R.id.address);
        text_profession = findViewById(R.id.profession);
        //  interest = findViewById(R.id.interest);
        text_brief = findViewById(R.id.brief);
        if(!isOnline(Other_Profile.this)){
            ifconnection(coordinatorLayout,"No internet connection");
            return;
        }else {
            update_details();
        }
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
////// Add click to like function
                if(!isOnline(Other_Profile.this)){
                    ifconnection(coordinatorLayout,"No internet connection");
                    return;
                }
                if(!loggedin().contentEquals("yes")){
                    ifconnection(coordinatorLayout,"Login before you can like");
                    return;
                }
                if (likevalue.contentEquals("yes")) {
                    changeLikedValue();
                } else if (likevalue.contentEquals("no")) {
                    changeLikedValue();
                }
            }

        });
      //  change_avatar.setOnClickListener(v -> selectImage());
    }
    public void changeLikedValue() {
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("u_id", base_u_id())
                .add("liked", likevalue)
                .add("like_uid",b.getString("userid"))
                .build();
        Request request = new Request.Builder().url(Constant.ipadress+"charticon.php").post(body).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Other_Profile.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
            ifconnection(coordinatorLayout,"failed to add to chart try again later");
                    }}
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String myResponse = response.body().string();
                Other_Profile.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jso;
                        try {
                            jso = new JSONObject(myResponse);
                            if (jso.getString("status").contentEquals("1")) {
                                String val= jso.getString("val");
                                if(val.contentEquals("yes")) {
                                    likevalue="yes";
                                    like.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.heart2));
                                }else{
                                    likevalue="no";
                                    like.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.heart));
                                }

                            }else {
                                ifconnection(coordinatorLayout,"failed to add to chart try again later");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }}
                );
            }
        });

    }
    public void update_details() {
      //  Log.i("name",name.getText().toString());
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("u_id", base_u_id())
                .add("userid",b.getString("userid"))
                .build();
        Request request = new Request.Builder().url(Constant.ipadress+"other_profile.php").post(body).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Other_Profile.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ifconnection(coordinatorLayout,"Update fail try again later");
                    }}
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String myResponse = response.body().string();

                Other_Profile.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        JSONObject jso;

                        try {
                            jso = new JSONObject(myResponse);

                            likevalue= jso.getString("liked");
                            if(likevalue.contentEquals("yes")){
                                like.setImageDrawable( ContextCompat.getDrawable(getApplicationContext(), R.drawable.heart2));
                            }else if(likevalue.contentEquals("no")){
                                like.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.heart));
                            }
                            if (jso.getString("status").contentEquals("1")) {
                                JSONObject details = jso.getJSONObject("data");
                                String brief = details.getString("brief");
                                String profession = details.getString("profession");
                                String email = details.getString("email");
                              //  String interest = details.getString("interest");
                                String name = details.getString("name");
                                String avatar = details.getString("avatar");
                                String phone = details.getString("phone");
                                String address = details.getString("address");
                                if(!avatar.contentEquals("")) {
                                    if (isOnline(Other_Profile.this)) {
                                        Picasso.with(Other_Profile.this)
                                                .load(avatar)
                                                .placeholder(R.drawable.placeholder)
                                                .resize(120, 120)
                                                .transform(new CircleTransform())
                                                .into(pro);
                                    }
                                }

                                text_email.setText(email);
                                text_name.setText(name);
                                text_profession.setText(profession);
                               // text_interest.setText(interest);
                                text_brief.setText(brief);
                                text_phone.setText(phone);
                                text_address.setText(address);

                                edit.apply();

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
