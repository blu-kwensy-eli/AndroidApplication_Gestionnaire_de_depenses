package org.protech.theprophet.android.gestionnairedepenses;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import org.protech.theprophet.android.gestionnairedepenses.dao.ConfigurationDAO;
import org.protech.theprophet.android.gestionnairedepenses.dao.DepenseDAO;
import org.protech.theprophet.android.gestionnairedepenses.entities.Depense;
import org.protech.theprophet.android.gestionnairedepenses.utils.DepenseFormatter;

// AbsListView.OnScrollListener 
// je voudrais que quand la liste des dépenses est "scrolled" le total des dépenses affichées (TexteView) se rétracte ou s'affiche selon le sens du scroll
public class ListeDepensesActivity extends Activity implements MenuItem.OnMenuItemClickListener, AdapterView.OnItemClickListener {

    ProgressDialog dialog;

    
    protected String deviseParDefaut;
    protected String texteRecherche = null;
//    protected String texteSousTitreModeSelectionMultiple = null;
    protected String modeDeTri = TRI_PAR_DATE_DECROISSANTE;

    public static final String TRI_PAR_DATE_DECROISSANTE = "TRI_DATE_DECROISSANTE";
    public static final String TRI_PAR_DATE_ET_TYPE_DECROISSANTS = "TRI_PAR_DATE_ET_TYPES_DECROISSANTS";

    public static final String DEPENSE_A_AFFICHER = "depense";
    public static final String SOMME_DEPENSES_SELECTIONNEES = "SOMME_DEPENSES_SELECTIONNEES";
    public static final String TEXTE_RECHERCHE = "TEXTE_RECHERCHE";
     public static final String MULTISELECTION_MODE_SUBTITLE_TEXT = "MULTISELECTION_MODE_SUBTITLE_TEXT";
    
    public static final int CODE_REQUETE_POUR_DETAILS_DEPENSE = 1;
    public static final int CODE_REQUETE_POUR_NOUVELLE_DEPENSE = 2;

    protected ListView listeDepensesListView = null;
    protected EditText rechercherEditText = null;
    protected TextView sommeDepensesTextView = null;
    protected TextView listeDepensesEmpty = null;
    protected TextSwitcher sommeDepensesTextSwitcher = null;
    //menu item
    private MenuItem repartirMenuItem = null;
    private MenuItem newMenuItem = null;
    private MenuItem searchMenuItem = null;
    private MenuItem configurationMenuItem = null;
    private MenuItem testMenuItem = null;

    Animation inAnimation = null;
    Animation outAnimation = null;
    //Racine
    protected LinearLayout rootLayout = null;

    //
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        dialog = new ProgressDialog(this);
        dialog.setTitle("Indeterminate");
        dialog.setMessage("Please wait while loading...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);

        setTitle(R.string.liste_des_depenses);
        ConfigurationDAO configDao = new ConfigurationDAO(this);
        configDao.open();
        deviseParDefaut = configDao.findValeurByCode(ConfigurationDAO.CONFIG_CODE_DEVISE_PAR_DEFAUT);

        rootLayout = (LinearLayout) View.inflate(this, R.layout.liste_depenses, null);

        rechercherEditText = (EditText) rootLayout.findViewById(R.id.liste_depenses_rechercher_edit_text);
        rechercherEditText.addTextChangedListener(new SearchCallback());

        listeDepensesEmpty = (TextView) rootLayout.findViewById(R.id.liste_depenses_empty_text_view);
        //
        initListeDepensesView(rootLayout, R.id.liste_depenses_list_view);
        listeDepensesListView.setEmptyView(listeDepensesEmpty);
        //Sélecionner plusieurs dépenses
        listeDepensesListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        //Ecouteurs

        listeDepensesListView.setMultiChoiceModeListener(new ModeCallback());
        listeDepensesListView.setOnItemClickListener(this);
        sommeDepensesTextView = (TextView) rootLayout.findViewById(R.id.liste_depenses_somme_depenses_text_view);

        sommeDepensesTextSwitcher = (TextSwitcher) rootLayout.findViewById(R.id.liste_depenses_somme_depenses_text_switcher);
        sommeDepensesTextSwitcher.setAnimateFirstView(true);

        float yDelta = -20;

        inAnimation = new android.view.animation.TranslateAnimation(0, 0, 0, yDelta);
        inAnimation.setStartTime(200);
        inAnimation.setDuration(500);
        inAnimation = new android.view.animation.TranslateAnimation(0, 0, yDelta, 0);
        inAnimation.setStartTime(200);
        inAnimation.setDuration(500);
//        inAnimation.setRepeatCount(2);
//        inAnimation.setRepeatMode(Animation.REVERSE);
        sommeDepensesTextSwitcher.setInAnimation(inAnimation);
        sommeDepensesTextSwitcher.setOutAnimation(outAnimation);

        chargementDonnees();
        //
        getActionBar().setSubtitle("Appui long pour sélectionner plusieurs dépenses");
//        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.background_97977b));

        handleIntent(getIntent());
        setContentView(rootLayout);

    }

    @Override
    protected void onResume() {
        super.onResume(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Fait la somme d'une liste de dépense dans une devise donnée
     *
     * @param listeDepenses curseur représentant la liste des dépenses pour
     * lesquelles faire la somme
     * @param closeCursor true pour fermer le curseur
     * @return la somme
     */
    private double sommeDepensesDeviseParDefaut(Cursor listeDepenses, boolean closeCursor) {

        double somme = 0d;
        ConfigurationDAO configDao = new ConfigurationDAO(this);
        configDao.open();
        //Devise par défaut
        String deviseArrivee = configDao.findValeurByCode(ConfigurationDAO.CONFIG_CODE_DEVISE_PAR_DEFAUT);

        while (listeDepenses.moveToNext()) {
            Double montantDep = listeDepenses.getDouble(listeDepenses.getColumnIndex(DepenseDAO.FIELD_MONTANT));
            //la devise de départ est la devise de la dépense courante
            String deviseDepart = listeDepenses.getString(listeDepenses.getColumnIndex(DepenseDAO.FIELD_DEVISE));
            //La devise par défaut depuis la base de données

            //Taux de change de la devise de la dépense courante vers la devise par défaut
            //exemple si conversion FCFA --> FCFA taux = 1
            Double taux = deviseDepart.equals(deviseArrivee)
                    ? 1d
                    : Double.valueOf(configDao.findValeurByDevises(deviseDepart, deviseArrivee));
            //Cummul des montants convertis
            somme += montantDep * taux;
        }
//        String deviseParDefaut = configDao.findValeurByCode(ConfigurationDAO.CONFIG_CODE_DEVISE_PAR_DEFAUT);
        if (closeCursor) {
            listeDepenses.close();
        }
        return somme;
    }

    public void refreshSommeDepensesView(Cursor c, boolean closeCursor) {
        Double sommeDepenses = sommeDepensesDeviseParDefaut(c, closeCursor);
        sommeDepensesTextSwitcher.setTag(sommeDepenses);
        //La somme en nombre formattée avec la devise locale
        sommeDepensesTextSwitcher.setText(DepenseFormatter.formatMontantToView((Double) sommeDepensesTextSwitcher.getTag(), deviseParDefaut));
    }

    private void refreshData() {
        if (texteRecherche != null && !texteRecherche.isEmpty()) {
            Intent i = new Intent(ListeDepensesActivity.this, ListeDepensesActivity.class);
            i.setAction(Intent.ACTION_SEARCH);
            i.putExtra(SearchManager.QUERY, texteRecherche);
            startActivity(i);
        } else {
            handleSearch("");
            // on affiche la vue Aucune dépense quand il n'y en a pas
            rechercherEditText.setText(rechercherEditText.getText());
            if (getListeDepensesViewAdapter().getCount() == 0) {
                listeDepensesEmpty.setVisibility(View.VISIBLE);
                sommeDepensesTextSwitcher.setVisibility(View.GONE);
            } else {
                listeDepensesEmpty.setVisibility(View.GONE);
                sommeDepensesTextSwitcher.setVisibility(View.VISIBLE);
            }
        }

    }

    private void refreshDataNew() {

        handleSearch("");
        // on affiche la vue Aucune dépense quand il n'y en a pas
        rechercherEditText.setText(rechercherEditText.getText());
        if (getListeDepensesViewAdapter().getCount() == 0) {
            listeDepensesEmpty.setVisibility(View.VISIBLE);
            sommeDepensesTextSwitcher.setVisibility(View.GONE);
        } else {
            listeDepensesEmpty.setVisibility(View.GONE);
            sommeDepensesTextSwitcher.setVisibility(View.VISIBLE);
        }
    }

    private void chargementDonnees() {

        //accès à la base de donnees
//        Toast.makeText(getApplicationContext(), "debug8", Toast.LENGTH_LONG).show();
        DepenseDAO dao = new DepenseDAO(this);
//        Toast.makeText(getApplicationContext(), "debug9", Toast.LENGTH_LONG).show();
        SQLiteDatabase db = dao.open();
//        Toast.makeText(getApplicationContext(), "debug10", Toast.LENGTH_LONG).show();

        Cursor c = dao.findAllOrderByDateDESC();

        Double totalDepenses = sommeDepensesDeviseParDefaut(c, false);
//        Double totalDepenses = dao.totalMontants();

        sommeDepensesTextSwitcher.setTag(totalDepenses);
        //La somme en nombre formattée avec la devise locale
//        sommeDepensesTextView.setText(DepenseFormatter.formatMontantToView((Double) sommeDepensesTextView.getTag(), "FCFA"));
        ConfigurationDAO configDao = new ConfigurationDAO(this);
        configDao.open();
        //Devise par défaut
        String deviseParDefaut = configDao.findValeurByCode(ConfigurationDAO.CONFIG_CODE_DEVISE_PAR_DEFAUT);
        sommeDepensesTextSwitcher.setText(DepenseFormatter.formatMontantToView((Double) sommeDepensesTextSwitcher.getTag(), deviseParDefaut));

        // ou LayoutInflater inflater = this.getLayoutInflater()
        // ou LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)
        LayoutInflater inflater = LayoutInflater.from(this);

        CursorAdapter adapter = new DepenseCursorAdapter(this, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER, inflater);

        adapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence constraint) {
                Cursor c = listeDepensesListviewFilterQuery(constraint);
                //en mode filtre
                ((DepenseCursorAdapter) getListeDepensesViewAdapter()).setHighlightText(constraint.toString());
//                refreshSommeDepensesView(c, false);
                return c;
            }

        });

        listeDepensesListView.setAdapter(adapter);
        if (c.getCount() == 0) {
            listeDepensesEmpty.setVisibility(View.VISIBLE);
            sommeDepensesTextSwitcher.setVisibility(View.GONE);
        } else {
            listeDepensesEmpty.setVisibility(View.GONE);
            sommeDepensesTextSwitcher.setVisibility(View.VISIBLE);
        }
        //Les sections (Les dates de jour en entêtes voir DepenseCursorAdapter#bindView()) ne s'affichent pas quand je mets pas ca
        handleSearch("");

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater mInflater = getMenuInflater();
        mInflater.inflate(R.menu.liste_depenses, menu);
//        rootMenuItem = menu.findItem(R.id.root_menu_item);
//        rootMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//        rootMenuItem.setShowAsAction(MenuItem.SHOW_AS);
//        rootMenuItem.setIcon(android.R.drawable.ic_menu_manage);

        repartirMenuItem = menu.findItem(R.id.group_by_type_menu_item);
        repartirMenuItem.setOnMenuItemClickListener(this);

        newMenuItem = menu.findItem(R.id.new_menu_item);
        newMenuItem.setOnMenuItemClickListener(this);

        searchMenuItem = menu.findItem(R.id.search_menu_item);
        SearchCallback listener = new SearchCallback();
        searchMenuItem.setOnActionExpandListener(listener);
//        searchMenuItem.setOnMenuItemClickListener(this);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager
                = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView
                = (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(listener);

        testMenuItem = menu.findItem(R.id.test_menu_item);
        testMenuItem.setOnMenuItemClickListener(this);

        configurationMenuItem = menu.findItem(R.id.config_menu_item);
        configurationMenuItem.setOnMenuItemClickListener(this);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

//        Toast.makeText(this, "onSaveInstanceState", Toast.LENGTH_LONG).show();
        //Pour sauvegarder le mode multisélection
        outState.putDouble(SOMME_DEPENSES_SELECTIONNEES, (Double) sommeDepensesTextSwitcher.getTag());
        outState.putString(TEXTE_RECHERCHE, texteRecherche);
//        outState.putString(MULTISELECTION_MODE_SUBTITLE_TEXT, texteSousTitreModeSelectionMultiple);
        // Mode multisélection

        //Pour sauvegarder le mode recherche
        //Fin Mode recherche
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        Toast.makeText(this, "onRestoreInstanceState", Toast.LENGTH_LONG).show();

        //pour restaurer le mode multisélection
        Double sommeDepenses = savedInstanceState.getDouble(SOMME_DEPENSES_SELECTIONNEES, 0d);
        sommeDepensesTextSwitcher.setTag(sommeDepenses);
        //          La somme en nombre formattée avec la devise locale
        sommeDepensesTextSwitcher.setText(DepenseFormatter.formatMontantToView((Double) sommeDepensesTextSwitcher.getTag(), deviseParDefaut));
        //Fin restauration multisélection

        //Pour restaurer le mode recherche
        texteRecherche = savedInstanceState.getString(TEXTE_RECHERCHE, null);
        //      si mode edition
        if (texteRecherche != null && !texteRecherche.isEmpty()) {
//            Toast.makeText(this, "onRestoreInstanceState  startserchactivity", Toast.LENGTH_LONG).show();
            Intent i = new Intent(ListeDepensesActivity.this, ListeDepensesActivity.class);
            i.setAction(Intent.ACTION_SEARCH);
            i.putExtra(SearchManager.QUERY, texteRecherche);
            startActivity(i);

        }
        // Fin restauration mode recherche

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onSearchRequested() {
        return super.onSearchRequested(); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean onMenuItemClick(MenuItem mi) {
        switch (mi.getItemId()) {
            case R.id.new_menu_item:
                Intent intent = new Intent(ListeDepensesActivity.this, NouvelleDepenseActivity.class);
                //C'est startActivity(intent) ;qui marche
                startActivityForResult(intent, CODE_REQUETE_POUR_NOUVELLE_DEPENSE);
                break;
            //si erreur return false je crois hein
            case R.id.group_by_type_menu_item:
//                Toast.makeText(getApplicationContext(), "Repartir", Toast.LENGTH_LONG).show();
                if (modeDeTri.equals(TRI_PAR_DATE_DECROISSANTE)) {
                    mi.setTitle(R.string.menu_item_title_retablir);
                    setModeDeTri(TRI_PAR_DATE_ET_TYPE_DECROISSANTS);
                } else if (modeDeTri.equals(TRI_PAR_DATE_ET_TYPE_DECROISSANTS)) {
                    setModeDeTri(TRI_PAR_DATE_DECROISSANTE);
                    mi.setTitle(R.string.menu_item_title_repartir);
                }
                refreshData();

                //si erreur return false je crois hein
                break;

            case R.id.search_menu_item:
//                startActionMode(new SearchCallback());

                break;
            case R.id.test_menu_item:
//                Intent i1 = new Intent(ListeDepensesActivity.this, TestActivity.class);
//                startActivity(i1);
                dialog.show();
                break;
            case R.id.config_menu_item:
                Intent i = new Intent(ListeDepensesActivity.this, ConfigurationActivity.class);
                startActivity(i);

                break;
        }
        return true;
    }

    //clic  sur un élément de la liste des dépense
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(ListeDepensesActivity.this, DetialsDepenseActivity.class);
//        Depense d = (Depense) view.getTag();
        Depense d = (Depense) listeDepensesListView.getItemAtPosition(position);
        intent.putExtra(DEPENSE_A_AFFICHER, d);
        intent.putExtra("position", position);
        //C'est startActivity(intent)
//                startActivity(intent);
        // renvoi d un resultat eventuel par l activite DetailsDepenseActivity
        startActivityForResult(intent, CODE_REQUETE_POUR_DETAILS_DEPENSE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            //resultat renvoyé par l'activité DetialsDepenseActivity
            case CODE_REQUETE_POUR_DETAILS_DEPENSE:
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
                                //mise à jour//       

                                //si mode edition
                                if (texteRecherche != null && !texteRecherche.isEmpty()) {
                                    Intent i = new Intent(ListeDepensesActivity.this, ListeDepensesActivity.class);
                                    i.setAction(Intent.ACTION_SEARCH);
                                    i.putExtra(SearchManager.QUERY, texteRecherche);
                                    startActivity(i);
                                } else {
//                                    refreshData();
                                    refreshDataNew();
                                }

                            }
                        }
                        break;
                    case RESULT_OK:
                        //suppression ok 
                        if (data != null) {
                            if (data.hasExtra("position")) {
                                int position = data.getIntExtra("position", -1);
                                double montant = data.getDoubleExtra("montant", 0d);
//                                Toast.makeText(this, "tu peux supprimer la view à la position " + position, Toast.LENGTH_LONG).show();

                                //C'est trop ... barbare je trouve
                                //Moi je voudrais par exemple supprimer "visuellement" l'élément concerné au lieu de tout recharger de la base après une seule suppression
//                                chargementDonnees();
                                //si mode edition
                                if (texteRecherche != null && !texteRecherche.isEmpty()) {
                                    Intent i = new Intent(ListeDepensesActivity.this, ListeDepensesActivity.class);
                                    i.setAction(Intent.ACTION_SEARCH);
                                    i.putExtra(SearchManager.QUERY, texteRecherche);
                                    startActivity(i);
                                } else {
//                                     Toast.makeText(this, "texteRecherche == "+((texteRecherche != null)?"not ":" ")+ "null", Toast.LENGTH_LONG).show();
//                                    refreshData();
                                    //Ca stabilise l'interface
                                    refreshDataNew();

                                }

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
            case CODE_REQUETE_POUR_NOUVELLE_DEPENSE:
                switch (resultCode) {
                    case RESULT_OK:
                        //ajout ok 
                        if (data != null) {
                            if (data.hasExtra("montant")) {

                                double montant = data.getDoubleExtra("montant", 0d);

//                                refreshData();
                                //si mode edition
                                if (texteRecherche != null && !texteRecherche.isEmpty()) {
                                    Intent i = new Intent(ListeDepensesActivity.this, ListeDepensesActivity.class);
                                    i.setAction(Intent.ACTION_SEARCH);
                                    i.putExtra(SearchManager.QUERY, texteRecherche);
                                    startActivity(i);
                                } else {
//                                    refreshData();
                                    refreshDataNew();
                                }
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

    @Override
    protected void onDestroy() {
        super.onDestroy(); //To change body of generated methods, choose Tools | Templates.
//        Toast.makeText(getApplicationContext(), "appel onDestroy ListeDepenses", Toast.LENGTH_LONG).show();
        getListeDepensesViewAdapter().getCursor().close();

    }

    //Classe pour gérer les sélections multiples
    protected class ModeCallback implements ListView.MultiChoiceModeListener {

        Double sommeDepenses = new Double(0);

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete_selected_menu_item:
                    //boite de dialogue supression multiple
                    AlertDialog.Builder builder = new AlertDialog.Builder(ListeDepensesActivity.this);
                    builder.setIcon(android.R.drawable.ic_dialog_alert);
                    builder.setTitle("Suppression multiple");
                    builder.setMessage("Supprimer les éléments sélectionnés ?");
                    //voir méthode onClick(DialogInterface, int)
                    builder.setPositiveButton(R.string.dialog1_reponse_oui, new ModeDialogInterfaceListener(mode) {
                        public void onClick(DialogInterface di, int which) {
                            long[] depensesASupprimer = listeDepensesListView.getCheckedItemIds();
                            DepenseDAO dao = new DepenseDAO(ListeDepensesActivity.this);
                            dao.open();
                            for (int i = 0; i < depensesASupprimer.length; i++) {
                                long id = depensesASupprimer[i];
                                dao.delete(id);
                            }
                            di.dismiss();
                            mode.finish();
                        }
                    });
                    //voir méthode onClick(DialogInterface, int)
                    builder.setNegativeButton(R.string.dialog1_reponse_non, new ModeDialogInterfaceListener(mode) {

                        public void onClick(DialogInterface di, int which) {
                            di.dismiss();

                        }
                    });
                    //On affiche la boite de dialogue
                    builder.create().show();

                    break;
                default:
                    break;
            }
            return true;
        }

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {

            //Initialisation
            configDao = new ConfigurationDAO(ListeDepensesActivity.this);
            configDao.open();

            //pour gerer la somme des depenses selectionnees
            //voir aussi methode onItemCheckedStateChanged
            //voir aussi methode onDestroyActionMode
            //sauvegarde de la somme initiale
            sommeDepenses = (Double) sommeDepensesTextSwitcher.getTag();
            //initialisation sommes des depenses selectionnees
//            sommeDepensesTextSwitcher.setTag(new Double(0));

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.liste_depenses_action_mode_multiselect, menu);
            mode.setTitle("Sélectionner dépenses");
//            toggleCheckBoxes(true);
            return true;
        }

        public void onDestroyActionMode(ActionMode mode) {
            //pour gerer la somme des depenses selectionnees
            //voir aussi methode onItemCheckedStateChanged
            //voir aussi methode onCreateActionMode
            //si mode edition
//            if (texteRecherche != null && !texteRecherche.isEmpty()) {
//                Intent i = new Intent(ListeDepensesActivity.this, ListeDepensesActivity.class);
//                i.setAction(Intent.ACTION_SEARCH);
//                i.putExtra(SearchManager.QUERY, texteRecherche);
//                startActivity(i);
//            } else {
//                refreshData();
//            }

//            refreshData();
            //Ca "stabilise" l'interface
            if (texteRecherche != null && !texteRecherche.isEmpty()) {
                Intent i = new Intent(ListeDepensesActivity.this, ListeDepensesActivity.class);
                i.setAction(Intent.ACTION_SEARCH);
                i.putExtra(SearchManager.QUERY, texteRecherche);
                startActivity(i);
            } else {
                refreshDataNew();
            }
        }
        //Pour l'accès aux paramètres de configurations depuis la base de données
        //Devise par défaut, taux de change, etc.
        ConfigurationDAO configDao;

        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            //nombre d elements selectionnes dans la liste
            final int checkedCount = listeDepensesListView.getCheckedItemCount();

            //pour afficher la somme des depenses selectionnees
            //voir aussi methode onCreateActionMode
            //voir aussi methode onDestroyActionMode
            Depense d = (Depense) listeDepensesListView.getItemAtPosition(position);

            double taux = Double.valueOf(configDao.findValeurByDevises(d.getDevise(), deviseParDefaut));
            double m = (checked ? d.getMontant() : -d.getMontant()) * taux;
            //si premier cochage
            if (checkedCount == 1 && checked) {
                sommeDepensesTextSwitcher.setTag(m);
            } else {
                sommeDepensesTextSwitcher.setTag((Double) sommeDepensesTextSwitcher.getTag() + m);
            }
            sommeDepensesTextSwitcher.setText(DepenseFormatter.formatMontantToView((Double) sommeDepensesTextSwitcher.getTag(), deviseParDefaut));
            //test
//            
//            switch (checkedCount) {
//                case 0:
//                    texteSousTitreModeSelectionMultiple = null;
//                    break;
//                case 1:
//                   texteSousTitreModeSelectionMultiple ="1 élément sélectionné";
//                    break;
//                default:
//                    texteSousTitreModeSelectionMultiple = "" + checkedCount + " éléments sélectionnés";
//                    break;
//            }
//             mode.setSubtitle(texteSousTitreModeSelectionMultiple);
        }

    }

    public abstract class ModeDialogInterfaceListener implements DialogInterface.OnClickListener {

        protected ActionMode mode;

        public ModeDialogInterfaceListener(ActionMode mode) {
            this.mode = mode;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String s = intent.getStringExtra(SearchManager.QUERY);
            handleSearch(s);
        }
        if (ConfigurationActivity.ACTION_CHANGE_CONFIGURATION.equals(intent.getAction())) {
//            Toast.makeText(this, "appel intent "+ConfigurationActivity.ACTION_CHANGE_CONFIGURATION, Toast.LENGTH_LONG).show();
            //Mise à jour de la devise par défaut si nécessaire
            if (intent.hasExtra(ConfigurationDAO.CONFIG_CODE_DEVISE_PAR_DEFAUT)) {
                String nouvelleDeviseParDefaut = intent.getStringExtra(ConfigurationDAO.CONFIG_CODE_DEVISE_PAR_DEFAUT);
                setDeviseParDefaut(nouvelleDeviseParDefaut);
            }
            //Mode édition
            if (texteRecherche != null && !texteRecherche.isEmpty()) {
                Intent i = new Intent(ListeDepensesActivity.this, ListeDepensesActivity.class);
                i.setAction(Intent.ACTION_SEARCH);
                i.putExtra(SearchManager.QUERY, texteRecherche);
                startActivity(i);
            } else {
                //Ca stabilise l'interface a la place de refreshData()
                refreshDataNew();
            }

        }
    }

    private class SearchCallback implements ActionMode.Callback, TextWatcher, MenuItem.OnActionExpandListener, SearchView.OnQueryTextListener {

        EditText zoneDeTextRechercher;

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.setTitle(R.string.rechercher);

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.liste_depenses_action_mode_search, menu);
            zoneDeTextRechercher = new EditText(ListeDepensesActivity.this);
            zoneDeTextRechercher.addTextChangedListener(this);
            mode.setCustomView(zoneDeTextRechercher);

            return true;

        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return true;
        }

        public void onDestroyActionMode(ActionMode mode) {
//            refreshData(0);
//            refreshDataNew(0);
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            
//            if(s.toString().isEmpty()){
//                rechercherEditText.clearFocus();
//            }
//            dialog.show();
            getListeDepensesViewAdapter().getFilter().filter(s.toString());
//            dialog.dismiss();
            return;
        }

        public boolean onMenuItemActionExpand(MenuItem item) {
            return true;
        }

        //Lorsque le menu rechercher se ferme
        public boolean onMenuItemActionCollapse(MenuItem item) {
            switch (item.getItemId()) {
                //menu rechercher
                case R.id.search_menu_item:
//                         if (!texteRecherche.isEmpty()) { 
////                        l'interface n'est pas stable avec ces appels
//                        chargementDonnees();
//                        refreshData();
//            }
                    handleSearch("");
                    break;
            }

            return true;
        }

        public boolean onQueryTextSubmit(String query) {
            return onQueryTextChange(query);
        }

        public boolean onQueryTextChange(String newText) {
            handleSearch(newText);
            return false;
        }

    }

    private void handleSearch(String query) {
        //On applique le filtre
        getListeDepensesViewAdapter().getFilter().filter(query);
        //On met a jour la somme des dépenses puisque la liste sera restrainte
        refreshSommeDepensesView(listeDepensesListviewFilterQuery(query), true);
        //
        texteRecherche = query;

    }

    public void setDeviseParDefaut(String DeviseParDefaut) {
        this.deviseParDefaut = DeviseParDefaut;
    }

    private Cursor listeDepensesListviewFilterQuery(CharSequence constraint) {
        DepenseDAO dao = new DepenseDAO(ListeDepensesActivity.this);
        dao.open();
        Cursor c = null;
        if (modeDeTri.equals(TRI_PAR_DATE_DECROISSANTE)) {
            c = dao.findByLibelleOrderByDateDESC(constraint.toString());
        } else if (modeDeTri.equals(TRI_PAR_DATE_ET_TYPE_DECROISSANTS)) {
            c = dao.findByLibelleOrderByTypeAndDateDESC(constraint.toString());
        }

        return c;
    }

    protected void initListeDepensesView(ViewGroup rootLayout, int layoutResourceId) {
        listeDepensesListView = (ListView) rootLayout.findViewById(layoutResourceId);

    }

    protected CursorAdapter getListeDepensesViewAdapter() {
        return ((DepenseCursorAdapter) listeDepensesListView.getAdapter());

    }

    //on change le mode de tri
    public void setModeDeTri(String modeDeTri) {
        this.modeDeTri = modeDeTri;
        //
        DepenseDAO dao = new DepenseDAO(this);
        dao.open();
        Cursor c = null;
        DepenseCursorAdapter adptr = ((DepenseCursorAdapter) getListeDepensesViewAdapter());
        if (modeDeTri.equals(TRI_PAR_DATE_DECROISSANTE)) {
            //en mode tri par date decroissante
            c = dao.findAllOrderByDateDESC();
            adptr.setAfficherDatesEnEntete(true);
            adptr.setAfficherTypesEnEntete(false);
            adptr.setAfficherPartieHeureSeulement(true);

        } else if (modeDeTri.equals(TRI_PAR_DATE_ET_TYPE_DECROISSANTS)) {
            //en mode tri par date et type decroissants
            c = dao.findAllOrderByTypeAndDateDESC();
            adptr.setAfficherDatesEnEntete(false);
            adptr.setAfficherTypesEnEntete(true);
            adptr.setAfficherPartieHeureSeulement(false);
        }
        adptr.changeCursor(c);
    }

}
