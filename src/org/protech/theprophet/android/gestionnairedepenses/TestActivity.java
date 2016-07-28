/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.protech.theprophet.android.gestionnairedepenses;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import org.protech.theprophet.android.gestionnairedepenses.dao.DepenseDAO;
import org.protech.theprophet.android.gestionnairedepenses.utils.DepenseFormatter;

/**
 *
 * @author TheProphet
 */
public class TestActivity extends Activity {

    View root;
    TextView txt_view;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
//        android.R.drawable.

        setContentView(R.layout.test);
        txt_view = (TextView) findViewById(R.id.text_view);
        DepenseDAO dao = new DepenseDAO(this);
        dao.open();
//        Cursor c = dao.getDb().rawQuery("select id,montant, date_reelle, t.type from depense d, (select type, max(montant) as montant_max from depense group by type) t where t.montant_max = t.montant", null);
        Cursor c = dao.getDb().rawQuery("select id,montant, date_reelle,  max(montant) as montant_max from depense group by type", null);
//        Cursor c = dao.getDb().rawQuery("select id,montant, date_reelle, t.type from depense, (select type, max(montant) as montant_max from depense group by type) t where t.montant_max = depense.montant", null);
        String txt = "";
        while (c.moveToNext()) {
            String montant = String.valueOf(c.getDouble(c.getColumnIndex(DepenseDAO.FIELD_MONTANT)));
            String id = String.valueOf(c.getLong(c.getColumnIndex(DepenseDAO.FIELD_ID)));
            String type = c.getString(c.getColumnIndex(DepenseDAO.FIELD_TYPE));
            String date = DepenseFormatter.formatDateToString(DepenseFormatter.formatDateRelleDBToModel(c.getLong(c.getColumnIndex(DepenseDAO.FIELD_DATE_REELLE))));
            txt += ("id -> " + id + ",type -> " + type + ", montant -> " + montant + ",date -> " + date + "\n");

            txt_view.setText(txt);

        }
    }

//     @Override
//    public void onCreate(Bundle icicle) {
//        super.onCreate(icicle);
////        android.R.drawable.
//    
//
//        root = View.inflate(this, R.layout.test, null);
//         txt_view = (TextView)root.findViewById(R.id.text_view);
//        DepenseDAO dao = new DepenseDAO(this);
//        dao.open();
//        Cursor c =  dao.getDb().rawQuery("select id,montant, date_reelle from depense, (select type, max(montant) as montant_max from depense group by type) t where t.montant_max = depense.montant", null);
//        String txt = "";
//        while (c.moveToNext()) {
//            String montant = String.valueOf(c.getDouble(c.getColumnIndex(DepenseDAO.FIELD_MONTANT)));
//            String id =  String.valueOf(c.getDouble(c.getColumnIndex(DepenseDAO.FIELD_ID)));
//            String date =  DepenseFormatter.formatDateToString(DepenseFormatter.formatDateRelleDBToModel(c.getLong(c.getColumnIndex(DepenseDAO.FIELD_MONTANT))));
//             txt += ("id -> "+id+", montant -> "+montant+"date -> "+date+"\n");
//        }
//        txt_view.setText(txt);
//        setContentView(root);
//    }
}
