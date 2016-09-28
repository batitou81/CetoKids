package com.baptiste.cetokids.view;

import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.baptiste.cetokids.data.CetoDBAdapter;
import com.baptiste.cetokids.helper.Tools;
import com.example.baptiste.myapplication.R;

import java.util.List;

public class MesAlimentsActivity extends BaseDrawerActivity implements AdapterView.OnItemSelectedListener {

    private CetoDBAdapter dbHelper;
    private SimpleCursorAdapter dataAdapter;
    private AlertDialog.Builder alertDialog;
    private ListView listView;
    private Spinner spinner;
    private int categorie_id;

    private EditText ajoutRepadNom;
    private EditText ajoutRepadProteine;
    private EditText ajoutRepadGlucide;
    private EditText ajoutRepadLipide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spinner = (Spinner) findViewById(R.id.addcategory);
        dbHelper = new CetoDBAdapter(getApplicationContext());
        dbHelper.createDatabase();
        dbHelper.open();

        alertDialog = new AlertDialog.Builder(this);

        listView = getListView();

        displayListView();


    }


    private void displayListView() {

        Cursor cursor = dbHelper.fetchAllAliment();
        String[] columns = new String[]{
                "ALIMENT", "PROTEINE", "GLUCIDE", "LIPIDE",
        };
        int[] to = new int[]{
                R.id.aliment,
                R.id.proteine,
                R.id.glucide,
                R.id.lipide,
        };

        dataAdapter = new SimpleCursorAdapter(
                this, R.layout.layout_aliment_info,
                cursor,
                columns,
                to,
                0);

        listView.setAdapter(dataAdapter);

        EditText myFilter = (EditText) findViewById(R.id.myFilter);
        myFilter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });
        myFilter.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                dataAdapter.getFilter().filter(s.toString());
            }
        });

        dataAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                return dbHelper.fetchAlimentByName(constraint.toString());
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView listView, View view,
                                    final int position, long id) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MesAlimentsActivity.this);
                alertDialog.setTitle(R.string.confirmation);
                alertDialog.setMessage(R.string.confirmation_suppression_element);
                alertDialog.setPositiveButton(R.string.oui, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {

                            Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                            int aliment_id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));

                            if (dbHelper.alimentUtiliseDansRecette(aliment_id)) {
                                Toast.makeText(getApplicationContext(), getString(R.string.impossible_recette_existe_aliment),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                dbHelper.removeAliment(aliment_id);
                                Toast.makeText(getApplicationContext(), getString(R.string.aliment_supprime) ,
                                        Toast.LENGTH_LONG).show();
                                displayListView();
                                dataAdapter.notifyDataSetChanged();

                            }


                        } catch (IndexOutOfBoundsException e) {
                            Toast.makeText(getApplicationContext(),
                                    R.string.suppression_element_impossible, Toast.LENGTH_SHORT).show();
                        }


                        dialog.dismiss();

                    }
                });
                alertDialog.setNegativeButton(R.string.non, new DialogInterface.OnClickListener() {

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

    protected ListView getListView() {
        if (listView == null) {
            listView = (ListView) findViewById(R.id.listView1);
        }
        return listView;
    }

    protected void setListAdapter(ListAdapter adapter) {
        getListView().setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ajout_aliment, menu);
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mes_aliments;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_ajout_aliment) {
            //startActivityForResult(RecetteActivity.creerRecette(this,0F,0F,0F),90);
            //PoPup alert dialog pour ajouter un element. Long clic pour editer
            alertDisplay();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void alertDisplay() {

        LayoutInflater li = LayoutInflater.from(this);

        final View promptsView = li.inflate(R.layout.layout_ajout_edit_aliment, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setView(promptsView);

        final Spinner mSpinner = (Spinner) promptsView.findViewById(R.id.addcategory);

        CetoDBAdapter db = new CetoDBAdapter(getApplicationContext());
        List<String> categorieName = db.fetchAllCatgories();
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, categorieName);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(dataAdapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int pos, long id) {
                categorie_id = pos + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        ajoutRepadNom = (EditText) promptsView.findViewById(R.id.addaliment);
        ajoutRepadProteine = (EditText) promptsView.findViewById(R.id.addproteine);
        ajoutRepadGlucide = (EditText) promptsView.findViewById(R.id.addglucide);
        ajoutRepadLipide = (EditText) promptsView.findViewById(R.id.addlipide);
        //spinner.setSelection(0);

        alertDialogBuilder.setPositiveButton(getString(R.string.ajouter), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String alimentString = ajoutRepadNom.getText().toString();

                boolean diesePresent = alimentString.contains("#");

                if (diesePresent) {
                    Toast.makeText(getApplicationContext(), getString(R.string.valeur_interdite_diese),
                            Toast.LENGTH_LONG).show();
                } else if (dbHelper.alimentExiste(alimentString)) {
                    Toast.makeText(getApplicationContext(), getString(R.string.element_exite_deja),
                            Toast.LENGTH_LONG).show();
                } else {

                    Float proteineValue;
                    Float glucideValue;
                    Float lipidesValue;
                    try {
                        proteineValue = Float.parseFloat(ajoutRepadProteine.getText().toString());
                        glucideValue = Float.parseFloat(ajoutRepadGlucide.getText().toString());
                        lipidesValue = Float.parseFloat(ajoutRepadLipide.getText().toString());


                        if (alimentString.isEmpty() || proteineValue.isNaN() || glucideValue.isNaN() || lipidesValue.isNaN()) {
                            Toast.makeText(getApplicationContext(), getString(R.string.toutes_les_valeurs_a_renseigner),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            dbHelper.insertItem(alimentString, proteineValue, glucideValue, lipidesValue, categorie_id);
                            Toast.makeText(getApplicationContext(), ajoutRepadNom.getText() + " : " + getString(R.string.ajoute) + " !",
                                    Toast.LENGTH_LONG).show();
                            displayListView();
                            dataAdapter.notifyDataSetChanged();
                        }


                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.toutes_les_valeurs_a_renseigner),
                                Toast.LENGTH_LONG).show();
                    }

                }
            }


        });

        alertDialogBuilder.setNegativeButton(getString(R.string.annuler), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.setMessage(getString(R.string.ajout_aliment));

        alertDialogBuilder.setView(mSpinner);


        alertDialog.show();

    }
/*
    private void loadSpinnerData() {
        // database handler
        CetoDBAdapter db = new CetoDBAdapter(getApplicationContext());

        // Spinner Drop down elements
        List<String> catname = db.fetchAllCatgories();

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, catname);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }*/


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
