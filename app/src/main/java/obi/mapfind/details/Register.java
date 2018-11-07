package obi.mapfind.details;


import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import obi.mapfind.BaseActivity;
import obi.mapfind.R;


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
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private CoordinatorLayout coordinatorLayout;

// ...

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
        email.setText("omega1aco@gmail.com");
        firstname.setText("arinze");
        lastname.setText("obi");
        pass.setText("omega1");
        conpass.setText("omega1");

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
             yes(email.getText().toString(),pass.getText().toString());
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

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
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
public void  yes(String emaill, String password){
    mAuth.createUserWithEmailAndPassword(emaill,password)
            .

    addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete (@NonNull Task < AuthResult > task) {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information

                Log.d(TAG, "createUserWithEmail:success");
                FirebaseUser user = mAuth.getCurrentUser();
                userID = user.getUid();
              String full_name=  lastname.getText().toString()+" "+ firstname.getText().toString();
                UserInformation userInformation = new UserInformation(email.getText().toString(),full_name,"","","");
                myRef.child("users").child(userID).setValue(userInformation);
                Toast.makeText(Register.this, "Verify your account and Login.",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);

            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                Toast.makeText(Register.this, "Sorry there was an error in registration",
                        Toast.LENGTH_SHORT).show();

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
