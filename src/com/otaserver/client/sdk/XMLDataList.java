package com.otaserver.client.sdk;

import java.util.ArrayList;
import java.util.List;

public class XMLDataList {
	private static XMLDataList mXMLDataList = null ;
	private List<ParserXMLData> XMLInfos  = null ;
	private XMLDataList(){
		XMLInfos = new ArrayList<ParserXMLData>() ;
	}
		
	public static XMLDataList getInstance(){
		if(mXMLDataList==null){
			mXMLDataList = new XMLDataList() ;
		}
		return mXMLDataList ;
	}

	public List<ParserXMLData> getXMLInfos() {
		return XMLInfos;
	}

	public void setXMLInfos(List<ParserXMLData> xMLInfos) {
		XMLInfos = xMLInfos;
	}
	
	public void deleteList(){
		XMLInfos.clear() ;
	}

}
