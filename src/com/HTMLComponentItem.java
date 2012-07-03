/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import com.sun.lwuit.Component;
import com.sun.lwuit.html.DefaultHTMLCallback;
import com.sun.lwuit.html.HTMLComponent;

/**
 *
 * @author caxthelm
 */

public class HTMLComponentItem extends ComponentItem {
    
    public HTMLComponentItem(String _sText) {
        super(COMP_HTMLTEXT);
        HTMLComponent cTextComp = new HTMLComponent();//new HTMLRequestHandler());
        //cTextComp.setWidth(500);
        cTextComp.setShowImages(true);
        
        cTextComp.getUnselectedStyle().setMargin(0, 0, 0, 0);
        cTextComp.getSelectedStyle().setMargin(0, 0, 0, 0);
        cTextComp.setBodyText(_sText);
        m_cComponent = cTextComp;
    }  
}

