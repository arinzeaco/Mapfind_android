package obi.mapfind.details;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import obi.mapfind.BaseActivity;
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


public class Register extends BaseActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {
    private static final String TAG = "GoogleActivity";

    private String userID;
    Button reg;
    private GoogleApiClient mGoogleApiClient;
   // private ProgressBar progressBar;
    EditText email, pass, conpass, firstname, lastname;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private CoordinatorLayout coordinatorLayout;
    SharedPreferences sp;
    SharedPreferences.Editor edit;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_AUTO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        coordinatorLayout = (CoordinatorLayout)findViewById(R.id
                .layouts);

       // progressBar = (ProgressBar) findViewById(R.id.progressBar);
        email = (EditText) findViewById(R.id.email);
        pass = (EditText) findViewById(R.id.password);
        conpass = (EditText) findViewById(R.id.password2);

        firstname = (EditText) findViewById(R.id.firstname);
        lastname = (EditText) findViewById(R.id.lastname);
        reg = (Button) findViewById(R.id.register);
        email.setText("arinzeaco@gmail.com");
        firstname.setText("arinze");
        lastname.setText("obi");
        pass.setText("111111");
        conpass.setText("111111");

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isOnline(Register.this)){
                    ifconnection(coordinatorLayout,"No Internet Connection");
                    return;
                }
                if (firstname.getText().toString().length() < 2) {
                    Toast.makeText(Register.this, "Invalid Firstname", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (lastname.getText().toString().length() < 2) {
                    Toast.makeText(Register.this, "Invalid Lastname", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (!isEmailValid(email.getText().toString())) {
                    Toast.makeText(Register.this, "Not a valid email",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!pass.getText().toString().contentEquals(conpass.getText().toString())) {
                    Toast.makeText(Register.this, "Both passwords must match", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pass.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Register_email(email.getText().toString(),pass.getText().toString());
            }
        });


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.action_settings))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                } else {
                    // User is signed out

                }
                // ...
            }
        };
    }

    public void login_mysql(String userid) {
        String full_name=  firstname.getText().toString()+" "+ lastname.getText().toString();
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("u_id", userid)
                .add("name", full_name)
                .add("email", email.getText().toString())
                .add("avatars","")
                .build();
        Request request = new Request.Builder().url(Constant.ipadress+"check_login.php").post(body).build();
        // Request request = new Request.Builder().url("http://10.0.2.2/better/charticon.php").post(body).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Register.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
            ifconnection(coordinatorLayout,"Registration failed");
                    return;
                    }}
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String myResponse = response.body().string();

                Register.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        JSONObject jso;
                        try {
                            jso = new JSONObject(myResponse);


                            if (jso.getString("status").contentEquals("1")) {
                                user = mAuth.getCurrentUser();
                                user.sendEmailVerification();
                                Intent uo = new Intent(Register.this, Login.class);
                                finish();
                                startActivity(uo);
                                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                            }else{
                                ifconnection(coordinatorLayout,"Login fail try again later");
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
    public boolean isEmailValid(String email) {


        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }
public void  Register_email(String email, String password){
    mAuth.createUserWithEmailAndPassword(email,password)
            .

    addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete (@NonNull Task < AuthResult > task) {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information

                Log.d(TAG, "createUserWithEmail:success");
                FirebaseUser user = mAuth.getCurrentUser();
                userID = user.getUid();
                login_mysql(userID);
             //   ifconnection(coordinatorLayout,"Check your email to verify");
//                Intent intent = new Intent(Register.this, Login.class);
//                startActivity(intent);

            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                ifconnection(coordinatorLayout,"Register fail try again later");

            }

            // ...
        }
    }

    );
}
    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
