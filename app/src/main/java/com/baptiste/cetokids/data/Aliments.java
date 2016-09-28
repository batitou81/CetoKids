package com.baptiste.cetokids.data;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by Baptiste on 11/09/2016.
 */
public class Aliments implements Serializable {

    private int id;
    private String aliment = null;
    private Float proteine;
    private Float glucide;
    private Float lipide;
    private int portion;

    public Aliments() {
        super();
    }

    public Aliments(String aliment, Float proteine, Float glucide, Float lipide, int portion) {
        super();
        this.aliment = aliment;
        this.proteine = proteine;
        this.glucide = glucide;
        this.lipide = lipide;
        this.portion = portion;
        Random rand = new Random();
        this.id = rand.nextInt();
    }

    public Aliments(String aliment, String proteine, String glucide, String lipide, String portion, String id) {
        super();
        this.aliment = aliment;
        this.proteine = Float.parseFloat(proteine);
        this.glucide = Float.parseFloat(glucide);
        this.lipide = Float.parseFloat(lipide);
        this.portion = Integer.parseInt(portion);
        this.id = Integer.parseInt(id);
    }

    public String getAliment() {
        return aliment;
    }

    public void setAliment(String aliment) {
        this.aliment = aliment;
    }

    public Float getProteine() {
        return proteine;
    }

    public void setProteine(Float name) {
        this.proteine = name;
    }

    public Float getGlucide() {
        return glucide;
    }

    public void setGlucide(Float glucide) {
        this.glucide = glucide;
    }

    public Float getLipide() {
        return lipide;
    }

    public void setLipide(Float lipide) {
        this.lipide = lipide;
    }

    public int getPortion() {
        return portion;
    }

    public void setPortion(int portion) {
        this.portion = portion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
