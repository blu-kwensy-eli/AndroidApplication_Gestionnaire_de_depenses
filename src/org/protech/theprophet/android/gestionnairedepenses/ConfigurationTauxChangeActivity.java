/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.protech.theprophet.android.gestionnairedepenses;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.protech.theprophet.android.gestionnairedepenses.dao.ConfigurationDAO;
import org.protech.theprophet.android.gestionnairedepenses.entities.Depense;
import org.protech.theprophet.android.gestionnairedepenses.utils.DepenseFormatter;

/**
 *
 * @author TheProphet
 */
public class ConfigurationTauxChangeActivity extends ConfigurationActivity {

    ArrayList<String> codes;

    //sera envoyé dans onDestroy()
//    private Intent broadcastConfigChanges;
    @Override
    public void chargerConfiguration() {
        List<HashMap<String, String>> liste = new ArrayList<HashMap<String, String>>();
        codes = new ArrayList();
        ConfigurationDAO dao = new ConfigurationDAO(this);
        dao.open();
//        Cursor c = dao.findByCode("%TAUX_DE_CHANGE_%");
//        while (c.moveToNext()) {
//            HashMap<String, String> element;
//            //Config 1
//            element = new HashMap<String, String>();
//            element.put("code", c.getString(c.getColumnIndex(ConfigurationDAO.FIELD_CODE)));
//            element.put("valeur",  c.getString(c.getColumnIndex(ConfigurationDAO.FIELD_VALEUR)));
//            liste.add(element);
//        }
//        c.close();

        HashMap<String, String> element;
        //Config 1
        element = new HashMap<String, String>();
        element.put("code", Depense.DEVISE_EURO + " -> " + Depense.DEVISE_FCFA);
        element.put("valeur", dao.findValeurByCode(ConfigurationDAO.CONFIG_CODE_TAUX_DE_CHANGE_EUR_FCFA));
        codes.add(ConfigurationDAO.CONFIG_CODE_TAUX_DE_CHANGE_EUR_FCFA);
        liste.add(element);

        //Config 2
        element = new HashMap<String, String>();
        element.put("code", Depense.DEVISE_USD + " -> " + Depense.DEVISE_FCFA);
        element.put("valeur", dao.findValeurByCode(ConfigurationDAO.CONFIG_CODE_TAUX_DE_CHANGE_USD_FCFA));
        codes.add(ConfigurationDAO.CONFIG_CODE_TAUX_DE_CHANGE_USD_FCFA);
        liste.add(element);

        ListAdapter adapter = new ConfigurationListAdapter(this,
                //Valeurs à insérer
                liste,
                R.layout.configuration_item,
                new String[]{"code", "valeur"},
                new int[]{R.id.configuration_code, R.id.configuration_valeur});
//        ListAdapter adapter = new ConfigurationListAdapter(this,
//                //Valeurs à insérer
//                liste,
//                android.R.layout.simple_list_item_2,
//                new String[]{"text1", "text2"},
//                new int[]{android.R.id.text1, android.R.id.text2},
//                new ConfigClickListener());
        //Pour finir, on donne à la ListView le SimpleAdapter
        configListView.setAdapter(adapter);
        configListView.invalidate();
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        //Configuration de l'intent
//        broadcastConfigChanges = new Intent(ConfigurationTauxChangeActivity.this, ListeDepensesActivity.class);
//        broadcastConfigChanges.setAction(ConfigurationActivity.ACTION_CHANGE_CONFIGURATION);
        setContentView(R.layout.configuration);

        configListView = (ListView) findViewById(R.id.config_list_view);
        chargerConfiguration();
        configListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> av, View view, int pos, long l) {

                ConfigurationListAdapter configAdapter = ((ConfigurationListAdapter) configListView.getAdapter());
                //Code de l'élément de configuration dans la bas de données
                String configCode = codes.get(pos);
                //Descriptif du code de l'élément de configuration
                String configLabel = configAdapter.getListe().get(pos).get(ConfigurationListAdapter.TEXT1);
                //Valeur de l'élément de configuration
                String configValue = configAdapter.getListe().get(pos).get(ConfigurationListAdapter.TEXT2);

                AlertDialog.Builder builder = new AlertDialog.Builder(ConfigurationTauxChangeActivity.this);
                View v = LayoutInflater.from(ConfigurationTauxChangeActivity.this).inflate(R.layout.configuration_dlg_input, null);
                EditText edt_txt = (EditText) v.findViewById(R.id.configuration_dlg_input_nouvelle_valeur);
                edt_txt.setText(DepenseFormatter.formatMontantToViewForEdition(Double.valueOf(configValue), ""));
                builder.setView(v);
                builder.setPositiveButton(R.string.dialog_configuration_changer_taux_ok, new DialogInterfaceWithViewListener(edt_txt, configCode) {
                    public void onClick(DialogInterface di, int i) {
                        EditText edt_txt = (EditText) view;
//                        Toast.makeText(ConfigurationTauxChangeActivity.this, edt_txt.getText(), Toast.LENGTH_SHORT).show();

                        String taux = edt_txt.getText().toString();
                        String errorText = "";
                        //si vide ou ne contient que le point décimal
                        if (taux.isEmpty() || (taux.lastIndexOf(".") == 0 && taux.length() == 1)) {
                            errorText = "Valeur de taux invalide";
                        }

                        //Si erreur
                        if (!errorText.isEmpty()) {
                            Toast.makeText(ConfigurationTauxChangeActivity.this, errorText, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        //sinon on enregistre
                        //Accès base de données
                        ConfigurationDAO dao = new ConfigurationDAO(ConfigurationTauxChangeActivity.this);
                        //Mode lecture/ecriture
                        dao.open();
                        //Mise a jour bd
//                        Toast.makeText(ConfigurationTauxChangeActivity.this, data + " " + Double.valueOf(taux).toString(), Toast.LENGTH_SHORT).show();
                        dao.update(data, Double.valueOf(taux).toString());
                        chargerConfiguration();
                    }
                });
                builder.setNegativeButton(R.string.dialog_configuration_changer_taux_annuler, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface di, int i) {
                        di.dismiss();
                    }
                });

                                    TextView titleView = (TextView)v.findViewById(R.id.application_dlg_custom_title_value);
                    titleView.setText("Taux " + configLabel);
//                    android.R.style.TextAppearance_DeviceDefault_DialogWindowTitle

//                builder.setTitle("Taux " + configLabel);
////                //On affiche la boite de dialogue
                AlertDialog alt_dlg = builder.create();
                alt_dlg.setCancelable(false);
                // Change the title divider
//    final Resources res = getResources();
//    final int titleDividerId = res.getIdentifier("titleDivider", "id", "android");
//    final View titleDivider = alt_dlg.findViewById(titleDividerId);
//    titleDivider.setBackgroundColor(res.getColor(android.R.color.holo_orange_dark));
                alt_dlg.show();
//                Toast.makeText(ConfigurationTauxChangeActivity.this, configLabel, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void processOnDestroy() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public abstract class DialogInterfaceWithViewListener implements DialogInterface.OnClickListener {

        protected View view;
        protected String data;

        public DialogInterfaceWithViewListener(View view, String data) {
            this.view = view;
            this.data = data;
        }

    }
}
