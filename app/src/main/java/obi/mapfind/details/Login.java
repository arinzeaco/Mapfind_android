package obi.mapfind.details;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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

/**
 * Created by Thinker on 7/14/2017.
 */

public class Login extends BaseActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener{
    private EditText inputEmail, inputPassword;

    private ProgressBar progressBar;
    private RelativeLayout coordinatorLayout;
    private Button btnSignup, btnLogin, btnResetPassword;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private SignInButton btnSignIn;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private String userID;
    FirebaseUser user;
    SharedPreferences sp;
    SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
       sp = PreferenceManager
                .getDefaultSharedPreferences(Login.this);
         edit = sp.edit();
        initToolbar("Login","");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        coordinatorLayout = findViewById(R.id
                .layouts);
      //  progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnSignup =  findViewById(R.id.btn_signup);
        btnLogin = findViewById(R.id.btn_login);
        btnResetPassword = findViewById(R.id.btn_reset_password);

        inputEmail.setText("arinzeaco@gmail.com");
        inputPassword.setText("111111");

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
                .requestIdToken(getString(R.string.app_name))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        btnSignIn = (SignInButton) findViewById(R.id.sign_in_button);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!isOnline(Login.this)){
                    ifconnection(coordinatorLayout,"No internet connection");
                    return;
                }
                final ProgressDialog progressDialog = new ProgressDialog(Login.this,
                        R.style.AppTheme);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Logging in...");
                progressDialog.show();
                //authenticate user
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                               user = mAuth.getCurrentUser();

                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        Toast.makeText(Login.this, "password too short",
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    Toast.makeText(Login.this, "wrong user name or pasword",
                                            Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                } else if(task.isSuccessful()) {
                                    if (user.isEmailVerified()) {
                                        user.getUid();
                                        userID = user.getUid();

                                        edit.putString("userID", userID);
                                        edit.commit();

                                        login_mysql();
                                        progressDialog.dismiss();
                                    }
                                    if(!user.isEmailVerified()) {
                                        Toast.makeText(Login.this, "Please go to you mail and verify account",
                                                Toast.LENGTH_SHORT).show();
                                        user.sendEmailVerification();
                                        progressDialog.dismiss();
                                    }
                                }
                            }

                        });
            }
        });

    }
    public void gmaillog(final String tEmail, final String tName, final String tpic) {
        myRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) {


                    edit.putString("state", "logged");
                    edit.putString("email", tEmail);
                    edit.putString("name", tName);
                    edit.putString("pic", tpic);
                    edit.commit();
                    Intent uo = new Intent(getApplicationContext(), MainActivity.class);
                    finish();
                    startActivity(uo);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                }
                else {
                    login_mysql();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }
  public void login_mysql() {
    OkHttpClient client = new OkHttpClient();
    RequestBody body = new FormBody.Builder()
            .add("u_id", user.getUid())
            .add("name", user.getDisplayName())
            .add("email", user.getEmail())
            .add("avatars",user.getDisplayName())
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
                    Toast.makeText(getApplicationContext(), "An",
                            Toast.LENGTH_SHORT).show();
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
                            edit.putString("u_id", user.getUid());
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
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {

            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        final ProgressDialog progressDialog = new ProgressDialog(Login.this,
                                R.style.AppTheme);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Signing in...");
                        progressDialog.show();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            String pic=user.getPhotoUrl().toString();
                            user.getUid();
                            userID = user.getUid();


                            edit.putString("userID", userID);
                            edit.apply();
                            gmaillog(user.getEmail(),user.getDisplayName(),pic);
                            progressDialog.dismiss();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }

    private void showData(DataSnapshot dataSnapshot) {

        for(DataSnapshot ds : dataSnapshot.getChildren()){
            UserInformation uInfo = new UserInformation();
            uInfo.setName(ds.child(userID).getValue(UserInformation.class).getName());
            uInfo.setEmail(ds.child(userID).getValue(UserInformation.class).getEmail());
            uInfo.setUrl(ds.child(userID).getValue(UserInformation.class).getUrl());
            uInfo.setLocation(ds.child(userID).getValue(UserInformation.class).getLocation());
            uInfo.setPhone_number(ds.child(userID).getValue(UserInformation.class).getPhone_number());

            edit.putString("state", "logged");
            edit.putString("email", uInfo.getEmail());
            edit.putString("name", uInfo.getName());
            edit.putString("pic", uInfo.getUrl());
            edit.putString("phone_number", uInfo.getPhone_number());
            edit.putString("location", uInfo.getLocation());
            edit.apply();
            Intent uo = new Intent(getApplicationContext(), MainActivity.class);
            finish();
            startActivity(uo);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}