package org.protech.theprophet.android.gestionnairedepenses;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import org.protech.theprophet.android.gestionnairedepenses.dao.DepenseDAO;
import org.protech.theprophet.android.gestionnairedepenses.entities.Depense;
import org.protech.theprophet.android.gestionnairedepenses.utils.DepenseFormatter;

// AbsListView.OnScrollListener 
// je voudrais que quand la liste des dépenses est "scrolled" le total des dépenses affichées (TexteView) se rétracte ou s'affiche selon le sens du scroll
public class ListeDepensesActivityNotUse extends Activity implements MenuItem.OnMenuItemClickListener, AbsListView.OnScrollListener, AdapterView.OnItemClickListener {

    private ListView listeDepensesListView = null;
    private TextView sommeDepensesTextView = null;
//    private TextSwitcher sommeDepensesTextSwitcher = null;
    //menu item
    private MenuItem repartirMenuItem = null;
    private MenuItem closeMenuItem = null;
    private MenuItem newMenuItem = null;
    private MenuItem rootMenuItem = null;
    //Racine
    private LinearLayout rootLayout = null;
    public static final int REQUEST_CODE_FROM_DEPENSE_DETAILS = 1;
    public static final int NOUVELLE_DEPENSE = 2;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setTitle(R.string.liste_des_depenses);
        rootLayout = (LinearLayout) View.inflate(this, R.layout.liste_depenses
                , null);

        listeDepensesListView = (ListView) rootLayout.findViewById(R.id.liste_depenses_list_view);
//        listeDepensesListView.setOnScrollListener(this);
        listeDepensesListView.setOnItemClickListener(this);
        listeDepensesListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        sommeDepensesTextView = (TextView) rootLayout.findViewById(R.id.liste_depenses_somme_depenses_text_view);
//        sommeDepensesTextSwitcher= (TextSwitcher) rootLayout.findViewById(R.id.liste_depenses_somme_depenses_text_switcher);
        
        chargementDonnees();
        
        setContentView(rootLayout);

//        Toast.makeText(getApplicationContext(), "appel onCreate ListeDepenses", Toast.LENGTH_LONG).show();
    }

    private void refreshData(double m) {

        DepenseDAO dao = new DepenseDAO(this);
        dao.open();
        ((CursorAdapter) listeDepensesListView.getAdapter()).changeCursor(dao.findAllOrderByDateDESC());
        ((CursorAdapter) listeDepensesListView.getAdapter()).notifyDataSetChanged();
        //Pour pouvoir manipuler l'info brute : la somme en nombre
        sommeDepensesTextView.setTag((Double) sommeDepensesTextView.getTag() + m);
        //La somme en nombre formattée avec la devise locale
        sommeDepensesTextView.setText(DepenseFormatter.formatMontantToView((Double) sommeDepensesTextView.getTag(), "FCFA"));
//        sommeDepensesTextSwitcher.setText(DepenseFormatter.formatMontantToView((Double) sommeDepensesTextView.getTag(), "FCFA"));

    }

    private void chargementDonnees() {

        //accès à la base de donnees
        DepenseDAO dao = new DepenseDAO(this);
        SQLiteDatabase db = dao.open();
        Cursor c = dao.findAllOrderByDateDESC();

//        Cursor totalMontants = dao.totalMontants();
//        Double sommeDepensesVal = 0d;
//        while (totalMontants.moveToNext()) {
//            sommeDepensesVal = totalMontants.getDouble(0);
//        }
//        totalMontants.close();

        //Pour pouvoir manipuler l'info brute : la somme en nombre
        sommeDepensesTextView.setTag(dao.totalMontants());
        //La somme en nombre formattée avec la devise locale
        sommeDepensesTextView.setText(DepenseFormatter.formatMontantToView((Double) sommeDepensesTextView.getTag(), "FCFA"));

        LayoutInflater inflater = LayoutInflater.from(this);
        // ou LayoutInflater inflater = this.getLayoutInflater()
        // ou LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)
        CursorAdapter adapter = new DepenseCursorAdapter(this, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER, inflater);

        listeDepensesListView.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        super.onPause(); //To change body of generated methods, choose Tools | Templates.
//        Toast.makeText(getApplicationContext(), "appel onPause ListeDepenses", Toast.LENGTH_LONG).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        
        MenuInflater mInflater = getMenuInflater();
        mInflater.inflate(R.menu.liste_depenses, menu);
        rootMenuItem = menu.findItem(R.id.root_menu_item);
        rootMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        rootMenuItem.setIcon(android.R.drawable.ic_menu_manage);
        
        repartirMenuItem = menu.findItem(R.id.group_by_type_menu_item);
        repartirMenuItem.setOnMenuItemClickListener(this);
        

        newMenuItem = menu.findItem(R.id.new_menu_item);
        newMenuItem.setOnMenuItemClickListener(this);
        
        

        return true;

    }
    
//
//    @Override
//    public boolean onCreatePanelMenu(int featureId, Menu menu) {
//        MenuInflater mInflater = getMenuInflater();
//        mInflater.inflate(R.menu.liste_depenses, menu);
//
//        repartirMenuItem = menu.findItem(R.id.group_by_type_menu_item);
//        repartirMenuItem.setOnMenuItemClickListener(this);
//
//        closeMenuItem = menu.findItem(R.id.close_menu_item);
//        closeMenuItem.setOnMenuItemClickListener(this);
//
//        newMenuItem = menu.findItem(R.id.new_menu_item);
//        newMenuItem.setOnMenuItemClickListener(this);
//        
//        return super.onCreatePanelMenu(featureId, menu); //To change body of generated methods, choose Tools | Templates.
//    }
//    
    

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //enregistrer le positionnement de l'ascenceur de la liste des dépenses
//        outState.putInt("LISTE_DEPENSES_LIST_VIEW_SCROLL_X", listeDepensesListView.getScrollX());
//        outState.putInt("LISTE_DEPENSES_LIST_VIEW_SCROLL_Y", listeDepensesListView.getScrollY());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //rétablir le positionnement de l'ascenceur de la liste des dépenses
//        listeDepensesListView.setScrollX(savedInstanceState.getInt("LISTE_DEPENSES_LIST_VIEW_SCROLL_X", 0));
//        listeDepensesListView.setScrollY(savedInstanceState.getInt("LISTE_DEPENSES_LIST_VIEW_SCROLL_Y", 0));

    }

    @Override
    public boolean onSearchRequested() {
        return super.onSearchRequested(); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean onMenuItemClick(MenuItem mi) {
        switch (mi.getItemId()) {
            case R.id.new_menu_item:
                Intent intent = new Intent(ListeDepensesActivityNotUse.this, NouvelleDepenseActivity.class);
                //C'est startActivity(intent) qui marche
                startActivityForResult(intent, NOUVELLE_DEPENSE);

                break;

            //si erreur return false je crois hein
            case R.id.group_by_type_menu_item:
//                Toast.makeText(getApplicationContext(), "Repartir", Toast.LENGTH_LONG).show();
                //si erreur return false je crois hein
                break;
        }
        return true;
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
//        Toast.makeText(this, String.valueOf(scrollState), Toast.LENGTH_LONG).show();
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        switch (view.getId()) {
            case R.id.liste_depenses_list_view:

                break;
        }

    }

    //clic  sur un élément de la liste des dépense
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Toast.makeText(this, view.isClickable() ? "is clickable" : "is not clickable", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(ListeDepensesActivityNotUse.this, DetialsDepenseActivity.class);
        intent.putExtra("depense", (Depense) view.getTag());
        intent.putExtra("position", position);
        //C'est startActivity(intent)
//                startActivity(intent);

        startActivityForResult(intent, REQUEST_CODE_FROM_DEPENSE_DETAILS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            //resultat renvoyé par l'activité DetialsDepenseActivity
            case REQUEST_CODE_FROM_DEPENSE_DETAILS:
                switch (resultCode) {
                    case DetialsDepenseActivity.RESULT_EDIT_DEPENSE_OK:
                        //Modification ok
                        if (data != null) {
                            if (data.hasExtra("depense_modifiee")) {
                                Depense d = (Depense) data.getSerializableExtra("depense_modifiee");
                                //Position de l'élément de liste correspondant à la dépense modifiée
                                int position = data.getIntExtra("position", -1);
                                //le montant de la dépense avant modification
                                double ancienMontant = data.getDoubleExtra("ancien_montant", 0d);
                                //le montant de la dépense après modification
                                double montant = d.getMontant();
                                //mise à jour
//                             
                                refreshData(-ancienMontant + montant);

//                                DepenseDAO dao = new DepenseDAO(this);
//                                SQLiteDatabase db = dao.open();
//                                ((CursorAdapter) listeDepensesListView.getAdapter()).changeCursor(dao.findAllByDateDESCForCursorAdapter());
//                                ((CursorAdapter) listeDepensesListView.getAdapter()).notifyDataSetChanged();
//                                //Pour pouvoir manipuler l'info brute : la somme en nombre
//                                sommeDepensesTextView.setTag((Double) sommeDepensesTextView.getTag() - ancienMontant + montant);
//                                //La somme en nombre formattée avec la devise locale
//                                sommeDepensesTextView.setText(DepenseFormatter.formatMontantToView((Double) sommeDepensesTextView.getTag(), "FCFA"));
                            }
                        }
                        Toast.makeText(this, "RESULT_EDIT_OK", Toast.LENGTH_LONG).show();
                        break;
                    case RESULT_OK:
                        //suppression ok 
//                        Toast.makeText(this, "RESULT_OK", Toast.LENGTH_LONG).show();
                        if (data != null) {
                            if (data.hasExtra("position")) {
                                int position = data.getIntExtra("position", -1);
                                double montant = data.getDoubleExtra("montant", 0d);
//                                Toast.makeText(this, "tu peux supprimer la view à la position " + position, Toast.LENGTH_LONG).show();

                                //C'est trop ... barbare je trouve
                                //Moi je voudrais par exemple supprimer "visuellement" l'élément concerné au lieu de tout recharger de la base après une seule suppression
//                                chargementDonnees();
                                refreshData(- montant);

                            }
                        }
                        break;

                    case RESULT_FIRST_USER:

                        break;

                    case RESULT_CANCELED:
                        break;

                }
                break;
            //resultat renvoyé par l'activité NouvelleDepenseActivity
            case NOUVELLE_DEPENSE:
                switch (resultCode) {
                    case RESULT_OK:
                        //ajout ok 
                        if (data != null) {
                            if (data.hasExtra("montant")) {

                                double montant = data.getDoubleExtra("montant", 0d);

                                refreshData(montant);
                            }
                        }
                        break;

                    case RESULT_FIRST_USER:
                        break;

                    case RESULT_CANCELED:
                        break;

                }
                break;
        }
    }

}
