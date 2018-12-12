package obi.mapfind.details;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.widget.ProgressBar;
import android.widget.TextView;

import obi.mapfind.R;

public class ProgressBarClass extends Dialog {
    private final Context context;
    public final TextView txtTitle;
    ProgressBar pro;
    public ProgressBarClass(@NonNull final Context context) {
        super(context);
        this.context = context;

    /*-----------------------------------------------------------------
     |     CONFIGURE THE APPEARANCE OF THE DIALOG
     *-----------------------------------------------------------------*/
        setContentView(R.layout.dialog_otp);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        txtTitle = findViewById(R.id.act);
        pro = findViewById(R.id.progressBar);
        pro.setProgressTintList(ColorStateList.valueOf(Color.WHITE));
//        dialog.getWindow().setDimAmount(0.0f);
        int width = (int)(context.getResources().getDisplayMetrics().widthPixels*0.90);
        int height = (int)(context.getResources().getDisplayMetrics().heightPixels*0.80);
        getWindow().setLayout(width, height);
        getWindow().getAttributes().windowAnimations = R.style.AppTheme;
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


    /*-----------------------------------------------------------------
     |     FIND REFERENCE TO ALL VIEWS
     *-----------------------------------------------------------------*/

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
