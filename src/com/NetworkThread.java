/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXParseException;
/**
 *
 * @author caxthelm
 */
public class NetworkThread implements Runnable {
    
    Timer m_oTimeoutTimer;
    NetTimeoutTask m_oTimeoutTask;
    
    public boolean m_bIsNetDone = false;
    private int m_iResponseCode = 0;
    
    private String m_sURI;
    private String m_sPostData = "";
    private String m_sMethod;
    private int m_iParseStyle;
    private SAXParser m_oSaxParser = null;
    private JsonObject m_oResponseJSON = null;
    
    private class NetTimeoutTask extends TimerTask {
        public NetTimeoutTask() {
        }
        public void run() {            
            if(!m_bIsNetDone && mainMIDlet.getCurrentPage() != null) {
                mainMIDlet.getCurrentPage().failedNetwork(-1);
            }
            m_bIsNetDone = true;
            m_oTimeoutTimer.cancel();
        }
    }
    public static String LOGIN_NAME = "";
    public static String LOGIN_PASS = "";
    
    
    public NetworkThread(String _sURI, String _sPostData, String _sMethod, int _iParseStyle) {
        super();
        m_sURI = _sURI;
        m_sPostData = _sPostData;
        m_sMethod = _sMethod;
        m_iParseStyle = _iParseStyle;
        
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            m_oSaxParser = factory.newSAXParser();
        } catch(Exception e) {
        }
    }//end NetworkThread(String _sURI, String _sPostData, String _sMethod, int _iParseStyle)
    
    public void run() {
        System.out.println("req: " + m_sURI);
        //System.out.println("!@#$% Net Mem start: "+Runtime.getRuntime().freeMemory());
        
        m_oTimeoutTask = new NetTimeoutTask();
        m_oTimeoutTimer = new Timer();
        m_oTimeoutTimer.schedule(m_oTimeoutTask, 45000);
        m_bIsNetDone = false;
        
        if(!m_bIsNetDone) {
            m_oResponseJSON = doConnection(m_sURI, m_sPostData, m_sMethod);
        
        }    
        if(m_bIsNetDone) {
            m_oTimeoutTimer.cancel();
            NetworkController.hideLoadingDialog();
            //System.out.println("!@#$% Net Mem finish1: "+Runtime.getRuntime().freeMemory());
            return;
        }
        NetworkController.hideLoadingDialog();
        if(m_oResponseJSON != null){
            if(mainMIDlet.getCurrentPage() != null) {
                mainMIDlet.getCurrentPage().addData(m_oResponseJSON);
            }
        }else {
            if(mainMIDlet.getCurrentPage() != null) {
                mainMIDlet.getCurrentPage().failedNetwork(m_iResponseCode);
            }
        }
        m_oTimeoutTimer.cancel();
        cleanResponse();
        m_bIsNetDone = true;
        //System.out.println("!@#$% Net Mem finish3: "+Runtime.getRuntime().freeMemory());        
    }//end run()
    
    public JsonObject doConnection(String _sURL, String _sPostData, String _sMethod) {
        //Wireshark: ip.src == 91.211.72.162  || ip.dst == 91.211.72.162
        //Gumtree: ip.src == 195.78.85.210 || ip.dst == 195.78.85.210
        OutputStream output = null;
        InputStream input = null;
        HttpConnection connector = null;
        long currTime = System.currentTimeMillis();
        long endTime = 0;
        try {
            try {
                connector = (HttpConnection) Connector.open(_sURL);
            }catch (Exception ex) {
                //System.err.print(ex.toString());
                ex.printStackTrace();
            }
            if(connector == null) {
                System.out.println("connector is null");
                return null;
            }
            connector.setRequestMethod(_sMethod);
            
            output = connector.openOutputStream();
            if(_sMethod.equalsIgnoreCase(HttpConnection.POST)) {
                output.write(_sPostData.getBytes());
            }
            output.flush();
            //System.out.println("connector: " + uri);
            int iResponseCode = connector.getResponseCode();
            //System.out.println("ResponseCode: " + responseCode);
            m_iResponseCode = iResponseCode;
            if (!(iResponseCode >= HttpConnection.HTTP_OK && iResponseCode < HttpConnection.HTTP_MULT_CHOICE)) {                
                return null;
            }
            if (iResponseCode == 408) {
                //System.err.print("Timeout");
            }
            
            input = connector.openInputStream();
            
            /*xmlHandler handler = new xmlHandler();
            try {
                m_oSaxParser.parse(input, handler);
            } catch(SAXParseException e) {
                e.printStackTrace();
            }*/
            byte[] buffer = new byte[4096];
            int size = input.read(buffer);
            StringBuffer inputStr = new StringBuffer();
            while(size > -1) {
                String s = new String(buffer, "UTF-8");
                inputStr.append(s);
                size = input.read(buffer);
            }
            JsonObject outputJson = (JsonObject)Json.parse(inputStr.toString());
            return outputJson;
            //return handler.getNodes();

        } 
        catch (Exception ex) {
            //showSuccessPopup(ex.getMessage());
            ex.printStackTrace();
        } 
        finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception ex) {
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (Exception ex) {
                }
            }
            if (connector != null) {
                try {
                    connector.close();
                } catch (Exception ex) {
                }
            }
        }//end finally
        endTime = System.currentTimeMillis();
        //System.out.println("fail: "+(endTime - currTime));
        return null;
    }//end doConnection(String uri, String postData, String method)
    
    /****Utility **************************************************************/ 
        
    private void cleanResponse() {
        try {
            if(m_oResponseJSON != null) {
                m_oResponseJSON.cleanChildren();
            }
            System.gc();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }//end cleanResponse()
    
    public boolean isNetworkDone() {
        return m_bIsNetDone;
    }
    public void setNetworkDone() {
        m_bIsNetDone = true;
    }
}
