/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.protech.theprophet.android.gestionnairedepenses;

import android.app.Activity;
import android.app.AlertDialog;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.protech.theprophet.android.gestionnairedepenses.dao.ConfigurationDAO;
import org.protech.theprophet.android.gestionnairedepenses.entities.Depense;

/**
 *
 * @author TheProphet
 */
public class ConfigurationActivity extends Activity {

    protected ListView configListView;
    private static final String LIBELLE_CONFIG_DEVISE_PAR_DEFAUT = "Devise locale";
    private static final String LIBELLE_CONFIG_TAUX_DE_CHANGE = "Taux de change";
    //Gérer les changements de configuration
    public static final String ACTION_CHANGE_CONFIGURATION = "org.protech.theprophet.android.gestionnairedepenses.action.CHANGE_CONFIGURATION";
    //sera envoyé dans onDestroy()
    private Intent broadcastConfigChanges;

    public void chargerConfiguration() {
        List<HashMap<String, String>> liste = new ArrayList<HashMap<String, String>>();

        HashMap<String, String> element;
        //Config 1
        element = new HashMap<String, String>();
        element.put("code", LIBELLE_CONFIG_DEVISE_PAR_DEFAUT);
        ConfigurationDAO dao = new ConfigurationDAO(ConfigurationActivity.this);
        dao.open();
        String deviseParDefautActuelle = dao.findValeurByCode(ConfigurationDAO.CONFIG_CODE_DEVISE_PAR_DEFAUT);
        element.put("valeur", deviseParDefautActuelle);
        liste.add(element);

        //Config 2
        element = new HashMap<String, String>();
        element.put("code", LIBELLE_CONFIG_TAUX_DE_CHANGE);
        element.put("valeur", "");
        liste.add(element);
        //Config 3
        //...
        //Config 4
        //...
        //Config 5
        //...
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
//        android.R.drawable.

        //Configuration de l'intent
        broadcastConfigChanges = new Intent(ConfigurationActivity.this, ListeDepensesActivity.class);
        broadcastConfigChanges.setAction(ACTION_CHANGE_CONFIGURATION);

        setContentView(R.layout.configuration);

        configListView = (ListView) findViewById(R.id.config_list_view);
        chargerConfiguration();
        configListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> av, View view, int pos, long l) {

                ConfigurationListAdapter configAdapter = ((ConfigurationListAdapter) configListView.getAdapter());
                //Descriptif du code de l'élément de configuration
                String configLabel = configAdapter.getListe().get(pos).get(ConfigurationListAdapter.TEXT1);
                //Valeur de l'élément de configuration
                String configValue = configAdapter.getListe().get(pos).get(ConfigurationListAdapter.TEXT2);

//                TwoLineListItem item = (TwoLineListItem) view;
//                //Descriptif du code de l'élément de configuration
//                String configLabel = item.getText1().getText().toString();
//                //Valeur de l'élément de configuration
//                String configValue = item.getText2().getText().toString();
//                
                //Si on veut changer la devise par défaut
                if (configLabel.equals(LIBELLE_CONFIG_DEVISE_PAR_DEFAUT)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ConfigurationActivity.this);
                    
                    ListAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_single_choice, Depense.DEVISES_DEPENSE_DEFAUT);
                    
                    String deviseParDefautActuelle = configValue;
                    //La position de la devise par défaut actuelle dans le tableau de choix 
                    int posDevise = 0;
                    for (int i = 0; i < Depense.DEVISES_DEPENSE_DEFAUT.length; i++) {
                        String devise = Depense.DEVISES_DEPENSE_DEFAUT[i];
                        if (devise.equals(deviseParDefautActuelle)) {
                            posDevise = i;
                            break;
                        }

                    }

                    builder.setSingleChoiceItems(adapter, posDevise, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface di, int which) {

                            String nouvelleDeviseParDefaut = ((AlertDialog) di).getListView().getItemAtPosition(which).toString();
                            //Accès base de données
                            ConfigurationDAO dao = new ConfigurationDAO(ConfigurationActivity.this);
                            //Mode lecture/ecriture
                            dao.open();
                            //Mise a jour bd
                            dao.update(ConfigurationDAO.CONFIG_CODE_DEVISE_PAR_DEFAUT, nouvelleDeviseParDefaut);
                            //nouvelle devise par défaut
                            broadcastConfigChanges.putExtra(ConfigurationDAO.CONFIG_CODE_DEVISE_PAR_DEFAUT, nouvelleDeviseParDefaut);
                            //Mise a jour interface
                            chargerConfiguration();
                            //fermeture boite de dialogue
                            di.dismiss();

//                            Toast.makeText(ConfigurationActivity.this, nouvelleDeviseParDefaut, Toast.LENGTH_SHORT).show();
                        }
                    });

//                    View titleLayout = ((LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.application_dlg_custom_title,null);
//                    TextView titleView = (TextView)titleLayout.findViewById(R.id.application_dlg_custom_title_value);
//                    titleView.setText("Sélectionner");
//                    builder.setCustomTitle(titleLayout);
                    builder.setTitle("Sélectionner");
//                //On affiche la boite de dialogue
                    AlertDialog alt_dlg = builder.create();

                    alt_dlg.show();

                } else if (configLabel.equals(LIBELLE_CONFIG_TAUX_DE_CHANGE)) {
//                       Toast.makeText(ConfigurationActivity.this, LIBELLE_CONFIG_TAUX_DE_CHANGE, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(ConfigurationActivity.this, ConfigurationTauxChangeActivity.class);
                    startActivity(i);
                }
            }
        });

    }

    public class ConfigClickListener implements DialogInterface.OnClickListener {

        public void onClick(DialogInterface di, int i) {

//            Toast.makeText(ConfigurationActivity.this, ((AlertDialog) di).getListView().getItemAtPosition(i).toString(), Toast.LENGTH_SHORT).show();
            di.dismiss();

        }

    }

    protected void processOnDestroy() {
        startActivity(broadcastConfigChanges);
//        Toast.makeText(ConfigurationActivity.this, "process on destroy", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        processOnDestroy();
    }

}
