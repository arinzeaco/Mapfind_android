package obi.mapfind;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import obi.mapfind.Utils.BaseActivity;
import obi.mapfind.Utils.Constant;
import obi.mapfind.Utils.EmptyRecyclerViewAdapter;
import obi.mapfind.fragment.Chart_Adapter;
import obi.mapfind.fragment.Chart_Getter;
import obi.mapfind.Utils.RecyclerItemClickListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class User_Contact extends BaseActivity {
    private RecyclerView recyclerView;
    RelativeLayout errors;   private List<Chart_Getter> getter;
    Chart_Adapter chart_Adapter;
    BaseActivity ba;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts);
        initToolbar("Contacts","");
        recyclerView = findViewById(R.id.recycler_view);
        errors= (RelativeLayout) findViewById(R.id.errors);
        errors.setVisibility(View.GONE);

        getter = new ArrayList<>();
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(User_Contact.this);
        recyclerView.setLayoutManager(mLayoutManager);
        // recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        Getliked(base_u_id());
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(User_Contact.this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {

                        Bundle b = new Bundle();

                        b.putString("userid", String.valueOf(getter.get(position).getId()));


                        Intent in = new Intent(User_Contact.this, Other_Profile.class);

                        in.putExtras(b);
                        startActivity(in);

                    }
                })
        );
    }

    public void Getliked(String userid) {

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("u_id", userid)
                .build();
        Request request = new Request.Builder().url(Constant.ipadress+"chart.php").post(body).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                // Toast.makeText(getActivity(), getId(),
                //       Toast.LENGTH_SHORT).show();
                if (User_Contact.this!=null) {
                    User_Contact.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            errors.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String myResponse = response.body().string();
                Log.i("datass",myResponse);
                if (User_Contact.this!=null) {
                    User_Contact.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            JSONObject jso;
                            try {
                                jso = new JSONObject(myResponse);

                                if (jso.getString("status").contentEquals("1")) {
                                    JSONArray details = jso.getJSONArray("data");
                                    for (int i = 0; i < details.length(); i++) {
                                        Chart_Getter detailList = new Chart_Getter();
                                        String userid = details.getJSONObject(i).getString("u_id");
                                        String profession = details.getJSONObject(i).getString("profession");
//                                String email = details.getString("email");
//                                String interest = details.getString("interest");
                                      //  String dist =details.getJSONObject(i).getString("dist");
                                        String name =details.getJSONObject(i).getString("name");
                                        String avatar = details.getJSONObject(i).getString("avatar");
                                        String phone = details.getJSONObject(i).getString("phone");
                                        String address = details.getJSONObject(i).getString("address");
                                        Double longitude =details.getJSONObject(i).getDouble("longitude");
                                        Double latitude =details.getJSONObject(i).getDouble("latitude");


                                        detailList.setId(userid);
                                        detailList.setName(name);
                                        detailList.setImage(avatar);
                                        detailList.setProfession(profession);
                                        detailList.setAddress(address);
                                      //  detailList.setDist(dist);
                                        detailList.setLiked("yes");
                                        getter.add(detailList);


                                        chart_Adapter = new Chart_Adapter(User_Contact.this,getter);
                                        recyclerView.setAdapter(chart_Adapter);
                                        chart_Adapter.notifyDataSetChanged();

                                    }
                                } else if (jso.getString("success").contentEquals("2")) {
                                    EmptyRecyclerViewAdapter empt = new EmptyRecyclerViewAdapter("You have not item for sell");
                                    recyclerView.setAdapter(empt);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
            }
        });
    }
}