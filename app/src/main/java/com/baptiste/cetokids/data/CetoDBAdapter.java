package com.baptiste.cetokids.data;

import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CetoDBAdapter {

    private static final String DATABASE_NAME = "CETOKIDSDB";
    private static final int DATABASE_VERSION = 11;
    private static final String TABLE_NAME = "ALIMENTS";
    private static final String TITLE = "NoTitle";
    private static final String BODY = "NoBody";

    protected static final String TAG = "DataAdapter";
    public static final String KEY_ROWID = "_id";

    private final Context mContext;
    private SQLiteDatabase mDb;
    private DataBaseHelper mDbHelper;

    public CetoDBAdapter(Context context) {
        this.mContext = context;
        mDbHelper = new DataBaseHelper(mContext);
    }

    public CetoDBAdapter createDatabase() throws SQLException {
        try {
            mDbHelper.createDataBase();
        } catch (IOException mIOException) {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public CetoDBAdapter open() throws SQLException {
        try {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        } catch (SQLException mSQLException) {
            Log.e(TAG, "open >>" + mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    public Cursor fetchAllAliment() {

        Cursor mCursor = mDb.query("Aliments", new String[]{KEY_ROWID, "ALIMENT", "PROTEINE", "GLUCIDE", "LIPIDE"},
                null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor fetchAllRepas() {

        Cursor mCursor = mDb.query("Repas", new String[]{KEY_ROWID, "repas_nom", "repas_proteine", "repas_glucide", "repas_lipide", "repas_kcal", "repas_ratio"},
                null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor fetchAllRecettes() {

        Cursor mCursor = mDb.rawQuery("select _id, recette_nom, proteine, lipide, glucide, kcal, round(lipide/(proteine+glucide),1)||\":1\" ratio from (select r._id _id, r.recette_nom recette_nom, round(sum(a.proteine*rc.portion/100.0),1) proteine, round(sum(a.lipide*rc.portion/100.0),1) lipide, round(sum(a.glucide*rc.portion/100.0),1) glucide, round(sum(a.proteine*(rc.portion/100.0))*4+sum(a.lipide*rc.portion/100.0)*9+sum(a.glucide*rc.portion/100.0)*4,1) kcal from recette_aliment rc join aliments a on (rc.aliment_id = a._id)  join recettes r on (r._id = rc.recette_id) group by recette_nom);",null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
	
    public Cursor getRecetteById(int recette_id) {	

        Cursor mCursor = mDb.rawQuery("select _id, recette_nom, proteine, lipide, glucide, kcal, round(lipide/(proteine+glucide),1)||\":1\" ratio from (select r._id _id, r.recette_nom recette_nom, round(sum(a.proteine*rc.portion/100.0),1) proteine, round(sum(a.lipide*rc.portion/100.0),1) lipide, round(sum(a.glucide*rc.portion/100.0),1) glucide, round(sum(a.proteine*(rc.portion/100.0))*4+sum(a.lipide*rc.portion/100.0)*9+sum(a.glucide*rc.portion/100.0)*4,1) kcal from recette_aliment rc join aliments a on (rc.aliment_id = a._id)  join recettes r on (r._id = rc.recette_id) where r._id=? group by recette_nom);", new String[] { recette_id+"" });

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor fetchAllAlimentRecettes(int recette_id) {

        Cursor mCursor = mDb.rawQuery(
                "select rc._id _id, a.aliment aliment, rc.portion portion,a.proteine proteine_origine,glucide glucide_origine,lipide lipide_origine, round(a.proteine*(rc.portion/100.0),1) proteine, round(a.lipide*(rc.portion/100.0),1)  lipide , round(a.glucide*(rc.portion/100.0),1)  glucide from recette_aliment rc join aliments a on (rc.aliment_id = a._id) where rc.recette_id=?",
                new String[] { recette_id+"" });

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Boolean alimentExiste(String alimentName){
        String query =  "select aliment from aliments where aliment='"+alimentName.replace("'","''")+"'";
        Cursor mCursor = mDb.rawQuery(query
                ,
                null);
        if(mCursor.getCount()== 0){
            return false;
        }else{
            return true;
        }
    }
	//TODO bizarre, cat_id PLUS _id... a revoir
    public List<String> fetchAllCatgories() {
        List<String> categorie_list = new ArrayList<String>();

        // Select All Query
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor = db.query("Categories", new String[]{KEY_ROWID, "CAT_ID", "CAT_NAME"},
                null, null, null, null, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                categorie_list.add(cursor.getString(2));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();

        // returning lables
        return categorie_list;
    }

    public List<String> fetchAllRepasObjectif(){
        List<String> categorie_list = new ArrayList<String>();

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query("Repas", new String[]{KEY_ROWID, "repas_nom", "repas_proteine", "repas_glucide", "repas_lipide", "repas_kcal", "repas_ratio"},
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                categorie_list.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return categorie_list;
    }

    public Cursor fetchAllRepasObjectifById(int id){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor mCursor = db.query("Repas", new String[]{KEY_ROWID, "repas_nom", "repas_proteine", "repas_glucide", "repas_lipide", "repas_kcal", "repas_ratio"},
                KEY_ROWID+"="+id, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        return mCursor;
    }

    public Cursor fetchAllRepasObjectifByString(String repas){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor mCursor = db.query("Repas", new String[]{KEY_ROWID, "repas_nom", "repas_proteine", "repas_glucide", "repas_lipide", "repas_kcal", "repas_ratio"},
                "repas_nom='"+repas.replace("'","''")+"'", null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        return mCursor;
    }

    public String getAlimentIdByName(String alimentName){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor mCursor = db.query("aliments", new String[]{KEY_ROWID, "aliment"},
                "aliment='"+alimentName.replace("'","''")+"'", null, null, null, null);
        return mCursor.getColumnIndexOrThrow("_id")+"";
    }

    public void close() {
        mDbHelper.close();
    }

    public Cursor fetchAlimentByName(String inputText) throws SQLException {
        Log.w(TAG, inputText);
        Cursor mCursor = null;
        if (inputText == null || inputText.length() == 0) {
            mCursor = mDb.query("Aliments", new String[]{KEY_ROWID, "ALIMENT", "PROTEINE", "GLUCIDE", "LIPIDE"},
                    null, null, null, null, null);

        } else {
            mCursor = mDb.query(true, "Aliments", new String[]{KEY_ROWID, "ALIMENT", "PROTEINE", "GLUCIDE", "LIPIDE"},
                    "aliment like '%" + inputText.replace("'","''") + "%'", null,
                    null, null, null, null);
        }
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }


	
    public Cursor fetchAlimentByCategorie(int cat_id) throws SQLException {
        Cursor mCursor = null;
       /* if (cat_id==null) {
            mCursor = mDb.query("Aliments", new String[]{KEY_ROWID, "ALIMENT", "PROTEINE", "GLUCIDE", "LIPIDE"},
                    null, null, null, null, null);

        } else {*/
        //TODO if cat_id null
            mCursor = mDb.query(true, "Aliments", new String[]{KEY_ROWID, "ALIMENT", "PROTEINE", "GLUCIDE", "LIPIDE"},
                    "cat_id=" + cat_id, null,
                    null, null, null, null);
      //  }
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public void insertItem(String aliment, Float proteine, Float glucide, Float lipide, int catgeorie) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String sql1 = "insert into " + TABLE_NAME + " (ALIMENT,PROTEINE,GLUCIDE,LIPIDE,CATEGORIE) values('" + aliment.replace("'","''") + "'," + proteine + "," + glucide + "," + lipide + "," + catgeorie + ");";
        try {
            Log.i("sql1=", sql1);
            db.execSQL(sql1);
        } catch (SQLException e) {
        }
    }

    public void insertRepas(String repas, Float proteine, Float glucide, Float lipide, int kcal, String ratio) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String sql1 = "insert into Repas (repas_nom,repas_proteine,repas_glucide,repas_lipide,repas_kcal,repas_ratio) values('" + repas.replace("'","''") + "'," + proteine + "," + glucide + "," + lipide + "," + kcal + ",'" + ratio + "');";
        try {
            Log.i("sql1=", sql1);
            db.execSQL(sql1);
        } catch (SQLException e) {
        }
    }

    public void removeRepas(long position) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete("repas", "_id=" + position, null);
    }

    public void removeRcette(int position) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete("recette_aliment", "recette_id=" + position, null);
        db.delete("recettes", "_id=" + position, null);
    }

    public void removeAlimentFromRecette(int position) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete("recette_aliment", "recette_id=" + position, null);
    }

        public void removeAliment(int aliment_id) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete("aliments", "_id=" + aliment_id, null);
    }


    public int getLastRecette() {

        Cursor mCursor = mDb.query("Recettes", new String[]{KEY_ROWID, "RECETTE_NOM"},
                null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToLast();
        }
        return Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("_id")));
    }

    public void insertRecetteAliment(int recette_id, int aliment_id, int portion) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String sql1 = "insert into recette_aliment (recette_id,aliment_id, portion) values(" + recette_id + "," + aliment_id + "," + portion + ");";
        try {
            Log.i("sql1=", sql1);
            db.execSQL(sql1);
        } catch (SQLException e) {
        }
    }


    public void insertRecette(String recette) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String sql1 = "insert into recettes (recette_nom) values('" + recette.replace("'","''") + "');";
        try {
            Log.i("sql1=", sql1);
            db.execSQL(sql1);
        } catch (SQLException e) {
        }
    }


    public boolean recetteExiste(String recette_name) {
        Cursor mCursor = null;
        mCursor = mDb.query(true, "recettes", new String[]{KEY_ROWID, "recette_nom"},
                "recette_nom = '" + recette_name.replace("'","''") + "'", null,
                null, null, null, null);

        if (mCursor.getCount() == 0) {
            return false;
        } else {
            return true;

        }
    }

    public boolean alimentUtiliseDansRecette (int aliment_id) {
        Cursor mCursor = null;
        mCursor = mDb.query(true, "recette_aliment", new String[]{KEY_ROWID},
                "aliment_id = " + aliment_id, null,
                null, null, null, null);

        if (mCursor.getCount() == 0) {
            return false;
        } else {
            return true;

        }
    }

    public boolean repasExiste (String repas_nom) {
        Cursor mCursor = null;
        mCursor = mDb.query(true, "repas", new String[]{KEY_ROWID},
                "repas_nom = '" + repas_nom.replace("'","''") + "'", null,
                null, null, null, null);

        if (mCursor.getCount() == 0) {
            return false;
        } else {
            return true;

        }
    }

}