/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pages;

import com.mainMIDlet;

import com.sun.lwuit.*;
import com.sun.lwuit.events.*;

import java.util.Timer;
import java.util.TimerTask;
/**
 *
 * @author caxthelm
 */
public class SplashPage extends BasePage
{
    boolean m_bHasNetwork = true;
    Timer m_tiSplashTimer;
    private class SetPageTimerTask extends TimerTask {
        public SetPageTimerTask() {
        }//send SetPageTimerTask()
        public void run() {
            mainMIDlet.setCurrentPage(new MainPage(), true);
        }//end run()
    }
    
    public SplashPage(boolean _bHasNetwork) {
        super("SplashPageForm", PAGE_SPLASH);
        try {
            if(!m_bIsLoaded) {
                //TODO: make error dialog.
                System.err.println("We failed to load");
                return;
            }
            m_bHasNetwork = _bHasNetwork;
            if(!m_bHasNetwork) {
                m_cForm.addShowListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ev) {
                        m_cForm.removeShowListener(this);
                        String netErrorTitle = mainMIDlet.getString("NetErrorTitle");
                        Dialog dialog = new Dialog(netErrorTitle);
                        if(dialog != null) {                    
                            String netErrorText = mainMIDlet.getString("NetErrorText");
                            TextArea text = new TextArea(netErrorText);
                            text.setUIID("LabelClear");
                            text.setEditable(false);
                            dialog.addComponent(text);

                            String OkSk = mainMIDlet.getString("ok");
                            Command commands = new Command(OkSk);
                            dialog.addCommand(commands);
                            dialog.show(10, 10, 10, 10, false);
                        }
                        mainMIDlet.getMIDlet().notifyDestroyed();
                    }
                });
            }
            m_cForm.show();
            
            if(m_bHasNetwork) {
                m_tiSplashTimer = new Timer();
                m_tiSplashTimer.schedule(new SetPageTimerTask(), 1000);
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }//end SplashPage(boolean _bHasNetwork)
    
    public void actionPerformed(ActionEvent ae) {
      //mainMIDlet.getMIDlet().notifyDestroyed();
        System.err.println("Action Splash: " + ae.getCommand().getCommandName());
    }//end actionPerformed(ActionEvent ae)
}
