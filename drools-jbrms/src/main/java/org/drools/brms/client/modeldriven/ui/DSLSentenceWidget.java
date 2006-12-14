package org.drools.brms.client.modeldriven.ui;

import org.drools.brms.client.modeldriven.model.DSLSentence;
import org.drools.brms.client.modeldriven.model.DSLSentenceFragment;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This displays a widget to edit a DSL sentence.
 * 
 * @author Michael Neale
 *
 */
public class DSLSentenceWidget extends Composite {

    private HorizontalPanel horiz = new HorizontalPanel();
    
    /**
     * @param sentence The sentence to render/bind to.
     * @param lastOne true if this is the last one to render.
     * @param firstOne true if this is the first one to render.
     */
    public DSLSentenceWidget(DSLSentence sentence, boolean lastOne, boolean firstOne) {
        
        
        if (lastOne) {
            this.horiz.setStyleName( "model-builderInnerLastRow-Background" );
        }
        if (firstOne) {
            this.horiz.setStyleName( "model-builderInnerFirstRow-Background" );
        }
        if (!lastOne && !firstOne) {
            this.horiz.setStyleName( "model-builderInnerNoBorders-Background" );
        }
        
        init( sentence );
        
        
    }
    
    public DSLSentenceWidget(DSLSentence sentence) {
        this.horiz.setStyleName( "model-builderInner-Background" );
        init( sentence );
    }

    private void init(DSLSentence sentence) {
        for ( int i = 0; i < sentence.elements.length; i++ ) {
            final DSLSentenceFragment el = sentence.elements[i];
            if (!el.isEditableField) {                
                horiz.add( new Label(el.value) );
            } else {
                horiz.add( new HTML("&nbsp;") );
                final TextBox box = new TextBox();
                box.addChangeListener( new ChangeListener() {
                    public void onChange(Widget w) {
                      el.value = box.getText();  
                    }
                });
                box.setText( el.value );
                box.setVisibleLength( el.value.length() );
                horiz.add( box );
                horiz.add( new HTML("&nbsp;") );
            }
        }
        
        initWidget( this.horiz );
    }

    
    
    
}
