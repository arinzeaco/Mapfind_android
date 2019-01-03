package obi.mapfind;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import obi.mapfind.Utils.BaseActivity;
import obi.mapfind.Utils.CircleTransform;
import obi.mapfind.Utils.Constant;
import obi.mapfind.details.ProgressBarClass;
import obi.mapfind.details.View_image;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Other_Profile extends BaseActivity {

    TextView text_email,text_phone,text_address,
            text_profession ,text_brief;
   ImageView pro;
    TextView text_name;
    TextView right_text;
    SharedPreferences sp;   String likevalue;
    BitmapDrawable background;
    RelativeLayout coordinatorLayout;
    SharedPreferences.Editor edit;
    Intent in;
    Bundle b; ImageView like_icon;   ImageButton backbtn,image_call,image_email;
    ProgressBarClass progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_profile);
        sp = PreferenceManager
                .getDefaultSharedPreferences(Other_Profile.this);
        edit = sp.edit();
        in = getIntent();
        b = in.getExtras();
        coordinatorLayout =  findViewById(R.id.layouts);
        pro =  findViewById(R.id.propic);
        text_name = findViewById(R.id.name);
        text_email = findViewById(R.id.email);
        text_phone= findViewById(R.id.phone);
        text_address = findViewById(R.id.address);
        text_profession = findViewById(R.id.profession);
        text_brief = findViewById(R.id.brief);
        like_icon =  findViewById(R.id.like_icon);
        backbtn = findViewById(R.id.backbtn);
        image_call = findViewById(R.id.image_call);
        image_email = findViewById(R.id.image_email);
        progressDialog = new ProgressBarClass(this);
        progressDialog.txtTitle.setText("Loading.........");
        progressDialog.show();
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //clikable();
        image_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+text_phone.getText().toString()));
                startActivity(intent);
            }
        });
        image_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
sendEmail();
            }
        });
        if(!isOnline(Other_Profile.this)){
            ifconnection(coordinatorLayout,"No internet connection");
            return;
        }else {
            get_details();
        }
        like_icon.setOnClickListener(new View.OnClickListener() {
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

                    changeLikedValue();

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
                        progressDialog.dismiss();
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
                                likevalue= jso.getString("val");
                                if(likevalue.contentEquals("yes")) {
                                    ifconnection(coordinatorLayout,"liked");
                                    like_icon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.heart2));
                                }else{
                                    ifconnection(coordinatorLayout,"unliked");
                                    like_icon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.heart));
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
    public void get_details() {
     Log.i("name_one","1");

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
                        progressDialog.dismiss();
                        Log.i("nameoone","2");
                        JSONObject jso;

                        try {
                            jso = new JSONObject(myResponse);

                            likevalue= jso.getString("liked");
                            if(likevalue.contentEquals("yes")){
                                like_icon.setImageDrawable( ContextCompat.getDrawable(getApplicationContext(), R.drawable.heart2));
                            }else if(likevalue.contentEquals("no")){
                                like_icon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.heart));
                            }
                            if (jso.getString("status").contentEquals("1")) {
                                Log.i("nameoone","3");
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
                                        Picasso.get()
                                                .load(avatar)
                                                .placeholder(R.drawable.placeholder)
                                                .resize(120, 120)
                                                .transform(new CircleTransform())
                                                .into(pro);
                                    }
                                }else{
                                    Picasso .get()
                                            .load(R.drawable.placeholder)
                                            .placeholder(R.drawable.placeholder)
                                            .resize(120, 120)
                                            .transform(new CircleTransform())
                                            .into(pro);
                                }

                                text_email.setText(email);
                                text_name.setText(name);
                                text_profession.setText(profession);
                               // text_interest.setText(interest);
                                text_brief.setText(brief);
                                text_phone.setText(phone);
                                text_address.setText(address);
                                if(text_phone.getText().toString().contentEquals("")){
                                    image_call.setVisibility(View.GONE);
                                }

                                edit.apply();
                                pro.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Bundle b = new Bundle();
                                        //   Toast.makeText(Find.this,marker.getSnippet(),Toast.LENGTH_SHORT).show();
                                        b.putString("url",avatar);
                                        Intent in = new Intent(Other_Profile.this, View_image.class);
                                        in.putExtras(b);
                                        startActivity(in);
                                    }
                                });

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
    protected void sendEmail() {
        String[] TO = {""};
        String[] CC = {""};
        final Intent emailIntent  = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","", null));

        emailIntent.putExtra(Intent.EXTRA_EMAIL, text_email.getText().toString());
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "From mapfind");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "");

        try {
            startActivity(Intent.createChooser(emailIntent, "Choose an Email client :"));
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            ifconnection(coordinatorLayout,"Open email failed");
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
