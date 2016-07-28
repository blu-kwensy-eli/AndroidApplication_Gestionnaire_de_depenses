/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.protech.theprophet.android.gestionnairedepenses.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
import org.protech.theprophet.android.gestionnairedepenses.dao.ConfigurationDAO;
import org.protech.theprophet.android.gestionnairedepenses.dao.DepenseDAO;
import org.protech.theprophet.android.gestionnairedepenses.entities.Depense;

/**
 *
 * @author TheProphet
 */
public class AppSQLiteOpenHelper extends SQLiteOpenHelper {

    private Context ctx;

    public AppSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        ctx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ///vesion 1
        db.execSQL(DepenseDAO.SQL_CREATE_TABLE);
        //db.rawQuery("insert into "+DepenseDAO.TABLE+"("+DepenseDAO.FIELD_LIBELLE+","+DepenseDAO.FIELD_MONTANT+","+DepenseDAO.FIELD_TYPE+","+DepenseDAO.FIELD_DEVISE+","+DepenseDAO.FIELD_DATE_REELLE+") values('Test',123.45, 'Communication', 'USD',"+Calendar.getInstance().getTimeInMillis()+")", null);
        //version 2
        upgradeVersionNumber02(db);
//        Toast.makeText(ctx, "appel onCreate db", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //quand l'utilisateur met à jour son appli on ne fait rien
        Toast.makeText(ctx, "appel onUpgrade db", Toast.LENGTH_LONG).show();
        if (newVersion == 2) {
            Toast.makeText(ctx, "new version == 2", Toast.LENGTH_LONG).show();
            upgradeVersionNumber02(db);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Toast.makeText(ctx, "appel onDowngrade db", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
//        Toast.makeText(ctx, "appel onOpen ", Toast.LENGTH_LONG).show();
    }

    //Historique évolution application
    private void upgradeVersionNumber02(SQLiteDatabase db) {
        db.execSQL(ConfigurationDAO.SQL_CREATE_TABLE);
        db.execSQL("delete from "+ConfigurationDAO.TABLE);
        //insertions des configurations par défaut;
        String insert = "insert into " + ConfigurationDAO.TABLE + "(" + ConfigurationDAO.FIELD_CODE + "," + ConfigurationDAO.FIELD_VALEUR + ") "
                + "values"
                + "('" + ConfigurationDAO.CONFIG_CODE_DEVISE_PAR_DEFAUT + "','" + Depense.DEVISES_DEPENSE_DEFAUT[0] + "'),"
                + "('" + ConfigurationDAO.CONFIG_CODE_TAUX_DE_CHANGE_EUR_FCFA + "','" + ConfigurationDAO.VALEUR_DEFAUT_TAUX_DE_CHANGE_EUR_FCFA + "'),"
                + "('" + ConfigurationDAO.CONFIG_CODE_TAUX_DE_CHANGE_USD_FCFA + "','" + ConfigurationDAO.VALEUR_DEFAUT_TAUX_DE_CHANGE_USD_FCFA + "')"
                + "";
        db.execSQL(insert);
        //Insertion des configurations automatiquement générés
        String codeTaux, valeurTaux;
        
        // FCFA --> EUR
         codeTaux = "TAUX_DE_CHANGE_"+Depense.DEVISE_FCFA+"_"+Depense.DEVISE_EURO;
         valeurTaux = String.valueOf(1/Double.valueOf(ConfigurationDAO.VALEUR_DEFAUT_TAUX_DE_CHANGE_EUR_FCFA ));
         db.execSQL( "insert into " + ConfigurationDAO.TABLE + "(" + ConfigurationDAO.FIELD_CODE + "," + ConfigurationDAO.FIELD_VALEUR + ") values('" + codeTaux + "','" + valeurTaux + "')");
        // FCFA --> USD
         codeTaux = "TAUX_DE_CHANGE_"+Depense.DEVISE_FCFA+"_"+Depense.DEVISE_USD;
         valeurTaux = String.valueOf(1/Double.valueOf(ConfigurationDAO.VALEUR_DEFAUT_TAUX_DE_CHANGE_USD_FCFA ));
         db.execSQL( "insert into " + ConfigurationDAO.TABLE + "(" + ConfigurationDAO.FIELD_CODE + "," + ConfigurationDAO.FIELD_VALEUR + ") values('" + codeTaux + "','" + valeurTaux + "')");
         
        // EUR --> USD == EUR --> FCFA puis FCFA --> USD 
         codeTaux = "TAUX_DE_CHANGE_"+Depense.DEVISE_EURO+"_"+Depense.DEVISE_USD;
         valeurTaux = String.valueOf(Double.valueOf(ConfigurationDAO.VALEUR_DEFAUT_TAUX_DE_CHANGE_EUR_FCFA ) / Double.valueOf(ConfigurationDAO.VALEUR_DEFAUT_TAUX_DE_CHANGE_USD_FCFA ));
         db.execSQL( "insert into " + ConfigurationDAO.TABLE + "(" + ConfigurationDAO.FIELD_CODE + "," + ConfigurationDAO.FIELD_VALEUR + ") values('" + codeTaux + "','" + valeurTaux + "')");
         
        // USD --> EUR == USD --> FCFA puis FCFA --> EUR 
         codeTaux = "TAUX_DE_CHANGE_"+Depense.DEVISE_USD+"_"+Depense.DEVISE_EURO;
         valeurTaux = String.valueOf(Double.valueOf(ConfigurationDAO.VALEUR_DEFAUT_TAUX_DE_CHANGE_USD_FCFA ) / Double.valueOf(ConfigurationDAO.VALEUR_DEFAUT_TAUX_DE_CHANGE_EUR_FCFA ));
         db.execSQL( "insert into " + ConfigurationDAO.TABLE + "(" + ConfigurationDAO.FIELD_CODE + "," + ConfigurationDAO.FIELD_VALEUR + ") values('" + codeTaux + "','" + valeurTaux + "')");
         
//        Toast.makeText(ctx, "insertion OK...", Toast.LENGTH_LONG).show();
        
    }
}
