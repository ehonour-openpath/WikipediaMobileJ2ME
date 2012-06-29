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
import com.JsonObject;
import com.NetworkController;
/**
 *
 * @author caxthelm
 */
public class ArticlePage extends BasePage {
    //Common Command Ids ;
    private final int COMMAND_BACK = COMMAND_RIGHT;
    private final int COMMAND_SEARCH = COMMAND_CENTER;
    private final int COMMAND_SAVEPAGE = COMMAND_LEFT;
    private final int COMMAND_BOOKMARK = COMMAND_SAVEPAGE + 1;
    //private final int Command_Privacy = Command_Terms + 1;
    private final int COMMAND_HOME = COMMAND_BOOKMARK + 1;
    
    //Lwuit Commands:   
    JsonObject m_oData = null;
    String m_sTitle = "";
    String m_sCurrentSections = "0";
    TextField searchTextField = null;
    public ArticlePage(String _sTitle, JsonObject _oData) {
        super("ArticlePageForm", PAGE_MAIN);
        
        if(!m_bIsLoaded) {
            //TODO: make error dialog.
            System.err.println("We failed to load");
            return;
        }
        m_oData = _oData;
        m_sTitle = _sTitle;
        try {
            //Create dynamic components here.
            Label cTitleLabel = (Label)mainMIDlet.getBuilder().findByName("SubjectTitleLabel", m_cHeaderContainer);
            if(cTitleLabel != null) {
                cTitleLabel.setText(_sTitle);
            }
            
            m_cForm.addShowListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    m_cForm.removeShowListener(this);
                    addData(m_oData);
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
        if(true){
            return;
        }
        m_cForm.removeAllCommands();
        String  str = "";
        str = mainMIDlet.getString("ExitSK");
        m_cForm.addCommand(new Command(str, COMMAND_BACK), i++);
        str = mainMIDlet.getString("SearchSK");
        m_cForm.addCommand(new Command(str, COMMAND_SEARCH), i++);
        str = mainMIDlet.getString("SavePageSK");
        m_cForm.addCommand(new Command(str, COMMAND_SAVEPAGE), i++);
        str = mainMIDlet.getString("BookMarkSK");
        m_cForm.addCommand(new Command(str, COMMAND_BOOKMARK), i++);
        //str = mainMIDlet.getString("PrivacySK");
        //mForm.addCommand(new Command(str, Command_Privacy), Command_Privacy);
        str = mainMIDlet.getString("HomeSK");
        m_cForm.addCommand(new Command(str, COMMAND_HOME), i++);
        
    }//end updateSoftkeys()
    
    public void actionPerformed(ActionEvent ae) {
        System.err.println("Action article: " + ae.getCommand().getId());
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
            case COMMAND_SEARCH:
                    mainMIDlet.setCurrentPage(new SearchPage());
                break;
            case COMMAND_SAVEPAGE:
                    //mainMIDlet.setCurrentPage(new SearchPage());
                break;
            case COMMAND_BOOKMARK:
                //mainMIDlet.setCurrentPage(new WebViewDialog("TermsUrl"));
                break;
            case COMMAND_HOME:
                    mainMIDlet.setCurrentPage(new MainPage(), true);
                break;
            default://dealing with the dynamic events
                {
                }
                break;
        }
    } //end actionPerformed(ActionEvent ae)
    
    public void refreshPage() {        
        m_cForm.addShowListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                //Putting the refresh in a show listener to make sure the page is ready to refresh.
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
        if(_results == null) {
            //We have nothing, make the data call.
            //NetworkController.getInstance().performSearch(m_sTitle, null);
        }
        if(m_cContentContainer != null)
        {
            m_cContentContainer.removeAll();
            TextArea loremIpsum = new TextArea();
            loremIpsum.setText("Lorem ipsum dolor sit amet, consectetuer adipiscing"
                    + " elit, sed diam nonummy nibh euismod tincidunt ut laoreet "
                    + "dolore magna aliquam erat volutpat. Ut wisi enim ad minim "
                    + "veniam, quis nostrud exerci tation ullamcorper suscipit"
                    + " lobortis nisl ut aliquip ex ea commodo consequat. Duis "
                    + "autem vel eum iriure dolor in hendrerit in vulputate velit"
                    + " esse molestie consequat, vel illum dolore eu feugiat nulla "
                    + "facilisis at vero eros et accumsan et iusto odio dignissim "
                    + "qui blandit praesent luptatum zzril delenit augue duis "
                    + "dolore te feugait nulla facilisi. Nam liber tempor cum "
                    + "soluta nobis eleifend option congue nihil imperdiet doming"
                    + " id quod mazim placerat facer possim assum.");
            loremIpsum.setUIID("No_Margins");
            loremIpsum.setEditable(false);
            loremIpsum.setFocus(true);
            m_cContentContainer.addComponent(loremIpsum);
            Container cont = mainMIDlet.getBuilder().createContainer(mainMIDlet.getResources(), "SubCategoryItem");
            if(cont != null) {
                Label title = (Label)mainMIDlet.getBuilder().findByName("SubCategoryTitleLabel", cont);
                title.setText("Lorem Ipsum");
                m_cContentContainer.addComponent(cont);
            }
        }
        m_cForm.repaint();
    }//end addData(Object _results)
    
    
}
