package com.example.serj.inmobiliariacp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import com.squareup.picasso.Picasso;

public class Detalle extends Fragment implements View.OnClickListener{

    private View v;
    private ArrayList<File> fotos;
    private ImageView iv;
    private Button btAnt, btSig;
    private int pos;
    private int id;

    public Detalle() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_detalle, container, false);
        initComponents();
        return v;
    }

    private void initComponents() {
        iv = (ImageView)v.findViewById(R.id.galeria);
        btAnt = (Button)v.findViewById(R.id.btAnterior);
        btAnt.setOnClickListener(this);
        btSig = (Button)v.findViewById(R.id.btSiguiente);
        btSig.setOnClickListener(this);
    }

    private void guardarImagenes() {
        File[] allPhotos = getActivity().getExternalFilesDir(Environment.DIRECTORY_DCIM).listFiles();
        for(File f : allPhotos) {
            String photoId = f.getPath();
            if(photoId.contains("inmueble_"+ this.id + "_")){
                fotos.add(f);
            }
        }
        if(!fotos.isEmpty()) {
            pos = 0;
            mostrarImagen();
        } else {
            iv.setImageDrawable(getResources().getDrawable(R.drawable.nodisponible));
            Toast.makeText(getActivity(), getString(R.string.no_fotos), Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarImagen() {
        //Bitmap foto = BitmapFactory.decodeFile(fotos.get(pos).getPath());
        //iv.setImageBitmap(foto);
        Picasso.with(getActivity()).load(fotos.get(pos)).fit().into(iv);
    }

    public void setDetalle(int id){
        // Recoge y muestra todas las imagenes de un Inmueble
        this.id = id;
        fotos = new ArrayList<File>();
        guardarImagenes();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btAnterior:
                anterior();
                break;
            case R.id.btSiguiente:
                siguiente();
                break;
        }
    }

    public void anterior(){
        // Retrocede una imagen en la galería
        if( fotos != null && pos-1 >= 0){
            pos--;
            mostrarImagen();
        }
    }

    public void siguiente(){
        // Avanza una imagen en la galería
        if(fotos != null && pos + 1 < fotos.size()){
            pos++;
            mostrarImagen();
        }
    }
}
