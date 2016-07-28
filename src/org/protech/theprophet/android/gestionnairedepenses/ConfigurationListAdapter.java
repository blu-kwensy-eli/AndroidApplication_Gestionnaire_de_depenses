/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.protech.theprophet.android.gestionnairedepenses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author BLU Kwensy Eli
 */
public class ConfigurationListAdapter extends SimpleAdapter{
    public static final String TEXT1 = "code";
    public static final String TEXT2 = "valeur";
    View.OnClickListener listener;
    private List<HashMap<String, String>> liste ;
        LayoutInflater inflater;
    
    
    public ConfigurationListAdapter(Context context, List<HashMap<String, String>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
       this.liste = data;
        inflater = LayoutInflater.from(context);        
    }
    
    public ConfigurationListAdapter(Context context, List<HashMap<String, String>> data, int resource, String[] from, int[] to, View.OnClickListener listener) {
        this(context, data, resource, from, to);
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
//        v.setOnClickListener(listener);
        return v;
    }

    public List<HashMap<String, String>> getListe() {
        return liste;
    }


    

    
    
    
}
