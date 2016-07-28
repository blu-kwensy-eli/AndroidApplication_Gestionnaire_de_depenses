/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.protech.theprophet.android.gestionnairedepenses;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import org.protech.theprophet.android.gestionnairedepenses.dao.ConfigurationDAO;
import org.protech.theprophet.android.gestionnairedepenses.dao.DepenseDAO;
import org.protech.theprophet.android.gestionnairedepenses.entities.Depense;
import org.protech.theprophet.android.gestionnairedepenses.utils.DepenseFormatter;

/**
 *
 * @author TheProphet
 */
public class DetialsDepenseActivity extends Activity implements MenuItem.OnMenuItemClickListener, DialogInterface.OnClickListener {

    private ScrollView rootLayout;
    private Depense displayed;
    private TextView detailsDepenseDeviseLocaleTextView;
    private TextView detailsDepenseValeurDeviseLocaleTextView;
    private TextView detailsDepensePourcentageTextView;
    private TextView detailsDepenseIdTextView;
    private TextView detailsDepenseLibelleTextView;
    private TextView detailsDepenseMontantTextView;
    private TextView detailsDepenseTypeTextView;
    private TextView detailsDepenseDeviseTextView;
    private MenuItem deleteMenuItem;
    private MenuItem editMenuItem;
    private TextView detailsDepenseDateTextView;
    //position dans la liste (ListeDepensesActivity)
    private int position;
    private static final int MODIFIER_DEPENSE = 2;
    public static final int RESULT_EDIT_DEPENSE_OK = 2;
    public static final int RESULT_DELETE_DEPENSE_OK = 4;

    //remplis les chan$mps de données avec les informations sur la dépense
    public void showData(Depense displayed) {
        if (displayed != null) {
            DepenseDAO dao = new DepenseDAO(this);
            dao.open();
            String pourcentage = DepenseFormatter.formatMontantToView(dao.pourcentageJourMontant(displayed), "");
            detailsDepensePourcentageTextView = (TextView) rootLayout.findViewById(R.id.details_depense_pourcentage);
            detailsDepensePourcentageTextView.setText(pourcentage);
//            
            ConfigurationDAO configDao = new ConfigurationDAO(this);
            configDao.open();
            String deviseLocale = configDao.findValeurByCode(ConfigurationDAO.CONFIG_CODE_DEVISE_PAR_DEFAUT);
            
            detailsDepenseDeviseLocaleTextView = (TextView) rootLayout.findViewById(R.id.details_depense_devise_locale);
            detailsDepenseDeviseLocaleTextView.setText("Valeur en "+deviseLocale);
            
            String deviseDepart = displayed.getDevise();
            detailsDepenseValeurDeviseLocaleTextView = (TextView) rootLayout.findViewById(R.id.details_depense_valeur_devise_locale);
             Double taux = deviseLocale.equals(deviseDepart)
                    ? 1d
                    : Double.valueOf(configDao.findValeurByDevises(deviseDepart, deviseLocale));
             Double m = displayed.getMontant()*taux;
//             
            detailsDepenseValeurDeviseLocaleTextView.setText(DepenseFormatter.formatMontantToView(m, ""));
            
            
            detailsDepenseIdTextView = (TextView) rootLayout.findViewById(R.id.details_depense_id);
            detailsDepenseIdTextView.setText(String.valueOf(displayed.getId()));
            detailsDepenseDeviseTextView = (TextView) rootLayout.findViewById(R.id.details_depense_devise);
            detailsDepenseDeviseTextView.setText(displayed.getDevise());
            detailsDepenseMontantTextView = (TextView) rootLayout.findViewById(R.id.details_depense_montant);
            detailsDepenseMontantTextView.setText(DepenseFormatter.formatMontantToView(displayed.getMontant(), ""));
            detailsDepenseLibelleTextView = (TextView) rootLayout.findViewById(R.id.details_depense_libelle);
            detailsDepenseLibelleTextView.setText(displayed.getLibelle());
            detailsDepenseTypeTextView = (TextView) rootLayout.findViewById(R.id.details_depense_type);
            detailsDepenseTypeTextView.setText(displayed.getType());
            detailsDepenseDateTextView = (TextView) rootLayout.findViewById(R.id.details_depense_date);
            detailsDepenseDateTextView.setText(DepenseFormatter.formatDateToString(displayed.getDateRelle()));
        }
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
//        Toast t1 = Toast.makeText(this, "Appel méthode onCreate", Toast.LENGTH_LONG);
//        t1.show();
        rootLayout = (ScrollView) View.inflate(this, R.layout.details_depense, null);
        //Objet envoyé de l'activité ListeDepensesActivity
        //une méthode est d'utiliser la base de données : j'envoie l'id de l'enregistrement correspondant via l'Intent, je le récupère pour retrouver l'enregitrement depuis la base
        Intent i = getIntent();

        position = i.getIntExtra("position", -1);

        //Code pour retrouver la dépense d'id idDepense depuis la base de données
        //A venir
        //........
        //........
        //........
        //........
        //Envoyé de ListeDepensesActivity
        displayed = (Depense) i.getSerializableExtra(ListeDepensesActivity.DEPENSE_A_AFFICHER);
        //remplir les champs de données
        showData(displayed);
        setContentView(rootLayout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
//        Toast t1 = Toast.makeText(this, "Appel méthode onCreateOptionsMenu", Toast.LENGTH_LONG);
//        t1.show();
        MenuInflater mInflater = getMenuInflater();
        mInflater.inflate(R.menu.details_depense, menu);

        //Element de menu "Modifier"
        editMenuItem = menu.findItem(R.id.edit_menu_item);
        editMenuItem.setOnMenuItemClickListener(this);
        if (displayed.getId() == null) {
            editMenuItem.setVisible(false);
        }

        //Element de menu "Supprimer"
        deleteMenuItem = menu.findItem(R.id.delete_menu_item);
        deleteMenuItem.setOnMenuItemClickListener(this);
        //cas impossible généralement
        if (displayed.getId() == null) {
            deleteMenuItem.setVisible(false);
        }

        return true;

    }

//    @Override
//    public boolean onCreatePanelMenu(int featureId, Menu menu) {
//         MenuInflater mInflater = getMenuInflater();
//        mInflater.inflate(R.menu.details_depense, menu);
//
//        //Element de menu "Modifier"
//        editMenuItem = menu.findItem(R.id.edit_menu_item);
//        editMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//        editMenuItem.setIcon(android.R.drawable.ic_menu_edit);
//        
//        editMenuItem.setOnMenuItemClickListener(this);
//        if (displayed.getId() == null) {
//            editMenuItem.setVisible(false);
//        }
//
//        //Element de menu "Supprimer"
//        deleteMenuItem = menu.findItem(R.id.delete_menu_item);
//        deleteMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//        deleteMenuItem.setIcon(android.R.drawable.ic_menu_delete);
//        
//        deleteMenuItem.setOnMenuItemClickListener(this);
//        
//        //cas impossible généralement
//        if (displayed.getId() == null) {
//            deleteMenuItem.setVisible(false);
//        }
//
//
//        return super.onCreatePanelMenu(featureId, menu); //To change body of generated methods, choose Tools | Templates.
//        
//    }
    public boolean onMenuItemClick(MenuItem mi) {
        switch (mi.getItemId()) {
            case R.id.delete_menu_item:
//                Toast.makeText(getApplicationContext(), "item Supprimer", Toast.LENGTH_LONG).show();
                //REVOIR     BOITE DE DIALOGUE ETES VOUS SUR DE VOULOIR SUPPRIMER
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setTitle(R.string.dialog1_titre);
                //voir méthode onClick(DialogInterface, int)
                builder.setPositiveButton(R.string.dialog1_reponse_oui, this);
                //voir méthode onClick(DialogInterface, int)
                builder.setNegativeButton(R.string.dialog1_reponse_non, this);
                //On affiche la boite de dialogue
                builder.create().show();

                break;
            //si erreur return false je crois hein

            case R.id.edit_menu_item:
                Intent intent = new Intent(DetialsDepenseActivity.this, ModifierDepenseActivity.class
                );
                intent.putExtra("depense", displayed);
                //voir méthode
                startActivityForResult(intent, MODIFIER_DEPENSE);
                break;
        }
        return true;
    }

    //Gestionnaire boite de dialogue supprimer dépense
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                //Code de Suppression ca marche nickel
                DepenseDAO dao = new DepenseDAO(this);
                SQLiteDatabase db = dao.open();
                dao.delete(displayed.getId());
                db.close();
                //Renvoie a la liste des dépenses (Activité parent)
                //REVOIR        Je ne sais pas si c'est la bonne méthode
                //      marche pas bien pour ce cas ci. Je veux retourner à l'activité parent. 
                //      Selon le comportemet en findWithMaxDateGroupByDay, il semblerait que le parent se retrouve en tête de pile de l'enfant
                //      Ce qui ne me semble pas correct
//                Intent intent = new Intent(DetialsDepenseActivity.this, ListeDepensesActivity.class);
//                startActivity(intent);
                //REVOIR        Je ne sais pas si c'est la bonne méthode
                //L'activité parente est réaffichée trop vite
                //La liste affichée contient toujours l'lélément supprimé
//                finish();

                //Tout se passe bien
                Intent i = new Intent();
                i.putExtra("position", position);
                i.putExtra("montant", displayed.getMontant());
                setResult(RESULT_OK, i);
                //Voir ListeDepensesActivity.onActivityResult pour traitement de cette situation
                finish();
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                dialog.cancel();
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case MODIFIER_DEPENSE:
                switch (resultCode) {
                    case RESULT_OK:
                        //Modification ok 
                        //Si la dépense a été effectivement modifiée
                        if (data.hasExtra("depense_modifiee")) {
                            showData((Depense) data.getSerializableExtra("depense_modifiee"));
                            //REVOIR                            
                            data.putExtra("position", position);
                            data.putExtra("ancien_montant", displayed.getMontant());
                            setResult(RESULT_EDIT_DEPENSE_OK, data);
                            finish();
                        }
//                        Toast.makeText(this, "RESULT_OK", Toast.LENGTH_LONG).show();
                        break;
                    case RESULT_CANCELED:
                        //Modification annulée
//                        Toast.makeText(this, "RESULT_CANCELED_MODIFIER_DEPENSE", Toast.LENGTH_LONG).show();
                        break;

                }
                break;
        }
    }

}
