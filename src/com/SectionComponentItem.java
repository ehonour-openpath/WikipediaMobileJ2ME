/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import com.sun.lwuit.*;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.html.*;
/**
 *
 * @author caxthelm
 */
public class SectionComponentItem extends ComponentItem {
    
    public SectionComponentItem(String _sTitle, int _iActionId, String _sSectionId) {
        super(COMP_SECTION, _sSectionId);
        setActionID(_iActionId);
    }
    public Component createComponent(String _sTitle, boolean _bActive) {
        Container cCont = mainMIDlet.getBuilder().createContainer(mainMIDlet.getResources(), "SectionItem");
        if(cCont != null) {
            Label cTitle = (Label)mainMIDlet.getBuilder().findByName("SectionTitleLabel", cCont);
            if(cTitle != null) {
                cTitle.setText(_sTitle);
            }
            Button cButton = (Button)mainMIDlet.getBuilder().findByName("SectionArrowButton", cCont);
            if(cButton != null) {
                Image newImage = mainMIDlet.getResources().getImage("arrowIconActive_large");
        
                Command newCommand = new Command("", newImage, m_iActionId);
                cButton.setCommand(newCommand);
            }
        }
        m_cComponent = (Component)cCont;
        return m_cComponent;
    }
}
