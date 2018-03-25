package com.corelabsplus.getmethere.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.corelabsplus.getmethere.R;

public class SelectModeActivity extends AppCompatActivity {

    private RelativeLayout publicT, onDemandT, walkT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mode);

        publicT = (RelativeLayout) findViewById(R.id.public_transport);
        onDemandT = (RelativeLayout) findViewById(R.id.odemand_transport);
        walkT = (RelativeLayout) findViewById(R.id.walk_transport);

        publicT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectModeActivity.this, BusesActivity.class);
                startActivity(intent);
            }
        });
    }
}
