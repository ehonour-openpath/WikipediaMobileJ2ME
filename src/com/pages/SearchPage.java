/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pages;

import com.sun.lwuit.*;
import com.sun.lwuit.events.*;
import com.sun.lwuit.Display;


import java.util.Vector;

import com.mainMIDlet;
import com.NetworkController;
/**
 *
 * @author caxthelm
 */
public class SearchPage extends BasePage {
    //Common Command Ids ;
    private final int COMMAND_BACK = COMMAND_RIGHT;
    private final int COMMAND_HOME = COMMAND_CENTER;
    
    //Lwuit Commands:   
    
    TextField m_cSearchTextField = null;
    Button m_cSearchButton = null;
    public SearchPage() {
        super("SearchPageForm", PAGE_SEARCH);
        try {
            if(!m_bIsLoaded) {
                //TODO: make error dialog.
                System.err.println("We failed to load");
                return;
            }
            //Create dynamic components here.
            
            m_cSearchTextField = (TextField)mainMIDlet.getBuilder().findByName("SearchTextField", m_cHeaderContainer);
            m_cSearchButton = (Button)mainMIDlet.getBuilder().findByName("SearchIconButton", m_cHeaderContainer);            
            if(m_cSearchButton != null) {
                m_cSearchButton.setVisible(false);
            }
            if(m_cSearchTextField != null) {
                m_cSearchTextField.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ev) {
                        TextField myText = (TextField)ev.getComponent();
                        if(!Display.getInstance().editingText) {
                            Display.getInstance().editString(ev.getComponent(), myText.getMaxSize(), myText.getConstraint(), myText.getText());
                        }
                    }
                });
                
                m_cSearchTextField.addDataChangeListener(new DataChangedListener()  {
                    public void dataChanged(int i, int i1) {
                        if(m_cSearchTextField != null) {
                            String message = m_cSearchTextField.getText();
                            if(m_cSearchButton != null) {
                                if(message != null && !message.equalsIgnoreCase(""))
                                {
                                    m_cSearchButton.setVisible(true);
                                }else 
                                    m_cSearchButton.setVisible(false);
                            }                            
                        }
                        m_cForm.repaint();
                    }
                });
            }
            
            m_cForm.addKeyListener(-4, new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    Component focusedComp = m_cForm.getFocused();
                    if(!(focusedComp instanceof TextField) || ((TextField)focusedComp).getText().length() == 0) {
                        //mainMIDlet.setCurrentPage(new BrowsePage(), true);
                    }
                }
            });
            m_cForm.addKeyListener(-3, new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    Component focusedComp = m_cForm.getFocused();
                    if(!(focusedComp instanceof TextField) || ((TextField)focusedComp).getText().length() == 0) {
                        //mainMIDlet.setCurrentPage(new WatchListPage(), true);
                    }
                }
            });
            //Add softkeys here.
            m_cForm.addShowListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    m_cForm.removeShowListener(this);
                    addData(null);
                }
            });
            updateSoftkeys();
            m_cForm.addCommandListener(this);
            //mForm.repaint();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }//end SearchPage()
    
    public void updateSoftkeys() {
        int i = 0;
        m_cForm.removeAllCommands();
        String  str = "";
        str = mainMIDlet.getString("BackSK");
        m_cForm.addCommand(new Command(str, COMMAND_BACK), i++);
        str = mainMIDlet.getString("HomeSK");
        m_cForm.addCommand(new Command(str, COMMAND_HOME), i++);
        
    }//end updateSoftkeys()
    
    public void actionPerformed(ActionEvent ae) {
        System.err.println("Action search: " + ae.getCommand().getId());
        int commandId = ae.getCommand().getId();
        if(commandId == COMMAND_OK) {
            Component focusedComp = m_cForm.getFocused();
            if(focusedComp instanceof Button){
                Button test = (Button)focusedComp;
                commandId = test.getCommand().getId();
            }else if(focusedComp instanceof Container) {
                Container testCont = (Container)focusedComp;
                Button test = (Button)testCont.getLeadComponent();
                if(test != null && testCont.getLeadComponent() instanceof Button) {
                    commandId = test.getCommand().getId();
                }
            }
        }
        switch(commandId) {                
            //Softkeys
            case COMMAND_BACK:
                mainMIDlet.pageBack();
                break;
            case COMMAND_HOME:
                {
                    mainMIDlet.setCurrentPage(new MainPage(), true);
                }
                break;                
            case COMMAND_SEARCHBUTTON:
                //TODO: Network connection to get "did you mean" items. 
                if(m_cSearchButton != null && m_cSearchButton.isVisible()) {
                    String text = "";
                    if(m_cSearchTextField != null) {
                        text = m_cSearchTextField.getText();
                    }
                    if(text.length() > 0) {
                        mainMIDlet.setCurrentPage(new ArticlePage(text, null));
                    }
                }
                break;
        }
    }//end actionPerformed(ActionEvent ae)
    
    public void refreshPage() {        
        m_cForm.addShowListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                //System.out.println("reshowing search");
                m_cForm.removeShowListener(this);
                checkRefresh();
            }
        });
    }//end refreshPage()
    
    private void checkRefresh() {
        NetworkController.hideLoadingDialog();
        addData(null);
        Thread.yield();
        
        super.refreshPage();
    }//end checkRefresh()
    
    public void addData(Object _results) {
        m_cForm.repaint();
    }//end addData(Object _results)
    
    
}
