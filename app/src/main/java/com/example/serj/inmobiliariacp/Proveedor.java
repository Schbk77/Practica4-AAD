package com.example.serj.inmobiliariacp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class Proveedor extends ContentProvider{

    public static String AUTORIDAD = "com.example.serj.inmobiliariacp.proveedor";
    private Ayudante abd;
    private static final UriMatcher convierteUri2Int;
    private static final int INMUEBLE = 1, INMUEBLE_ID = 2;

    static {
        convierteUri2Int = new UriMatcher(UriMatcher.NO_MATCH);
        convierteUri2Int.addURI(AUTORIDAD, Contrato.TablaInmueble.TABLA, INMUEBLE);
        convierteUri2Int.addURI(AUTORIDAD, Contrato.TablaInmueble.TABLA + "/#", INMUEBLE_ID);
    }

    public Proveedor(){}

    @Override
    public boolean onCreate() {
        abd = new Ayudante(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (convierteUri2Int.match(uri)) {
            case INMUEBLE:
                break;
            case INMUEBLE_ID:
                selection = Contrato.TablaInmueble._ID + " = ?";
                selectionArgs =  new String[]{uri.getLastPathSegment()};
                break;
            default:
                throw new IllegalArgumentException("URI " + uri);
        }
        SQLiteDatabase bd = abd.getReadableDatabase();
        Cursor cursor = bd.query(Contrato.TablaInmueble.TABLA,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (convierteUri2Int.match(uri)){
            case INMUEBLE:
                return Contrato.TablaInmueble.CONTENT_TYPE_INMUEBLE;
            case INMUEBLE_ID:
                return Contrato.TablaInmueble.CONTENT_TYPE_INMUEBLE_ID;
            default:
                return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if(convierteUri2Int.match(uri) != INMUEBLE) {
            throw new IllegalArgumentException("URI " + uri);
        }
        SQLiteDatabase bd = abd.getWritableDatabase();
        long id = bd.insert(Contrato.TablaInmueble.TABLA, null, values);
        if(id > 0) {
            Uri uriElemento = ContentUris.withAppendedId(Contrato.TablaInmueble.CONTENT_URI, id);
            getContext().getContentResolver().notifyChange(uriElemento, null);
            return uriElemento;
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = abd.getWritableDatabase();
        switch (convierteUri2Int.match(uri)) {
            case INMUEBLE:
                break;
            case INMUEBLE_ID:
                selection = Contrato.TablaInmueble._ID + " = ?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                break;
            default:
                throw new IllegalArgumentException("URI " + uri);
        }
        int cuenta = db.delete(Contrato.TablaInmueble.TABLA, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cuenta;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = abd.getWritableDatabase();
        switch (convierteUri2Int.match(uri)) {
            case INMUEBLE:
                break;
            case INMUEBLE_ID:
                selection = Contrato.TablaInmueble._ID + " = ?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                break;
            default:
                throw new IllegalArgumentException("URI " + uri);
        }
        int cuenta = db.update(Contrato.TablaInmueble.TABLA, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cuenta;
    }
}
