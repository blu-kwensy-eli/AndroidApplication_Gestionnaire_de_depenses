/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.protech.theprophet.android.gestionnairedepenses.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

/**
 *
 * @author TheProphet
 */
public class ConfigurationDAO extends DAOBase {

    //l'utilisateur ne peut
    //nom de la table correspondante
    public static final String TABLE = "Congiguration";

    //nom des champs
    public static final String FIELD_CODE = "code";
    public static final String FIELD_VALEUR = "valeur";

    public static final String CONFIG_CODE_DEVISE_PAR_DEFAUT = "DEVISE_PAR_DEFAUT";
    public static final String LABEL_TAUX_DE_CHANGE = "TAUX_DE_CHANGE";
    //Taux de change par défaut
    public static final String CONFIG_CODE_TAUX_DE_CHANGE_EUR_FCFA = "TAUX_DE_CHANGE_EUR_FCFA";
    public static final String CONFIG_CODE_TAUX_DE_CHANGE_USD_FCFA = "TAUX_DE_CHANGE_USD_FCFA";
    public static final String VALEUR_DEFAUT_TAUX_DE_CHANGE_EUR_FCFA = ""+655.95;    
    public static final String VALEUR_DEFAUT_TAUX_DE_CHANGE_USD_FCFA = ""+500.00;
    //Taux de change dérivés
    public static final String CONFIG_CODE_TAUX_DE_CHANGE_FCFA_EUR = "TAUX_DE_CHANGE_FCFA_EUR";
    public static final String CONFIG_CODE_TAUX_DE_CHANGE_FCFA_USD = "TAUX_DE_CHANGE_FCFA_USD";
    public static final String CONFIG_CODE_TAUX_DE_CHANGE_EUR_USD = "TAUX_DE_CHANGE_EUR_USD";
    public static final String CONFIG_CODE_TAUX_DE_CHANGE_USD_EUR = "TAUX_DE_CHANGE_USD_EUR";
    
    
    //Toutes les colonnes pour un select global
    public static final String[] COLUMNS = new String[]{FIELD_CODE, FIELD_VALEUR};

    //Requete SQL de création
    public static final String SQL_CREATE_TABLE
            = "CREATE TABLE IF NOT EXISTS " + TABLE
            + "("
            + " " + FIELD_CODE + " TEXT,"
            + " " + FIELD_VALEUR + " TEXT"
            + ")";

    public ConfigurationDAO(Context pContext) {
        super(pContext);
    }

    public Cursor findByCode(String code) {
        //Clause distinct
        boolean distinct = true;
        //WHERE
        //Expression clause
        String where = FIELD_CODE + " LIKE ? ";
        //Arguments
        String[] argumentsWhere = new String[]{code};
        //GROUP BY
        //Clause 
        String groupBy = null;
        //conditions having
        String having = null;
        //ORDER BY
        String orderBy = FIELD_CODE +" ASC ";
        //LIMIT
        String limit = null;

        return sqliteDatabase.query(distinct, TABLE, COLUMNS, where, argumentsWhere, groupBy, having, orderBy, limit);
    }

    public String findValeurByCode(String code) {
        Cursor c = findByCode(code);
        String valeur = "";
        while (c.moveToNext()) {
            valeur = c.getString(c.getColumnIndex(FIELD_VALEUR));
        }
//                Toast.makeText(super.ctx, "nombre de lignes de configuration"+c.getCount(), Toast.LENGTH_LONG).show();
        c.close();
        return valeur;
    }

    public String findValeurByDevises(String deviseDepart, String deviseArrivee) {
        if(deviseDepart.equals(deviseArrivee)){
            return ""+1d;
        }
        String code = "TAUX_DE_CHANGE_"+deviseDepart+"_"+deviseArrivee;
        return  findValeurByCode(code);
    }
    
    public void update(String code, String valeur){
        ContentValues valeurs = new ContentValues();
        valeurs.put(FIELD_VALEUR, valeur);
        //WHERE
        //Expression clause
        String clauseWhere = FIELD_CODE + " LIKE ? ";
        //Arguments
        String[] argumentsWhere = new String[]{code};
        sqliteDatabase.update(TABLE, valeurs, clauseWhere, argumentsWhere);
        
                        Toast.makeText(super.ctx, "appael update ("+code+","+valeur+")", Toast.LENGTH_LONG).show();
        //En cas de mise a jour d'un taux de change on met a jour les taux de change dérivé
        
        //si EUR --> FCFA change, 
        if(code.equals(CONFIG_CODE_TAUX_DE_CHANGE_EUR_FCFA)){            
            double  EUR_FCFA = Double.valueOf(valeur);
            //on met a jour FCFA --> EUR
            update(CONFIG_CODE_TAUX_DE_CHANGE_FCFA_EUR, String.valueOf(1/EUR_FCFA));
            //on met a jour EUR --> USD
            double USD_FCFA = Double.valueOf(findValeurByCode(CONFIG_CODE_TAUX_DE_CHANGE_USD_FCFA));
            update(CONFIG_CODE_TAUX_DE_CHANGE_EUR_USD, String.valueOf(EUR_FCFA/USD_FCFA) );
            //on met a jour USD --> EUR
            update(CONFIG_CODE_TAUX_DE_CHANGE_USD_EUR, String.valueOf(USD_FCFA/EUR_FCFA) );
        }
        
        //si USD --> FCFA change, 
        if(code.equals(CONFIG_CODE_TAUX_DE_CHANGE_USD_FCFA)){            
            double  USD_FCFA = Double.valueOf(valeur);
            //on met a jour FCFA --> USD
            update(CONFIG_CODE_TAUX_DE_CHANGE_FCFA_USD, String.valueOf(1/USD_FCFA));
            //on met a jour EUR --> USD
            double EUR_FCFA = Double.valueOf(findValeurByCode(CONFIG_CODE_TAUX_DE_CHANGE_EUR_FCFA));
            update(CONFIG_CODE_TAUX_DE_CHANGE_EUR_USD, String.valueOf(EUR_FCFA/USD_FCFA) );
            //on met a jour USD --> EUR
            update(CONFIG_CODE_TAUX_DE_CHANGE_USD_EUR, String.valueOf(USD_FCFA/EUR_FCFA) );
        }
        
        
    }
}
