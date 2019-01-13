package obi.mapfind.details;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import obi.mapfind.Other_Profile;
import obi.mapfind.Utils.BaseActivity;
import obi.mapfind.Utils.CircleTransform;
import obi.mapfind.Utils.Constant;
import obi.mapfind.MainActivity;
import obi.mapfind.R;
import obi.mapfind.Utils.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Profile extends BaseActivity {

    EditText name,email,phone,
            brief;
    TextView address;
    String urll; ImageView pro;
    TextView  right_text;
    SharedPreferences sp;
    Bitmap bitmap;  BitmapDrawable background;
    RelativeLayout coordinatorLayout;
    LinearLayout addr;
    SharedPreferences.Editor edit;
    private SearchableSpinner profession;
    private ArrayAdapter professionAdapter;
     String userChoosenTask; ImageButton clear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        sp = PreferenceManager
                .getDefaultSharedPreferences(Profile.this);
        edit = sp.edit();
        Profile_data();
        initToolbar("Profile","Save");
      //  clear= findViewById(R.id.clear);

        right_text= findViewById(R.id.right_text);
        right_text.setOnClickListener(v -> {
            if(!isOnline(Profile.this)){
                ifconnection(coordinatorLayout,"No internet connection");
                return;
            }
            update_details();
        });

        pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        profession.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getSelectedItem().toString().contentEquals("     --Select nothing--")){
                    int pos = new ArrayList<>(Arrays.asList(Constant.professionlist)).indexOf("");

                    profession.setSelection(pos);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                // sometimes you need nothing here
            }
        });
//        FirebaseAuth fAuth = FirebaseAuth.getInstance();
//        fAuth.signOut();
//        SharedPreferences.Editor edit = sp.edit();
//
//        SharedPreferences settings = Profile.this.getSharedPreferences("PreferencesName", Context.MODE_PRIVATE);
//        settings.edit().clear().apply();
//        edit.apply();
//        Intent uo = new Intent(Profile.this, MainActivity.class);
//
//        startActivity(uo);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // handle result of CropImageActivity

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), result.getUri());
                    background = new BitmapDrawable(bitmap);
                    saveImage(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                ifconnection(coordinatorLayout,"Cropping failed: " + result.getError());

            }
        }
    }
    private void selectImage() {
        final CharSequence[] items = { "Change Image","Remove Image", "View Image",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, (dialog, item) -> {
            boolean result= Utility.checkPermission(Profile.this);

            if (items[item].equals("Change Image")) {
                userChoosenTask ="Take Photo";
                if(result)
                    croper();
            } else if (items[item].equals("Remove Image")) {
                if(result)
                    removeimage();
            } else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }else if (items[item].equals("View Image")){

                Bundle b = new Bundle();
              b.putString("url",base_avatar());
                Intent in = new Intent(Profile.this, View_image.class);
                in.putExtras(b);
                startActivity(in);
            }
        });
        builder.show();
    }
    private void removeimage() {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("u_id", base_u_id())
                .build();
        Request request = new Request.Builder().url(Constant.ipadress+"remove_avatar.php").post(body).build();
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

                Profile.this.runOnUiThread(() -> {

                    JSONObject jso;

                    try {
                        jso = new JSONObject(myResponse);


                        if (jso.getString("status").contentEquals("1")) {
                            edit.putString("avatar","");
                            edit.apply();

                                    Picasso .get()
                                            .load(R.drawable.placeholder)
                                            .placeholder(R.drawable.placeholder)
                                            .resize(120, 120)
                                            .transform(new CircleTransform())
                                            .into(pro);

                        }else{
                            ifconnection(coordinatorLayout,"failed to remove picture");
                            return;

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                );
            }
        });
    }

    public void croper(){
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityTitle("")
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setCropMenuCropButtonTitle("Done")
                .setRequestedSize(400, 400)
                .setCropMenuCropButtonIcon(R.drawable.arrow)
                .start(Profile.this);
    }


    public void Profile_data(){
        coordinatorLayout = findViewById(R.id
                .layouts);
        pro =  findViewById(R.id.propic);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone= findViewById(R.id.phone);
        address = findViewById(R.id.address);
        addr = findViewById(R.id.add);
        profession = findViewById(R.id.profession);
        //  interest = findViewById(R.id.interest);
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
//        phone.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent in = new Intent(Profile.this, Change_Phone_Mysql.class);
//                startActivity(in);
//
//            }
//        });
        if(in.hasExtra("address")){
            address.setText(b.getString("address"));
        }else{
            address.setText(base_address());
        }
        addr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Profile.this, MapsActivity.class);
                startActivity(in);

            }
        });

        brief.setText(base_brief());
        professionAdapter = new ArrayAdapter<>(Profile.this, android.R.layout.simple_spinner_dropdown_item,  Constant.professionlist);
        profession.setAdapter(professionAdapter);

        int pos = new ArrayList<String>(Arrays.asList(Constant.professionlist)).indexOf(base_profession());

        profession.setSelection(pos);
        if(!base_avatar().isEmpty() && !base_avatar().contentEquals("")) {
            if (isOnline(getApplicationContext())) {
                Picasso .get()
                        .load(base_avatar())
                        .placeholder(R.drawable.placeholder)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
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
                .add("profession",profession.getSelectedItem().toString())
                .add("brief",brief.getText().toString())
                .add("interest",base_interest())
                .add("phone",phone.getText().toString())
                .add("address",address.getText().toString())
                .add("latitude",base_latitude())
                .add("longitude",base_longitude())
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    croper();
                } else {
                    //code for deny
                }
                break;
        }
    }

    public void gets(File imagepath){
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("u_id",base_u_id())
                .addFormDataPart("image", String.valueOf(imagepath),
                        RequestBody.create(MediaType.parse("image/*jpg"), imagepath))
                .build();

        Request request = new Request.Builder().url(Constant.ipadress+"update_avatar.php")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

                Profile.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ifconnection(coordinatorLayout,"Failed to change image");

                    }}
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String myResponse = response.body().string();
                Log.i( "response",myResponse );
                Profile.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jso;
                        try {
                            jso = new JSONObject(myResponse);


                            if (jso.getString("status").contentEquals("1")) {

                                edit.putString("avatar", jso.getString("data"));
                                edit.apply();

                                if (loggedin().contentEquals("yes")) {
                                    if (!(jso.getString("data").contentEquals(""))) {
                                      Log.i("prodata",jso.getString("data"));
                                        Picasso.get()
                                                .load(jso.getString("data"))
                                                .placeholder(R.drawable.placeholder)
                                                .error(R.drawable.error)
                                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                                .resize(150, 150)
                                                .transform(new CircleTransform())
                                                .into(pro);
                                    }

                                }

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

    public void saveImage(Bitmap bmp) {
//        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
//        File myDir = new File(root + "/saved_images_1");
//        myDir.mkdirs();
//        Random generator = new Random();
//        int n = 10000;
//        n = generator.nextInt(n);
//        String fname = "Image-" + n + ".jpg";
//        File file = new File(myDir, fname);
//        if (file.exists())
//            file.delete();
   // }
        try{
            int size = 0;
            ByteArrayOutputStream bos = new ByteArrayOutputStream(size);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] bArr = bos.toByteArray();
            bos.flush();
            bos.close();

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";

            FileOutputStream fos = openFileOutput(imageFileName + ".jpeg", Context.MODE_PRIVATE);
            fos.write(bArr);
            fos.flush();
            fos.close();

            File mFile = new File(getFilesDir().getAbsolutePath(), imageFileName + ".jpeg");
            gets(mFile.getAbsoluteFile());
            Log.i("ppath", String.valueOf(mFile.getAbsoluteFile()));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
