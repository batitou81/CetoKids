package com.baptiste.cetokids.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baptiste.cetokids.data.Aliments;
import com.baptiste.cetokids.data.CetoDBAdapter;
import com.baptiste.cetokids.helper.Tools;
import com.example.baptiste.myapplication.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class RecetteActivity extends AppCompatActivity implements MyListRecetteAdapater.ListRecetteListener {

    private static final int MODE_CREATE = 0x0001;
    private static final int MODE_DISPLAY = 0x0002;
    private static final int MODE_NEW_RECETTE = 0x0003;

    private static final String EXTRA_MODE = "EXTRA_MODE";

    private static final String EXTRA_GLUCIDE_OBJECTIF = "glucideObjectif";
    private static final String EXTRA_PROTEINE_OBJECTIF = "proteineObjectif";
    private static final String EXTRA_LIPIDE_OBJECTIF = "lipideObjectif";

    private static final String EXTRA_RECETTE_NOM = "recette_nom";
    private static final String EXTRA_RECETTE_ID = "recette_id";

    private CetoDBAdapter dbHelper;
    String alimentFromCalculActivity = "";
    public List<Aliments> items;
    private MyListRecetteAdapater myListAdapter;
    public Float glucideObjectif = 0F;
    public Float proteineObjectif = 0F;
    public Float lipideObjectif = 0F;
    public int kcalObjectif = 0;
    public String ratioObjectif = "";
    private Boolean recetteEnregistree = false;

    private ListView lv;
    private TextView recipeKcal;
    private TextView recipeRatio;
    private TextView recipePro;
    private TextView recipeLip;
    private TextView recipeGlu;
    private TextView goalKcal;
    private TextView goalRatio;
    private TextView goalProt;
    private TextView goalLip;
    private TextView goalGlu;
    private String recette_nom = null;
    private Integer recette_id;
    private Boolean editRecette = false;


    public static Intent creerRecette(Context context, Float glucideObjectif, Float proteineObjectif, Float lipideObjectif) {
        Intent intent = new Intent(context, RecetteActivity.class);
		if (glucideObjectif==0F && proteineObjectif==0F && lipideObjectif==0F)
		{
			intent.putExtra(EXTRA_MODE, MODE_NEW_RECETTE);
		}else{
			intent.putExtra(EXTRA_GLUCIDE_OBJECTIF, glucideObjectif);
			intent.putExtra(EXTRA_PROTEINE_OBJECTIF, proteineObjectif);
			intent.putExtra(EXTRA_LIPIDE_OBJECTIF, lipideObjectif);
			intent.putExtra(EXTRA_MODE, MODE_CREATE);
		}
        return intent;
    }
	
    public static Intent afficherRecette(Context context, int recetteId, String recetteNom) {
        Intent intent = new Intent(context, RecetteActivity.class);
        intent.putExtra(EXTRA_RECETTE_NOM, recetteNom);
        intent.putExtra(EXTRA_RECETTE_ID, recetteId);
        intent.putExtra(EXTRA_MODE, MODE_DISPLAY);
        return intent;
    }
	

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recette);

        // Create list of items based upon class
        items = new ArrayList<>(30);

        // Open database
        dbHelper = new CetoDBAdapter(getApplicationContext());
        dbHelper.createDatabase();
        dbHelper.open();


        goalKcal = (TextView) findViewById(R.id.goalsKcal);
        goalRatio = (TextView) findViewById(R.id.goalRatio);
        goalProt = (TextView) findViewById(R.id.goalProt);
        goalLip = (TextView) findViewById(R.id.goalLip);
        goalGlu = (TextView) findViewById(R.id.goalGlu);

        recipeKcal = (TextView) findViewById(R.id.recipeKcal);
        recipeRatio = (TextView) findViewById(R.id.recipeRatio);
        recipePro = (TextView) findViewById(R.id.recipeProt);
        recipeGlu = (TextView) findViewById(R.id.recipeGlu);
        recipeLip = (TextView) findViewById(R.id.recipeLip);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int mode = getIntent().getIntExtra(EXTRA_MODE, -1);

            switch (mode) {
                case MODE_CREATE:
                    glucideObjectif = extras.getFloat(EXTRA_GLUCIDE_OBJECTIF);
                    lipideObjectif  = extras.getFloat(EXTRA_LIPIDE_OBJECTIF);
                    proteineObjectif= extras.getFloat(EXTRA_PROTEINE_OBJECTIF);

                    kcalObjectif = Tools.getKcal(lipideObjectif,glucideObjectif, proteineObjectif );
                    ratioObjectif = Tools.getRatio(lipideObjectif, glucideObjectif, proteineObjectif);
                    break;
                case MODE_NEW_RECETTE:
					demandeObjectif();
					calculTotal();
                    break;

                case MODE_DISPLAY:
                    editRecette=true;
                    recette_nom = extras.getString(EXTRA_RECETTE_NOM);
                    recette_id=extras.getInt(EXTRA_RECETTE_ID);
                    Cursor cursor = dbHelper.fetchAllAlimentRecettes(recette_id);
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        String alimentName = cursor.getString(cursor.getColumnIndexOrThrow("aliment"));
                        String alimentPortion = cursor.getString(cursor.getColumnIndexOrThrow("portion"));
                        String alimentProteine = cursor.getString(cursor.getColumnIndexOrThrow("proteine_origine"));
                        String alimentLipide = cursor.getString(cursor.getColumnIndexOrThrow("lipide_origine"));
                        String alimentGlucide = cursor.getString(cursor.getColumnIndexOrThrow("glucide_origine"));
                        Integer alimentId = Integer.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("_id")));

                        items.add(new Aliments(alimentName, alimentProteine, alimentGlucide, alimentLipide, alimentPortion, alimentId + ""));
                    }

                    calculTotal();
                    demandeObjectif();
                    calculTotal();
                    break;

                default:
                    Log.e("", "Wrong mode: " + mode);
            }

        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarRecette);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        goalKcal.setText(kcalObjectif + "");
        goalRatio.setText(ratioObjectif);
        goalProt.setText(proteineObjectif + "");
        goalLip.setText(lipideObjectif + "");
        goalGlu.setText(glucideObjectif + "");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (items.isEmpty() || recetteEnregistree) {
                    recetteEnregistree = false;

                        finishWithResult();

                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(RecetteActivity.this);
                    alertDialog.setTitle("Retour");
                    alertDialog.setMessage(getString(R.string.confirmation_sortie_ecran_recette));


                    alertDialog.setPositiveButton(getString(R.string.quitter), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            onBackPressed();
                        }
                    });

                    alertDialog.setNegativeButton(getString(R.string.annuler), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.show();
                }


            }
        });

        //totalRecette = (TextView) findViewById(R.id.totalRecette);


        // Populate the list view
        myListAdapter = new MyListRecetteAdapater(this, this, items);
        lv = getListView();
        lv.setAdapter(myListAdapter);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView listView, View view,
                                    int position, long id) {


                final int postion = listView.getPositionForView(view);

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(RecetteActivity.this);
                alertDialog.setTitle(getString(R.string.confirmation));
                alertDialog.setMessage(getString(R.string.confirmation_retrait_element));
                alertDialog.setPositiveButton(getString(R.string.oui), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            recetteEnregistree = false;
                            Toast.makeText(getApplicationContext(),
                                    items.get(postion).getAliment() + " : " +  getString(R.string.supprimee) +" ! ", Toast.LENGTH_SHORT).show();


                            items.remove(postion);
                            myListAdapter.notifyDataSetChanged();
                            calculTotal();


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


            }
        });

    }

    private void finishWithResult() {
        Intent intent = getIntent();
        setResult(RESULT_OK, intent);
        finish();
    }

	
    public void calculTotal() {

        Float glucideTotal = 0F;
        Float proteineTotal = 0F;
        Float lipideTotal = 0F;
        for (int i = 0; i < items.size(); i++) {
            glucideTotal += items.get(i).getGlucide() / (Float.parseFloat("100") / items.get(i).getPortion());
            proteineTotal += items.get(i).getProteine() / (Float.parseFloat("100") / items.get(i).getPortion());
            lipideTotal += items.get(i).getLipide() / (Float.parseFloat("100") / items.get(i).getPortion());
        }

        recipeGlu.setText(round(glucideTotal, 1) + "");
        recipeLip.setText(round(lipideTotal, 1) + "");
        recipePro.setText(round(proteineTotal, 1) + "");

        Float ratio = Float.parseFloat("0");
        if (!(glucideTotal == 0 || proteineTotal == 0 || lipideTotal == 0)) {
            ratio = lipideTotal / (glucideTotal + proteineTotal);
        }

        Integer kcal = Tools.getKcal(lipideTotal, glucideTotal, proteineTotal);
        Float rationObjectifCalcul;
        if (ratioObjectif.isEmpty()) {
            rationObjectifCalcul = Float.parseFloat("0");
        } else {
            String[] parts = ratioObjectif.split(":");
            rationObjectifCalcul = Float.parseFloat(parts[0].replaceAll(",", "."));
        }
        recipeKcal.setText(kcal + "");
        recipeRatio.setText(Tools.getRatio(lipideTotal, glucideTotal, proteineTotal));

        if (kcal > kcalObjectif * 1.05 || kcal < kcalObjectif * 0.95) {
            recipeKcal.setTextColor(Color.parseColor("#FF0000"));
        } else {
            recipeKcal.setTextColor(Color.parseColor("#228B22"));
        }

        if (ratio > rationObjectifCalcul * 1.05 || ratio < rationObjectifCalcul * 0.95) {
            recipeRatio.setTextColor(Color.parseColor("#FF0000"));
        } else {
            recipeRatio.setTextColor(Color.parseColor("#228B22"));
        }

        if (glucideTotal > glucideObjectif * 1.05 || glucideTotal < glucideObjectif * 0.95) {
            recipeGlu.setTextColor(Color.parseColor("#FF0000"));
        } else {
            recipeGlu.setTextColor(Color.parseColor("#228B22"));
        }

        if (lipideTotal > lipideObjectif * 1.05 || lipideTotal < lipideObjectif * 0.95) {
            recipeLip.setTextColor(Color.parseColor("#FF0000"));
        } else {
            recipeLip.setTextColor(Color.parseColor("#228B22"));
        }

        if (proteineTotal > proteineObjectif * 1.05 || proteineTotal < proteineObjectif * 0.95) {
            recipePro.setTextColor(Color.parseColor("#FF0000"));
        } else {
            recipePro.setTextColor(Color.parseColor("#228B22"));
        }

    }

    public static float round(float value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sauvegarde) {
            createRecette();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //METHOD WHICH WILL HANDLE DYNAMIC INSERTION
    public void addItems(View v) {
        Intent intent = new Intent(this, ListeAlimentActivity.class);
        startActivityForResult(intent, 90);
    }

    /* Called when the second activity's finished */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 90) {

            recetteEnregistree = false;
            Bundle extras = data.getExtras();
            alimentFromCalculActivity = extras.getString("alimentToReplace");

            String stringTmp = alimentFromCalculActivity;
            String[] parts = stringTmp.split("#");
            //TODO remplacer par retourner l'aliment id et un getalimentinfo by id
            Aliments newAliment = new Aliments(parts[1], parts[2], parts[3], parts[4], parts[5], parts[0]);


            items.add(newAliment);
            myListAdapter.notifyDataSetChanged();

            Toast.makeText(this, newAliment.getAliment() + " : " + getString(R.string.ajoute)+".", Toast.LENGTH_SHORT).show();

            calculTotal();

        }
    }

    protected ListView getListView() {
        if (lv == null) {
            lv = (ListView) findViewById(R.id.listViewAliment);
        }
        return lv;
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

    public void createRecette() {

        if (items.isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.enregsitrement_impossible)  , Toast.LENGTH_SHORT).show();
        } else {
            if (!editRecette) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

                alertDialog.setTitle(getString(R.string.enregistrer_recette));
                final EditText recetteName = new EditText(RecetteActivity.this);
                alertDialog.setView(recetteName);

                alertDialog.setPositiveButton(getString(R.string.sauvegarder), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {


                    }
                });

                alertDialog.setNegativeButton(getString(R.string.annuler), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

                final AlertDialog dialog = alertDialog.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Boolean wantToCloseDialog = false;
                        if (dbHelper.recetteExiste(recetteName.getText().toString())) {
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.enregistrement_impossible_existe_deja), Toast.LENGTH_SHORT).show();
                        } else if (recetteName.getText().toString().isEmpty()) {
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.enregistrement_impossible_nom_vide), Toast.LENGTH_SHORT).show();
                        } else {
                            dbHelper.insertRecette(recetteName.getText().toString());
                            int recette_id = dbHelper.getLastRecette();
                            for (Aliments aliment : items) {
                                dbHelper.insertRecetteAliment(recette_id, aliment.getId(), aliment.getPortion());
                            }
                            recetteEnregistree = true;
                            wantToCloseDialog = true;
                        }
                        if (wantToCloseDialog)
                            dialog.dismiss();
                    }
                });

            } else {
                dbHelper.removeAlimentFromRecette(recette_id);
                for (Aliments aliment : items) {
                    dbHelper.insertRecetteAliment(recette_id, aliment.getId(), aliment.getPortion());
                }
                Toast.makeText(getApplicationContext(),
                        recette_nom+" - " + getString(R.string.mis_a_jour) , Toast.LENGTH_SHORT).show();
                recetteEnregistree = true;
            }
        }
    }


    public void demandeObjectif() {

        LayoutInflater li = LayoutInflater.from(this);

        final View promptsView = li.inflate(R.layout.layout_selection_repas_popup, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setView(promptsView);

        alertDialogBuilder.setTitle(getString(R.string.objetif));

        final Spinner mSpinner = (Spinner) promptsView.findViewById(R.id.spinner_selection_repas);
        CetoDBAdapter db = new CetoDBAdapter(getApplicationContext());
        List<String> repasName = db.fetchAllRepasObjectif();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, repasName);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(dataAdapter);


        alertDialogBuilder.setPositiveButton(getString(R.string.charger), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
				//TODO probleme sur le spinner ... a revoir
    //            int idRepas = (int) mSpinner.getSelectedItemId() + 1;
                String repas = mSpinner.getSelectedItem().toString();
/*
                Toast.makeText(getApplicationContext(),
                         " : " +  repas +" ! ", Toast.LENGTH_SHORT).show();*/

                Cursor cursor = dbHelper.fetchAllRepasObjectifByString(repas);
                //TODO Try Catch si retourn rien
                glucideObjectif = Float.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("repas_glucide")));
                proteineObjectif = Float.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("repas_proteine")));
                lipideObjectif = Float.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("repas_lipide")));
                ratioObjectif = cursor.getString(cursor.getColumnIndexOrThrow("repas_ratio"));
                kcalObjectif = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("repas_kcal")));

                goalKcal.setText(kcalObjectif + "");
                goalRatio.setText(ratioObjectif);
                goalProt.setText(proteineObjectif + "");
                goalLip.setText(lipideObjectif + "");
                goalGlu.setText(glucideObjectif + "");
                dialog.dismiss();
            }
        });

        alertDialogBuilder.setNegativeButton(getString(R.string.continue_sans_objectif), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.setTitle(getString(R.string.objetif));
        alertDialog.setMessage(getString(R.string.selection_objectif_continuer));

        alertDialogBuilder.setView(mSpinner);


        alertDialog.show();

    }

    @Override
    public void onRecetteChanged() {
        calculTotal();
    }
}


