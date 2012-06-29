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
    public static final int COMP_CATEGORYBUTTON = 1;
    public static final int COMP_RESULTSITEM = 2;
    
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
    
    //Component creations
    public boolean createCategoryTextItem(String _label) {
        Container returnComp = mainMIDlet.getBuilder().createContainer(mainMIDlet.getResources(), "CategoryTextItem");
        if(returnComp == null) {
            return false;
        }
                
        Label catLabel = (Label)mainMIDlet.getBuilder().findByName("CategoryLabel", returnComp);
        TextField catTextField = (TextField)mainMIDlet.getBuilder().findByName("CategoryTextField", returnComp);
       
        if(catLabel != null) {            
            catLabel.setText(_label);
        }
        if(catTextField != null) {
            catTextField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev)
                {
                    TextField myText = (TextField)ev.getComponent();
                    if(!Display.getInstance().editingText)
                        Display.getInstance().editString(ev.getComponent(), myText.getMaxSize(), myText.getConstraint(), myText.getText());
                }
            });
        }
        
        m_cComponent = (Component)returnComp;
        return true;
    }//end createCategoryTextItem(String _label)
    
    public boolean createCategoryItem(String _label, String _button) {
        Container returnComp = mainMIDlet.getBuilder().createContainer(mainMIDlet.getResources(), "CategoryItem");
        if(returnComp == null) {
            return false;
        }
                
        Label catLabel = (Label)mainMIDlet.getBuilder().findByName("CategoryLabel", returnComp);
        Button catButton = (Button)mainMIDlet.getBuilder().findByName("CategoryButton", returnComp);
       
        if(catLabel != null) {
            catLabel.setText(_label);
        }
        if(catButton != null) {
            //catButton.setText(_button);
            catButton.setCommand(new Command(_button, m_iActionId));
        }
        m_cComponent = (Component)returnComp;
        return true;
    }//end createCategoryItem(String _label, String _button)
    
    public boolean createCategoryButtonItem(String _button) {
        Container returnComp = mainMIDlet.getBuilder().createContainer(mainMIDlet.getResources(), "CategoryButtonItem");
        if(returnComp == null) {
            return false;
        }
        Button catButton = (Button)mainMIDlet.getBuilder().findByName("CategoryButton", returnComp);
       
        if(catButton != null) {
            //catButton.setText(_button);
            catButton.setCommand(new Command(_button, m_iActionId));
        }
        m_cComponent = (Component)returnComp;
        return true;
    }//end createCategoryButtonItem(String _button)
    
    public boolean createChoiceItem(String _button) {
        Container returnComp = mainMIDlet.getBuilder().createContainer(mainMIDlet.getResources(), "ChoiceListItem");
        if(returnComp == null) {
            return false;
        }
        
        Button textButton = (Button)mainMIDlet.getBuilder().findByName("Text", returnComp);
       
        if(textButton != null) {
            //textButton.setText(_button);
            textButton.setCommand(new Command(_button, m_iActionId));
        }
        m_cComponent = (Component)returnComp;
        return true;
    }//end createChoiceItem(String _button)
    
    public boolean createResultsListItem(String _label, String _price, String _type, String _url) {
        
        Container returnComp = mainMIDlet.getBuilder().createContainer(mainMIDlet.getResources(), "ResultsListItem");
        if(returnComp == null) {
            System.err.println("got null");
            return false;
        }
        Button imageButton = (Button)mainMIDlet.getBuilder().findByName("Image", returnComp);
        TextArea text = (TextArea)mainMIDlet.getBuilder().findByName("Text", returnComp);
        Label price = (Label)mainMIDlet.getBuilder().findByName("Price", returnComp);
        Label type = (Label)mainMIDlet.getBuilder().findByName("Type", returnComp);
        if(imageButton != null) {
            Image img = null;
            try {
                if(_url.length() != 0 && _url != null && _url.indexOf("http://") > -1) {
                    ImageDownloadService img2 = new ImageDownloadService(_url, imageButton);
                    NetworkManager.getInstance().addToQueue(img2);
                }else {
                    img = mainMIDlet.getResources().getImage("noImage");
                    imageButton.setIcon(img);
                }
                //image = Image.createImage(item.getImage(0, 0));
                imageButton.setPreferredW(58);
                imageButton.setPreferredH(58);
            }catch(Exception e) {
                e.printStackTrace();
            }
            imageButton.setCommand(new Command("", img, m_iActionId));
        }
        if(text != null) {
            text.setText(_label);
        }
        if(price != null) {
            price.setText(_price);
            price.setEnabled(false);
        }
        if(type != null) {
            type.setText(_type);
            type.setEnabled(false);
        }
        m_cComponent = (Component)returnComp;
        return true;
    }//end createResultsListItem(String _label, String _price, String _type, String _url)
    
    public void cleanChildren() {
        m_cComponent = null;
        JsonObject.cleanChild(m_oAttachedObject);
        m_oAttachedObject = null;
    }//end cleanChildren()
}
