package com.doctor.app.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.doctor.app.R;
import com.doctor.app.helper.AppConst;
import com.doctor.app.model.Clinic_image;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<Clinic_image> mResources;

    public ViewPagerAdapter(Context mContext,List<Clinic_image> list) {
        this.mContext = mContext;
        this.mResources = list;
    }

    @Override
    public int getCount() {
        return mResources.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.fullimage_row, container, false);

        Clinic_image model = mResources.get(position);

        ImageView tv=(ImageView) itemView.findViewById(R.id.fullimage);
        tv.setScaleType(ImageView.ScaleType.FIT_XY);
        // Log.d("photo","url:"+AppConst.MAIN+model);
        Picasso.with(mContext)
                .load(AppConst.clinic_img_url+model.getImage_name())
                .placeholder(R.drawable.default_image)
                .error(R.drawable.default_image)
                .into(tv);
        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}