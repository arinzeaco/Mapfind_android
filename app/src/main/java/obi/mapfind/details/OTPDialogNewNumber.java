package obi.mapfind.details;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.Executor;

import obi.mapfind.BaseActivity;
import obi.mapfind.MainActivity;
import obi.mapfind.R;

import static android.support.constraint.Constraints.TAG;


class OTPDialogNewNumber extends Dialog {
    private final Context context;

    private final TextView txtTitle;
    private Button btnConfirm;
    EditText editTxtOTP;

    public String number;
    public String verifyid;
    String numberwithout;
    BaseActivity base;
    private FirebaseAuth mAuth;

    public OTPDialogNewNumber(@NonNull final Context context) {
        super(context);
        this.context = context;
        base= new BaseActivity();
    /*-----------------------------------------------------------------
     |     CONFIGURE THE APPEARANCE OF THE DIALOG
     *-----------------------------------------------------------------*/
        setContentView(R.layout.dialog_otp);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
//        dialog.getWindow().setDimAmount(0.0f);
        int width = (int)(context.getResources().getDisplayMetrics().widthPixels*0.90);
        int height = (int)(context.getResources().getDisplayMetrics().heightPixels*0.80);
        getWindow().setLayout(width, height);
        getWindow().getAttributes().windowAnimations = R.style.AppTheme;
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mAuth = FirebaseAuth.getInstance();

    /*-----------------------------------------------------------------
     |     FIND REFERENCE TO ALL VIEWS
     *-----------------------------------------------------------------*/
        txtTitle = findViewById(R.id.txtTitle);
        btnConfirm = findViewById(R.id.btnConfirm);
        editTxtOTP = findViewById(R.id.editTxtOTP);


        btnConfirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(editTxtOTP.getText().toString().length() < 4){
                  //  Notification.show(context, "The OTP must be at least 4 characters", Notification.ERROR);
                    Toast.makeText(context,"The OTP must be at least 4 characters",Toast.LENGTH_LONG).show();
                  return;
                }
                else {

                    verifyPhoneNumberWithCode(verifyid, editTxtOTP.getText().toString());
                }
            }
        });
    }
    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // [START_EXCLUDE]

                            Bundle b = new Bundle();


                            b.putString("newphone",numberwithout);

                            Intent in = new Intent(context, Change_Phone.class);
                            in.putExtras(b);
                            context.startActivity(in);

                        } else {
                            // Sign in failed, display a message and update the UI

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                               // base.ifconnection(coordinatorLayout,"Invalid code");
                                Toast.makeText(context,"Invalid Code",Toast.LENGTH_LONG).show();
                            }

                        }
                    }
                });
    }

    @Override
    public void show() {
        super.show();
    }

    public void show(String message) {
        txtTitle.setText(message);
        show();
    }
}
