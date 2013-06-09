package com.otaserver.client.sdk;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import android.util.Log;

public class RWProperties {
	private static final String Tag = "ReadProperties" ;
	
	/**
	 * Read properties by the key
	 * @param filePath  文件路径
	 * @param key  映射文件key的值
	 * @return  返回对应key所得到的value
	 */
	public static String readValue(String filePath,String key){
		Properties props = new Properties();
	    try {
		InputStream ips = new BufferedInputStream(new FileInputStream(filePath));
		props.load(ips);
		String value = props.getProperty(key);
		Log.d(Tag, "value="+value) ;
		return value;
	   } catch (FileNotFoundException e) {
		e.printStackTrace();
	   } catch (IOException e) {
		e.printStackTrace();
	   }
	   return null;
	}
	
	/**
	 * Write properties for lenovomm server address
	 * @param filepath  映射文件的路径
	 * @param PropertiesKey  映射文件内容中key
	 * @param PropertiesValue  映射文件内容中value
	 */
	public static void writeProperties(String filepath,String PropertiesKey,String PropertiesValue){
		Properties props = new Properties();
		try{
			OutputStream ops= new FileOutputStream(filepath) ;
			props.setProperty(PropertiesKey, PropertiesValue);
			props.store(ops, "set");
		}catch(IOException e){ 
			e.printStackTrace() ;
		}
	}
	
}