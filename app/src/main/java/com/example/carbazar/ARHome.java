package com.example.carbazar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ARHome extends AppCompatActivity  implements BottomNavigationView.OnNavigationItemSelectedListener{

    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private RelativeLayout relativeLayoutBMW;
    private RelativeLayout relativeLayoutPorsch;
    private RelativeLayout relativeLayoutBenz;
    private RelativeLayout relativeLayoutElentra;

    private RelativeLayout relativeLayoutAcura;
    private RelativeLayout relativeLayoutAcuraBlue;
    private RelativeLayout relativeLayoutPorscheRed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arhome);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        android.view.Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("AR Models");

        relativeLayoutBMW = findViewById(R.id.bmw_RL);
        relativeLayoutPorsch = findViewById(R.id.porsch_RL);
        relativeLayoutBenz = findViewById(R.id.benz_RL);
        relativeLayoutElentra = findViewById(R.id.elantra_RL);

        relativeLayoutAcura = findViewById(R.id.acura_RL);
        relativeLayoutAcuraBlue = findViewById(R.id.acurablue_RL);
        relativeLayoutPorscheRed = findViewById(R.id.porsche_red_RL);

        relativeLayoutBMW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ARHome.this, ARDetailed.class);
                intent.putExtra("key", "BMW");
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        relativeLayoutPorsch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ARHome.this, ARDetailed.class);
                intent.putExtra("key", "Porsch");
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        relativeLayoutBenz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ARHome.this, ARDetailed.class);
                intent.putExtra("key", "Benz");
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        relativeLayoutElentra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ARHome.this, ARDetailed.class);
                intent.putExtra("key", "Elentra");
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        relativeLayoutAcura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ARHome.this, ARDetailed.class);
                intent.putExtra("key", "acura");
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        relativeLayoutAcuraBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ARHome.this, ARDetailed.class);
                intent.putExtra("key", "acurablue");
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        relativeLayoutPorscheRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ARHome.this, ARDetailed.class);
                intent.putExtra("key", "porsche_red");
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.ar_vr_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent1 = new Intent(ARHome.this, MainActivity.class);
                startActivity(intent1);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                return true;

            case R.id.vrTour:
                Intent intent2 = new Intent(ARHome.this, viewInVR.class);
                startActivity(intent2);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent1 = new Intent(ARHome.this, MainActivity.class);
        startActivity(intent1);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){

            case R.id.home_nav:
                //
                Intent intent2 = new Intent(ARHome.this, MainActivity.class);
                startActivity(intent2);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            case R.id.post_nav:
                //
                Intent intent1 = new Intent(ARHome.this, postAd.class);
                startActivity(intent1);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            case R.id.chatbot_nav:
                //
                Intent intent3 = new Intent(ARHome.this, chatbot.class);
                startActivity(intent3);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            case R.id.ar_vr_nav:
                //
                return true;

            case R.id.account_nav:
                //
                Intent intent = new Intent(ARHome.this, account.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;


        }
        return false;
    }
}
