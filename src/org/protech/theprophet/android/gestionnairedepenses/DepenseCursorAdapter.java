/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.protech.theprophet.android.gestionnairedepenses;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.FilterQueryProvider;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import org.protech.theprophet.android.gestionnairedepenses.customviews.DepenseItemLayout;
import org.protech.theprophet.android.gestionnairedepenses.dao.DepenseDAO;
import org.protech.theprophet.android.gestionnairedepenses.entities.Depense;
import org.protech.theprophet.android.gestionnairedepenses.utils.DepenseFormatter;

/**
 *
 * @author TheProphet
 */
public class DepenseCursorAdapter extends CursorAdapter {

    //le texte recherché est mis en surbrillance
    private String highlightText = "";

    private LayoutInflater inflater;
    //Les données extraites de la base de données
    private Cursor cur;
    private Context ctx;
    //- Pour contenir les identifiants des dépenses dont les dates seront visibles en entête de sections constituées en groupant les dépenses d'une même journée
    //- Les clés du tableau associatif seront des dates en format dd/MM/yyyy voir Depense.FORMAT_DATE
    //- Pour une journée i.e une clé, la valeur correspondante sera l'identifiant de la dépense commençant(resp. terminant)
    //cette journée si les dépenses sont triées des plus récentes (resp. anciennes) aux plus anciennes (resp. récentes)
    private HashMap<String, Long> map = new HashMap<String, Long>();
    //- Pour contenir les identifiants des dépenses dont les dates seront visibles en en-tête de sections constituées en groupant les dépenses d'une même type
    //- Les clés du tableau associatif seront les types de dépense
    //- Pour un type i.e une clé, la valeur correspondante sera l'identifiant de la dépense commençant(resp. terminant)
    //la section si les dépenses sont triées des plus récentes (resp. anciennes) aux plus anciennes (resp. récentes)
    private HashMap<String, Long> map2 = new HashMap<String, Long>();

    //Pour les données triées initialement
    //si true, Pour chaque dépense dans la liste on n'affiche que l'heure
    boolean afficherPartieHeureSeulement = true;
    //si true, les dates en entete de section seront  visibles
    private boolean afficherDatesEnEntete = true;
    //si true, les types en entete de section seront  visibles
    private boolean afficherTypesEnEntete = false;

    // Choisx de conception : si les données sont groupées par type les variables "afficherPartieHeureSeulement" et "afficherDatesEnEntete" sont inversées
    public DepenseCursorAdapter(Context ctx, Cursor c, int flags, LayoutInflater inflater) {
        super(ctx, c, flags);
        this.cur = c;
        this.inflater = inflater;
        this.ctx = ctx;
        setListHeaderIdsForDaySections();

    }

    @Override
    public Object getItem(int position) {
        Cursor c = getCursor();
        if (c != null) {
            if (getCursor().moveToPosition(position)) {
                //Identifiant
                Long id = c.getLong(c.getColumnIndex(DepenseDAO.FIELD_ID));
                //Montant
                Double mont = c.getDouble(c.getColumnIndex(DepenseDAO.FIELD_MONTANT));
                //devise
                String dev = c.getString(c.getColumnIndex(DepenseDAO.FIELD_DEVISE));
                //libellé
                String lib = c.getString(c.getColumnIndex(DepenseDAO.FIELD_LIBELLE));
                //date reelle
                Date datR = DepenseFormatter.formatDateRelleDBToModel(c.getLong(c.getColumnIndex(DepenseDAO.FIELD_DATE_REELLE)));
                //date systeme
                Date datS = DepenseFormatter.formatDateSystemeDBToModel(c.getLong(c.getColumnIndex(DepenseDAO.FIELD_DATE_REELLE)));
                //Type
                String typ = c.getString(c.getColumnIndex(DepenseDAO.FIELD_TYPE));

                Depense dep = new Depense(id, lib, mont, typ, datS, datR, dev);
                return dep;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = super.getView(position, convertView, parent);

        return v;
    }

    @Override
    public long getItemId(int position) {
        Cursor c = getCursor();
        if (c != null) {
            if (c.moveToPosition(position)) {
                return c.getLong(c.getColumnIndex(DepenseDAO.FIELD_ID));
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
//        View v =    inflater.inflate(R.layout.depense_item, null);
        DepenseItemLayout view = (DepenseItemLayout) inflater.inflate(R.layout.depense_item, null);
        ViewHolder holder = new ViewHolder();
        holder.dateSiPremier = ((TextView) view.findViewById(R.id.depense_date_si_premier));
        holder.id = ((TextView) view.findViewById(R.id.depense_id));
        holder.dateReelle = ((TextView) view.findViewById(R.id.depense_date));
        holder.montant = ((TextView) view.findViewById(R.id.depense_montant));
        holder.libelle = ((TextView) view.findViewById(R.id.depense_libelle));
        holder.type = ((TextView) view.findViewById(R.id.depense_type));
        holder.illustrationType = ((TextView) view.findViewById(R.id.depense_illustration_type));
        holder.checkedState = (CheckBox) view.findViewById(R.id.depense_checked_checkbox);
        holder.enteteDeSection = (TextView) view.findViewById(R.id.entete_de_section_pour_depenses_de_meme_type);

        // on associe les données a la vue
        view.setTag(holder);

        holder.checkedState.setFocusable(false);
        view.setCheckedStateView(holder.checkedState);
        view.setElementEnteteDeSection(holder.enteteDeSection);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor c) {

        //Identifiant
        Long id = c.getLong(c.getColumnIndex(DepenseDAO.FIELD_ID));
        //Montant
//        Double mont = cur.getDouble(DepenseDAO.FIELD_MONTANT_POS);
        Double mont = c.getDouble(c.getColumnIndex(DepenseDAO.FIELD_MONTANT));
        //devise
        String dev = c.getString(c.getColumnIndex(DepenseDAO.FIELD_DEVISE));
        //libellé
        String lib = c.getString(c.getColumnIndex(DepenseDAO.FIELD_LIBELLE));
        //date reelle
        Date datR = DepenseFormatter.formatDateRelleDBToModel(c.getLong(c.getColumnIndex(DepenseDAO.FIELD_DATE_REELLE)));
        //date systeme
        Date datS = DepenseFormatter.formatDateSystemeDBToModel(c.getLong(c.getColumnIndex(DepenseDAO.FIELD_DATE_REELLE)));
        //Type
        String typ = c.getString(c.getColumnIndex(DepenseDAO.FIELD_TYPE));

        Depense dep = new Depense(id, lib, mont, typ, datS, datR, dev);

        ViewHolder holder = (ViewHolder) view.getTag();

        holder.id.setText(String.valueOf(dep.getId()));
        if (afficherPartieHeureSeulement) {
            holder.dateReelle.setText(DepenseFormatter.getTimePart(datR));
        } else {
            holder.dateReelle.setText(DepenseFormatter.formatDateToString(datR));
        }

        holder.montant.setText(DepenseFormatter.formatMontantToView(dep));

        holder.libelle.setTag(lib);
        if (highlightText.isEmpty()) {
            holder.libelle.setText(lib);
        } else {
            holder.libelle.setText(mettreEnSurbrilllance((String) holder.libelle.getTag()));
        }

        holder.type.setText(typ);
        holder.illustrationType.setText(DepenseFormatter.formatTypeToView(dep));

        /// Customisation de la vue, affichage des sections, etc.
        //date au format dd/MM/yyyy
        String dateDep = DepenseFormatter.getDatePart(dep.getDateRelle());
        String today = DepenseFormatter.getDatePart(Calendar.getInstance().getTime());

//        //dates ultérieure a la date d'aujourd'hui
//        boolean dateEstUlterieure = dep.getDateRelle().after(Calendar.getInstance().getTime());
        if (afficherDatesEnEntete && map.containsValue(id)) {
            holder.dateSiPremier.setVisibility(View.VISIBLE);
//                //changer de couleur d'entete de date pour les dépenses dont la date est ultérieure a la date d'aujourd'hui
//                if (dateEstUlterieure) {
//                    holder.dateSiPremier.setBackgroundColor(ctx.getResources().getColor(R.color.color4));
//                }
            //Affiche le texte aujourd'hui à la place de la date quand nécessaire
            holder.dateSiPremier.setText(dateDep.equals(today) ? ctx.getResources().getString(R.string.aujourd_hui) : dateDep);
        } else {
            holder.dateSiPremier.setVisibility(View.GONE);
            holder.dateSiPremier.setText("");
        }

//     
        if (afficherTypesEnEntete && map2.containsValue(id)) {
            holder.enteteDeSection.setVisibility(View.VISIBLE);
            holder.enteteDeSection.setText(ctx.getResources().getString(R.string.titre_entete_de_type, typ));
        } else {
            holder.enteteDeSection.setVisibility(View.GONE);
            holder.enteteDeSection.setText("");
        }

    }

    //redéfinis les identifiants de dépense dont la date sera en tête de section
    private void setListHeaderIdsForDaySections() {
        DepenseDAO dao = new DepenseDAO(ctx);
        dao.open();
        map = dao.processFindWithMaxDateGroupByDay(getCursor(), "_id", false);
    }

    //redéfinis les identifiants de dépense dont le type sera en tête de section
    private void setListHeaderIdsForTypeSections() {
        DepenseDAO dao = new DepenseDAO(ctx);
        dao.open();
        map2 = dao.processFindWithMaxDateGroupByType(getCursor(), "_id", false);
    }

    @Override
    public void notifyDataSetChanged() {

        if (afficherDatesEnEntete) {
            setListHeaderIdsForDaySections();
        }
        if (afficherTypesEnEntete) {
            setListHeaderIdsForTypeSections();
        }

        super.notifyDataSetChanged();
    }

    @Override
    public FilterQueryProvider getFilterQueryProvider() {
        return super.getFilterQueryProvider(); //To change body of generated methods, choose Tools | Templates.
    }

    static class ViewHolder {

        private TextView libelle;
        private TextView montant;
        private TextView type;
        private TextView illustrationType;
        private TextView dateSiPremier;
        private TextView enteteDeSection;
        private TextView id;
        private TextView dateReelle;
        private CheckBox checkedState;
    }

    public void setAfficherDatesEnEntete(boolean afficherDatesEnEntete) {
        this.afficherDatesEnEntete = afficherDatesEnEntete;
    }

    public void setAfficherTypesEnEntete(boolean afficherTypesEnEntete) {
        this.afficherTypesEnEntete = afficherTypesEnEntete;
    }

    public void setAfficherPartieHeureSeulement(boolean afficherPartieHeureSeulement) {
        this.afficherPartieHeureSeulement = afficherPartieHeureSeulement;
    }

    private CharSequence mettreEnSurbrilllance(String input) {
        //si texte a mettre en surbrillance n'existe pas
        if (highlightText == null || highlightText.isEmpty()) {
            return input;
        }
        //sinon
        //copie de la valeur d'entrée
        String copieInput = String.copyValueOf(input.toCharArray());
        //couleur de mise en surbrillance
        int c = ctx.getResources().getColor(R.color.color4);

        //Recherche du texte a mettre en surbrillance (la casse du texte recherché peut exclure certaines correspondances)
        //le texte recherché peut exister en plusieurs occurences
        ArrayList<Integer> indexes = new ArrayList<Integer>();
        //Les occurences à mettre en surbrillance
        ArrayList<String> strings = new ArrayList<String>();
        //position de départ de la première occurence
        int i0 = copieInput.toLowerCase().indexOf(highlightText.toLowerCase());
        //position de départ de la dernière occurence
        int in = copieInput.toLowerCase().lastIndexOf(highlightText.toLowerCase());
        //tant qu'on n'atteint pas la dernière occurrence en recherchant
        while (i0 <= in && i0 != -1) {
            ///on ajoute i0
            indexes.add(i0);
            //l'un des textes a mettre a surbrillance
            String occurenceHighlight = copieInput.substring(i0, i0 + highlightText.length());
            if(!strings.contains(occurenceHighlight)){
                strings.add(occurenceHighlight);
            }            
            //on positionne le début de la recherche a l'index suivant
            i0 = copieInput.toLowerCase().indexOf(highlightText.toLowerCase(), i0+1);
        }
         //Mise en surbrillance
         for (String occ : strings) {
            copieInput = copieInput.replace(occ, "<font color=\"" + String.valueOf(c)
                + "\"><b>" + occ + "</b></font>");
        }
        return Html.fromHtml(copieInput);
    }

    public void setHighlightText(String highlightText) {
        this.highlightText = highlightText;
    }

}
