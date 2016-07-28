/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.protech.theprophet.android.gestionnairedepenses.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 *
 * @author BLU Kwensy Eli
 */
public class DepenseItemLayout extends RelativeLayout implements Checkable {

    private CheckBox checkedStateView;
    private TextView elementEnteteDeSection;
    

    public DepenseItemLayout(Context context) {
        super(context);
    }

    public DepenseItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DepenseItemLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setChecked(boolean checked) {
        checkedStateView.setChecked(checked);
        checkedStateView.setVisibility(checked ? View.VISIBLE : View.GONE);
    }

    public boolean isChecked() {
        return checkedStateView.isChecked();
    }

    public void toggle() {
        setChecked(!isChecked());
    }

    public void setCheckedStateView(CheckBox checkedStateView) {
        this.checkedStateView = checkedStateView;
    }

    public CheckBox getCheckedStateView() {
        return checkedStateView;
    }

    public void setElementEnteteDeSection(TextView elementEnteteDeSection) {
        this.elementEnteteDeSection = elementEnteteDeSection;
        this.elementEnteteDeSection.setEnabled(false);
        this.elementEnteteDeSection.setActivated(false);
        this.elementEnteteDeSection.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            }
        });
    }

    public TextView getElementEnteteDeSection() {
        return elementEnteteDeSection;
    }

}
