package com.example.serj.inmobiliariacp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Ayudante extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "inmobiliaria.db";
    public static final int DATABASE_VERSION = 1;

    public Ayudante(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + Contrato.TablaInmueble.TABLA +
                " (" + Contrato.TablaInmueble._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Contrato.TablaInmueble.LOCALIDAD + " TEXT, " +
                Contrato.TablaInmueble.DIRECCION + " TEXT, " +
                Contrato.TablaInmueble.TIPO + " TEXT, " +
                Contrato.TablaInmueble.PRECIO + " INTEGER, " +
                Contrato.TablaInmueble.SUBIDO + " INTEGER DEFAULT 0)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table if exists " + Contrato.TablaInmueble.TABLA;
        db.execSQL(sql);
        onCreate(db);
    }
}
