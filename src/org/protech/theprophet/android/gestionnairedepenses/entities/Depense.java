/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.protech.theprophet.android.gestionnairedepenses.entities;

import android.widget.Checkable;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author TheProphet
 */
public class Depense implements Serializable{

    public static final String[] TYPES_DEPENSE_DEFAUT = new String[]{
        "Divers","Alimentation", "Transport", "Logement", "Communication"
    };
    public static final String DEVISE_EURO = "EUR";
    public static final String DEVISE_USD = "USD";
    public static final String DEVISE_FCFA = "FCFA";
    public static final String[] DEVISES_DEPENSE_DEFAUT = new String[]{
        DEVISE_FCFA, DEVISE_USD, DEVISE_EURO
    };
    public static String FORMAT_HEURE = "HH:mm";
    public static String FORMAT_DATE = "dd/MM/yyyy";
    public static String FORMAT_DATE_HEURE = FORMAT_DATE+" "+FORMAT_HEURE;
    /**
     * Identifiant de la dépense
     */
    private Long id;
    /**
     * Libellé de la dépense
     */
    private String libelle;
    /**
     * Le montant de la dépense
     */
    private Double montant;
    /**
     * Le type de la dépense
     */
    private String type;
    /**
     * la date système à laquelle la dépense à été effectuée
     */
    private Date dateSysteme;
    /**

     * la date réelle à laquelle la dépense à été effectuée
     */
    private Date dateRelle;
    /**
     * la devise du montant de la dépense
     */
    private String devise = DEVISES_DEPENSE_DEFAUT[0];
    /**
     * true si cette dépense est la première pour sa date commodité pour
     * l'affichage groupé des dépenses par date
     */
    private boolean premierPourSaDate;
    /**
     * true l'enregistrement correspondant à cette dépense a été supprimer
     */
    private boolean deleted;

    //pour les dépenses qui existent déja
    public Depense(Long id, String libelle, Double montant, String type, Date dateSysteme, Date dateRelle, String devise) {
        //remplacr après par "super" qui est long
        this(libelle, montant, type);
        this.id=id;
        this.dateSysteme = dateSysteme;
        this.dateRelle = dateRelle;
        this.devise = devise;
        
    }
    public Depense(String libelle, Double montant, String type, Date dateSysteme, Date dateRelle, String devise) {
        this(libelle, montant, type);
        this.dateSysteme = dateSysteme;
        this.dateRelle = dateRelle;
        this.devise = devise;
        
    }

    public Depense(String libelle, Double montant, String type) {
        this.libelle = libelle;
        this.montant = montant;
        this.type = type;
        this.premierPourSaDate = false;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDateSysteme() {
        return dateSysteme;
    }

    public void setDateSysteme(Date dateSysteme) {
        this.dateSysteme = dateSysteme;
    }

    public Date getDateRelle() {
        return dateRelle;
    }

    public void setDateRelle(Date dateRelle) {
        this.dateRelle = dateRelle;
    }

    public String getDevise() {
        return devise;
    }

    public void setDevise(String devise) {
        this.devise = devise;
    }

    public boolean isPremierPourSaDate() {
        return premierPourSaDate;
    }

    public void setPremierPourSaDate(boolean premierPourSaDate) {
        this.premierPourSaDate = premierPourSaDate;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    
    //Deux dépenses sont les mêmes si elles ont le même identifiant
    @Override
    public boolean equals(Object o) {
        if(o==null){
            return false;
        }
        return ((Depense)o).getId().equals(id);
    }
    
    

}
