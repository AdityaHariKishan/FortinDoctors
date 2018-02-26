package com.doctor.app.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.util.Log;

import com.doctor.app.FullScreenImage;
import com.doctor.app.R;
import com.doctor.app.helper.AppConst;
import com.doctor.app.model.Clinic_image;
import com.doctor.app.model.M;
import com.doctor.app.model.SuccessPojo;
import com.doctor.app.util.ImageHelper;
import com.doctor.app.webservice.DoctorAPI;
import com.doctor.app.webservice.Service;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.ItemViewHolder>
{

    protected ArrayList<Clinic_image> mImageIds = new ArrayList<Clinic_image>();
    Context context;
    String TAG="RecyclerListAdapter";

    public RecyclerListAdapter(Context context, ArrayList<Clinic_image> imagePaths) {

        this.mImageIds = imagePaths;
        this.context=context;
//          mItems.addAll(Arrays.asList(context.getResources().getStringArray(R.array.dummy_items)));
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        final Clinic_image imagePath = this.mImageIds.get(position);
        if(imagePath.getIsserver()){
            Picasso.with(context)
                    .load(AppConst.clinic_img_url+imagePath.getImage_name())
                    .placeholder(R.drawable.ic_user)
                    .error(R.drawable.ic_user)
                    .into(holder.handleView);
        }else {
            Bitmap bitmap = ImageHelper.getDesirableBitmap(imagePath.getImage_name(), 100);
            holder.handleView.setImageBitmap(bitmap);
        }
        holder.handleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(context, FullScreenImage.class);
                FullScreenImage.imgurl=imagePath.getImage_name();
                FullScreenImage.isserver=imagePath.getIsserver();
                context.startActivity(it);
            }
        });
        holder.ivdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imagePath.getIsserver()) {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(context);
                    } else {
                        builder = new AlertDialog.Builder(context);
                    }
                    builder.setTitle("Delete Clinic Image")
                            .setMessage("Are you sure you want to delete this clinic image?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    delImage(imagePath.getId(),position);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .show();

                }else{
                    mImageIds.remove(position);
                    notifyDataSetChanged();
                }
            }
        });
    }

    public void onAdd(Clinic_image newThing){
        this.mImageIds.add(newThing);
    }
    public void removeAllItems(){
        this.mImageIds = new ArrayList<Clinic_image>();
    }

    public ArrayList<Clinic_image> getItems(){
        return this.mImageIds;
    }

    @Override
    public int getItemCount() {
        return mImageIds.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder
    {
        ImageView ivdelete;
        public final ImageView handleView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            handleView = (ImageView) itemView.findViewById(R.id.handle);
            ivdelete=(ImageView)itemView.findViewById(R.id.ivdelete);
        }
    }

    private void delImage(String id, final int pos) {

        M.showLoadingDialog(context);
        DoctorAPI mAuthenticationAPI = Service.createService(context,DoctorAPI.class);
        Call<SuccessPojo> call = mAuthenticationAPI.deleteImage(id);
        call.enqueue(new retrofit2.Callback<SuccessPojo>() {
            @Override
            public void onResponse(Call<SuccessPojo> call, Response<SuccessPojo> response) {
                Log.d("response:","data:"+response);
                // response.isSuccessful() is true if the response code is 2xx
                if (response.isSuccessful()) {
                    SuccessPojo pojo = response.body();
                    M.hideLoadingDialog();
                    if(pojo.getSuccess().equals("1")){
                        mImageIds.remove(pos);
                        notifyDataSetChanged();
                    }
                } else {
                    int statusCode = response.code();
                    M.hideLoadingDialog();
                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();
                }
            }

            @Override
            public void onFailure(Call<SuccessPojo> call, Throwable t) {
                Log.d("response:","fail:"+t.getMessage());
                M.hideLoadingDialog();
                // handle execution failures like no internet connectivity
            }
        });
    }
}
