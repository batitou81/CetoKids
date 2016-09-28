package com.baptiste.cetokids.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.baptiste.cetokids.data.CetoDBAdapter;
import com.example.baptiste.myapplication.R;

public class ListeAlimentActivity extends AppCompatActivity {
    private CetoDBAdapter dbHelper;
    private SimpleCursorAdapter dataAdapter;
    private String alimentName;
    private int idAliment;
    private Float proteineValue;
    private Float glucideValue;
    private Float lipidesValue;
    private Integer portionValue;
    private AlertDialog.Builder alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_aliement);

        dbHelper = new CetoDBAdapter(getApplicationContext());
        dbHelper.createDatabase();
        dbHelper.open();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        alertDialog = new AlertDialog.Builder(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // back button pressed
                onBackPressed();

            }
        });

        //Cursor testdata = mDbHelper.getTestData();
        displayListView();
        //mDbHelper.close();
    }

    private void finishWithResult() {
        Intent intent = getIntent();

        String alimentStringToReplace = idAliment + "#" + alimentName + "#" + proteineValue + "#" + glucideValue + "#" + lipidesValue + "#" + portionValue;
        intent.putExtra("alimentToReplace", alimentStringToReplace);

        setResult(RESULT_OK, intent);
        finish();
    }

        private void displayListView() {


            Cursor cursor = dbHelper.fetchAllAliment();

            // The desired columns to be bound
            String[] columns = new String[]{
                    "ALIMENT", "PROTEINE", "GLUCIDE", "LIPIDE",
            };

            // the XML defined views which the data will be bound to
            int[] to = new int[]{
                    R.id.aliment,
                    R.id.proteine,
                    R.id.glucide,
                    R.id.lipide,
            };

            // create the adapter using the cursor pointing to the desired data
            //as well as the layout information
            dataAdapter = new SimpleCursorAdapter(
                    this, R.layout.layout_aliment_info,
                    cursor,
                    columns,
                    to,
                    0);

            ListView listView = (ListView) findViewById(R.id.listView1);
            // Assign adapter to ListView
            listView.setAdapter(dataAdapter);


        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView listView, View view,
                                    int position, long id) {
                // Get the cursor, positioned to the corresponding row in the result set
                dataAdapter.getItem(position);
                final Cursor cursor = (Cursor) listView.getItemAtPosition(position);


                // Get the state's capital from this row in the database.
                idAliment =
                        Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("_id")));
                alimentName =
                        cursor.getString(cursor.getColumnIndexOrThrow("ALIMENT")).replaceAll("'", "\\'");
                proteineValue = Float.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("PROTEINE")).replaceAll(",", "."));
                glucideValue = Float.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("GLUCIDE")).replaceAll(",", "."));
                lipidesValue = Float.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("LIPIDE")).replaceAll(",", "."));


                alertDialog.setTitle(getString(R.string.quantite_initiale));
                alertDialog.setMessage(getString(R.string.quantite_en_g));

                final NumberPicker np = new NumberPicker(ListeAlimentActivity.this);
                alertDialog.setView(np);
                np.setMinValue(0);
                np.setMaxValue(1000);
                np.setWrapSelectorWheel(false);
                np.setValue(100);

                alertDialog.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        np.clearFocus();
                        portionValue = np.getValue();
                        finishWithResult();
                    }
                });

                alertDialog.setNegativeButton(getString(R.string.annuler), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

                AlertDialog d = alertDialog.create();
                d.getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                d.show();


            }
        });

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
            public Cursor runQuery(CharSequence constraint) {
                return dbHelper.fetchAlimentByName(constraint.toString());
            }
        });

    }
	/*
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_ajout_aliment) {
			//TODO          
			Toast.makeText(this, "afficher alert dialog ajout aliment",
                Toast.LENGTH_LONG).show();			
			return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ajout_aliment, menu);
        return true;
    }*/

}
