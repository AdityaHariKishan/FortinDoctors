package com.doctor.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.doctor.app.helper.ConnectionDetector;
import com.doctor.app.model.M;

public class SplashScreen extends AppCompatActivity {

    ConnectionDetector connectionDetector;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        context=SplashScreen.this;

        connectionDetector = new ConnectionDetector(this);
//        startActivity(new Intent(SplashScreen.this,TestActivity.class));
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if(!connectionDetector.isConnectingToInternet())
                {
                    Toast.makeText(SplashScreen.this,"Internet network not Avaliable",Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    if (M.getID(SplashScreen.this).toString().trim().length() <= 0) {
                        Intent it = new Intent(SplashScreen.this, LoginActivity.class);
                        finish();
                        startActivity(it);
                    } else {
                        if(M.getRole(context).equals("doctor")){
                            Intent it = new Intent(SplashScreen.this, MainActivity.class);
                            finish();
                            startActivity(it);
                        }else {
                            if (M.getCity(context) != null && M.getCity(context).trim().length() > 0) {
                                Intent it = new Intent(SplashScreen.this, MainActivity.class);
                                finish();
                                startActivity(it);
                            } else {
                                Intent it = new Intent(SplashScreen.this, SelectCity.class);
                                finish();
                                startActivity(it);
                            }
                        }
                    }
                }
            }

        }, 1000);
    }
}
