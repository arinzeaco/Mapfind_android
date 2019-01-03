package obi.mapfind.details;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import obi.mapfind.R;
import obi.mapfind.Utils.BaseActivity;

public class ResetPasswordActivity extends BaseActivity {

    private EditText inputEmail;
    private Button btnReset, btnBack;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private CoordinatorLayout coordinatorLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        initToolbar("Reset Password","");
        inputEmail =  findViewById(R.id.email);
        btnReset = findViewById(R.id.btn_reset_password);
        btnBack =  findViewById(R.id.btn_back);
        progressBar =  findViewById(R.id.progressBar);
        coordinatorLayout = findViewById(R.id
                .layouts);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (!isEmailValid(inputEmail.getText().toString())) {
                      ifconnection(coordinatorLayout,"Not a valid email");
                    return;
                }
                if(!isOnline(ResetPasswordActivity.this)){
                    ifconnection(coordinatorLayout,"No internet connection");
                    return;
                }
                FirebaseAuth auth = FirebaseAuth.getInstance();

                auth.sendPasswordResetEmail(inputEmail.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    ifconnection(coordinatorLayout,"Check your email for new password.");
                                }else{
                                    ifconnection(coordinatorLayout,"Email don't exist");
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
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
}
