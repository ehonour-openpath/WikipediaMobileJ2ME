/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pages;


import com.NetworkController;
import com.mainMIDlet;

import com.sun.lwuit.*;
import com.sun.lwuit.events.*;
import java.util.TimerTask;
import java.util.Vector;

/**
 *
 * @author caxthelm
 */
public class BasePage implements ActionListener {
    //Page IDs;
    public static final int PAGE_SPLASH = 1;
    public static final int PAGE_MAIN = 2;
    public static final int PAGE_SEARCH = 3;
    public static final int PAGE_SETTINGS = 4;
    public static final int PAGE_ARTICLE = 5;
    
    public static final int DIALOG_IMAGE = -1;
    public static final int DIALOG_SEARCHRESULT = -2;
    
    //Common Command Ids ;
    //Note: commands 0 through 9 are reserved for softkeys.
    public static final int COMMAND_RIGHT = 0;
    public static final int COMMAND_CENTER = 1;
    public static final int COMMAND_LEFT = 2;
    public static final int COMMAND_OK = -1;
    //commands 10 through 29 are reserved for common IDs
    public static final int COMMAND_SEARCHBUTTON = 20;
    //Commands 30+ are dynamic commands.
    
    int m_iPageType = PAGE_SPLASH;
    
    Form m_cForm = null;
    Dialog m_cDialog = null;
    Container m_cContentContainer = null;
    Container m_cHeaderContainer = null;
    public boolean m_bIsLoaded = false;
    public boolean m_bIsDialog = false;
    
    Dialog m_cFailDialog = null;
    
    
    public class RefreshTimerTask extends TimerTask {
        public RefreshTimerTask() {
        }
        public void run() {
            refreshPage();
        }
    }
    
    public BasePage(String _sPageName, int _iPageType) {
        NetworkController.cancelNetwork();
        m_iPageType = _iPageType;
        if(m_iPageType < 0) {
            m_bIsDialog = true;
        }
        try {
            Container cont = mainMIDlet.getBuilder().createContainer(mainMIDlet.getResources(), _sPageName);
            if(m_bIsDialog) {
                m_cDialog = (Dialog) cont;
                
                m_cContentContainer = (Container)mainMIDlet.getBuilder().findByName("ContentContainer", m_cDialog);
                if(m_cContentContainer != null) {
                }
            }else {
                m_cForm = (Form) cont;
                m_cHeaderContainer = (Container)mainMIDlet.getBuilder().findByName("HeaderContainer", m_cForm);
                if(m_cContentContainer != null) {
                }                
                m_cContentContainer = (Container)mainMIDlet.getBuilder().findByName("ContentContainer", m_cForm);
                if(m_cContentContainer != null) {
                }
            }
            if(m_cContentContainer == null) {
                System.err.println("page: "+m_iPageType+", container not found");
            }
            m_bIsLoaded = true;            
        }
        catch(Exception e) {
            e.printStackTrace();
            System.err.println("page: "+m_iPageType+", page fail");
            m_bIsLoaded = false;
        }    
    }//end BasePage(String _sPageName, int _iPageType)
    
    public int getType() {
        return m_iPageType;
    }//end getType()
    
    public void showForm() {
        if(m_bIsDialog) {
            m_cDialog.show();
        }   
        if(m_cForm != null) {
            m_cForm.show();
        }
    }//end showForm()

    public void actionPerformed(ActionEvent ae) {
    }//end actionPerformed(ActionEvent ae)
    
    public void refreshPage() {
        if(m_cForm != null) {
            m_cForm.refreshTheme();
            m_cForm.invalidate();
            m_cForm.repaint();//must be repaint for loading screens to show.
            //mForm.show();//this remakes the entire page.
            //mainMIDlet.mainBuilder.reloadForm();//this causes a crash.
        }
    }//end refreshPage()
    
    public void updateSoftkeys() {
    }//end updateSoftkeys()
    
    public void addData(Object _oResults) {        
    }//end addData(Object _results)
    
    public void failedNetwork(int _iResponse) {
        //System.out.println("showing Failure");
        try {
            String netErrorTitle = mainMIDlet.getString("NetErrorTitle");
            m_cFailDialog = new Dialog(netErrorTitle);
            if(m_cFailDialog != null) {
                String OkSk = mainMIDlet.getString("ok");
                Command commands = new Command(OkSk);
                m_cFailDialog.addCommand(new Command(OkSk){
                    public void actionPerformed(ActionEvent ev) {
                        m_cFailDialog.dispose();
                        m_cForm.show();
                        }
                    });

                String netErrorText = mainMIDlet.getString("NetErrorText");
                TextArea text = new TextArea(netErrorText);
                //text.setUIID("LabelClear");
                text.setEditable(false);
                m_cFailDialog.addComponent(text);
                
                /*if(mainMIDlet.phoneType == mainMIDlet.TYPE_S60) {
                    m_cFailDialog.show(195, 196, 55, 56, false, true);
                }else*/
                    m_cFailDialog.show(10, 10, 10, 10, false);
            }
            //throw new RuntimeException("base failed");
        } catch (Exception e ) {
            e.printStackTrace();
        }
    }//end failedNetwork(int _response)
    
    public void dispose()
    {
        if(m_cDialog != null) {
            //mDialog.dispose();
            //System.out.println("We finished showing");
        }        
    }//end dispose()
}
