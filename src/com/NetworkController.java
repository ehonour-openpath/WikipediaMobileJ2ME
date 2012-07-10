package com;
import com.sun.lwuit.*;
import com.sun.lwuit.Display;


import javax.microedition.io.HttpConnection;

/**
 * @author caxthelm
 */ 
public class NetworkController {
    
    public static String BASE_URL = "http://en.wikipedia.org";
    public static String WEBAPI = "/w/api.php";
    public static String APIVERSION = "";
    
    static String m_sResponseString = "";
    private static NetworkController m_oInstance = null;
    private static Dialog m_cLoadingDialog = null;
    private static NetworkThread m_tNetThread = null;
    
    public static final int PARSE_SEARCH = 0;
    
    
    Label labelAttempt;

    private NetworkController() {
    }

    public static NetworkController getInstance() {
        if (m_oInstance == null) {
            m_oInstance = new NetworkController();
        }
        return m_oInstance;
    }//end getInstance()

    /****Loading Methods ******************************************************/
    public static void showLoadingDialog() {
//        mainMIDlet.getCurrentPage().showLoadingDialog(false);
        if (m_cLoadingDialog == null) {
            //System.out.println("showing loading");
            m_cLoadingDialog = (Dialog)mainMIDlet.getBuilder().createContainer(mainMIDlet.getResources(), "LoadingDialog");
        }
        try
        {
            if(m_cLoadingDialog != null && !m_cLoadingDialog.isVisible()) {
                /*if(mainMIDlet.phoneType == mainMIDlet.TYPE_S60) {
                    m_cLoadingDialog.show(195, 196, 55, 56, false, false);
                }else*/
                    m_cLoadingDialog.showModeless();
                Thread.yield();
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        //System.out.println("finished showing loading");
    }//end showLoadingDialog()
    
    public static void hideLoadingDialog() {
        if(m_cLoadingDialog != null && m_cLoadingDialog.isVisible()) {
            //System.out.println("disposing loading");
            m_cLoadingDialog.dispose();
            m_cLoadingDialog = null;
            Thread.yield();
        }
        //System.out.println("finished disposing loading");
    }//end hideLoadingDialog()

    /****Search ************************************************/
      
    public void performSearch(String _sURL) {
        NetworkController.showLoadingDialog();
        //System.out.println("search Url: "+url.toString());
        networkNexus(_sURL, "", HttpConnection.GET, PARSE_SEARCH);
        //return results;
    }//end performSearch(String url)
    
    public void performSearch(String _sSearchTerm, String _sSection) {
        //?action=mobileview&format=json&page=purple&sections=0&prop=text%7Csections
        /*if (_sSearchTerm != null && _sSearchTerm.length() == 0) {
            mainMIDlet.previousSearchQuery = _sSearchTerm;
        }else {
            mainMIDlet.previousSearchQuery = "";
        }*/
        StringBuffer url = new StringBuffer();
        url.append(WEBAPI +"?");
        
        //adding common items: action, props, format
        url.append("action=mobileview");
        url.append("&");
        url.append("format=json");
        url.append("&");
        url.append("prop=text%7Csections%7Cnormalizedtitle");
        url.append("&");
        url.append("sectionprop=toclevel%7Cline%7Cnumber");

        //http://rest.annunci.ebay.it/columbus-api/ads?categoryId=16842752&locationId=-1&q=rft&page=0&size=25&extension[histogram]=category&_ver=1.10
        if(_sSection == null || _sSection.length() == 0) {
            _sSection = "0";
        }
        if(!_sSection.equalsIgnoreCase("-1")) {
            url.append("&");
            url.append("sections=");
            url.append(_sSection);
        }
        
        
        String sKeyword = _sSearchTerm.trim();
        if (sKeyword != null && sKeyword.length() > 0)
        {
            url.append("&");
            sKeyword = sKeyword.replace(' ', '+');
            url.append("page=");           
            // We append an underscore to the end of the keyword to ensure
            // that we get back a normalized title.
            url.append(HtmlEncode(sKeyword) + "_");
            //url.append("");
        }
        
        url.append("&noheadings=");
        
        performSearch(BASE_URL+url.toString());
    }//end performSearch
    
    /****Network Methods ******************************************************/
    
    public void networkNexus(String _sURL, String _sPostData, String _sMethod, int _iParseType) {
        //System.out.println("!@#$% start Mem1: "+Runtime.getRuntime().freeMemory());
        m_tNetThread = new NetworkThread(_sURL, _sPostData, _sMethod, _iParseType);
        Display.getInstance().callSerially(m_tNetThread);            
        //System.out.println("out: "+response);        
    }//end networkNexus(String uri, String postData, String method, int parseType)
    
    public static void cancelNetwork() {
        hideLoadingDialog();
        if(m_tNetThread != null && !m_tNetThread.isNetworkDone()) {
            m_tNetThread.setNetworkDone();
        }
        m_tNetThread = null;
    }//end cancelNetwork()

    
    public static String HtmlEncode(String _sInput) {
         return com.sun.lwuit.html.HTMLUtils.encodeString(_sInput);
    }//end HtmlEncode(String input)
    

    public static String htmlReplace(String _sNeedle, String _sHaystack) {
        int index = _sHaystack.indexOf(_sNeedle);
        if(index >= 0) {
            String replacement = HtmlEncode(_sNeedle);        
            return Utilities.replace(_sNeedle, replacement, _sHaystack);
        }
        return _sHaystack;
    }//end htmlReplace(String needle, String haystack)
}
