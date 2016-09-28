package com.baptiste.cetokids.helper;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;


/**
 * Created by Baptiste on 22/09/2016.
 */
public class Tools {

    public static int getKcal(Float lipide, Float glucide, Float proteine) {
        return Math.round((glucide * 4) + (lipide * 9) + (proteine * 4));
    }

    public static String getRatio(Float lipide, Float glucide, Float proteine) {
        try {
            if (!(lipide == 0 || glucide == 0 || proteine == 0)) {
                float ratio = lipide / (glucide + proteine);
                DecimalFormat df = new DecimalFormat("###.#", DecimalFormatSymbols.getInstance(Locale.FRENCH));
                return String.valueOf(df.format(ratio)) + ":1";
            }
        } catch (NumberFormatException e) {
            return "";
        }
        return "0";
    }
}
