/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.protech.theprophet.android.gestionnairedepenses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;
import org.protech.theprophet.android.gestionnairedepenses.entities.Depense;
import org.protech.theprophet.android.gestionnairedepenses.utils.DepenseFormatter;

/**
 *
 * @author TheProphet
 */
public class DepenseAdapter extends BaseAdapter {
    
    LayoutInflater inflater;
    List<Depense> list;

    public DepenseAdapter() {
    }

    public DepenseAdapter(List<Depense> list, LayoutInflater inflater) {
        this.list = list;
        this.inflater = inflater;
    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int r, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        // Si la vue n'est pas recyclée
        if (convertView == null) {
            // On récupère le layout
            convertView = inflater.inflate(R.layout.depense_item, null);

            holder = new ViewHolder();
            // On place les widgets de notre layout dans le holder
            holder.id = (TextView) convertView.findViewById(R.id.depense_id);
            holder.montant = (TextView) convertView.findViewById(R.id.depense_montant);
            holder.libelle = (TextView) convertView.findViewById(R.id.depense_libelle);
            holder.type = (TextView) convertView.findViewById(R.id.depense_type);
            holder.illustrationType = (TextView) convertView.findViewById(R.id.depense_illustration_type);
            holder.dateReelle = (TextView) convertView.findViewById(R.id.depense_date);
            holder.dateSiPremier = (TextView) convertView.findViewById(R.id.depense_date_si_premier);

            // puis on insère le holder en tant que tag dans le layout
            convertView.setTag(holder);
        } else {
            // Si on recycle la vue, on récupère son holder en tag
            holder = (ViewHolder) convertView.getTag();
        }

        // 
        Depense dep = (Depense) getItem(r);
        // Si cet élément existe vraiment…
        if (dep != null) {
            // On place dans le holder les informations sur l'entité
            //pas besoin de gérer null
            if (dep.getId() != null) {
                holder.id.setText(String.valueOf(dep.getId()));
            }
            holder.dateReelle.setText(DepenseFormatter.formatDateToString(dep.getDateRelle()));
            holder.montant.setText(DepenseFormatter.formatMontantToView(dep));
            holder.libelle.setText(dep.getLibelle());
            holder.type.setText(dep.getType());
            holder.illustrationType.setText(DepenseFormatter.formatTypeToView(dep));
            if (dep.isPremierPourSaDate()) {
                holder.dateSiPremier.setVisibility(View.VISIBLE);
            } else {
                //::QUAND J4ENL7VE LE else CA MARCHE PAS 
                holder.dateSiPremier.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    static class ViewHolder {
        private TextView libelle;
        private TextView montant;
        private TextView type;
        private TextView illustrationType;
        private TextView dateSiPremier;
        private TextView id;
        private TextView dateReelle;
    }
    public void removeItem(int position){
        list.remove(position);
        notifyDataSetChanged();
    }
    
    public void setItem(int position, Depense d){
        list.set(position, d);
        notifyDataSetChanged();
    }
}
