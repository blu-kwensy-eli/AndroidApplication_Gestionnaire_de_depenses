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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.Calendar;
import java.util.Date;
import org.protech.theprophet.android.gestionnairedepenses.dao.DepenseDAO;
import org.protech.theprophet.android.gestionnairedepenses.entities.Depense;
import org.protech.theprophet.android.gestionnairedepenses.utils.DepenseFormatter;

/**
 *
 * @author TheProphet
 */
public class ModifierDepenseActivity extends Activity implements View.OnClickListener {

    private ScrollView rootLayout;
    private Button okBtn = null;
    private Spinner modifierDepenseTypeSpinner;
    private Spinner modifierDepenseDeviseSpinner;
    private EditText modifierDepenseMontantEditText;
    private EditText modifierDepenseLibelleEditText;
    private EditText modifierDepenseDateEditText;
    //Dépense en train d'être modifiée
    private Depense depense;
    
    private final Date dateSysteme = Calendar.getInstance().getTime();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        rootLayout = (ScrollView) View.inflate(this, R.layout.modifier_depense, null);
        Intent i = getIntent();
        //Code pour retrouver la dépense d'id idDepense depuis la base de données
        //A venir
        //........
        //........
        //........
        //........
        //
        depense = (Depense) i.getSerializableExtra("depense");

        modifierDepenseLibelleEditText = (EditText) rootLayout.findViewById(R.id.modifier_depense_libelle_edit_text);
        modifierDepenseLibelleEditText.setText(depense.getLibelle());
        modifierDepenseMontantEditText = (EditText) rootLayout.findViewById(R.id.modifier_depense_montant_edit_text);
        modifierDepenseMontantEditText.setText(DepenseFormatter.formatMontantToViewForEdition(depense.getMontant(), ""));

        modifierDepenseDateEditText = (EditText) rootLayout.findViewById(R.id.modifier_depense_date_edit_text);
        //peut être null car ce View n'existe que dans l'affichage en portrait//REVOIR
        if (modifierDepenseDateEditText != null) {
            modifierDepenseDateEditText.setText(DepenseFormatter.formatDateToString(depense.getDateRelle()));
        }

        modifierDepenseTypeSpinner = (Spinner) rootLayout.findViewById(R.id.modifier_depense_type_spinner);
        ArrayAdapter adapter1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Depense.TYPES_DEPENSE_DEFAUT);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modifierDepenseTypeSpinner.setAdapter(adapter1);
        //Sélectionner la position dans la liste de choix des types, qui correspond au type de la dépense qu'on veut modifier
        int position = 0;
        for (int j = 0; j < Depense.TYPES_DEPENSE_DEFAUT.length; j++) {
            if(Depense.TYPES_DEPENSE_DEFAUT[j].equals(depense.getType())){
                position=j;
                break;
            }            
        }
        //on sélectionne donc  le type correspodant
        modifierDepenseTypeSpinner.setSelection(position);

        modifierDepenseDeviseSpinner = (Spinner) rootLayout.findViewById(R.id.modifier_depense_devise_spinner);
        ArrayAdapter adapter2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Depense.DEVISES_DEPENSE_DEFAUT);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modifierDepenseDeviseSpinner.setAdapter(adapter2);
        //Sélectionner la position dans la liste de choix des devises, qui correspond à la devise de la dépense qu'on veut modifier
        position = 0;
        for (int j = 0; j < Depense.DEVISES_DEPENSE_DEFAUT.length; j++) {
            if(Depense.DEVISES_DEPENSE_DEFAUT[j].equals(depense.getDevise())){
                position=j;
                break;
            }            
        }
        //on sélectionne donc la devise correspodante
        modifierDepenseDeviseSpinner.setSelection(position);
        
        
        okBtn = (Button) rootLayout.findViewById(R.id.modifier_depense_ok_btn);
        okBtn.setOnClickListener(this);

//android.R.drawable.
        setContentView(rootLayout);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.modifier_depense_ok_btn:
                String debugText = "";
                //Montant
                String mont = (String) modifierDepenseMontantEditText.getText().toString();
                debugText += "Montant : " + mont + "\n";
                //devise
                String dev = (String) modifierDepenseDeviseSpinner.getSelectedItem();
                debugText += "Devise : " + dev + "\n";
                //libellé
                String lib = (String) modifierDepenseLibelleEditText.getText().toString();
                debugText += "Libellé : " + lib + "\n";
                //date
                String dat = "";
                if (modifierDepenseDateEditText != null) {
                    dat = (String) modifierDepenseDateEditText.getText().toString();
                    debugText += "Date : " + dat + "\n";
                }
                //Type
                String typ = (String) modifierDepenseTypeSpinner.getSelectedItem();
                debugText += "Type : " + typ;

                // Validation des entrées
                String errorText = "";
                boolean addLineBreak = false;
                if (modifierDepenseDateEditText != null) {

                    addLineBreak = dat.isEmpty() || lib.isEmpty();
                    //Si vide ou ne contient que le point décimal
                    if (mont.isEmpty() || (mont.lastIndexOf(".")==0 && mont.length()==1)) {
                        errorText += "Veuillez saisir un montant valide"+ (addLineBreak?"\n":"");
                    }                    
                    addLineBreak = dat.isEmpty();
                    if (lib.isEmpty()) {
                        errorText += "Veuillez saisir un libellé"+ (addLineBreak?"\n":"");
                    }
                    addLineBreak = false;
                    if (dat.isEmpty()) {
                        errorText += "Veuillez saisir une date"+ (addLineBreak?"\n":"");
                    }
//                    if(DepenseFormatter.formatStringToDate(this, dat)==null){
//                        errorText+="Veuillez saisir une date valide\n";
//                    }
                }
                if(errorText.length()!=0){
                    Toast.makeText(this, errorText, Toast.LENGTH_LONG).show();
                    return;
                }
//                Depense newD = new De

//                Toast.makeText(this, debugText, Toast.LENGTH_LONG).show();
                //base de données
                DepenseDAO dao = new DepenseDAO(this);
                SQLiteDatabase db = dao.open();
                //Depense représentant les modifications
                Depense d = new Depense(depense.getId(),lib, Double.valueOf(mont), typ, dateSysteme, DepenseFormatter.formatStringToDate(dat), dev);
                dao.update(d);
                db.close();
                //Tout se passe bien
                Intent data = new Intent();
                data.putExtra("depense_modifiee", d);
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
        super.onPause();
//        Toast.makeText(this, "appel onPause ModifierDepense", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy(); //To change body of generated methods, choose Tools | Templates.
//        Toast.makeText(this, "appel onDestroy ModifierDepense", Toast.LENGTH_LONG).show();
    }

}
