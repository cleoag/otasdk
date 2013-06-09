package com.otaserver.client.sdk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.SAXParserFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import android.content.Context;
import android.util.Log;

public class CheckUpdate {
	private static final String Tag = "CheckUpdate";
	/**
	 * 该属性表示当前版本已经是最新，无需升级。
	 */
	public static final int VerionIsNew = 530 ;
	
	/**
	 * 该属性表示下载xml升级信息失败，无法得到升级信息导致不能升级。
	 */
	public static final int UpdateInfoDownloadError = 531 ;
	private Context context = null;
	private String firmwareVersion = null;
	private String imei = null ;
	private String locale = null ;
	private String deviceModel = null ;
	private String UpdateInfoPath = null ;
	private String serverAddr = null;
	private int Timeout = 3900;
	private MobileInfo myMobileInfo = null;
	private List<ParserXMLData> XMLInfos = null ;
	private boolean ThreadisRunning = false ;
	
	/**
	 * 查询类的构造函数
	 * @param Context
	 * @param xmlInfoPath   该参数是上层提供的一个文件路径，用于存放从服务端获取的升级信息文件（该文件格式为XML）
	 */
	
	public CheckUpdate(Context context,String xmlInfoPath) {
		this.context = context;
		UpdateInfoPath = xmlInfoPath ;
		getUrlAndFilePath();

	}

	/**
	 * 查询类的构造函数
	 * @param context Context对象
	 * @param firmwareVersion  固件版本号
	 * @param imei  设备IMEI号
	 * @param localLanguage  设备当前语言
	 * @param deviceModel   设备型号（如LenovoP700）
	 * @param xmlInfoPath   该参数是上层提供的一个文件路径，用于存放从服务端获取的升级信息文件（该文件格式为XML）
	 */
	
	public CheckUpdate(Context context, String firmwareVersion, String imei,
			String localLanguage,String deviceModel,String xmlInfoPath) {
		this.context = context;
		this.firmwareVersion = firmwareVersion ;
		this.imei = imei ;
		this.deviceModel = deviceModel ;
		locale = localLanguage ;
		UpdateInfoPath = xmlInfoPath ;
		getUrlAndFilePath();

	}
	

	/**
	 * 该函数用于得到查询服务器地址和下载查询信息XML文件时保存的路径
	 */
	
	private void getUrlAndFilePath() {
		myMobileInfo = new MobileInfo(context,firmwareVersion,imei,locale,deviceModel);
		serverAddr = myMobileInfo.getServerUrl() + myMobileInfo.getPostStr();

	}
	
	/**
	 * 该函数是暴露给上层的，用于开启一个线程执行查询操作，并将查询到的信息通过callback通知给上层
	 * @param mcheckdInter  内部接口对象,用于底层向上层传递升级信息
	 */

	public synchronized void StartCheck(checkUpdateInter mcheckdInter) {
		final checkUpdateInter checkUpdateInter = mcheckdInter;
		if(ThreadisRunning){
			return ;
		}
		File path = new File(UpdateInfoPath) ;
		if(!path.exists())
			path.mkdirs() ;

		Thread CheckThread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				ThreadisRunning = true ;
				if (downloadUpdateFile()) {
					ParserXML(ReadXMLToStr(UpdateInfoPath+Constants_OtaSdk.UpdateInfoFileName));
					ParserXMLData mParserXMLData = null;
					for (Iterator<ParserXMLData> it = XMLInfos.iterator(); it
							.hasNext();) {
						mParserXMLData = (ParserXMLData) it.next();
					}
					// 判断是否存在“firmware”节点.
					if (mParserXMLData != null) {
						Map<String,String> updateMessage = mParserXMLData.getDescLand() ;
						String packageSize = mParserXMLData.getSize() ;
						String MD5 = mParserXMLData.getMd5() ;
						String downloadUrl = mParserXMLData.getDownloadurl() ;
						String targetVersion = mParserXMLData.getObject_to_name() ;
						// 如果是固件升级包则需要判断升级包的版本号是否大于当前手机的版本号.
						if (isFirmwareUpdatePackage(mParserXMLData)) {
							// 如果升级包的版本号大于当前手机的版本号，则升级.
							if (checkUpdateInter
									.checkNewestFirmwarePackageVer(mParserXMLData
											.getName())) {

								checkUpdateInter.NeedUpdate(
										updateMessage , packageSize ,MD5,downloadUrl,targetVersion);
								Log.d(Tag,"有Firmware节点，固件升级包的版本号大于当前手机的版本号，则升级");
							}
							// 如果升级包的版本号小于当前手机的版本号，则不升级.
							else {
								checkUpdateInter.NotUpdate(VerionIsNew);
								Log.d(Tag,
										"有Firmware节点，固件升级包的版本号小于当前手机的版本号，则不升级");
							}

						}
						// 如果不是固件升级包，该包就应该是差分包，不能对比版本号，可直接升级.
						else {
							checkUpdateInter.NeedUpdate(
									updateMessage , packageSize ,MD5,downloadUrl,targetVersion);
							Log.d(Tag, "有Firmware节点，不是固件升级包，是拆分包，则直接升级");
						}
					}
					// 如果没有"firmware"节点，不用升级.
					else {
						checkUpdateInter.NotUpdate(VerionIsNew);
						Log.d(Tag, "没有Firmware节点，则不升级");
					}
				}
				// 下载出现错误时(比如没有联网或是网页不存等)，不需要升级.
				else {
					checkUpdateInter.NotUpdate(UpdateInfoDownloadError);
					Log.d(Tag, "下载xml出现错误，则不升级");
				}
				
				ThreadisRunning = false ;

			}

		});
		CheckThread.start();
	}
	
	/**
	 * 记录查询的时间,用于轮询功能中时间的对比
	 */
	public void recordCheckTime(String TimeRecordFilePath , String TimeRecordFileName) {
		long curTime = System.currentTimeMillis();
		String curTimeStr = String.valueOf(curTime);
		BufferedWriter Writer = null ;
		
		File mPathTimerFile = new File(TimeRecordFilePath) ;
		if(!mPathTimerFile.exists())
			mPathTimerFile.mkdirs() ;
		
		File timeFile = new File(TimeRecordFilePath
				+ TimeRecordFileName);
		
		if (timeFile.exists())
			timeFile.delete();

		try {
			//timeFile.createNewFile();
			Writer = new BufferedWriter(new FileWriter(timeFile)) ;
			Writer.write(curTimeStr,0,curTimeStr.length()) ;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(Writer!=null){
				try {
					Writer.close() ;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				
		}

	}
	

	
	//Check if is firmware update package
	private boolean isFirmwareUpdatePackage(ParserXMLData mParserXMLData){
		String XMLElement_Name = mParserXMLData.getName() ;
		String XMLElement_ObjectToName = mParserXMLData.getObject_to_name() ;
		if(XMLElement_Name.equals(XMLElement_ObjectToName)){
			return true;
		}
		return false ;
	}
			
		

	/**
	 * 该函数通过HttpGet实现了下载XML文件的功能，并且可通过返回值判断是否成功下载
	 * @return 返回一个boolean类型的值，true则表示下载成功，否则下载失败
	 */
	
	private boolean downloadUpdateFile() {
		File updinfopath = new File(UpdateInfoPath+Constants_OtaSdk.UpdateInfoFileName);

		if (!updinfopath.exists()) {
			updinfopath.delete() ;
		}
			
		try {
			HttpClient client = new DefaultHttpClient();
			client.getParams().setParameter(Constants_OtaSdk.http_socket_timeout,
					Timeout);

			client.getParams().setParameter(Constants_OtaSdk.http_connection_timeout,
					Timeout);

			client.getParams().setParameter(
					Constants_OtaSdk.http_connection_manager_timeout,
					Timeout);

			HttpGet get = new HttpGet(serverAddr);
			Log.w(Tag, "httpget in fotacheck " + get.toString());

			HttpResponse response = client.execute(get);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				InputStream inputstream = response.getEntity().getContent();
				File infoFile = new File(UpdateInfoPath+Constants_OtaSdk.UpdateInfoFileName);
				FileOutputStream outputstream = new FileOutputStream(infoFile);
				byte[] buffer = new byte[1024];
				int line = 0;
				while ((line = inputstream.read(buffer)) != -1) {
					outputstream.write(buffer, 0, line);
				}
				inputstream.close();
				outputstream.close();

				return true;
			}
			
		} 
		catch(IllegalStateException e){
			e.printStackTrace() ;
		}
		catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (SocketTimeoutException e) {
			Log.w(Tag, "timeout was catched");

			e.printStackTrace();

		} catch (IllegalArgumentException e) {
			e.printStackTrace();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return false;
	}


	/**
	 * 将携带.xml格式的文件读写成字符串
	 * @param filepath XML的文件路径
	 * @return  返回一个描述了完整XML信息的字符串
	 */
	private String ReadXMLToStr(String filepath) {
		File xmlFile = new File(filepath);
		if (xmlFile.exists()) {
			try {
				InputStreamReader reader = new InputStreamReader(
						new FileInputStream(xmlFile));
				BufferedReader bufferreader = new BufferedReader(reader);
				StringBuffer stringbuffer = new StringBuffer();
				String inputLine = null;
				while ((inputLine = bufferreader.readLine()) != null) {
					stringbuffer.append(inputLine);
				}
				String xmlStr = stringbuffer.toString() ;
				Log.d(Tag, "XML File is" + xmlStr);
				reader.close() ;
				bufferreader.close() ;
				return xmlStr ;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return null;
	}

	/**
	 *  解析下载的xml文件，解析类会将解析后的数据存入List中
	 * @param XMLStr  包含XML信息的字符串
	 */
	
	private void ParserXML(String XMLStr) {
		// 创建SAXParserFactory解析器工厂
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		try {
			// 创建XMLReader对象，xml文件解析器
			XMLReader xmlReader = parserFactory.newSAXParser().getXMLReader();
			// 注册内容事件处理器（设置xml文件解析器的解析方式）
			XMLDataList mXMLDataList = XMLDataList.getInstance() ;
			mXMLDataList.deleteList() ;
			XMLInfos = mXMLDataList.getXMLInfos() ;
			xmlReader.setContentHandler(new UpdateInfoXmlParser(XMLInfos));
			// 开始解析xml格式文件
			xmlReader.parse(new InputSource(new StringReader(XMLStr)));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
	/**
	 * 查询类的内部接口，上层应用实现该接口，用于底层向上层传递升级信息
	 * @author root
	 *
	 */
	
	public interface checkUpdateInter {
		/**
		 * 查询有新版本升级时回调该方法通知上层
		 * @param updateMessage 升级信息的集合，key为语言缩写，如"en、zh、tw等"，value为对应语言的升级信息。
		 * @param packageSize 升级包的大小，单位为byte
		 * @param md5  升级包的MD5值，可用于对比升级包的正确性。
		 * @param downloadUrl  下载升级包的URL
		 * @param targetVer  升级包的目标版本
		 */
		void NeedUpdate(Map<String,String> updateMessage ,String packageSize ,String md5,String downloadUrl,String targetVer);
		
		/**
		 * 查询不需要升级时会回调该方法
		 * @param reason 提供不需要升级的原因。该值为CheckUpdate类中两个属性的其中之一，包括VerionIsNew、UpdateInfoDownloadError。
		 * 其中VerionIsNew表示当前版本已为最新；UpdateInfoDownloadError则代表升级信息下载出错。
		 */
		void NotUpdate(int reason);
		
		/**
		 * 如果当前版本已经高于服务器上的最新版本时，此时去服务器查询仍会返回一个相对于服务端最新的版本，可实际上这个版本低于终端的当前版本，在此情况下升级会导致版本倒退。
		 * 为了避免这个问题，需要对服务端返回的版本与当前版本做一下对比，最好用正则表达式提出版本的日期，再做比较。如果服务器>终端， 返回true则升级；服务器<终端，返回false则不升级
		 * @param targetVer  升级包的目标版本，用这个版本与终端的当前版本作对比
		 * @return 返回true需要升级；返回false则不需要升级
		 */
		boolean checkNewestFirmwarePackageVer(String targetVer) ;
	}

}
