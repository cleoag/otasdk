package com.otaserver.client.sdk;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.content.Context;
import android.util.Log;


public class SendUpdateReport {
	private static final String Tag = "SendUpdateReport" ;
	private Context context = null ;
	
	/**
	 * 发送报告接口类的构造函数
	 * @param context  Context对象
	 */
	public SendUpdateReport(Context context) {
		this.context = context;
	}
		
	
	/**
	 * 发送下载结果报告的函数
	 * @param ErrorCode 数字格式，0代表成功，非0值代表下载过程发生错误.
	 * @param ErrorMsg  下载失败后由程序返回的错误信息描述。最大长度1024。如果下载成功，该参数可为"" .
	 * @param curfirmwarever  终端升级前版本。最大长度128位。
	 * @param targetVer  固件升级的目标版本。
	 * @param imei  imei码或设备序列号。最长支持128位英文字符和数字。
	 * @param deviceModel  设备型号。最大长度20个字符。Android上对应为android.os.Build.MODEL 。例如: 3GW100。
	 */
	public void DownloadReport(String ErrorCode, String ErrorMsg,
			String curfirmwarever, String targetVer, String imei,
			String deviceModel) {
		MobileInfo mobileInfo = new MobileInfo(context);
		String Stage = "download" ;
		String Locale = mobileInfo.getLocLanguage() ;
		String serveraddress = mobileInfo.getServerUrl();
		String post =  getPost(imei,ErrorCode,ErrorMsg,curfirmwarever,curfirmwarever,targetVer,Stage,deviceModel,Locale);
		String url_downloaded = serveraddress + post ;
		Log.d(Tag, "send report url = " + url_downloaded);
		sendReportThread send = new sendReportThread(url_downloaded);
		send.start();
	}
	
	/**
	 * 发送升级结果报告的函数
	 * @param ErrorCode 数字格式，0代表成功，非0值代表升级过程发生错误.
	 * @param ErrorMsg  升级失败后由程序返回的错误信息描述。最大长度1024。如果升级成功，该参数可为"" .
	 * @param curfirmwarever 客户端检测到的固件版本。最大长度128位。
	 * @param updatefrom 固件升级前版本。
	 * @param targetVer 固件升级的目标版本。
	 * @param imei imei码或设备序列号。最长支持128位英文字符和数字。
	 * @param deviceModel 设备型号。最大长度20个字符。Android上对应为android.os.Build.MODEL 。例如: 3GW100。
	 */
	public void UpdateReport(String ErrorCode, String ErrorMsg,
			String curfirmwarever, String updatefrom, String targetVer,String imei,
			String deviceModel){
		MobileInfo mobileInfo = new MobileInfo(context);
		String Stage = "update" ;
		String Locale = mobileInfo.getLocLanguage() ;
		String serveraddress = mobileInfo.getServerUrl();
		String post =  getPost(imei,ErrorCode,ErrorMsg,curfirmwarever,updatefrom,targetVer,Stage,deviceModel,Locale);
		String url_updated = serveraddress + post ;
		Log.d(Tag, "send report url = " + url_updated);
		sendReportThread send = new sendReportThread(url_updated);
		send.start();
	}
	
	
	private String getPost(String IMEI,String ErrorCode,String ErrorMsg,
						   String CurFirmwareVer,String UpdateFrom,String UpdateTo,
						   String Stage,String DeviceModel,String Locale) {
		StringBuffer post = new StringBuffer();
		post.append("action=reportsubmit&deviceid=")
	     .append(EncodeURL(IMEI)) 
	     .append("&errorcode=")
	     .append(EncodeURL(ErrorCode))
	     .append("&errormsg=")
	     .append(EncodeURL(ErrorMsg))
	     .append("&curfirmwarever=")
	     .append(EncodeURL(CurFirmwareVer))
	     .append("&updatefrom=")
	     .append(EncodeURL(UpdateFrom))
	     .append("&updateto=")
	     .append(EncodeURL(UpdateTo))
	     .append("&stage=")
	     .append(EncodeURL(Stage))
	     .append("&devicemodel=")
	     .append(EncodeURL(DeviceModel))
	     .append("&locale=")
	     .append(EncodeURL(Locale)) ;
		

		return post.toString();
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
	

	/**
	 * 携带相应的参数，向服务器发送请求
	 * @author root
	 *
	 */
	private class sendReportThread extends Thread {
		private String ServerUrl = null;

		public sendReportThread(String ServerUrl) {
			this.ServerUrl = ServerUrl ;
		}
		
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try{
				HttpGet get = new HttpGet(ServerUrl) ;
				HttpClient client = new DefaultHttpClient() ;
				HttpResponse request =  client.execute(get) ;
				if(request.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
					Log.d(Tag, "send report Success");
				}else{
					Log.d(Tag, "send report Failure");
				}
				
			}catch(ClientProtocolException e){
				e.printStackTrace() ;
			}catch(IOException e){
				e.printStackTrace() ;
			}catch(Exception e){
				e.printStackTrace() ;
			}
		}
		
	}

	
	
} 
