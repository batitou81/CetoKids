
package com.baptiste.cetokids.view;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baptiste.cetokids.data.CetoDBAdapter;
import com.baptiste.cetokids.helper.Tools;
import com.example.baptiste.myapplication.R;

public class RepasActivity extends BaseDrawerActivity {

    private CetoDBAdapter dbHelper;
    private SimpleCursorAdapter dataAdapter;
    private AlertDialog.Builder alertDialog;
    private AlertDialog.Builder alertDialogAjoutRepas;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_repas);


        listView = getListView();
        alertDialog = new AlertDialog.Builder(this);
        alertDialogAjoutRepas = new AlertDialog.Builder(this);
/*

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });*/


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view,
                                           final int position, final long id) {

                alertDialog.setTitle(getString(R.string.confirmation));
                alertDialog.setMessage(getString(R.string.confirmation_suppression_repas));
                alertDialog.setPositiveButton(getString(R.string.oui), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            dbHelper.removeRepas(id);
                            displayListView();
                            dataAdapter.notifyDataSetChanged();

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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView listView, View view,
                                    int position, long id) {
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                Float glucides = cursor.getFloat(cursor.getColumnIndexOrThrow("repas_glucide"));
                Float proteines = cursor.getFloat(cursor.getColumnIndexOrThrow("repas_proteine"));
                Float lipides = cursor.getFloat(cursor.getColumnIndexOrThrow("repas_lipide"));

                startActivityForResult(RecetteActivity.creerRecette(RepasActivity.this, glucides, proteines, lipides), 0);
            }
        });


        dbHelper = new CetoDBAdapter(getApplicationContext());
        dbHelper.createDatabase();
        dbHelper.open();

        displayListView();
 }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_repas;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mes_repas, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_ajout_repas) {
            displayAlert();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void displayAlert() {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_ajout_repas, null);
        alertDialogAjoutRepas.setView(alertLayout);
        final EditText ajoutRepadNom = (EditText) alertLayout.findViewById(R.id.ajoutRepadNom);
        final EditText ajoutRepadProteine = (EditText) alertLayout.findViewById(R.id.ajoutRepadProteine);
        final EditText ajoutRepadGlucide = (EditText) alertLayout.findViewById(R.id.ajoutRepadGlucide);
        final EditText ajoutRepadLipide = (EditText) alertLayout.findViewById(R.id.ajoutRepadLipide);


        final TextView kcalResult = (TextView) alertLayout.findViewById(R.id.kcalResult);
        final TextView ratioResult = (TextView) alertLayout.findViewById(R.id.ratioResult);


        ajoutRepadProteine.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                onChangeText(ajoutRepadGlucide, ajoutRepadLipide, ajoutRepadProteine, kcalResult, ratioResult);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        ajoutRepadGlucide.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

                onChangeText(ajoutRepadGlucide, ajoutRepadLipide, ajoutRepadProteine, kcalResult, ratioResult);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        ajoutRepadLipide.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

                onChangeText(ajoutRepadGlucide, ajoutRepadLipide, ajoutRepadProteine, kcalResult, ratioResult);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });


        alertDialogAjoutRepas.setPositiveButton(getString(R.string.ajouter), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                try {
                    final String tmpNomRepas = ajoutRepadNom.getText().toString();
                    final Float tmpProteineRepas = Float.parseFloat(ajoutRepadProteine.getText().toString());
                    final Float tmpGlucideRepas = Float.parseFloat(ajoutRepadGlucide.getText().toString());
                    final Float tmpLipideRepas = Float.parseFloat(ajoutRepadLipide.getText().toString());
                    if (dbHelper.repasExiste(tmpNomRepas)){
                        Toast.makeText(RepasActivity.this, getString(R.string.repas_existe_deja),
                                Toast.LENGTH_LONG).show();
                    }else {
                        dbHelper.insertRepas(tmpNomRepas, tmpProteineRepas, tmpGlucideRepas, tmpLipideRepas,
                                Tools.getKcal(tmpLipideRepas, tmpGlucideRepas, tmpProteineRepas),
                                Tools.getRatio(tmpLipideRepas, tmpGlucideRepas, tmpProteineRepas).replaceAll(",", "."));
                        displayListView();
                        dataAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    Toast.makeText(RepasActivity.this, getString(R.string.toutes_les_valeurs_a_renseigner),
                            Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }


        });

        alertDialogAjoutRepas.setNegativeButton(getString(R.string.annuler), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        AlertDialog d = alertDialogAjoutRepas.create();
        d.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        d.show();

    }

    private void displayListView() {

        Cursor cursor = dbHelper.fetchAllRepas();

        // The desired columns to be bound
        String[] columns = new String[]{
                "repas_nom", "repas_proteine", "repas_glucide", "repas_lipide", "repas_kcal", "repas_ratio",
        };

        // the XML defined views which the data will be bound to
        int[] to = new int[]{
                R.id.repasNom,
                R.id.repasProteine,
                R.id.repasGlucide,
                R.id.repasLipide,
                R.id.repasKcal,
                R.id.repasRatio,
        };

        // create the adapter using the cursor pointing to the desired data
        //as well as the layout information
        dataAdapter = new SimpleCursorAdapter(
                this, R.layout.layout_repas,
                cursor,
                columns,
                to,
                0);


        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);
    }

    protected ListView getListView() {
        if (listView == null) {
            listView = (ListView) findViewById(R.id.listViewRepas);
        }
        return listView;
    }

    protected void setListAdapter(ListAdapter adapter) {
        getListView().setAdapter(adapter);
    }

    protected ListAdapter getListAdapter() {
        ListAdapter adapter = getListView().getAdapter();
        if (adapter instanceof HeaderViewListAdapter) {
            return ((HeaderViewListAdapter) adapter).getWrappedAdapter();
        } else {
            return adapter;
        }
    }

    public void onChangeText(EditText glucidesET, EditText lipidesET, EditText proteinesET, TextView kcalText, TextView ratioText) {
        if (glucidesET.getText().toString().isEmpty() || lipidesET.getText().toString().isEmpty() || proteinesET.getText().toString().isEmpty()) {

        } else {
            try {

                Float lipides = Float.parseFloat(lipidesET.getText().toString());
                Float glucides = Float.parseFloat(glucidesET.getText().toString());
                Float proteines = Float.parseFloat(proteinesET.getText().toString());

                kcalText.setText(String.valueOf(Tools.getKcal(lipides, glucides, proteines)));
                ratioText.setText(Tools.getRatio(lipides, glucides, proteines));
            } catch (NumberFormatException e) {
                Toast.makeText(RepasActivity.this, e.toString(),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

}
