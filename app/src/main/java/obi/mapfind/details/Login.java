package obi.mapfind.details;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import obi.mapfind.Utils.BaseActivity;
import obi.mapfind.Utils.Constant;
import obi.mapfind.MainActivity;

import obi.mapfind.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Thinker on 7/14/2017.
 */

public class Login extends BaseActivity{
    private EditText inputEmail, inputPassword;
    private GoogleSignInClient mGoogleSignInClient;
    ProgressBar  mprogressBar;
    private RelativeLayout coordinatorLayout;
    private Button btnSignup, btnLogin, btnResetPassword;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private SignInButton btnSignIn;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private String userID;
    FirebaseUser user;
    SharedPreferences sp;
    SharedPreferences.Editor edit;
    ProgressBarClass progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
       sp = PreferenceManager
                .getDefaultSharedPreferences(Login.this);
         edit = sp.edit();
        initToolbar("Login","");

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressBarClass(this);
        progressDialog.txtTitle.setText("Please wait.........");

        inputEmail = findViewById(R.id.name);
        inputPassword = findViewById(R.id.password);
        coordinatorLayout = findViewById(R.id
                .layouts);
      //  progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnSignup =  findViewById(R.id.btn_signup);
        btnLogin = findViewById(R.id.btn_login);
        btnResetPassword = findViewById(R.id.btn_reset_password);

//        inputEmail.setText("arinzeaco@gmail.com");
//        inputPassword.setText("111111");

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, ResetPasswordActivity.class));
            }
        });


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient =  GoogleSignIn.getClient(this,gso);
        mAuth = FirebaseAuth.getInstance();
       
        btnSignIn = (SignInButton) findViewById(R.id.sign_in_button);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isOnline(Login.this)){
                    ifconnection(coordinatorLayout,"No internet connection");
                    return;
                }
                signIn();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    ifconnection(coordinatorLayout,"Enter email address!");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    ifconnection(coordinatorLayout,"Enter password!");
                    return;
                }
                if (password.length() < 6) {
                    ifconnection(coordinatorLayout,"password must not be lest that 6 letters");

                    return;
                }
                if(!isOnline(Login.this)){
                    ifconnection(coordinatorLayout,"No internet connection");
                    return;
                }

                //authenticate user
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                               user = mAuth.getCurrentUser();
                                progressDialog.show();
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    ifconnection(coordinatorLayout,"wrong user name or pasword");

                                } else if(task.isSuccessful()) {

                                    if (user.isEmailVerified()) {
                                        progressDialog.dismiss();
                                        user.getUid();
                                        userID = user.getUid();

                                        edit.putString("userID", userID);
                                        edit.commit();

                                        login_mysql(userID,"","","","");

                                    }
                                    if(!user.isEmailVerified()) {
                                        progressDialog.dismiss();
                                        ifconnection(coordinatorLayout,"Please go to you mail and verify account");

                                        user.sendEmailVerification();

                                    }
                                }
                            }

                        });
            }
        });

    }

  public void login_mysql(String u_id, String name, String email,String avatars, String phone) {

    OkHttpClient client = new OkHttpClient();
    RequestBody body = new FormBody.Builder()
            .add("u_id", u_id)
            .add("name", name)
            .add("email", email)
            .add("avatars",avatars)
            .add("phone",phone)
            .build();
    Request request = new Request.Builder().url(Constant.ipadress+"check_login.php").post(body).build();
    // Request request = new Request.Builder().url("http://10.0.2.2/better/charticon.php").post(body).build();
    Call call = client.newCall(request);
    call.enqueue(new Callback() {

        @Override
        public void onFailure(Call call, IOException e) {

            Login.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    ifconnection(coordinatorLayout,"Login failed at this time try again later");

                }}
            );
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {

            final String myResponse = response.body().string();

            Login.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    JSONObject jso;
                    try {
                        jso = new JSONObject(myResponse);


                        if (jso.getString("status").contentEquals("1")) {
                            progressDialog.dismiss();
                            JSONObject details = jso.getJSONObject("data");
                             String brief = details.getString("brief");
                             String profession = details.getString("profession");
                            String email = details.getString("email");
                            String interest = details.getString("interest");
                            String name = details.getString("name");
                            String avatar = details.getString("avatar");
                            String phone = details.getString("phone");
                            String address = details.getString("address");
                            String longitude = details.getString("longitude");
                            String latitude = details.getString("latitude");

                            edit.putString("loggedin", "yes");
                            edit.putString("u_id", u_id);
                            edit.putString("email", email);
                            edit.putString("name", name);
                            edit.putString("profession", profession);
                            edit.putString("interest", interest);
                            edit.putString("brief", brief);
                            edit.putString("avatar",avatar);
                            edit.putString("phone", phone);
                            edit.putString("address",address);
                            edit.putString("longitude",longitude);
                            edit.putString("latitude", latitude);
                            edit.apply();

                            Intent uo = new Intent(getApplicationContext(), MainActivity.class);
                            finish();
                            startActivity(uo);
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        }else{
                            progressDialog.dismiss();
                                ifconnection(coordinatorLayout,"Login fail try aganin later");
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                Log.i("oooooooooooo","3");

                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);

            }
        }

    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        progressDialog.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            String pic=user.getPhotoUrl().toString();
                            String picture;
                               if(pic.contentEquals("")|| pic.isEmpty()){
                                   picture="";
                               }else{
                                   picture=pic;
                               }
                           // Log.i("ttwooo",userID);
//                            Toast.makeText(Login.this, userID,
//                                    Toast.LENGTH_SHORT).show();
                            edit.putString("userID", userID);
                            edit.apply();

                           login_mysql(user.getUid(),user.getDisplayName(),user.getEmail(),picture,"");

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            ifconnection(coordinatorLayout, "Authentication failed.");

                        }

                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

}