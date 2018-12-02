package obi.mapfind.details;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import obi.mapfind.Utils.BaseActivity;
import obi.mapfind.Utils.Constant;
import obi.mapfind.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
    SharedPreferences sp;
    SharedPreferences.Editor edit;
    public OTPDialogNewNumber(@NonNull final Context context) {
        super(context);
        this.context = context;
        base= new BaseActivity();
        sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        edit = sp.edit();
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

                         change_number_mysql();

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

    public void change_number_mysql() {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("u_id", base.base_u_id())
                .add("name", number)
                .build();
        Request request = new Request.Builder().url(Constant.ipadress + "check_phone_number.php").post(body).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                getOwnerActivity().runOnUiThread(new Runnable() {
                                             @Override
                                             public void run() {
                                                 Toast.makeText(context, "Action Failed",
                                                         Toast.LENGTH_SHORT).show();
                                             }
                                         }
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String myResponse = response.body().string();

                getOwnerActivity().runOnUiThread(new Runnable() {
                                             @Override
                                             public void run() {

                                                 JSONObject jso;
                                                 try {
                                                     jso = new JSONObject(myResponse);


                                                     if (jso.getString("status").contentEquals("1")) {

                                                         edit.putString("phone", numberwithout);
                                                         edit.apply();
                                                         Toast.makeText(context,jso.getString("message")
                                                                 ,Toast.LENGTH_SHORT).show();
                                                         Intent uo = new Intent(context, Profile.class);
                                                         context.startActivity(uo);
                                                     } else {
                                                        Toast.makeText(context,"Failed to change number try again later"
                                                                ,Toast.LENGTH_SHORT).show();
                                                         return;
                                                     }

                                                 } catch (JSONException e) {
                                                     e.printStackTrace();
                                                 }

                                             }
                                         }
                );
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
