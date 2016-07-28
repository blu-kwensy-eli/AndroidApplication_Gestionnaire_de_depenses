/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.protech.theprophet.android.gestionnairedepenses.utils;

import android.text.format.DateFormat;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.protech.theprophet.android.gestionnairedepenses.entities.Depense;

/**
 *
 * @author TheProphet
 */
public class DepenseFormatter {

    public static String formatMontantToView(Depense d) {
        return formatMontantToView(d.getMontant(), d.getDevise());
    }

    /**
     * Formatter la représentation chaîne de caractères de la valeur numérique
     * représentant un montant et la devise dans laquelle elle est évaluée
     *
     * @param montantReel la valeur numérique représentant un montant
     * @param devise la devise dans laquelle elle est évaluée
     * @return
     */
    public static String formatMontantToView(Double montantReel, String devise) {

        String deviseLocal = (devise == null) ? "" : devise;

        //initialisation de la représentation chaine du montant à sa valeur réelle avec partie décimale (2 chiffres après la virgule)
        String montant = String.format("%.2f", montantReel);
        //si le montant n'a pas de partie décimale, on affiche la valeur entière
        if (montantReel.intValue() == montantReel.doubleValue()) {
            montant = String.format("%.0f", montantReel);
        }

//        //initialisation de la représentation chaine du montant à sa valeur réelle avec partie décimale
//        String montant = montantReel.toString();
//        //si le montant n'a pas de partie décimale
//        if (montantReel.intValue() == montantReel.doubleValue()) {
//            montant = String.valueOf(montantReel.intValue());
//        }
        //par défaut on met le montant après la devise
        String montantAvecDevise = montant + " " + deviseLocal;
        //cas particuliers
        if (Depense.DEVISE_USD.equals(deviseLocal)) {
            montantAvecDevise = "$ " + montant;
        }else if(Depense.DEVISE_EURO.equals(deviseLocal)) {
            montantAvecDevise =  montant+" €";
        }
        
        return montantAvecDevise.trim();
    }

    public static String formatMontantToViewForEdition(Double montantReel, String devise) {
        //remplacer la virgule décimale par le point décimal
        return formatMontantToView(montantReel, devise).replace(",", ".");
    }

    public static String formatTypeToView(Depense d) {
        String type = "(" + d.getType().charAt(0) + ")";
        return type;
    }

    public static Long formatDateSystemeToDB(Depense d) {
        return d.getDateSysteme().getTime();
    }

    public static Long formatDateRelleToDB(Depense d) {
        return d.getDateRelle().getTime();
    }

    public static Date formatDateSystemeDBToModel(Long date) {
        return new Date(date);
    }

    public static Date formatDateRelleDBToModel(Long date) {
        return new Date(date);
    }

    public static String formatDateToString(Date date) {

        return (date != null) ? DateFormat.format(Depense.FORMAT_DATE_HEURE, date).toString() : "";
    }

    public static String getDatePart(Date date) {

        return (date != null) ? DateFormat.format(Depense.FORMAT_DATE, date).toString() : "";
    }

    public static String getTimePart(Date date) {

        return (date != null) ? DateFormat.format(Depense.FORMAT_HEURE, date).toString() : "";
    }

    public static Date formatStringToDate(String date) {
        Date d = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat(Depense.FORMAT_DATE_HEURE);
            d = format.parse(date);
        } catch (ParseException ex) {
            Toast.makeText(null, "format de date incorrect", Toast.LENGTH_LONG).show();
        }
        return d;
    }
}
