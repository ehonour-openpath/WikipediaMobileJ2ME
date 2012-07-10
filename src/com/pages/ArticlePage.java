/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pages;

import com.*;
import com.sun.lwuit.*;
import com.sun.lwuit.events.*;
import com.sun.lwuit.Display;
import com.sun.lwuit.html.DefaultHTMLCallback;
import com.sun.lwuit.html.HTMLComponent;


import java.util.Vector;
import java.util.Stack;
import java.util.Hashtable;

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
    
    private Vector m_vArticleStack;
    
    private Label cTitleLabel;
    
    //Lwuit Commands:   
    JsonObject m_oData = null;
    String m_sTitle = "";
    String m_sCurrentSections = "0";
    TextField searchTextField = null;
    
    Hashtable m_oComponentList = new Hashtable();
    
    private int[] m_iToRequest = new int[6];
    
    public int[] getRequestInts() {
        return m_iToRequest;
    }
    
    public ArticlePage(String _sTitle, JsonObject _oData) {
        super("ArticlePageForm", PAGE_MAIN);
        
        cTitleLabel = (Label)mainMIDlet.getBuilder().findByName("SubjectTitleLabel", m_cHeaderContainer);
        
        m_vArticleStack = new Vector();
        String[] toAdd = new String[2];
        toAdd[0] = _sTitle;
        toAdd[1] = "0";
        m_vArticleStack.addElement(toAdd);
        
        if(!m_bIsLoaded) {
            //TODO: make error dialog.
            System.err.println("We failed to load");
            return;
        }
        m_oData = _oData;
        m_sTitle = _sTitle;
        try {
            //Create dynamic components here.
            
            
            m_cForm.addShowListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    m_cForm.removeShowListener(this);
                    if(m_oData == null) {
                        NetworkController.getInstance().performSearch(m_sTitle,  "0");
                    }else {
                        addData(m_oData);
                    }
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
                    if(m_vArticleStack != null && m_vArticleStack.size() > 0) {
                        String[] titleAndSections = (String[])m_vArticleStack.lastElement();
                        NetworkController.getInstance().performSearch(titleAndSections[0], titleAndSections[1]);
                        m_vArticleStack.removeElementAt(m_vArticleStack.size() - 1);
                    } else {
                        mainMIDlet.setCurrentPage(new MainPage());
                    }
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
                    if(commandId > 40) {
                        String sID = String.valueOf(commandId - 40);
                        Object section = m_oComponentList.get(new Integer(commandId));
                        if(section instanceof SectionComponentItem) {
                            SectionComponentItem sectionItem = (SectionComponentItem)section;
                            if(sectionItem.isActive())
                            {
                                sectionItem.setActive(false);
                            }else {
                                int arrayLevel = Integer.parseInt(sectionItem.getTag()) - 1;
                                m_iToRequest[arrayLevel] = Integer.parseInt(sID);
                                m_sCurrentSections = "0";
                                
                                for(int i = 0; i < arrayLevel + 1; i++) {
                                    m_sCurrentSections += "|" + m_iToRequest[i];
                                }
                                if(m_vArticleStack.size() > 0) {
                                    m_vArticleStack.removeElementAt(m_vArticleStack.size() - 1);
                                }
                                String[] toAdd = new String[2];
                                toAdd[0] = m_sTitle;
                                toAdd[1] = m_sCurrentSections;
                                m_vArticleStack.addElement(toAdd);
                                m_vArticleStack.addElement(section);
                                NetworkController.getInstance().performSearch(m_sTitle,  m_sCurrentSections);
                            }
                        }//end if(section instanceof SectionComponentItem)
                    }//end if(commandId > 40)
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
            String[] toAdd = new String[2];
            toAdd[0] = m_sTitle;
            toAdd[1] = m_sCurrentSections;
            m_vArticleStack.addElement(toAdd);
            NetworkController.getInstance().performSearch(m_sTitle, m_sCurrentSections);
        }
        
        m_sTitle = Utilities.getNormalizedTitleFromJSON((JsonObject)_results);
        System.out.println("   ---   in addData, title is " + m_sTitle);
        if(cTitleLabel != null) {
            String realTitle = m_sTitle.replace('_', ' ');
            cTitleLabel.setText(realTitle);
        }

        Vector sections = Utilities.getSectionsFromJSON((JsonObject)_results);
        Integer highestTocSoFar = new Integer(1);
        if(m_cContentContainer != null && sections != null && sections.size() > 0)
        {
            m_cContentContainer.removeAll();
            //Deal with the main article text first.
           
            Object oTextItem = sections.firstElement();
            if(oTextItem instanceof JsonObject) {
                String sText = (String)((JsonObject)oTextItem).get("text");
                sText = Utilities.stripSlash(sText);
                
                
                
                HTMLComponentItem oHTMLItem = new HTMLComponentItem(sText);
                HTMLComponent cTextComp = (HTMLComponent)oHTMLItem.getComponent();
                if(cTextComp != null) {
                    cTextComp.setPageUIID("Label");
                    cTextComp.setHTMLCallback(new DefaultHTMLCallback()
                    {
                        public boolean linkClicked(HTMLComponent htmlC, java.lang.String url) 
                        {
                            System.out.println("link: "+url);
                            int wikiIdx = url.indexOf("/wiki/");
                            if(wikiIdx >= 0) {
                                String title = url.substring(wikiIdx + 6);
                                NetworkController.getInstance().performSearch(title, "0");
                                //mainMIDlet.setCurrentPage(new ArticlePage(title, null));
                            }
                            return false;
                        }

                    }); 
                    m_cContentContainer.addComponent(cTextComp);
                }
            }//end if(oTextItem instanceof JsonObject)
           
            //Add in the other sections
            //Since we can cascade through sub-sections, we are using an Array to denote which level should get the child.
            //TODO: There must be a better way to do this.
            SectionComponentItem[] aSections = new SectionComponentItem[6];
            for(int i = 1; i < sections.size(); i++) {
                //System.out.println(sections.elementAt(i));
                JsonObject oSection = (JsonObject)sections.elementAt(i);
                String sTitle = (String)oSection.get("line");
                String sText = (String)oSection.get("text");
                boolean bActive = false;
                
                if(sText != null) {
                    bActive = true;
                }
                
                String sTocLevel = oSection.getString("toclevel");
                String sNumber = oSection.getString("number");
                //String sLevel = oSection.getString("level");
                String sID = oSection.getString("id");
                
                int arrayLevel = Integer.parseInt(sTocLevel) - 1;//TocLevels begin at 1;
                if(arrayLevel == 0 || (arrayLevel > 0 && aSections[arrayLevel - 1] != null 
                        && aSections[arrayLevel - 1].isActive())) 
                {
                    SectionComponentItem sectionItem = new SectionComponentItem(sTitle, 40 + i, sTocLevel);
                    Component cSectionComp = sectionItem.createComponent(sTitle, bActive, Integer.parseInt(sTocLevel));
                    if(cSectionComp != null) {
                        m_oComponentList.put(new Integer(40 + i), sectionItem);
                            //Whatever level we are at we shouldn't have any more sub-levels yet.
                        for(int j = arrayLevel; j < 5; j++)
                        {
                            aSections[j] = null;
                        }
                        //set this item into the array and set it to the child of the parent.
                        aSections[arrayLevel] = sectionItem;

                        //System.out.println(sText);
                        
                        if(sText != null && !(sText.length() < 1)) {
                            //TODO: Need to strip out the <h2> tag at the beginning
                            sectionItem.addText(sText);
                        }
                        
                        if(arrayLevel == 0) {
                            m_cContentContainer.addComponent(cSectionComp);
                        }else {
                            aSections[arrayLevel - 1].addSubsection(sectionItem);
                        }
                    }//end if(cSectionComp != null)
                }
            }//end for(int i = 1; i > sections.size(); i++)
        }//end if(m_cContentContainer != null && sections != null && sections.size() > 0)
        m_cForm.repaint();
    }//end addData(Object _results)
    
    
}
