package com.example.serj.inmobiliariacp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;

public class Lista extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private Adaptador ad;
    private Cursor cursor;
    private ListView lvLista;
    private Callbacks escuchador;
    private final Uri uri = Contrato.TablaInmueble.CONTENT_URI;
    private final int EDITAR_INMUEBLE = 1;

    public Lista() {}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        lvLista.setAdapter(ad);
        lvLista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cursor.moveToPosition(position);
                int idInmueble = cursor.getInt(0);
                escuchador.onItemSelected(idInmueble);
            }
        });
        registerForContextMenu(lvLista);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK){
            //Segun el resultado de otra actividad, realiza una acción determinada
            switch (requestCode){
                case EDITAR_INMUEBLE: {
                    int id = data.getIntExtra(getString(R.string.tag_id), 0);
                    ContentValues values = new ContentValues();
                    values.put(Contrato.TablaInmueble.LOCALIDAD, data.getStringExtra(getString(R.string.tag_localidad)));
                    values.put(Contrato.TablaInmueble.DIRECCION, data.getStringExtra(getString(R.string.tag_direccion)));
                    values.put(Contrato.TablaInmueble.TIPO, data.getStringExtra(getString(R.string.tag_tipo)));
                    values.put(Contrato.TablaInmueble.PRECIO, data.getIntExtra(getString(R.string.tag_precio), 0));

                    String where = Contrato.TablaInmueble._ID + " = ?";
                    String[] args = new String[]{id+""};
                    getActivity().getContentResolver().update(uri, values, where, args);
                    Toast.makeText(getActivity(),getString(R.string.inm_mod), Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lista, container, false);
        lvLista = (ListView)v.findViewById(R.id.listView);
        cursor = initCursor();
        ad = new Adaptador(this.getActivity(), R.layout.listadetalle, cursor);
        return v;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //Infla el menu que se visualiza al hacer longClick en un elemento del ListView
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menuopciones, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //Realiza la acción que se elija del menu contextual
        int id = item.getItemId();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int posicion = info.position;
        if(id == R.id.opEditar){
            return editar(posicion);
        }else if(id == R.id.opBorrar) {
            cursor.moveToPosition(posicion);
            int idInmueble = cursor.getInt(0);
            return borrar(idInmueble);
        }
        return super.onContextItemSelected(item);
    }

    private Cursor initCursor(){
        String[] projection = new String[]{Contrato.TablaInmueble._ID,
                Contrato.TablaInmueble.LOCALIDAD,
                Contrato.TablaInmueble.DIRECCION,
                Contrato.TablaInmueble.TIPO,
                Contrato.TablaInmueble.PRECIO,
                Contrato.TablaInmueble.SUBIDO};
        String sortOrder = Contrato.TablaInmueble.LOCALIDAD;
        return this.getActivity().getContentResolver().query(uri, projection, null, null, sortOrder);
    }

    public boolean anadir(){
        //Método que crea un AlertDialog con un layout personalizado y añade un inmueble nuevo
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View entradaTexto = inflater.inflate(R.layout.dialog_anadir, null);
        builder.setTitle(getString(R.string.nuevoinmueble));
        builder.setView(entradaTexto);
        final EditText et1 = (EditText)entradaTexto.findViewById(R.id.etLocalidad);
        final EditText et2 = (EditText)entradaTexto.findViewById(R.id.etDireccion);
        final EditText et3 = (EditText)entradaTexto.findViewById(R.id.etTipo);
        final EditText et4 = (EditText)entradaTexto.findViewById(R.id.etPrecio);
        builder.setPositiveButton(getString(R.string.btanadir), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Controla que los campos del EditText no estén vacios
                if (!et1.getText().toString().isEmpty() &&
                        !et2.getText().toString().isEmpty() &&
                        !et3.getText().toString().isEmpty() &&
                        !et4.getText().toString().isEmpty()) {
                    Inmueble nuevoInmueble = new Inmueble();
                    nuevoInmueble.setLocalidad(et1.getText().toString());
                    nuevoInmueble.setDireccion(et2.getText().toString());
                    nuevoInmueble.setTipo(et3.getText().toString());
                    nuevoInmueble.setPrecio(Integer.parseInt(et4.getText().toString()));

                    ContentValues values = new ContentValues();
                    values.put(Contrato.TablaInmueble.LOCALIDAD, nuevoInmueble.getLocalidad());
                    values.put(Contrato.TablaInmueble.DIRECCION, nuevoInmueble.getDireccion());
                    values.put(Contrato.TablaInmueble.TIPO, nuevoInmueble.getTipo());
                    values.put(Contrato.TablaInmueble.PRECIO, nuevoInmueble.getPrecio());
                    getActivity().getContentResolver().insert(uri, values);
                    cursor = initCursor();
                }else{
                    Toast.makeText(getActivity(), getString(R.string.campos_vacios), Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton(getString(R.string.bt_cancelar),null);
        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    private boolean borrar(final int idInmueble){
        //Método que crea un AlertDialog que nos permite borrar un Inmueble y sus fotos
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(R.string.dialog_title);
        alert.setMessage(R.string.dialog_message);
        alert.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                borrarFotosMemoria(idInmueble);
                String where = Contrato.TablaInmueble._ID + " = ?";
                String[] args = new String[]{idInmueble+""};
                getActivity().getContentResolver().delete(uri, where, args);
                Toast.makeText(getActivity(), getString(R.string.inm_del), Toast.LENGTH_SHORT).show();
            }
        });
        alert.setNegativeButton(R.string.no,null);
        AlertDialog dialog = alert.create();
        dialog.show();
        return true;
    }

    public void borrarFotosMemoria(int pos){
        // Borra todas las fotos almacenadas segun el id del elemento a borrar
        File[] allPhotos = getActivity().getExternalFilesDir(Environment.DIRECTORY_DCIM).listFiles();
        for(int i=0; i<allPhotos.length; i++){
            String photoId = allPhotos[i].getPath();
            if(photoId.contains(getString(R.string.inm)+ pos + "_")){
                allPhotos[i].delete();
            }
        }
    }

    public boolean editar(final int pos){
        //Método que lanza una actividad para obtener un resultado
        Intent nuevoIntent = new Intent(getActivity(), Editar.class);
        Bundle b = new Bundle();
        cursor.moveToPosition(pos);
        b.putInt(getString(R.string.tag_id), cursor.getInt(0));
        b.putString(getString(R.string.tag_localidad), cursor.getString(1));
        b.putString(getString(R.string.tag_direccion), cursor.getString(2));
        b.putString(getString(R.string.tag_tipo), cursor.getString(3));
        b.putInt(getString(R.string.tag_precio), cursor.getInt(4));
        nuevoIntent.putExtras(b);
        startActivityForResult(nuevoIntent, EDITAR_INMUEBLE);
        return true;
    }

    // LOADER

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this.getActivity(), uri, null, null, null, Contrato.TablaInmueble.LOCALIDAD +" collate localized asc");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ad.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ad.swapCursor(null);
    }

    // CALLBACKS

    public interface Callbacks {
        //Callback for when an item has been selected.
        public void onItemSelected(int id);
    }

    public void setEscuchador(Callbacks escuchador) {
        this.escuchador = escuchador;
    }

}
