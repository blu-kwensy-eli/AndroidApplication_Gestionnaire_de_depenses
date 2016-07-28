/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.protech.theprophet.android.gestionnairedepenses.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import org.protech.theprophet.android.gestionnairedepenses.utils.AppSQLiteOpenHelper;

/**
 *
 * @author TheProphet
 */
public abstract class DAOBase {
    // Nous sommes à la première version de la base
    // Si je décide de la mettre à jour, il faudra changer cet attribut
    //évolution à la version 2 le 18/06/2016
    protected final static int VERSION = 2;
    // Le nom du fichier qui représente ma base
    protected final static String NOM = "gestionnaire_depense_database.db";
    protected SQLiteDatabase sqliteDatabase = null;
    protected AppSQLiteOpenHelper appSqliteOpenHelper = null;

    protected Context ctx;
    public DAOBase(Context pContext) {
        ctx = pContext;
        this.appSqliteOpenHelper = new AppSQLiteOpenHelper(pContext, NOM, null,
                VERSION);
    }

    public SQLiteDatabase open() {
        // Pas besoin de fermer la dernière base puisque s'en charge
        sqliteDatabase = appSqliteOpenHelper.getWritableDatabase();
        return sqliteDatabase;
    }

    public void close() {
        sqliteDatabase.close();
    }

    public SQLiteDatabase getDb() {
        return sqliteDatabase;
    }
}
