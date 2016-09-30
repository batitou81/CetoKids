package com.baptiste.cetokids.view;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baptiste.cetokids.data.Aliments;
import com.baptiste.cetokids.data.CetoDBAdapter;
import com.example.baptiste.myapplication.R;

import java.util.ArrayList;

public class DetailRecetteAtivity extends AppCompatActivity {
    private CetoDBAdapter dbHelper;
    private SimpleCursorAdapter dataAdapter;
    private ArrayList<Aliments> alimentListeRecette;
    private String recette_nom;
    private int recette_id = 1;
    private TextView nomRecette;
    private TextView recetteProteine;
    private TextView recetteGlucide;
    private TextView recetteLipide;
    private TextView recetteKcal;
    private TextView recetteRatio;

    private TextView recetteCommentaire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        alimentListeRecette = new ArrayList<>(30);
        setContentView(R.layout.activity_detail_recette_ativity);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            recette_id = Integer.parseInt(extras.getString("recette_id"));
            recette_nom = extras.getString("recette_nom");
        }

        nomRecette = (TextView) findViewById(R.id.nomRecette);
        recetteProteine = (TextView) findViewById(R.id.recetteProteine);
        recetteGlucide = (TextView) findViewById(R.id.recetteGlucide);
        recetteLipide = (TextView) findViewById(R.id.recetteLipide);
        recetteKcal = (TextView) findViewById(R.id.recetteKcal);
        recetteRatio = (TextView) findViewById(R.id.recetteRatio);
        recetteCommentaire = (TextView) findViewById(R.id.recetteCommentaire);


        //nomRecette.setText(recette_nom);

        dbHelper = new CetoDBAdapter(getApplicationContext());
        dbHelper.createDatabase();
        dbHelper.open();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarDetailRecette);
        setSupportActionBar(toolbar);

        //Display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        displayInfoRecette(recette_nom, recette_id);
        displayListView(recette_id);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 90) {
            //todo
            displayListView(recette_id);
            dataAdapter.notifyDataSetChanged();
        }
    }

    private void displayInfoRecette(String recette_nom, int recette_id) {
        nomRecette.setText(recette_nom);

        Cursor cursor = dbHelper.getRecetteById(recette_id);
        cursor.moveToFirst();

        String proteine = cursor.getString(cursor.getColumnIndexOrThrow("proteine"));
        String lipide = cursor.getString(cursor.getColumnIndexOrThrow("lipide"));
        String glucide = cursor.getString(cursor.getColumnIndexOrThrow("glucide"));
        String kcal = cursor.getString(cursor.getColumnIndexOrThrow("kcal"));
        String ratio = cursor.getString(cursor.getColumnIndexOrThrow("ratio"));
        String commentaire = cursor.getString(cursor.getColumnIndexOrThrow("recette_commentaire"));

        nomRecette.setText(recette_nom);
        recetteProteine.setText(proteine);
        recetteGlucide.setText(glucide);
        recetteLipide.setText(lipide);
        recetteKcal.setText(kcal);
        recetteRatio.setText(ratio);
        recetteCommentaire.setText(commentaire);
    }

    private void displayListView(int recette_id) {


        Cursor cursor = dbHelper.fetchAllAlimentRecettes(recette_id);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String alimentName = cursor.getString(cursor.getColumnIndexOrThrow("aliment"));
            String alimentProteine = cursor.getString(cursor.getColumnIndexOrThrow("proteine"));
            String alimentPortion = cursor.getString(cursor.getColumnIndexOrThrow("portion"));
            String alimentLipide = cursor.getString(cursor.getColumnIndexOrThrow("lipide"));
            String alimentGlucide = cursor.getString(cursor.getColumnIndexOrThrow("glucide"));
            Integer alimentId = Integer.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("_id")));
            alimentListeRecette.add(new Aliments(alimentName, alimentProteine, alimentGlucide, alimentLipide, alimentPortion, alimentId + ""));
        }
        // The desired columns to be bound
        String[] columns = new String[]{
                "aliment", "portion", "proteine", "glucide", "lipide",
        };

        // the XML defined views which the data will be bound to
        int[] to = new int[]{
                R.id.alimentDetailRecette,
                R.id.portionDetailRecette,
                R.id.proteineDetailRecette,
                R.id.glucideDetailRecette,
                R.id.lipideDetailRecette,
        };

        // create the adapter using the cursor pointing to the desired data
        //as well as the layout information
        dataAdapter = new SimpleCursorAdapter(
                this, R.layout.layout_detail_recette,
                cursor,
                columns,
                to,
                0);

        ListView listView = (ListView) findViewById(R.id.listViewDetailRecette);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_recette, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.editer_recette) {
            //startActivity(RecetteActivity.afficherRecette(this, recette_id, recette_nom));
            startActivityForResult(RecetteActivity.afficherRecette(this, recette_id, recette_nom), 90);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
