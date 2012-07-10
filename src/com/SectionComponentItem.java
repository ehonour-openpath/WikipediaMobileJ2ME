/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import com.sun.lwuit.*;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.html.*;

import java.util.Vector;
/**
 *
 * @author caxthelm
 */
public class SectionComponentItem extends ComponentItem {
    
    private boolean m_bActive = false;
    private Vector m_vSubsections = null;
    private int m_iSectionLevel = 1;
    private String m_sParentID = "";
    private Container m_cSubContainer = null;
    
    public SectionComponentItem(String _sTitle, int _iActionId, String _sSectionId) {
        super(COMP_SECTION, _sSectionId);
        setActionID(_iActionId);
    }//end SectionComponentItem(String _sTitle, int _iActionId, String _sSectionId)
    
    public boolean isActive() {
        return m_bActive;
    }//end isActive()
    
    public void setActive(boolean _bActive) {
        m_bActive = _bActive;   
        Button cButton = null;
        if(m_cComponent != null) {
            cButton = (Button)mainMIDlet.getBuilder().findByName("SectionArrowButton", (Container)m_cComponent);
            if(cButton != null ) {
                Image newImage = null;
                if(_bActive) {
                    newImage = mainMIDlet.getResources().getImage("arrowIconActive_large");
                }else {
                    newImage = mainMIDlet.getResources().getImage("arrowIcon_large");
                    m_cSubContainer.removeAll();
                }
        
                Command newCommand = new Command("", newImage, m_iActionId);
                cButton.setCommand(newCommand);
            }
        }
    }//end setActive(boolean _bActive)
    
    public void addSubsection(SectionComponentItem _oSubSection) {
        if(_oSubSection == null) {
            return;
        }
        if( m_vSubsections == null) {
            m_vSubsections = new Vector();
        }
        m_vSubsections.addElement(_oSubSection);
        if(m_bActive && m_cSubContainer != null) {
            //System.out.println("adding Subsection to "+getTag()+": "+_oSubSection.getTag());
            _oSubSection.setParentID(m_sCompTag);
            m_cSubContainer.addComponent(_oSubSection.getComponent());
        }

    }//end addSubsection(SectionComponentItem _oSubSection)
    
    public void addText(String _text) {
         if(_text != null && _text.length() > 0) {
            HTMLComponentItem oHTMLItem = new HTMLComponentItem(_text);
            HTMLComponent cTextComp = (HTMLComponent)oHTMLItem.getComponent();
            cTextComp.setPageUIID("Label");
            m_cSubContainer.addComponent(cTextComp);
        }
    }
    
    public int getSectionLevel() {
        return m_iSectionLevel;
    }//end getSectionLevel()
    
    public void setParentID(String _sParentID) {
        m_sParentID = _sParentID;
    }//end setParentID(String _sParentID)
    
    public String getParentID() {
        return m_sParentID;
    }//end getParentID()
    
    public Component createComponent(String _sTitle, boolean _bActive, int _iSectionLevel) {
        m_bActive = _bActive;
        m_iSectionLevel = _iSectionLevel;
        Container cCont = null;
        switch(m_iSectionLevel) {
            case 1:
                cCont = mainMIDlet.getBuilder().createContainer(mainMIDlet.getResources(), "SectionItem");
                break;
            case 2:
                cCont = mainMIDlet.getBuilder().createContainer(mainMIDlet.getResources(), "SectionLevel2Item");
                break;
            case 3:
                cCont = mainMIDlet.getBuilder().createContainer(mainMIDlet.getResources(), "SectionLevel3Item");
                break;
            case 4:
                cCont = mainMIDlet.getBuilder().createContainer(mainMIDlet.getResources(), "SectionLevel4Item");
                break;
            default:
                cCont = mainMIDlet.getBuilder().createContainer(mainMIDlet.getResources(), "SectionLevel4Item");
                break;
        }
        if(cCont != null) {
            m_cSubContainer = (Container)mainMIDlet.getBuilder().findByName("SubContainer", cCont);
            if(m_iSectionLevel > 1) {
                Button cTitle = (Button)mainMIDlet.getBuilder().findByName("SectionTitleLabel", cCont);
                if(cTitle != null) {
                    Command newCommand = new Command(_sTitle, m_iActionId);
                    cTitle.setCommand(newCommand);
                }
            } else {
                Label cTitle = (Label)mainMIDlet.getBuilder().findByName("SectionTitleLabel", cCont);
                if(cTitle != null) {
                    cTitle.setText(_sTitle);
                }            
            }
        }
        m_cComponent = (Component)cCont;
        setActive(_bActive);
        return m_cComponent;
    }//end createComponent(String _sTitle, boolean _bActive, int _iSectionLevel)
}
