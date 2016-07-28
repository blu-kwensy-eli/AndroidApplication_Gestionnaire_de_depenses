/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.protech.theprophet.android.gestionnairedepenses.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import java.util.HashMap;
import org.protech.theprophet.android.gestionnairedepenses.entities.Depense;
import org.protech.theprophet.android.gestionnairedepenses.utils.DepenseFormatter;

/**
 *
 * @author TheProphet
 */
public class DepenseDAO extends DAOBase {

    //l'utilisateur ne peut
    //nom de la table correspondante
    public static final String TABLE = "Depense";

    //nom des champs
    public static final String FIELD_ID_FOR_CURSOR_ADAPTER = "_id";
    public static final String FIELD_ID = "id";
    public static final String FIELD_LIBELLE = "libelle";
    public static final String FIELD_MONTANT = "montant";
    public static final String FIELD_DEVISE = "devise";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_DATE_SYSTEME = "date_systeme";
    public static final String FIELD_DATE_REELLE = "date_reelle";
    public static final String FIELD_DELETED = "deleted";
    //Toutes les colonnes pour un select global
    public static final String[] COLUMNS = new String[]{FIELD_ID + " as " + FIELD_ID_FOR_CURSOR_ADAPTER, FIELD_ID, FIELD_LIBELLE, FIELD_MONTANT, FIELD_DEVISE, FIELD_TYPE, FIELD_DATE_SYSTEME, FIELD_DATE_REELLE, FIELD_DELETED};

    private static final String DEFAULT_ORDER_BY = FIELD_DATE_REELLE + " DESC";
    //Valeurs de la colonne deleted
    public static final Integer FIELD_DELETED_VALUE_TRUE = 1;
    public static final Integer FIELD_DELETED_VALUE_FALSE = 0;
    //Requete SQL de création
    public static final String SQL_CREATE_TABLE
            = "CREATE TABLE " + TABLE
            + "("
            + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + " libelle TEXT,"
            + " montant REAL,"
            + " devise TEXT,"
            + " type TEXT,"
            + " date_systeme INTEGER,"
            + " date_reelle INTEGER,"
            + " deleted INTEGER"
            + ")";

    public DepenseDAO(Context pContext) {
        super(pContext);
    }

//    public Cursor totalMontants() {        
//        Cursor c = sqliteDatabase.rawQuery("select SUM(" + FIELD_MONTANT + ") as sum from "+ TABLE, null);
//        return c;
//    }
    public Double totalMontants() {
        Cursor c = sqliteDatabase.rawQuery("select SUM(" + FIELD_MONTANT + ") as sum from " + TABLE, null);
        Double sommeDepensesVal = 0d;
        while (c.moveToNext()) {
            sommeDepensesVal = c.getDouble(0);
        }
        c.close();
        return sommeDepensesVal;
    }

    public void create(Depense d) {
        sqliteDatabase.execSQL("insert into depense("
                + FIELD_LIBELLE + ","
                + FIELD_MONTANT + ","
                + FIELD_DEVISE + ","
                + FIELD_TYPE + ","
                + FIELD_DATE_SYSTEME + ","
                + FIELD_DATE_REELLE + ","
                + FIELD_DELETED
                + ") values("
                + "\"" + d.getLibelle() + "\","
                + "\"" + d.getMontant() + "\","
                + "\"" + d.getDevise() + "\","
                + "\"" + d.getType() + "\","
                + "\"" + DepenseFormatter.formatDateSystemeToDB(d) + "\","
                + "\"" + DepenseFormatter.formatDateRelleToDB(d) + "\","
                + "\"" + FIELD_DELETED_VALUE_FALSE + "\""
                + ");");
    }

    public void update(Depense d) {
        ContentValues valeurs = new ContentValues();
        valeurs.put(FIELD_LIBELLE, d.getLibelle());
        valeurs.put(FIELD_MONTANT, d.getMontant());
        valeurs.put(FIELD_DEVISE, d.getDevise());
        valeurs.put(FIELD_TYPE, d.getType());
        valeurs.put(FIELD_DATE_SYSTEME, DepenseFormatter.formatDateSystemeToDB(d));
        valeurs.put(FIELD_DATE_REELLE, DepenseFormatter.formatDateRelleToDB(d));
        valeurs.put(FIELD_DELETED, d.isDeleted() ? FIELD_DELETED_VALUE_TRUE : FIELD_DELETED_VALUE_FALSE);
        //WHERE
        //Expression clause
        String clauseWhere = FIELD_ID + " = ?";
        //Arguments
        String[] argumentsWhere = new String[]{d.getId().toString()};

        sqliteDatabase.update(TABLE, valeurs, clauseWhere, argumentsWhere);
    }

    public void delete(Long id) {
        //WHERE
        //Expression clause
        String clauseWhere = FIELD_ID + " = ?";
        //Arguments
        String[] argumentsWhere = new String[]{id.toString()};
        sqliteDatabase.delete(TABLE, clauseWhere, argumentsWhere);
    }

    public Cursor computeFindAllASC(String fieldName) {
        //Clause distinct
        boolean distinct = true;
        //WHERE
        //Expression clause
        String where = null;
        //Arguments
        String[] argumentsWhere = null;
        //GROUP BY
        //Clause 
        String groupBy = null;
        //conditions having
        String having = null;
        //ORDER BY
        String orderBy = fieldName + " ASC";
        //LIMIT
        String limit = null;

        return sqliteDatabase.query(distinct, TABLE, COLUMNS, where, argumentsWhere, groupBy, having, orderBy, limit);
    }

    public Cursor computeFindAllDESC(String fieldName) {
        //Clause distinct
        boolean distinct = true;
        //WHERE
        //Expression clause
        String where = null;
        //Arguments
        String[] argumentsWhere = null;
        //GROUP BY
        //Clause 
        String groupBy = null;
        //conditions having
        String having = null;
        //ORDER BY
        String orderBy = fieldName + " DESC";
        //LIMIT
        String limit = null;

        return sqliteDatabase.query(distinct, TABLE, COLUMNS, where, argumentsWhere, groupBy, having, orderBy, limit);
    }
    public Cursor computeFindAllDESC(String orderByField1, String orderByField2) {
        //Clause distinct
        boolean distinct = true;
        //WHERE
        //Expression clause
        String where = null;
        //Arguments
        String[] argumentsWhere = null;
        //GROUP BY
        //Clause 
        String groupBy = null;
        //conditions having
        String having = null;
        //ORDER BY
        String orderBy = orderByField1 + ","+orderByField2+ " DESC";
        //LIMIT
        String limit = null;

        return sqliteDatabase.query(distinct, TABLE, COLUMNS, where, argumentsWhere, groupBy, having, orderBy, limit);
    }

    //toutes les dépenses par id croissant
    public Cursor findAllASC() {
        return computeFindAllASC(FIELD_ID);
    }

    //toutes les dépenses par id decroissant
    public Cursor findAllDESC() {
        return computeFindAllDESC(FIELD_ID);
    }

    //toutes les dépenses par date croissant
    public Cursor findAllOrderByDateASC() {
        return computeFindAllASC(FIELD_DATE_REELLE);
    }

    //toutes les dépenses par date decroissant
    public Cursor findAllOrderByDateDESC() {
        return computeFindAllDESC(FIELD_DATE_REELLE);
    }

    //toutes les dépenses par date puis par type decroissant 
    public Cursor findAllOrderByDateAndTypeDESC() {
        return computeFindAllDESC(FIELD_DATE_REELLE, FIELD_TYPE);
    }
    //toutes les dépenses par type puis par date decroissant 
    public Cursor findAllOrderByTypeAndDateDESC() {
        return computeFindAllDESC(FIELD_TYPE, FIELD_DATE_REELLE);
    }

    public Cursor findByTypeASC(String type) {
        //Clause distinct
        boolean distinct = true;
        //WHERE
        //Expression clause
        String where = FIELD_TYPE + " LIKE ? ";
        //Arguments
        String[] argumentsWhere = new String[]{type};
        //GROUP BY
        //Clause 
        String groupBy = null;
        //conditions having
        String having = null;
        //ORDER BY
        String orderBy = FIELD_ID + " ASC";
        //LIMIT
        String limit = null;

        return sqliteDatabase.query(distinct, TABLE, COLUMNS, where, argumentsWhere, groupBy, having, orderBy, limit);
    }

    public Cursor findByLibelle(String libelle) {
        //Clause distinct
        boolean distinct = true;
        //WHERE
        //Expression clause
        String where = null;
        if (!libelle.isEmpty()) {
            where = FIELD_LIBELLE + " LIKE ? ";
        }
        //Arguments
        String[] argumentsWhere = null;
        if (!libelle.isEmpty()) {
            argumentsWhere = new String[]{"%" + libelle + "%"};
        }
        //GROUP BY
        //Clause 
        String groupBy = null;
        //conditions having
        String having = null;
        //ORDER BY
        String orderBy = DEFAULT_ORDER_BY;
        //LIMIT
        String limit = null;

        return sqliteDatabase.query(distinct, TABLE, COLUMNS, where, argumentsWhere, groupBy, having, orderBy, limit);
    }
    public Cursor findByLibelleOrderByTypeAndDateDESC(String libelle) {
        //Clause distinct
        boolean distinct = true;
        //WHERE
        //Expression clause
        String where = null;
        if (!libelle.isEmpty()) {
            where = FIELD_LIBELLE + " LIKE ? ";
        }
        //Arguments
        String[] argumentsWhere = null;
        if (!libelle.isEmpty()) {
            argumentsWhere = new String[]{"%" + libelle + "%"};
        }
        //GROUP BY
        //Clause 
        String groupBy = null;
        //conditions having
        String having = null;
        //ORDER BY
        String orderBy = FIELD_TYPE+","+FIELD_DATE_REELLE+" DESC";
        //LIMIT
        String limit = null;

        return sqliteDatabase.query(distinct, TABLE, COLUMNS, where, argumentsWhere, groupBy, having, orderBy, limit);
    }
    public Cursor findByLibelleOrderByDateDESC(String libelle) {
        //Clause distinct
        boolean distinct = true;
        //WHERE
        //Expression clause
        String where = null;
        if (!libelle.isEmpty()) {
            where = FIELD_LIBELLE + " LIKE ? ";
        }
        //Arguments
        String[] argumentsWhere = null;
        if (!libelle.isEmpty()) {
            argumentsWhere = new String[]{"%" + libelle + "%"};
        }
        //GROUP BY
        //Clause 
        String groupBy = null;
        //conditions having
        String having = null;
        //ORDER BY
        String orderBy = FIELD_DATE_REELLE+" DESC";
        //LIMIT
        String limit = null;

        return sqliteDatabase.query(distinct, TABLE, COLUMNS, where, argumentsWhere, groupBy, having, orderBy, limit);
    }

    //le pourcentage du montant de la depense par rapport aux depenses du jour
    public Double pourcentageJourMontant(Depense dp) {

        //Date d'une dépense est composée de deux parties
        //  Partie Année/mois/jour
        //  Partie Heure:minutes
        //Ici on récupère la première partie
        String date1 = DepenseFormatter.getDatePart(dp.getDateRelle());
        //toutes les dépenses
        Cursor c = findAllASC();
        //somme des dépenses du mm jour
        Double s = 0d;
        //Parcours des dépenses
        while (c.moveToNext()) {
            Long d = c.getLong(c.getColumnIndex(FIELD_DATE_REELLE));
            //date seule courante
            String date2 = DepenseFormatter.getDatePart(DepenseFormatter.formatDateRelleDBToModel(d));

            //si les dates sont egales
            if (date1.equals(date2)) {
                s += c.getDouble(c.getColumnIndex(FIELD_MONTANT));
            }
        }
        c.close();
        return dp.getMontant() * 100 / s;
    }

    //identifiants des dépenses les plus récentes pour chaque journée
    /**
     *
     * @param idFieldName nom du champ identifiant
     * @return
     */
    public HashMap<String, Long> findWithMaxDateGroupByDay() {
        //Ctte requete devrait renvoyer qlq chose comme
        //id                  date_reelle (jj/mm/aaaa hh:ss)  | date_jma (jj/mm/aaaa)
        //1                     111111101                     | 22/05/2016     la première dépense de la date correspondante
        //4                     111345401                     | 21/06/2016     la première dépense de la date correspondante
        //5                     111345402                     | 21/06/2016
        //7                     111456566                     | 05/07/2016     la première dépense de la date correspondante
        //9                     111456567                     | 05/07/2016
        //
        //j'aimerais construire une structure de ce genre ou la date_jma est associée à l'identifiant de la première dépense du jour
        //  22/05/2016 -> 1 , 21/06/2016 -> 4 , 05/07/2016 -> 7
        // 
        Cursor c = sqliteDatabase.rawQuery("SELECT " + FIELD_ID + " ," + FIELD_DATE_REELLE + " from " + TABLE + " order by " + FIELD_DATE_REELLE + " DESC", new String[]{});

        return processFindWithMaxDateGroupByDay(c, FIELD_ID, true);
    }

    
    /**
     * Cette méthode permet de récupérer l'identifiant des dépenses commencant une journée
     * @param cursor Liste de dépenses triée par date décroissant (les dates les plus récentes en premier)
     * @param idFieldName nom du champ identifiant
     * @param closeCursor false Dans le cas ou cette méthode est utilisée dans un CursorAdapter
     
     * @return
     */
    
    public HashMap<String, Long> processFindWithMaxDateGroupByDay(Cursor cursor, String idFieldName, boolean closeCursor) {
        //Le curseur en paramètre devrait contenir au moins ces deux champs id, date_relle
        //
        //id                  date_reelle (jj/mm/aaaa hh:ss)  | date_jma (jj/mm/aaaa)
        //1                     111111101                     | 22/05/2016     la première dépense de la date correspondante
        //4                     111345401                     | 21/06/2016     la première dépense de la date correspondante
        //5                     111345402                     | 21/06/2016
        //7                     111456566                     | 05/07/2016     la première dépense de la date correspondante
        //9                     111456567                     | 05/07/2016
        //
        //j'aimerais construire une structure de ce genre ou la date_jma est associée à l'identifiant de la première dépense du jour
        //  22/05/2016 -> 1 , 21/06/2016 -> 4 , 05/07/2016 -> 7

        //tableau associatif de la date du jour (String) à l'id (Long) de la première dépense
        HashMap<String, Long> m = new HashMap();
        //Positiion des différents champs dans le résultat de requete
        final int indexFieldDateReelle = cursor.getColumnIndex(FIELD_DATE_REELLE);
        final int indexFieldId = cursor.getColumnIndex(idFieldName);
        while (cursor.moveToNext()) {
            //Identifiant de la dépense courante

            long id = cursor.getLong(indexFieldId);
            //Date réelle de la dépense courante
            long dateReelle = cursor.getLong(indexFieldDateReelle);
            //format jj/MM/aaaa de la date
            String date = DepenseFormatter.getDatePart(DepenseFormatter.formatDateRelleDBToModel(dateReelle));

            //si false , vu l'ordre du tri effectué lors de la requete, c'est que la première dépense a été déjà rajoutée
            if (!m.containsKey(date)) {
                m.put(date, id);
            }
        }
        if (closeCursor) {
            cursor.close();
        }
        return m;
    }
    public HashMap<String, Long> processFindWithMaxDateGroupByType(Cursor cursor, String idFieldName, boolean closeCursor) {
        //Curseur trié par date décroissante
        //Le curseur en paramètre devrait contenir au moins ces deux champs id, type
        //
        //id                    Type
        //1                     Alimentation                  | 05/07/2016     la première dépense de la date correspondante
        //4                     Alimentation                  | 05/07/2016
        //5                     Communication                 | 21/06/2016     la première dépense de la date correspondantes
        //7                     Communication                 | 21/06/2016
        //9                     Divers                        | 22/05/2016     la première dépense de la date correspondante
        //
        //j'aimerais construire une structure du genre où le type est associé à l'identifiant la dépense la plus récente du type correspondant
        //  Alimentation -> 1 , Communication -> 5 , Divers -> 9

        //tableau associatif du type (String) à l'id (Long) de dépense
        HashMap<String, Long> m = new HashMap();
        //Positiion des différents champs dans le résultat de requete
        
        final int indexFieldType= cursor.getColumnIndex(FIELD_TYPE);
        final int indexFieldId = cursor.getColumnIndex(idFieldName);
        while (cursor.moveToNext()) {
            //Identifiant de la dépense courante

            long id = cursor.getLong(indexFieldId);
            //le type de la dépense courante
            String type = cursor.getString(indexFieldType);
            
            //si false , vu l'ordre du tri effectué lors de la requete, c'est que la première dépense a été déjà rajoutée
            if (!m.containsKey(type)) {
                m.put(type, id);
            }
        }
        if (closeCursor) {
            cursor.close();
            
        }
        return m;
    }

}
