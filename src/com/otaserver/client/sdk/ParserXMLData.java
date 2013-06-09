package com.otaserver.client.sdk;

import java.util.Map;
import android.util.Log;

public class ParserXMLData {
	private static final String Tag = "ParserXMLData" ;
	private String num = null ;
	private String name = null ;
	private String object_to_name = null ;
	private String md5 = null ;
	private String size = null;
	private String level = null ;
	private String needbackup = null ;
	private String downloadurl = null ;
	private Map<String,String> DescLand = null ;
	
	
	//得到各国语言的描述
	public Map<String, String> getDescLand() {
		return DescLand;
	}


	public void setDescLand(Map<String, String> descLand) {
		DescLand = descLand;
	}

    //得到升级包的个数
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		Log.d(Tag, "num = "+ num) ;
		this.num = num;
	}
	
	//版本名，用于通过与“Object_to_name”进行比较来确定是否需要升级
	public String getName() {
		return name;
	}
	public void setName(String name) {
		Log.d(Tag, "name = "+ name) ;
		this.name = name;
	}
	
	public String getObject_to_name() {
		return object_to_name;
	}
	public void setObject_to_name(String object_to_name) {
		Log.d(Tag, "object_to_name = "+ object_to_name) ;
		this.object_to_name = object_to_name;
	}
	

	//用于验证zip包
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		Log.d(Tag, "md5 = "+ md5) ;
		this.md5 = md5;
	}
	
	//包的大小
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		Log.d(Tag, "size = "+ size) ;
		this.size = size;
	}
	
	//暂无用处
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		Log.d(Tag, "level = "+ level) ;
		this.level = level;
	}
	
	//暂无用处
	public String getNeedbackup() {
		return needbackup;
	}
	public void setNeedbackup(String needbackup) {
		Log.d(Tag, "needbackup = "+ needbackup) ;
		this.needbackup = needbackup;
	}
	
	//包的下载地址
	public String getDownloadurl() {
		return downloadurl;
	}
	public void setDownloadurl(String downloadurl) {
		Log.d(Tag, "downloadurl = "+ downloadurl) ;
		this.downloadurl = downloadurl;
	}
	
}
