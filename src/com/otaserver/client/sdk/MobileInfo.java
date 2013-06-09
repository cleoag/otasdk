package com.otaserver.client.sdk;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MobileInfo {
	private static final String Tag = "MobileInfo" ;
	
	private static final String OTA_firmware_3 = "firmware/3.1/updateservlet?" ;
	private static final String propertiesValue_lenovomm_serverAddress = "http://fus.lenovomm.com/" ;

	private String firmwareType = null ;
	private String modemVersion = null ;
	private String imeiNumber   = null ;
	private String localLanguage  = null ;
	private String Pid = null ;
	private String deviceModel  = null ;
	private Context context = null ;
    public MobileInfo(Context context){
    	this.context = context ;
    	getMoblieInfo() ;
    }
    
    public MobileInfo(Context context,String firmwareVersion, String imei,
			String Language,String deviceModel){
    	this.context = context ;
    	firmwareType = firmwareVersion ;
    	imeiNumber = imei ;
    	localLanguage = Language ;
    	this.deviceModel = deviceModel ;
    	getMoblieInfo() ;
    }
    
    //get SDcard Root directory
    public boolean SDcardStatus(){
    	  boolean sdCardExist = Environment.getExternalStorageState()  
    	                      .equals ("mounted");   //判断sd卡是否存在
    	  return sdCardExist;
    }
    //Server   
	public String getServerUrl() {
		if (SDcardStatus()) {
			String filepath = Constants_OtaSdk.PropertiesFilePath + Constants_OtaSdk.PropertiesFileName;
			String devserverAddress = RWProperties.readValue(filepath,
					Constants_OtaSdk.PropertiesKey_serverAddress);
			if (devserverAddress != null && !devserverAddress.equalsIgnoreCase("null")) {
				return devserverAddress + OTA_firmware_3;
			}

		}

		return propertiesValue_lenovomm_serverAddress + OTA_firmware_3;
	}
	
	//FirmwareType
	public void getFirmwareVersion() {
		if (SDcardStatus()) {
			String filepath = Constants_OtaSdk.PropertiesFilePath + Constants_OtaSdk.PropertiesFileName;
			String Properties_FType = RWProperties.readValue(filepath,
					Constants_OtaSdk.PropertiesKey_FirmwareType);
			if(null!=Properties_FType && !Properties_FType.equalsIgnoreCase("null"))
				firmwareType = Properties_FType ;
		}
		if (firmwareType == null) {
			firmwareType = android.os.Build.VERSION.INCREMENTAL;
		}
	}
	
	//LocalLanguage
	public void getLanguage(){
		if (SDcardStatus()) {
			String filepath = Constants_OtaSdk.PropertiesFilePath + Constants_OtaSdk.PropertiesFileName;
			String Properties_Locale = RWProperties.readValue(filepath,
					Constants_OtaSdk.PropertiesKey_LocaleLanguage);
			if(null != Properties_Locale && !Properties_Locale.equalsIgnoreCase("null"))
				localLanguage = Properties_Locale ;
		}
		if (localLanguage == null) {
			localLanguage  = context.getResources().getConfiguration().locale.getLanguage() ; 
		}
	}
				
	//DeviceModel
	public void getModel(){
		if (SDcardStatus()) {
			String filepath = Constants_OtaSdk.PropertiesFilePath + Constants_OtaSdk.PropertiesFileName;
			String properties_DeviceModel = RWProperties.readValue(filepath,
					Constants_OtaSdk.PropertiesKey_DeviceModel);
			if(properties_DeviceModel!=null && !properties_DeviceModel.equalsIgnoreCase("null"))
				deviceModel = properties_DeviceModel ;
		}
		if (deviceModel == null) {
			deviceModel  = android.os.Build.MODEL ;
		}
	}
	
	//IMEI
	public void getImei(){
		if (SDcardStatus()) {
			String filepath = Constants_OtaSdk.PropertiesFilePath + Constants_OtaSdk.PropertiesFileName;
			String properties_Imei = RWProperties.readValue(filepath,
					Constants_OtaSdk.PropertiesKey_IMEI);
			if(properties_Imei!=null && !properties_Imei.equalsIgnoreCase("null"))
				imeiNumber = properties_Imei ;
		}
		
		if (imeiNumber == null) {
			TelephonyManager tm = (TelephonyManager) context .getSystemService(Context.TELEPHONY_SERVICE);
			imeiNumber = tm.getDeviceId() ;
			//如果IMEI为null，那么获得SN号
			if(imeiNumber==null){
				imeiNumber = android.os.Build.SERIAL ;
				
				//如果IMEI和SN都为null，那么获得MAC地址  
				if(imeiNumber==null){
					WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE); 
					  
					WifiInfo info = wifi.getConnectionInfo(); 
					
					imeiNumber = info.getMacAddress(); 

				}
			}
		}

			
	}

    
    private void getMoblieInfo(){

    	getFirmwareVersion() ;
   
    	getLanguage() ;
	
    	getModel() ;

    	getImei() ;
    	
    		
    	Pid = PidShareHandler.getPid(context) ;

    	Log.d(Tag, "firmwareType = " + firmwareType + "modemVersion = " + modemVersion 
    			+ "imeiNumber = " + imeiNumber + "localLanguage = " + localLanguage 
    			+ "deviceModel = " + deviceModel
    			+"pid=" + Pid) ;
    }
    		
    
    public String getPostStr(){
    	//临时代码
    	deviceModel = deviceModel.replace(" ", "") ;
    	
    	StringBuffer postStrBuffer = new StringBuffer() ;
    	postStrBuffer.append("action=querynewfirmware&deviceid=")
    	  	   .append(EncodeURL(imeiNumber))
    	  	   .append("&curfirmwarever=")
    	  	   .append(EncodeURL(firmwareType))
    	  	   .append("&devicemodel=")
    	  	   .append(EncodeURL(deviceModel))
    	  	   .append("&locale=")
    	  	   .append(EncodeURL(localLanguage))
    	  	   .append("&pid=")
    	  	   .append(EncodeURL(Pid)) ;
    	Log.d(Tag, "PostStr = " + postStrBuffer.toString()) ;
    	return postStrBuffer.toString();
    	
    }
    
	
	/**
	 * 编码URL
	 * @param parameter  url参数value值
	 * @return
	 */
	private String EncodeURL(String parameter){
		String mEncodeparameter = "" ;
		try {
			mEncodeparameter = URLEncoder.encode(parameter, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d(Tag, "send report url = " + mEncodeparameter);
		return mEncodeparameter ;
	}
	

	public String getFirmwareType() {
		return firmwareType;
	}



	public String getModemVersion() {
		return modemVersion;
	}



	public String getImeiNumber() {
		return imeiNumber;
	}



	public String getLocLanguage() {
		return localLanguage;
	}



	public String getDeviceModel() {
		return deviceModel;
	}

	public String getPid() {
		return Pid;
	}
	

}
    
    




