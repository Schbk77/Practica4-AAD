package com.example.serj.inmobiliariacp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Adaptador extends CursorAdapter {

    private int recurso;
    private static LayoutInflater i;
    private Context contexto;

    static class ViewHolder {
        TextView tvLocalidad, tvDireccion, tvPrecio;
        ImageView iv;
    }

    public Adaptador(Context context, int recurso, Cursor cursor) {
        super(context, cursor, true);
        this.recurso = recurso;
        this.contexto = context;
        this.i = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = i.inflate(recurso, parent, false);
        ViewHolder vh = new ViewHolder();
        vh.tvLocalidad = (TextView)view.findViewById(R.id.tvLocalidad);
        vh.tvDireccion = (TextView)view.findViewById(R.id.tvDireccion);
        vh.tvPrecio = (TextView)view.findViewById(R.id.tvPrecio);
        vh.iv = (ImageView)view.findViewById(R.id.ivCasa);
        view.setTag(vh);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder vh = (ViewHolder) view.getTag();

        String localidad = cursor.getString(1);
        String direccion = cursor.getString(2);
        int precio = cursor.getInt(4);
        int subido = cursor.getInt(5);

        vh.tvLocalidad.setText(localidad);
        vh.tvDireccion.setText(direccion);
        vh.tvPrecio.setText(precio + "€");

        if(subido == 1){
            vh.iv.setImageResource(R.drawable.casa_subido);
        }else{
            vh.iv.setImageResource(R.drawable.casa);
        }
    }
}
