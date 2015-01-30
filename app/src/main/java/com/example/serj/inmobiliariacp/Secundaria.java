package com.example.serj.inmobiliariacp;

import android.app.Activity;
import android.os.Bundle;

public class Secundaria extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secundaria);
        Detalle fragmentDetalle = (Detalle)getFragmentManager().findFragmentById(R.id.fragment_detalle);
        if(fragmentDetalle != null && fragmentDetalle.isInLayout()){
            int id = getIntent().getIntExtra(getString(R.string.tag_id), 0);
            fragmentDetalle.setDetalle(id);
        }

    }
}
