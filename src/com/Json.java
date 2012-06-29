/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import java.util.Vector;
import java.util.Hashtable;

/**
 *
 * @author caxthelm
 */
public class Json {
    
    private final int JsonTokenNone = 0;
    private final int JsonTokenCurlyOpen = 1;
    private final int JsonTokenCurlyClose = 2;
    private final int JsonTokenSquaredOpen = 3;
    private final int JsonTokenSquaredClose = 4;
    private final int JsonTokenColon = 5;
    private final int JsonTokenComma = 6;
    private final int JsonTokenString = 7;
    private final int JsonTokenNumber = 8;
    private final int JsonTokenTrue = 9;
    private final int JsonTokenFalse = 10;
    private final int JsonTokenNull = 11;
    
    private String m_sJson = "";
    private int m_iIndex = 0;
    
    private Json(String _sJson) {
        m_sJson = _sJson;
        m_iIndex = 0;
    }
    
    public static Object parse(String _sJson) {

	//Return an empty Object if the JSON data is either null or empty
        if(_sJson != null && _sJson.length() > 0)
	{
            Json self = new Json(_sJson);
            //Parse the first value
            Object value = self.parseValue();
            self = null;
            //Return the parsed value
            return value;
	}else {
            //Return the empty Object
            return new JsonObject();
	}
    }//end parse(String _sJson)
    
    private Object parseValue() {
        //Determine what kind of data we should parse by
	//checking out the upcoming token
	switch(lookAhead()) {
            case JsonTokenString:
                return parseString();
            case JsonTokenNumber:
                return parseNumber();
            case JsonTokenCurlyOpen:
                return parseItem();
            case JsonTokenSquaredOpen:
                return parseArray();
            case JsonTokenTrue:
                nextToken();
                return new Boolean(true);
            case JsonTokenFalse:
                nextToken();
                return new Boolean(false);
            case JsonTokenNull:
                nextToken();
                return new Object();
            case JsonTokenNone:
                break;
	}

	//If there were no tokens, flag the failure and return an empty Object
	return null;
    }//end parseValue(String _sJson, int _iIndex)
    
    private JsonObject parseItem() {
	JsonObject map = new JsonObject();
	int token;

	//Get rid of the whitespace and increment index
	nextToken();

	//Loop through all of the key/value pairs of the object
	boolean done = false;
	while(!done) {
            //Get the upcoming token
            token = lookAhead();

            if(token == JsonTokenNone) {
                return map;
            }else if(token == JsonTokenComma) {
                nextToken();
            }
            else if(token == JsonTokenCurlyClose) {
                nextToken();
                return map;
            } else {
                    //Parse the key/value pair's name
                String name = parseString();

                if(name == null || name.length() <= 0) {
                    return map;
                }

                //Get the next token
                token = nextToken();

                //If the next token is not a colon, flag the failure
                if(token != JsonTokenColon) {
                    return map;
                }

                //Parse the key/value pair's value
                Object value = parseValue();

                if(value == null) {
                    return map;
                }
                map.put(name, value);
            }
	}

	//Return the map successfully
	return map;
    }//end parseObject(String _sJson, int _iIndex)
    
    private Vector parseArray() {
	Vector list = new Vector();

	nextToken();

	boolean done = false;
	while(!done) {
            int token = lookAhead();

            if(token == JsonTokenNone) {
                return list;
            }
            else if(token == JsonTokenComma) {
                nextToken();
            }
            else if(token == JsonTokenSquaredClose) {
                nextToken();
                return list;
            }else {
                Object value = parseValue();

                if(value == null) {
                    done = true;
                }else {
                    list.addElement(value);
                }
            }
	}
	return list;
    }//end parseArray()
    
    private String parseString() {
	eatWhitespace();
        
        m_iIndex++;//We start with the index on the opening ", so add 1 to it
        int endIdx = m_iIndex;
        boolean done = false;
        while(!done){
            if(endIdx == m_sJson.length()) {
                break;
            }
            int idx = m_sJson.indexOf('\"', endIdx);
            if(idx < 0) {
                //We have hit a problem, there was no suitable end quote!
                return "";
            }
            endIdx = idx;
            char atIdx = m_sJson.charAt(endIdx - 1);
            if(atIdx == '\\') {
                endIdx++;//not the right end quote, keep moving forward.
            }else {
                done = true;
            }
        }
        StringBuffer returnStr = new StringBuffer(m_sJson.substring(m_iIndex, endIdx));
        m_iIndex = endIdx + 1;
	return returnStr.toString();
    }//end parseString()
    
    private Integer parseNumber() {
        eatWhitespace();

	int lastIndex = lastIndexOfNumber(m_iIndex);
	int charLength = (lastIndex - m_iIndex) + 1;
	String numberStr;

	numberStr = m_sJson.substring(m_iIndex, m_iIndex + charLength);	
	m_iIndex = lastIndex + 1;
	return Integer.valueOf(numberStr);
    }//end parseNumber()
    
    private int lastIndexOfNumber(int _iIndex) {
        int lastIndex = _iIndex;
        String testString = "0123456789+-.eE";
	for(; lastIndex < m_sJson.length(); lastIndex++) {
            if(testString.indexOf(m_sJson.charAt(lastIndex)) == -1) {
                break;
            }
	}
	return lastIndex - 1;
    }//end lastIndexOfNumber()
    
    private void eatWhitespace() {
        String testString = " \t\n\r";
	for(; m_iIndex < m_sJson.length(); m_iIndex++) {
            if(testString.indexOf(m_sJson.charAt(m_iIndex)) == -1) {
                break;
            }
	}
    }//end eatWhitespace()
    
    private int lookAhead() {
	int saveIndex = m_iIndex;
        int next = nextToken();
	m_iIndex = saveIndex;
	return next;
    }//end lookAhead()
		
    private int nextToken() {
        eatWhitespace();

	if(m_iIndex == m_sJson.length()) {
            return JsonTokenNone;
	}

	char c = m_sJson.charAt(m_iIndex);
	m_iIndex++;
	switch(c) {
            case '{': return JsonTokenCurlyOpen;
            case '}': return JsonTokenCurlyClose;
            case '[': return JsonTokenSquaredOpen;
            case ']': return JsonTokenSquaredClose;
            case ',': return JsonTokenComma;
            case '"': return JsonTokenString;
            case '0': case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
            case '-': return JsonTokenNumber;
            case ':': return JsonTokenColon;
	}

	m_iIndex--;

	int remainingLength = m_sJson.length() - m_iIndex;

	//True
	if(remainingLength >= 4) {
            if (m_sJson.charAt(m_iIndex) == 't' && m_sJson.charAt(m_iIndex + 1) == 'r'
                && m_sJson.charAt(m_iIndex + 2) == 'u' && m_sJson.charAt(m_iIndex + 3) == 'e') {
                m_iIndex += 4;
                return JsonTokenTrue;
            }
	}

	//False
	if (remainingLength >= 5)
	{
            if (m_sJson.charAt(m_iIndex) == 'f' && m_sJson.charAt(m_iIndex + 1) == 'a' 
                && m_sJson.charAt(m_iIndex + 2) == 'l' && m_sJson.charAt(m_iIndex + 3) == 's' 
                && m_sJson.charAt(m_iIndex + 4) == 'e') {
                m_iIndex += 5;
                return JsonTokenFalse;
            }
	}

	//Null
	if (remainingLength >= 4)
	{
            if (m_sJson.charAt(m_iIndex) == 'n' && m_sJson.charAt(m_iIndex + 1) == 'u'
                && m_sJson.charAt(m_iIndex + 2) == 'l' && m_sJson.charAt(m_iIndex + 3) == 'l') {
                m_iIndex += 4;
                return JsonTokenNull;
            }
	}

	return JsonTokenNone;
    }//end nextToken()

    public String stripQuotes(String _sInput) {
        String retStr = _sInput;
        if(retStr != null && retStr.length() > 0) {
            retStr = retStr.trim();
            if(retStr.startsWith("\"")) {
                retStr = retStr.substring(1);
            }
            if(retStr.endsWith("\"")) {
                retStr = retStr.substring(0, retStr.length() - 2);
            }
            retStr = retStr.trim();
        }
        return retStr;
    }//end stripQuotes(String _sInput)

    /*public String reverseParse(Object _sInput) {
    }
    
    private QString reverseParseMap(Hashtable _hInput) {
    }
    
    private QString reverseParseList(Vector _vInput) {
    }*/
}
