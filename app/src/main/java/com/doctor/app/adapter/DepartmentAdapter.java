package com.doctor.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Log;
import com.doctor.app.DoctorDetail;
import com.doctor.app.DoctorList;
import com.doctor.app.R;
import com.doctor.app.RegisterActivity;
import com.doctor.app.helper.AppConst;
import com.doctor.app.model.DepartmentPojo;
import com.doctor.app.model.DoctorPojo;
import com.doctor.app.model.M;
import com.doctor.app.view.CropSquareTransformation;
import com.squareup.picasso.Picasso;

import java.util.List;


public class DepartmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 1;
    private List<DepartmentPojo> list;
    Context context;
    String TAG="DepartmentAdapter";

    public DepartmentAdapter(List<DepartmentPojo> list, Context mcontext) {
        context = mcontext;
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.department_row, parent, false);
        return new ViewHolderPosts(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final DepartmentPojo model=list.get(position);
        final ViewHolderPosts itemview = (ViewHolderPosts) holder;

        itemview.tvname.setText(model.getName());
        Log.d(TAG,AppConst.dept_img_url+model.getDept_image());
        Picasso.with(context)
                .load(AppConst.dept_img_url+model.getDept_image())
                .placeholder(R.drawable.ic_default)
                .error(R.drawable.ic_default)
                .into(itemview.iv);
        itemview.lldept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                M.setDepartment(model.getId(),context);
                Intent it=new Intent(context,DoctorList.class);
                context.startActivity(it);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolderPosts extends RecyclerView.ViewHolder {
        TextView tvname;
        ImageView iv;
        LinearLayout lldept;

        public ViewHolderPosts(View itemView) {
            super(itemView);
            tvname = (TextView) itemView.findViewById(R.id.tvdept);
            iv=(ImageView)itemView.findViewById(R.id.ivdept);
            lldept=(LinearLayout)itemView.findViewById(R.id.lldept);
        }

    }

}
