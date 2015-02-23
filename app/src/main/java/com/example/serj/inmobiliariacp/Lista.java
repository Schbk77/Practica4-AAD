package com.example.serj.inmobiliariacp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
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

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

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
        }else if(id == R.id.opSubir) {
            if(cursor.getInt(5) == 0){
                cursor.moveToPosition(posicion);
                Inmueble i = new Inmueble(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4),
                        0);
                return subir(i);
            } else {
                Toast.makeText(getActivity(),getString(R.string.inm_sync), Toast.LENGTH_SHORT).show();
            }

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

    private ArrayList<String> getFotos(String id){
        File[] allPhotos = getActivity().getExternalFilesDir(Environment.DIRECTORY_DCIM).listFiles();
        ArrayList<String> fotos = new ArrayList<String>();
        for(int i=0; i<allPhotos.length; i++){
            String photoId = allPhotos[i].getPath();
            if(photoId.contains(getString(R.string.inm)+ id + "_")){
                fotos.add(photoId);
            }
        }
        return fotos;
    }

    public boolean subir(Inmueble inmueble){
        new Upload().execute(inmueble);
        return true;
    }

    private String getUsuarioSharedPreferences() {
        SharedPreferences sp = getActivity().getSharedPreferences(getString(R.string.tag_usuario), Context.MODE_PRIVATE);
        return sp.getString(getString(R.string.tag_usuario), "");
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

    // SUBIR A HIBERNATE

    class Upload extends AsyncTask<Inmueble, Void, String> {

        private final String BASE_SERVIDOR = "http://192.168.1.16:8080/InmobiliariaHibernate/";
        private final String control = "control?target=inmueble&op=insert&action=opa";
        private final String controlsubir = "controlsubir?redirect=false";
        private ArrayList<String> archivos = new ArrayList<String>();
        private String archivoASubir = null;


        @Override
        protected String doInBackground(Inmueble... params) {
            String inmueble = params[0].getInmueble(getUsuarioSharedPreferences());
            String id = String.valueOf(params[0].getId());
            String respuesta = "";
            // Actualizar subido = 1
            ContentValues values = new ContentValues();
            values.put(Contrato.TablaInmueble.SUBIDO, 1);
            String where = Contrato.TablaInmueble._ID + " = ?";
            String[] args = new String[]{id};
            getActivity().getContentResolver().update(uri, values, where, args);
            // Añadir inmueble + usuario
            String idInsertado = post(BASE_SERVIDOR + control, inmueble).trim();
            // Coger todas las fotos de ese inmueble y subirlas
            archivos = getFotos(id);
            for (String archivo : archivos){
                archivoASubir = archivo;
                respuesta = postFile(BASE_SERVIDOR + controlsubir, "archivo", archivoASubir, idInsertado);
            }
            return respuesta.trim();
        }

        @Override
        protected void onPostExecute(String s) {
            Log.v("RESPUESTA:", s);
            if(s.equals("1")) {
                Toast.makeText(getActivity(), "Subida correcta", Toast.LENGTH_SHORT).show();
            } else if(s.equals("0")) {
                Toast.makeText(getActivity(), "Subida erronea", Toast.LENGTH_SHORT).show();
            }
        }

        private String post(String url, String inmueble){
            try {
                //Conexión post
                URL peticion = new URL(url);
                URLConnection conexion = peticion.openConnection();
                conexion.setDoOutput(true);
                //Escribir parametros
                OutputStreamWriter out = new OutputStreamWriter(conexion.getOutputStream());
                out.write(inmueble);
                out.close();
                //Leer respuesta
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        conexion.getInputStream()));
                String linea, todo = "";
                while ((linea = in.readLine()) != null) {
                    todo += linea + "\n";
                }
                in.close();
                return todo;
            } catch(Exception ex){
                return ex.toString();
            }
        }

        private String postFile(String url, String parametro, String archivo, String id){
            String resultado="";
            int status=0;
            try {
                //Conexión
                URL peticion = new URL(url);
                HttpURLConnection conexion = (HttpURLConnection) peticion.openConnection();
                conexion.setDoOutput(true);
                conexion.setRequestMethod("POST");
                //Archivo
                FileBody fileBody = new FileBody(new File(archivo));
                MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.STRICT);
                multipartEntity.addPart(parametro, fileBody);
                multipartEntity.addPart("id", new StringBody(id));
                conexion.setRequestProperty("Content-Type", multipartEntity.getContentType().getValue());
                OutputStream out = conexion.getOutputStream();
                try {
                    multipartEntity.writeTo(out);
                } catch(Exception ex){
                    return ex.toString();
                } finally {
                    out.close();
                }
                BufferedReader in = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                String decodedString;
                while ((decodedString = in.readLine()) != null) {
                    resultado+=decodedString+"\n";
                }
                in.close();
                status = conexion.getResponseCode();
            } catch (MalformedURLException ex) {
                Log.v("ERROR", ex.toString());
                return ex.toString();
            } catch (IOException ex) {
                Log.v("ERROR", ex.toString());
                return ex.toString();
            }
            return resultado; //+"\n"+status;
        }
    }
}
