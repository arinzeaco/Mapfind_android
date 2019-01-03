package obi.mapfind.Utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.ProgressBar;


import obi.mapfind.R;

public class MapfindLoadingDialog extends Dialog {

    public MapfindLoadingDialog(Context context) {
        super(context);
        setTitle(null);
        setCancelable(true);
        setOnCancelListener(null);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_custom_progress);

        ProgressBar pb = findViewById(R.id.progressBar1);
        final Animation myAnim = AnimationUtils.loadAnimation(context, R.anim.heart_beat);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        BounceInterpolator interpolator = new BounceInterpolator(0.1, 40);
        myAnim.setInterpolator(interpolator);
        pb.setVisibility(View.VISIBLE);
    }
}