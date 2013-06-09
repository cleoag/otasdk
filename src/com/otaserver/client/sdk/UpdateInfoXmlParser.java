package com.otaserver.client.sdk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import android.util.Log;


public class UpdateInfoXmlParser extends DefaultHandler{
	private static final String Tag = "UpdateInfoXmlParser" ;
	private List<ParserXMLData> xmldata ;
	private Map<String,String> DescLand = null ;
	private ParserXMLData mParserXMLData = null ;
	private String DescLand_KEY = null ;
	private int currentstate = 0;
	private final int isNum = 1 ;
	private final int isName = 2 ;
	private final int isObject_to_name = 3 ;
	private final int isDesc = 4 ;
	private final int isMd5 = 7 ;
	private final int isSize = 8 ;
	private final int isLevel = 9 ;
	private final int isNeedbackup = 10 ;
	private final int isDownloadurl = 11 ;

	

	protected UpdateInfoXmlParser(List<ParserXMLData> xmldata){
		super() ;
		//xmldata = new ArrayList<ParserXMLData>() ;
		this.xmldata = xmldata ;
	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
		Log.d(Tag, "startDocument") ;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		String ElenmentName = localName.trim() ;
		if(ElenmentName.length()>5){
			if(ElenmentName.substring(0, 5).equals(Constants_OtaSdk.XML_desc_)){
				Log.d(Tag, "desc is" + localName ) ;
				ElenmentName = localName.substring(0, 5) ;
				DescLand_KEY = localName.substring(5);
			}
			
		}
		if (ElenmentName.equals(Constants_OtaSdk.XML_firmwareupdate)){
			currentstate = 0 ;
			return ;
		}else if (ElenmentName.equals(Constants_OtaSdk.XML_firmware)){
			mParserXMLData = new ParserXMLData() ;
			DescLand = new HashMap<String,String>() ;
			return ;
		}else if (ElenmentName.equals(Constants_OtaSdk.XML_num)){
			currentstate = isNum ;
			return ;
		}else if (ElenmentName.equals(Constants_OtaSdk.XML_name)){
			currentstate = isName ;
			return ;
			
		}else if (ElenmentName.equals(Constants_OtaSdk.XML_object_to_name)){
			currentstate = isObject_to_name ;
			return ;
		}else if (ElenmentName.equals(Constants_OtaSdk.XML_desc_)){
			currentstate = isDesc ;
			return ;
		
		}else if (ElenmentName.equals(Constants_OtaSdk.XML_md5)){
			currentstate = isMd5 ;
			return ;
		}else if (ElenmentName.equals(Constants_OtaSdk.XML_size)){
			currentstate = isSize ;
			return ;
		}else if (ElenmentName.equals(Constants_OtaSdk.XML_level)){
			currentstate = isLevel ;
			return ;
		}else if (ElenmentName.equals(Constants_OtaSdk.XML_needbackup)){
			currentstate = isNeedbackup ;
			return ;
		}else if (ElenmentName.equals(Constants_OtaSdk.XML_downloadurl)){
			currentstate = isDownloadurl ;
			return ;
		}
		
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
	throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);
		String theString = new String(ch, start, length);
		switch(currentstate){
		case isNum :
			currentstate = 0 ;
			mParserXMLData.setNum(theString) ;
			break ;
		case isName :
			currentstate = 0 ;
			mParserXMLData.setName(theString) ;
			break ;
		case isObject_to_name :
			currentstate = 0 ;
			mParserXMLData.setObject_to_name(theString) ;
			break ;
		case isDesc :
			currentstate = 0 ;
			DescLand.put(DescLand_KEY, theString) ;
			break ;
		case isMd5 :
			currentstate = 0 ;
			mParserXMLData.setMd5(theString) ;
			break ;
		case isSize :
			currentstate = 0 ;
			mParserXMLData.setSize(theString) ;
			break ;
		case isLevel :
			currentstate = 0 ;
			mParserXMLData.setLevel(theString) ;
			break ;
		case isNeedbackup :
			currentstate = 0 ;
			mParserXMLData.setNeedbackup(theString) ;
			break ;
		case isDownloadurl :
			currentstate = 0 ;
			mParserXMLData.setDownloadurl(theString) ;
			break ;
		}
	}
	

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(uri, localName, qName);
		if(localName.equals(Constants_OtaSdk.XML_firmware)){
			mParserXMLData.setDescLand(DescLand) ;
			xmldata.add(mParserXMLData) ;
		}
	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		//super.endDocument();
		Log.d(Tag, "endDocument") ;
		
	}
	
	@Override
	public void error(SAXParseException e) throws SAXException {
		// TODO Auto-generated method stub
		super.error(e);
	}


}
