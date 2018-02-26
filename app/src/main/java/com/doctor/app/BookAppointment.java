package com.doctor.app;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.doctor.app.adapter.TimingAdapter;
import com.doctor.app.chips.ChipCloud;
import com.doctor.app.chips.ChipListener;
import com.doctor.app.helper.AppConst;
import com.doctor.app.model.DoctorDetailPojo;
import com.doctor.app.model.M;
import com.doctor.app.model.SuccessPojo;
import com.doctor.app.model.TimingSlots;
import com.doctor.app.webservice.APIAuthentication;
import com.doctor.app.webservice.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarListener;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class BookAppointment extends AppCompatActivity implements View.OnClickListener {

    EditText etnote;
    LinearLayout ll;
    TextView tvdoctor,tvnodoctor;
    Button btnbook;
    HorizontalCalendar horizontalCalendar;
    ChipCloud chipCloud;
    Context context;
    boolean isscroll = false;
    String TAG="BookAppointment",dt="",day="";
    DoctorDetailPojo dr;
    DatePickerDialog.OnDateSetListener d;
    SimpleDateFormat dtfmt=new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat dayfmt=new SimpleDateFormat("EEEE");
    SimpleDateFormat tmfmt=new SimpleDateFormat("HH:mm");
    SimpleDateFormat defaulttmfmt=new SimpleDateFormat("HH:mm:ss");
    String tm="",note="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context=BookAppointment.this;
        checkPermission();
        if(AppConst.selDoctor==null){
            finish();
        }else {
            dr=AppConst.selDoctor;
            initview();
        }
    }

    private void initview() {

        etnote=(EditText)findViewById(R.id.etnote);
        ll=(LinearLayout)findViewById(R.id.ll);
        ll.setVisibility(View.GONE);
        tvdoctor=(TextView)findViewById(R.id.tvdoctor);
        tvnodoctor=(TextView)findViewById(R.id.tvnodoctor);
        btnbook=(Button)findViewById(R.id.btnbook);
        chipCloud = (ChipCloud)findViewById(R.id.chip_cloud);

        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 6);

        horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView)
                .startDate(new Date())
                .endDate(endDate.getTime())
                .build();

        tvdoctor.setText(dr.getDoctor_name());

        btnbook.setOnClickListener(this);


        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(final Date date, int position) {

                if(!isscroll) {
                    dt = dtfmt.format(date.getTime());
                    day = dayfmt.format(date.getTime());
                    tm = "";
                    getTime();

                }
            }

            @Override
            public void onCalendarScroll(HorizontalCalendarView calendarView,
                                         int dx, int dy) {


                calendarView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);

                        isscroll = true;
                        Log.d("here...", "scroll----"+newState);
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                            isscroll = false;


                        }

                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        isscroll = true;
                    }
                });
            }

            @Override
            public boolean onDateLongClicked(Date date, int position) {
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
       if(v==btnbook){
            if(tm.trim().length()<=0){
                Toast.makeText(context,"Select Timing",Toast.LENGTH_SHORT).show();
            }else{
                if(etnote.getText().toString().length()<=0)
                    note="";
                else
                    note=etnote.getText().toString();
                Log.d(TAG,dr.getId()+" "+dt+" "+tm);
                book();
            }
        }
    }

    private void book() {


        M.showLoadingDialog(context);
        APIAuthentication mAuthenticationAPI = Service.createService(context,APIAuthentication.class);
        Call<SuccessPojo> call = mAuthenticationAPI.bookAppointment(dr.getId(),dt,tm,M.getID(context),note);
        call.enqueue(new retrofit2.Callback<SuccessPojo>() {
            @Override
            public void onResponse(Call<SuccessPojo> call, Response<SuccessPojo> response) {
                Log.d("response:","data:"+response);
                M.hideLoadingDialog();
                if (response.isSuccessful()) {
                    SuccessPojo pojo = response.body();

                    if(pojo.getSuccess().equals("1")){
                        Intent it=new Intent(context,AppointmentStatus.class);
                        it.putExtra("date",dt);
                        it.putExtra("time",tm);
                        finish();
                        startActivity(it);
                    }else{

                    }

                } else {
                    int statusCode = response.code();
                    M.hideLoadingDialog();
                    ResponseBody errorBody = response.errorBody();
                }
            }

            @Override
            public void onFailure(Call<SuccessPojo> call, Throwable t) {
                Log.d("response:","fail:"+t.getMessage());
                M.hideLoadingDialog();
            }
        });
    }

    private void getTime() {
//        M.cancelableshowLoadingDialog(context);
        APIAuthentication mAuthenticationAPI = Service.createService(context,APIAuthentication.class);
        Call<TimingSlots> call = mAuthenticationAPI.getTiming(dr.getId(),dt,day);
        call.enqueue(new retrofit2.Callback<TimingSlots>() {
            @Override
            public void onResponse(Call<TimingSlots> call, Response<TimingSlots> response) {
                Log.d("response:","data:"+response);
//                M.hidecancelLoadingDialog();
                if (response.isSuccessful()) {
                    TimingSlots pojo = response.body();

                    if(pojo.getSuccess().equals("1")){
                        ll.setVisibility(View.VISIBLE);
                        tvnodoctor.setVisibility(View.GONE);
                        btnbook.setVisibility(View.VISIBLE);
                        chipCloud.setVisibility(View.VISIBLE);
                        String[] tlist=pojo.getTiming().split(",");
                        setchips(tlist);
                    }else{
                        ll.setVisibility(View.VISIBLE);
                        tvnodoctor.setVisibility(View.VISIBLE);
                        btnbook.setVisibility(View.GONE);
                        chipCloud.setVisibility(View.GONE);
                    }
                } else {
                    int statusCode = response.code();

                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();
                }
            }

            @Override
            public void onFailure(Call<TimingSlots> call, Throwable t) {
                Log.d("response:","fail:"+t.getMessage());
//                M.hidecancelLoadingDialog();
                // handle execution failures like no internet connectivity
            }
        });
    }

    public void setchips(final String[] list)
    {
        final ArrayList<String> wlist = new ArrayList<String>();
        String array[] = null;

        for(String word: list) {
            String w = word;
            Date d=null;
            try {
                d=defaulttmfmt.parse(w);
                wlist.add(tmfmt.format(d));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        String[] namesArr = new String[wlist.size()];
        for (int i = 0; i < wlist.size(); i++) {
            namesArr[i] = wlist.get(i);
        }

        new ChipCloud.Configure()
                .chipCloud(chipCloud)
                .selectedColor(Color.parseColor("#fcd736"))
                .selectedFontColor(Color.parseColor("#000000"))
                .deselectedColor(Color.parseColor("#e1e1e1"))
                .deselectedFontColor(Color.parseColor("#ffffff"))
                .selectTransitionMS(500)
                .deselectTransitionMS(250)
                .mode(ChipCloud.Mode.SINGLE)
                .labels(namesArr)
                .allCaps(false)
                .gravity(ChipCloud.Gravity.CENTER)
                .textSize(getResources().getDimensionPixelSize(R.dimen.default_textsize))
                .verticalSpacing(getResources().getDimensionPixelSize(R.dimen.vertical_spacing))
                .minHorizontalSpacing(getResources().getDimensionPixelSize(R.dimen.min_horizontal_spacing))
                .chipListener(new ChipListener() {
                    @Override
                    public void chipSelected(int index) {
                        //...

                        tm = list[index];
                    }
                    @Override
                    public void chipDeselected(int index) {
                        //...
                        tm = "";
                    }
                })
                .build();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.WRITE_CALENDAR},102);
        } else {
        }
    }
}
