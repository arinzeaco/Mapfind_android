package obi.mapfind.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import obi.mapfind.BaseActivity;
import obi.mapfind.Constant;
import obi.mapfind.EmptyRecyclerViewAdapter;
import obi.mapfind.Other_Profile;
import obi.mapfind.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Fragment_contacts extends Fragment {
    private RecyclerView recyclerView;
    RelativeLayout errors;   private List<Chart_Getter> getter;
    Chart_Adapter chart_Adapter;
    BaseActivity ba;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        final RelativeLayout x = (RelativeLayout) inflater.inflate(R.layout.fragment_contacts, container, false);
        recyclerView = x.findViewById(R.id.recycler_view);
        errors= (RelativeLayout) x.findViewById(R.id.errors);
        errors.setVisibility(View.GONE);
        ba= new BaseActivity();
        getter = new ArrayList<>();
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
       // recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        Getliked(((BaseActivity)getActivity()).base_u_id());
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {

                        Bundle b = new Bundle();

                        b.putString("userid", String.valueOf(getter.get(position).getId()));


                        Intent in = new Intent(getActivity(), Other_Profile.class);

                        in.putExtras(b);
                        startActivity(in);

                    }
                })
        );
        return x;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Menu 1");
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
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
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
                if (getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
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
                                        detailList.setLiked("yes");
                                        getter.add(detailList);


                                        chart_Adapter = new Chart_Adapter(getActivity(),getter);
                                        recyclerView.setAdapter(chart_Adapter);
                                        chart_Adapter.notifyDataSetChanged();

                                    }
                                } else if (jso.getString("success").contentEquals("2")) {
                                EmptyRecyclerViewAdapter    empt = new EmptyRecyclerViewAdapter("You have not item for sell");
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