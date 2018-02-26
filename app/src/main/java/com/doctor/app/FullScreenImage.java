package com.doctor.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.doctor.app.helper.AppConst;
import com.doctor.app.util.ImageHelper;
import com.doctor.app.view.CropSquareTransformation;
import com.squareup.picasso.Picasso;

public class FullScreenImage extends AppCompatActivity {

    ImageView iv;
    Context context;
    String TAG="FullScreenImage";
    public static String imgurl;
    public static Boolean isserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context=FullScreenImage.this;

        iv=(ImageView)findViewById(R.id.img);

        if(isserver) {
            Picasso.with(context)
                    .load(AppConst.clinic_img_url + imgurl)
                    .placeholder(R.drawable.ic_user)
                    .error(R.drawable.ic_user)
                    .into(iv);
        }else{
            Bitmap bitmap = ImageHelper.getDesirableBitmap(imgurl, 100);
            iv.setImageBitmap(bitmap);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
