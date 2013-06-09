package com.otaserver.client.sdk;

public class Constants_OtaSdk {
	public static final String http_socket_timeout= "http.socket.timeout" ;
	public static final String http_connection_timeout= "http.connection.timeout" ;
	public static final String http_connection_manager_timeout= "http.connection-manager.timeout" ;
	public static final String Parser_xml_NoNode ="No this node" ;
	public static final String Parser_xml_NoChild = "no child node" ;
	public static final String Parser_xml_Error = "parse error" ;

	//XML Elements name
	public static final String XML_firmwareupdate = "firmwareupdate" ;
	public static final String XML_firmware = "firmware" ;
	public static final String XML_num = "num" ;
	public static final String XML_name = "name" ;
	public static final String XML_object_to_name = "object_to_name" ;
	public static final String XML_desc_ = "desc_" ;
	public static final String XML_md5 = "md5" ;
	public static final String XML_size = "size" ;
	public static final String XML_level = "level" ;
	public static final String XML_needbackup = "needbackup" ;
	public static final String XML_downloadurl = "downloadurl" ;
	public static final String XML_Error = "Error" ;
	public static final String XML_Error_Code = "Code" ;
	public static final String XML_Error_Message = "Message" ;
	
	//OnlineFileUpdate path

	public static final String UpdateZipPackageFileName = "update.zip" ;
	public static final String UpdateInfoFileName = "updinfo.xml" ;
	
	
	//SDcardFileUpdate path
	public static final String SDFilePath = "/sdcard/" ;

	
	//properties file
	public static final String PropertiesFilePath = "/sdcard/" ;
	public static final String PropertiesFileName = "OtaUpdateInfo.properties" ;
	public static final String PropertiesKey_serverAddress = "com.lenovo.ota.address" ;
	public static final String PropertiesKey_IMEI = "com.lenovo.ota.imei" ;
	public static final String PropertiesKey_DeviceModel  = "com.lenovo.ota.devicemodel" ;
	public static final String PropertiesKey_FirmwareType  = "com.lenovo.ota.firmwaretype" ;
	public static final String PropertiesKey_LocaleLanguage  = "com.lenovo.ota.localelanguage" ;
	
	
}
