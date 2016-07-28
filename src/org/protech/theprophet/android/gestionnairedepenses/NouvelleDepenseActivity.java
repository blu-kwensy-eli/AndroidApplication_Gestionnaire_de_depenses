/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.protech.theprophet.android.gestionnairedepenses;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Calendar;
import java.util.Date;
import org.protech.theprophet.android.gestionnairedepenses.customviews.Button;
import org.protech.theprophet.android.gestionnairedepenses.dao.DepenseDAO;
import org.protech.theprophet.android.gestionnairedepenses.entities.Depense;
import org.protech.theprophet.android.gestionnairedepenses.utils.DepenseFormatter;

/**
 *
 * @author TheProphet
 */
public class NouvelleDepenseActivity extends Activity implements View.OnClickListener {

    private ScrollView rootLayout;
    private Button okBtn = null;
    private Spinner nouvelleDepenseTypeSpinner;
    private Spinner nouvelleDepenseDeviseSpinner;
    private EditText nouvelleDepenseMontantEditText;
    private EditText nouvelleDepenseLibelleEditText;
    private EditText nouvelleDepenseDateEditText;
    private DatePicker nouvelleDepenseDateDatePicker;
    private TimePicker nouvelleDepenseHeureTimePicker;
    private final Date dateSysteme = Calendar.getInstance().getTime();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        rootLayout = (ScrollView) View.inflate(this, R.layout.nouvelle_depense, null);

        nouvelleDepenseLibelleEditText = (EditText) rootLayout.findViewById(R.id.nouvelle_depense_libelle_edit_text);
        nouvelleDepenseMontantEditText = (EditText) rootLayout.findViewById(R.id.nouvelle_depense_montant_edit_text);

        nouvelleDepenseDateEditText = (EditText) rootLayout.findViewById(R.id.nouvelle_depense_date_edit_text);
        //peut être null car ce View n'existe que dans l'affichage en portrait
        if (nouvelleDepenseDateEditText != null) {
            nouvelleDepenseDateEditText.setText(DepenseFormatter.formatDateToString(dateSysteme));
        }
        //peut être null car ce View n'existe que dans l'affichage en paysage
        nouvelleDepenseDateDatePicker = (DatePicker) rootLayout.findViewById(R.id.nouvelle_depense_date_date_picker);
        if (nouvelleDepenseDateDatePicker != null) {
        }
//peut être null car ce View n'existe que dans l'affichage en paysage
        nouvelleDepenseHeureTimePicker = (TimePicker) rootLayout.findViewById(R.id.nouvelle_depense_heure_time_picker);
        if (nouvelleDepenseHeureTimePicker != null) {
            nouvelleDepenseHeureTimePicker.setIs24HourView(Boolean.TRUE);
        }

        nouvelleDepenseTypeSpinner = (Spinner) rootLayout.findViewById(R.id.nouvelle_depense_type_spinner);
        ArrayAdapter adapter1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Depense.TYPES_DEPENSE_DEFAUT);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nouvelleDepenseTypeSpinner.setAdapter(adapter1);

        nouvelleDepenseDeviseSpinner = (Spinner) rootLayout.findViewById(R.id.nouvelle_depense_devise_spinner);
        ArrayAdapter adapter2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Depense.DEVISES_DEPENSE_DEFAUT);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nouvelleDepenseDeviseSpinner.setAdapter(adapter2);

        okBtn = (Button) rootLayout.findViewById(R.id.nouvelle_depense_ok_btn);
        okBtn.setOnClickListener(this);

//android.R.drawable.
        setContentView(rootLayout);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nouvelle_depense_ok_btn:
                String debugText = "";
                //Montant
                String mont = nouvelleDepenseMontantEditText.getText().toString().trim();
                debugText += "Montant : '" + mont + "'\n";
                //devise
                String dev = ((String) nouvelleDepenseDeviseSpinner.getSelectedItem()).trim();
                debugText += "Devise : '" + dev + "'\n";
                //libellé
                String lib = nouvelleDepenseLibelleEditText.getText().toString().trim();
                debugText += "Libellé : '" + lib + "'\n";
                //date
                String dat = "";
                if (nouvelleDepenseDateEditText != null) {
                    dat = (String) nouvelleDepenseDateEditText.getText().toString();
                    debugText += "Date : '" + dat + "'\n";
                } else {

                    //Année, mois, et jour
                    if (nouvelleDepenseDateDatePicker != null) {
                        Date d = new Date(nouvelleDepenseDateDatePicker.getCalendarView().getDate());
                        dat = DepenseFormatter.getDatePart(d);

                    }
                    //Heure et minute
                    if (nouvelleDepenseHeureTimePicker != null) {
                        //Heure
                        Integer heure = nouvelleDepenseHeureTimePicker.getCurrentHour();
                        //Minute
                        Integer minute = nouvelleDepenseHeureTimePicker.getCurrentMinute();
                        //Format HH:mm
                        //Heure et minute sur deux chiffres
                        dat += " " + String.format("%02d", heure) + ":" + String.format("%02d", minute);

                    }
                    debugText += "Date : '" + dat + "'\n";
                }
                dat = dat.trim();
                //Type
                String typ = nouvelleDepenseTypeSpinner.getSelectedItem().toString().trim();
                debugText += "Type : '" + typ + "'";

                // Validation des entrées
                String errorText = "";
                boolean addLineBreak = false;

                addLineBreak = dat.isEmpty() || lib.isEmpty();
                if (mont == null || mont.isEmpty() || (mont.lastIndexOf(".")==0 && mont.length()==1)) {
                    errorText += "Veuillez saisir un montant valide" + (addLineBreak ? "\n" : "");
                }

                addLineBreak = dat.isEmpty();
                if (lib.isEmpty()) {
                    errorText += "Veuillez saisir un libellé" + (addLineBreak ? "\n" : "");
                }
                addLineBreak = false;
                if (dat.isEmpty()) {
                    errorText += "Veuillez saisir une date" + (addLineBreak ? "\n" : "");
                }

//                Toast.makeText(this, debugText, Toast.LENGTH_LONG).show();
                if (!errorText.isEmpty()) {
                    Toast.makeText(this, errorText, Toast.LENGTH_LONG).show();
                    return;
                }

                //base de données
                DepenseDAO dao = new DepenseDAO(this);
                SQLiteDatabase db = dao.open();
                dao.create(new Depense(lib, Double.valueOf(mont), typ, dateSysteme, DepenseFormatter.formatStringToDate(dat), dev));
//                int count = dao.findAllASC().getCount();
//                Toast.makeText(this, "Nombre de tuples : " + count, Toast.LENGTH_LONG).show();
                db.close();

                Intent data = new Intent();
                data.putExtra("montant", Double.valueOf(mont));
                setResult(RESULT_OK, data);
                finish();
                break;
        }
    }

    /**
     * méthode appelée au changement de configuration (passage mode portrait -->
     * mode paysage par exemple) dépréciée
     *
     * @return
     */
    @Override
    public Object onRetainNonConfigurationInstance() {
        return super.onRetainNonConfigurationInstance(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void onPause() {
        super.onPause(); //To change body of generated methods, choose Tools | Templates.
//        Toast.makeText(this, "appel onPause NouvelleDepense", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume(); //To change body of generated methods, choose Tools | Templates.
//        Toast.makeText(this, "appel onResume NouvelleDepense", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy(); //To change body of generated methods, choose Tools | Templates.
//        Toast.makeText(this, "appel onDestroy NouvelleDepense", Toast.LENGTH_LONG).show();

    }

}
