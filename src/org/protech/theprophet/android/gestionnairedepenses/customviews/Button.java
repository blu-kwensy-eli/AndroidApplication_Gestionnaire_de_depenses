/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.protech.theprophet.android.gestionnairedepenses.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import org.protech.theprophet.android.gestionnairedepenses.R;

/**
 *
 * @author BLU Kwensy Eli
 */
public class Button extends android.widget.Button{

    private int bgColor;
    private int pressedBgColor;
    public Button(Context context) {
        super(context);
        initialiser();
    }

    public Button(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialiser();
    }

    public Button(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialiser();
    }

    
    private void initialiser(){
        bgColor = getResources().getColor(R.color.color9);
        pressedBgColor = getResources().getColor(R.color.white);
        setBackgroundColor(bgColor);
        setTextColor(pressedBgColor);
        
    }

    
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            setBackgroundColor(pressedBgColor);     
            setTextColor(bgColor);
        }else if(event.getAction() == MotionEvent.ACTION_UP){
            setBackgroundColor(bgColor);
            setTextColor(pressedBgColor);
        }
        invalidate();
        
        return super.onTouchEvent(event); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    
}
