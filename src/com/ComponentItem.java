/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import com.sun.lwuit.*;
import com.sun.lwuit.events.*;
import com.sun.lwuit.io.*;
import com.sun.lwuit.Display;
import com.sun.lwuit.io.services.ImageDownloadService;

/*
 *
 * @author caxthelm
 */


public class ComponentItem 
{    
    //Component Types;
    public static final int COMP_SECTION = 0;
    public static final int COMP_HTMLTEXT = 1;
    
    Component m_cComponent = null;
    int m_iCompType = -1;
    
    //ActionId is used for the command actions of buttons.
    int m_iActionId = 0;
    
    //Component tag is an extra variable that can be used to identify items for
    //sending to the server.  i.e. section ID.
    String m_sCompTag = "";
    
    Object m_oAttachedObject = null;
    
    
    public ComponentItem(int _iCompType) {
        m_iCompType = _iCompType;
    }//end ComponentItem(int _iCompType)
    
    public ComponentItem(int _iCompType, String _sCompTag) {
        m_sCompTag = _sCompTag;
        m_iCompType = _iCompType;
    }//end ComponentItem(int _iCompType, String _sCompTag)
    
    public ComponentItem(int _iCompType, Component _cComponent) {
        m_cComponent = _cComponent;
        m_iCompType = _iCompType;
    }//end ComponentItem(int _iCompType, Component _cComponent)
    
    public void setComponent(Component _cComponent) {
        m_cComponent = _cComponent;
    }//end setComponent(Component _cComponent)
    public Component getComponent() {
        return m_cComponent;
    }//end getComponent() 
    
    public boolean checkActionID(int _iActionId) {
        return _iActionId == m_iActionId;
    }//end checkID(int _iId)
    
    public void setActionID(int _iActionId) {
        m_iActionId = _iActionId;
    }//end getID()
    
    public int getActionID() {
        return m_iActionId;
    }//end getID()
    
    public String getTag() {
        return m_sCompTag;
    }//end getTag()
    
    public void setObject(Object _oAttachedObject) {
        m_oAttachedObject = _oAttachedObject;
    }//end setObject(Object _oAttachedObject)
    
    public Object getObject() {
        return m_oAttachedObject;
    }//end getObject()
    
    //Component creation should be done in inhereted classes.
    /*public Component createComponent() {
        return m_cComponent;
    }*/
    
    public void cleanChildren() {
        m_cComponent = null;
        JsonObject.cleanChild(m_oAttachedObject);
        m_oAttachedObject = null;
    }//end cleanChildren()
}
