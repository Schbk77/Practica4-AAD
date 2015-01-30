package com.example.serj.inmobiliariacp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Editar extends Activity {

    /**********************************************************************************************/
    /**************************************VARIABLES***********************************************/
    /**********************************************************************************************/

    static final int REQUEST_TAKE_PHOTO = 1;                         //Código de la actividad Cámara
    String mCurrentPhotoPath;                                        //Ruta de la imagen
    String localidad, direccion, tipo;
    int precio;
    int id;
    EditText et1, et2, et3, et4;
    TextView tv;

    /**********************************************************************************************/
    /**************************************ON...***************************************************/
    /**********************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Inicializa los componentes del layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar);
        initComponents();
    }

    /**********************************************************************************************/
    /***********************************MÉTODOS AUXILIARES*****************************************/
    /**********************************************************************************************/

    public void lanzarCamara(View v){
        // Al pulsar sobre el icono de la cámara
        // Se lanza la cámara para crear un archivo de imagen
        dispatchTakePictureIntent();
    }

    public void guardarCambios(View view){
        // Guarda los cambios que se hayan realizado sobre el inmueble
        if (!et1.getText().toString().isEmpty() &&
                !et2.getText().toString().isEmpty() &&
                !et3.getText().toString().isEmpty() &&
                !et4.getText().toString().isEmpty()) {
            localidad = et1.getText().toString();
            direccion = et2.getText().toString();
            tipo = et3.getText().toString();
            precio = Integer.parseInt(et4.getText().toString());

            Intent i = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(getString(R.string.tag_id), id);
            bundle.putString(getString(R.string.tag_localidad), localidad);
            bundle.putString(getString(R.string.tag_direccion), direccion);
            bundle.putString(getString(R.string.tag_tipo), tipo);
            bundle.putInt(getString(R.string.tag_precio), precio);
            i.putExtras(bundle);
            setResult(RESULT_OK, i);
            finish();
        }else{
            Toast.makeText(this, getString(R.string.campos_vacios), Toast.LENGTH_SHORT).show();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Se asegura de que hay una actividad que maneje el intent de la cámara
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Crea el archivo donde debería ir la foto
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error mientras se crea el archivo
            }
            // Continua solo si el archivo fue exitosamente creado
            if (photoFile != null) {
                tv.append(photoFile.getPath()+"\n");
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Crea un nombre para el archivo de imagen
        String timeStamp = new SimpleDateFormat(getString(R.string.formato)).format(new Date());
        String imageFileName = getString(R.string.inm) + String.valueOf(id)+'_' + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(
                imageFileName,                         /* nombre */
                getString(R.string.extension),         /* extension */
                storageDir                             /* ruta */
        );
        // Guarda el archivo
        mCurrentPhotoPath = getString(R.string.path) + image.getAbsolutePath();
        return image;
    }

    private void initComponents(){
        // Inicializa los componentes del diálogo
        et1 = (EditText)findViewById(R.id.etLocalidad);
        et2 = (EditText)findViewById(R.id.etDireccion);
        et3 = (EditText)findViewById(R.id.etTipo);
        et4 = (EditText)findViewById(R.id.etPrecio);
        tv = (TextView)findViewById(R.id.textView2);
        getExtras();
    }

    private void getExtras(){
        // Recoge los extras del Intent que llama a la Actividad
        Bundle b = getIntent().getExtras();
        if(b != null){
            localidad = b.getString(getString(R.string.tag_localidad));
            direccion = b.getString(getString(R.string.tag_direccion));
            tipo = b.getString(getString(R.string.tag_tipo));
            precio = b.getInt(getString(R.string.tag_precio));
            id = b.getInt(getString(R.string.tag_id));
        }
        et1.setText(localidad);
        et2.setText(direccion);
        et3.setText(tipo);
        et4.setText(String.valueOf(precio));
    }
}