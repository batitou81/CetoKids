package com.baptiste.cetokids.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baptiste.cetokids.data.Aliments;
import com.example.baptiste.myapplication.R;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Created by Baptiste on 17/09/2016.
 */
public class MyListRecetteAdapater extends BaseAdapter implements ListAdapter {

    private Context context;

    private WeakReference<ListRecetteListener> listenerRef;
    private int scrollStateFinal;
    private List<Aliments> items;

    public MyListRecetteAdapater(Context context, ListRecetteListener listener, List<Aliments> items) {
        this.context = context;
        this.listenerRef = new WeakReference<>(listener);
        this.items = items;
    }


    @Override
    public int getCount() {
        return items.size();
    }

    public Aliments getItem(int position) {
        return items.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, final ViewGroup parent) {

        // Get the current list item
        final Aliments item = items.get(position);
        // Get the layout for the list item
        final RelativeLayout itemLayout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.layout_recette_aliment, parent, false);
        // Set the icon as defined in our list item
        // Set the text label as defined in our list item
        final TextView txtLabel = (TextView) itemLayout.findViewById(R.id.alimentRecette);
        txtLabel.setText(item.getAliment());

        final TextView portionLabel = (TextView) itemLayout.findViewById(R.id.portionAlimentEnG);
        portionLabel.setText("Pour " + item.getPortion() + "g");

        final TextView protLabel = (TextView) itemLayout.findViewById(R.id.proteineRecette);
        protLabel.setText(item.getProteine() + "");

        final TextView protPortionLabel = (TextView) itemLayout.findViewById(R.id.proteineRecettePortion);
        protPortionLabel.setText(item.getProteine() + "");

        final TextView lipLabel = (TextView) itemLayout.findViewById(R.id.lipideRecette);
        lipLabel.setText(item.getLipide() + "");
        final TextView lipPortionLabel = (TextView) itemLayout.findViewById(R.id.lipideRecettePortion);
        lipPortionLabel.setText(item.getLipide() + "");

        final TextView gluLabel = (TextView) itemLayout.findViewById(R.id.glucideRecette);
        gluLabel.setText(item.getGlucide() + "");
        final TextView gluPortionLabel = (TextView) itemLayout.findViewById(R.id.glucideRecettePortion);
        gluPortionLabel.setText(item.getGlucide() + "");


        final NumberPicker portionEdit = (NumberPicker) itemLayout.findViewById(R.id.portionAliment);

        portionEdit.setMinValue(0);
        portionEdit.setMaxValue(1000);
        portionEdit.setWrapSelectorWheel(false);
        portionEdit.setValue(item.getPortion());

        gluLabel.setText(item.getGlucide().toString());
        lipLabel.setText(item.getLipide().toString());
        protLabel.setText(item.getProteine().toString());


        portionLabel.setText("Pour " + item.getPortion() + "g");
        gluPortionLabel.setText(round(item.getGlucide() / (Float.parseFloat("100") / item.getPortion()), 2) + "");
        lipPortionLabel.setText(round(item.getLipide() / (Float.parseFloat("100") / item.getPortion()), 2) + "");
        protPortionLabel.setText(round(item.getProteine() / (Float.parseFloat("100") / item.getPortion()), 2) + "");


        scrollStateFinal = 0;
        portionEdit.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (scrollStateFinal == 0) {


                    item.setPortion(picker.getValue());

                    portionLabel.setText("Pour " + item.getPortion() + "g");
                    gluPortionLabel.setText(round(item.getGlucide() / (Float.parseFloat("100") / item.getPortion()), 2) + "");
                    lipPortionLabel.setText(round(item.getLipide() / (Float.parseFloat("100") / item.getPortion()), 2) + "");
                    protPortionLabel.setText(round(item.getProteine() / (Float.parseFloat("100") / item.getPortion()), 2) + "");

                    ListRecetteListener listener = MyListRecetteAdapater.this.listenerRef.get();
                    listener.onRecetteChanged();

                }
            }
        });

        portionEdit.setOnScrollListener(new NumberPicker.OnScrollListener() {

            private int oldValue;  //You need to init this value.

            @Override
            public void onScrollStateChange(NumberPicker numberPicker, int scrollState) {
                scrollStateFinal = scrollState;


                if (scrollState == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE) {
                    //We get the different between oldValue and the new value
                    int valueDiff = numberPicker.getValue() - oldValue;

                    //Update oldValue to the new value for the next scroll
                    oldValue = numberPicker.getValue();

                    //Do action with valueDiff

                    item.setPortion(numberPicker.getValue());

                    portionLabel.setText("Pour " + item.getPortion() + "g");
                    gluPortionLabel.setText(round(item.getGlucide() / (Float.parseFloat("100") / item.getPortion()), 2) + "");
                    lipPortionLabel.setText(round(item.getLipide() / (Float.parseFloat("100") / item.getPortion()), 2) + "");
                    protPortionLabel.setText(round(item.getProteine() / (Float.parseFloat("100") / item.getPortion()), 2) + "");

                    ListRecetteListener listener = MyListRecetteAdapater.this.listenerRef.get();
                    listener.onRecetteChanged();
                }
            }
        });

        return itemLayout;
    }

    private static float round(float value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        super.unregisterDataSetObserver(observer);
    }

    public interface ListRecetteListener {
        void onRecetteChanged();
    }
}