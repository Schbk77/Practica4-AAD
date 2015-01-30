package com.example.serj.inmobiliariacp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class Principal extends Activity implements Lista.Callbacks{

    private boolean horizontal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return editarUsuario();
            case R.id.anadir_inmueble:
                Lista f = (Lista)getFragmentManager().findFragmentById(R.id.fragment_lista);
                return f.anadir();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Lista fragmentLista = (Lista)getFragmentManager().findFragmentById(R.id.fragment_lista);
        fragmentLista.setEscuchador(this);
        Detalle fragmentDetalle = (Detalle)getFragmentManager().findFragmentById(R.id.fragment_detalle);
        horizontal = fragmentDetalle != null && fragmentDetalle.isInLayout();
    }

    private boolean editarUsuario() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        final View entradaTexto = inflater.inflate(R.layout.usuario, null);
        builder.setTitle(getString(R.string.action_settings));
        builder.setView(entradaTexto);
        final EditText et1 = (EditText)entradaTexto.findViewById(R.id.etUsuario);
        String usuario = getUsuarioSharedPreferences();
        et1.setText(usuario);
        builder.setPositiveButton(getString(R.string.bt_aceptar), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                setUsuarioSharedPreferences(et1.getText().toString());
            }
        });
        builder.setNegativeButton(getString(R.string.bt_cancelar),null);
        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    private String getUsuarioSharedPreferences() {
        SharedPreferences sp = getSharedPreferences(getString(R.string.tag_usuario), Context.MODE_PRIVATE);
        return sp.getString(getString(R.string.tag_usuario), "");
    }

    private void setUsuarioSharedPreferences(String usuario) {
        SharedPreferences sp = getSharedPreferences(getString(R.string.tag_usuario), Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(getString(R.string.tag_usuario), usuario);
        ed.apply();
    }

    // CALLBACKS

    @Override
    public void onItemSelected(int id) {
        if(horizontal) {
            // Mostrar detalle en fragmento Detalle
            ((Detalle)getFragmentManager().findFragmentById(R.id.fragment_detalle)).setDetalle(id);
        } else {
            // Mostrar detalle en actividad Secundaria
            Intent intent = new Intent(Principal.this, Secundaria.class);
            intent.putExtra(getString(R.string.tag_id), id);
            startActivity(intent);
        }
    }
}
