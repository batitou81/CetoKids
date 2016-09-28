package com.baptiste.cetokids.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.baptiste.cetokids.data.CetoDBAdapter;
import com.example.baptiste.myapplication.R;

import java.util.List;


public class AjoutAlimentActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Button button1;
    OnClickListener listener1 = null;

    private CetoDBAdapter dbHelper;

    EditText glucides;
    EditText lipides;
    EditText proteines;
    EditText aliment;

    // Spinner element
    Spinner spinner;
    String categorieLabel;
    Integer categoriePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajout_aliment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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


        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(listener1);

        glucides = (EditText) findViewById(R.id.addglucide);
        lipides = (EditText) findViewById(R.id.addlipide);
        proteines = (EditText) findViewById(R.id.addproteine);
        aliment = (EditText) findViewById(R.id.addaliment);
        //category = (EditText) findViewById(R.id.addcategory);



        spinner = (Spinner) findViewById(R.id.addcategory);
        spinner.setOnItemSelectedListener(this);
        loadSpinnerData();

        dbHelper = new CetoDBAdapter(getApplicationContext());
        dbHelper.createDatabase();
        dbHelper.open();

        listener1 = new OnClickListener() {
            public void onClick(View v) {
                //TODO
                //Check si entree existe deja et si il y a un #
                String alimentString = aliment.getText().toString();

                boolean diesePresent = alimentString.contains("#");

                if (diesePresent) {
                    Toast.makeText(getApplicationContext(), getString(R.string.valeur_interdite_diese),
                            Toast.LENGTH_LONG).show();
                } else if(dbHelper.alimentExiste(alimentString)) {
                    Toast.makeText(getApplicationContext(), getString(R.string.element_exite_deja),
                            Toast.LENGTH_LONG).show();
                }
                else
                 {

                    Float proteineValue;
                    Float glucideValue;
                    Float lipidesValue;
                    try {
                        proteineValue = Float.parseFloat(proteines.getText().toString());
                        glucideValue = Float.parseFloat(glucides.getText().toString());
                        lipidesValue = Float.parseFloat(lipides.getText().toString());


                        if (alimentString.isEmpty() || proteineValue.isNaN() || glucideValue.isNaN() || lipidesValue.isNaN()) {
                            Toast.makeText(getApplicationContext(), getString(R.string.toutes_les_valeurs_a_renseigner),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            dbHelper.insertItem(alimentString, proteineValue, glucideValue, lipidesValue, categoriePosition);
                            Toast.makeText(getApplicationContext(), aliment.getText() + " : " + getString(R.string.ajoute) + " !",
                                    Toast.LENGTH_LONG).show();
                            glucides.setText("");
                            lipides.setText("");
                            proteines.setText("");
                            aliment.setText("");
                            spinner.setSelection(0);

                        }


                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.toutes_les_valeurs_a_renseigner),
                                Toast.LENGTH_LONG).show();
                    }

                }
            }
        };

        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(listener1);

    }

     //TODO Mettre une listview et pas un spinner
     
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
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        // TODO On selecting a spinner item
        categorieLabel = parent.getItemAtPosition(position).toString();
        categoriePosition = position + 1;

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_ajout_aliment) {
			//TODO
			return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ajout_aliment, menu);
        return true;
    }
}
