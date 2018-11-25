package obi.mapfind.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import obi.mapfind.CircleTransform;
import obi.mapfind.Other_Profile;
import obi.mapfind.R;
import obi.mapfind.home.InfoWindowData;

public class CustomInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public CustomInfoWindowGoogleMap(Context ctx){
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.map_custom_infowindow, null);

        TextView name = view.findViewById(R.id.name);
        TextView address = view.findViewById(R.id.address);
        TextView phone = view.findViewById(R.id.phone);
        ImageView img = view.findViewById(R.id.pic);
        TextView seemore= view.findViewById(R.id.seemore);

        InfoWindowData infoWindowData = (InfoWindowData) marker.getTag();
        name.setText(marker.getTitle());
        address.setText(infoWindowData.getAddress());
      phone.setText(infoWindowData.getPhone());

            if (!infoWindowData.getImage().contentEquals("")) {
                Picasso.get()
                        .load(infoWindowData.getImage())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.error)
                        .resize(150, 150)
                        .transform(new CircleTransform())
                        .into(img);
            }
//       view.setOnClickListener(v -> {
//           Toast.makeText(context,"fddf",Toast.LENGTH_SHORT).show();
//           Bundle b = new Bundle();
//           b.putString("userid", infoWindowData.getUserid());
//           Intent in = new Intent(context, Other_Profile.class);
//           in.putExtras(b);
//           context.startActivity(in);
//       });

        return view;
    }
}
