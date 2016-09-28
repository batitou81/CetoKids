package com.baptiste.cetokids.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baptiste.cetokids.helper.Tools;
import com.example.baptiste.myapplication.R;

public class MainActivity extends BaseDrawerActivity {

    EditText glucidesEditText;
    EditText lipidesEditText;
    EditText proteinesEditText;
    Button mButton;
    TextView kcalText;
    TextView ratioText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO : ne pas mettre de placeholder , mettre un 0 qui est enlevé au focus
        mButton = (Button) findViewById(R.id.calcul);
        glucidesEditText = (EditText) findViewById(R.id.glucides);
        lipidesEditText = (EditText) findViewById(R.id.lipides);
        proteinesEditText = (EditText) findViewById(R.id.proteines);
        kcalText = (TextView) findViewById(R.id.kcalResult);
        ratioText = (TextView) findViewById(R.id.ratioResult);

        kcalText.setText("0");
        ratioText.setText("0");

        mButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String glucidesStr = glucidesEditText.getText().toString();
                String proteinesStr = proteinesEditText.getText().toString();
                String lipidesStr = lipidesEditText.getText().toString();

                Float glucides = glucidesStr.isEmpty() ? null : Float.valueOf(glucidesStr);
                Float proteines = proteinesStr.isEmpty() ? null : Float.valueOf(glucidesStr);
                Float lipides = lipidesStr.isEmpty() ? null : Float.valueOf(glucidesStr);

                startActivityForResult(RecetteActivity.creerRecette(MainActivity.this, glucides, proteines, lipides), 0);
            }

        });

        Button load = (Button) findViewById(R.id.chargerObjectif);
        load.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Intent myIntent = new Intent(view.getContext(), ListeAlimentActivity.class);
                Intent myIntent = new Intent(view.getContext(), RepasActivity.class);
                startActivityForResult(myIntent, 0);
            }

        });

        glucidesEditText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                onChangeText();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        proteinesEditText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                onChangeText();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        lipidesEditText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                onChangeText();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });


    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    public void onChangeText() {
        if (glucidesEditText.getText().toString().isEmpty() || lipidesEditText.getText().toString().isEmpty() || proteinesEditText.getText().toString().isEmpty()) {
            //Toast.makeText(MainActivity.this, "Toutes les valeurs doivent être renseignées.",
            //      Toast.LENGTH_LONG).show();
        } else {
            try {

                Float lipides = Float.parseFloat(lipidesEditText.getText().toString());
                Float glucides = Float.parseFloat(glucidesEditText.getText().toString());
                Float proteines = Float.parseFloat(proteinesEditText.getText().toString());

                kcalText.setText(String.valueOf(Tools.getKcal(lipides, glucides, proteines)));
                ratioText.setText(Tools.getRatio(lipides, glucides, proteines));

            } catch (NumberFormatException e) {
                Toast.makeText(MainActivity.this, e.toString(),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

}
