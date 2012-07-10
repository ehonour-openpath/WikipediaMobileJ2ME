/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import java.util.Vector;
/**
 *
 * @author caxthelm
 */
public class Utilities {
    
    public static String replace(String _sNeedle, String _sReplacement, String _sHaystack) {
        String result = "";
        int index = _sHaystack.indexOf(_sNeedle);
        if(index == 0) {
            result = _sReplacement+_sHaystack.substring(_sNeedle.length());
            return replace(_sNeedle, _sReplacement, result);
        }else if(index > 0) {
            result = _sHaystack.substring(0,index)+ _sReplacement +_sHaystack.substring(index+_sNeedle.length());
            return replace(_sNeedle, _sReplacement, result);
        }else {
            return _sHaystack;
        }
    }//end replace(String needle, String replacement, String haystack)
    
    public static String deletePart(String _sNeedle, String _sHaystack) {
        String result = "";
        int index = _sHaystack.indexOf(_sNeedle);
        while(index > 0) {
            String temp = _sHaystack.substring(0, index);
            temp += _sHaystack.substring(index + _sNeedle.length());
            _sHaystack = temp;
            index = _sHaystack.indexOf(_sNeedle);
        }
        return _sHaystack;
    }//end replace(String needle, String replacement, String haystack)
    
    public static String stripSlash(String _sHaystack) {
        String result = "";
        int index = _sHaystack.indexOf('\\');
        while(index > 0 && index + 1 < _sHaystack.length()) {
            String temp = _sHaystack.substring(0, index);
            switch((char)_sHaystack.charAt(index+1)) {
                case '\"':
                    temp += '\"';
                    break;
                case '\'':
                    temp += '\'';
                    break;
                case 'n':
                    temp += '\n';
                    break;
                case 't':
                    temp += '\t';
                    break;
                case 'r':
                    temp += '\r';
                    break;
                case '\\':
                    temp += '\\';
                    break;
                case '/':
                    temp += '/';
                    break;
            }
            temp += _sHaystack.substring(index + 2);
            _sHaystack = temp;
            index = _sHaystack.indexOf('\\');
        }
        return _sHaystack;
    }//end replace(String needle, String replacement, String haystack)
    
    public static Vector getSectionsFromJSON(JsonObject _oJson) {
        Vector vReturnVec = null;
        if(_oJson == null)
            return null;
        
        Object oMobileView = _oJson.get("mobileview");
        if(oMobileView != null && oMobileView instanceof JsonObject) {
            Object oSections = ((JsonObject)oMobileView).get("sections");
            if(oSections != null && oSections instanceof Vector) {
                vReturnVec = (Vector)oSections;
            }
        }
        return vReturnVec;
    }
    
    public static String getNormalizedTitleFromJSON(JsonObject _oJson) {
        return ((JsonObject)((JsonObject)_oJson).get("mobileview")).getString("normalizedtitle");
    }
}
