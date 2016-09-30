package com.baptiste.cetokids.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.baptiste.cetokids.data.CetoDBAdapter;
import com.example.baptiste.myapplication.R;

public class MesRecettesActivity extends BaseDrawerActivity {
    private CetoDBAdapter dbHelper;
    private SimpleCursorAdapter dataAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_mes_recette);
/*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMesRecettes);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });*/

        dbHelper = new CetoDBAdapter(getApplicationContext());
        dbHelper.createDatabase();
        dbHelper.open();

        //listViewRecettes
        displayListView();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_mes_recette;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_ajout_recette) {
            startActivityForResult(RecetteActivity.creerRecette(this, 0F, 0F, 0F), 90);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 90) {

            displayListView();
            dataAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mes_recettes, menu);
        return true;
    }

    private void displayListView() {


        Cursor cursor = dbHelper.fetchAllRecettes();

        // The desired columns to be bound
        String[] columns = new String[]{
                "recette_nom", "proteine", "lipide", "glucide", "kcal", "ratio"
        };

        // the XML defined views which the data will be bound to
        int[] to = new int[]{
                R.id.recetteNom, R.id.recetteProteine, R.id.recetteLipide, R.id.recetteGlucide, R.id.recetteKcal, R.id.recetteRatio,
        };

        // create the adapter using the cursor pointing to the desired data
        //as well as the layout information
        dataAdapter = new SimpleCursorAdapter(
                this, R.layout.layout_mes_recettes,
                cursor,
                columns,
                to,
                0);

        listView = (ListView) findViewById(R.id.listViewMesRecettes);
        // Assign adapter to ListView
        listView.setLongClickable(true);
        listView.setAdapter(dataAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView listView, View view,
                                    int position, long id) {
                // dataAdapter.getItem(position);
                //dbHelper.removeRcette(position);
                final Cursor cursor = (Cursor) listView.getItemAtPosition(position);
                // Toast.makeText(getApplicationContext(),
                //      "Recette supprimée : " + Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("recette_nom"))), Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(view.getContext(), DetailRecetteAtivity.class);
                myIntent.putExtra("recette_id", cursor.getString(cursor.getColumnIndexOrThrow("_id")));
                myIntent.putExtra("recette_nom", cursor.getString(cursor.getColumnIndexOrThrow("recette_nom")));

                startActivityForResult(myIntent, 0);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view,
                                           final int position, long arg3) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MesRecettesActivity.this);
                alertDialog.setTitle(getString(R.string.confirmation));
                alertDialog.setMessage(getString(R.string.confirmation_suppression_recette));


                alertDialog.setPositiveButton(getString(R.string.oui), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            final Cursor cursor = (Cursor) listView.getItemAtPosition(position);
                            dbHelper.removeRcette(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("_id"))));
                            Toast.makeText(getApplicationContext(), getString(R.string.supprimee) + " !",
                                    Toast.LENGTH_SHORT).show();
                            displayListView();


                        } catch (IndexOutOfBoundsException e) {
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.suppression_element_impossible), Toast.LENGTH_SHORT).show();
                        }


                        dialog.dismiss();

                    }
                });
                alertDialog.setNegativeButton(getString(R.string.non), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

                alertDialog.show();
                return true;

            }
        });
    }
}
