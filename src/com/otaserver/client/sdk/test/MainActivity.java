package com.otaserver.client.sdk.test;

import java.util.Map;

import com.otaserver.client.sdk.CheckUpdate;
import com.otaserver.client.sdk.R;
import com.otaserver.client.sdk.CheckUpdate.checkUpdateInter;
import com.otaserver.client.sdk.SendUpdateReport;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.telephony.TelephonyManager;

public class MainActivity extends Activity implements checkUpdateInter,OnClickListener{
	private static final String Tag = "MainActivity" ;
	private static final int needupdate = 1 ;
	private static final int notupdate = 2 ;
	private static final String updatemessage_key  = "updatemessage" ;
	private static final String packageSize_key = "packageSize" ;
	private static final String md5_key  = "md5";
	private static final String downloadUrl_key = "downloadUrl" ;
	private static final String targetVer_key = "targetVer" ;
	private String targetVer = null ;
	private Button mCheckUpdate,mSendDownloadReport,mSendUpdateReport = null ;
	private TextView mShowUpdateInfo,mShowDownloadReportResult,mShowUpdateReportResult = null ;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidget() ;
    }
    
    private void initWidget(){
    	mCheckUpdate = (Button)findViewById(R.id.checkbutton) ;
    	mSendDownloadReport = (Button)findViewById(R.id.senddownloadreport) ;
    	mSendUpdateReport = (Button)findViewById(R.id.senddupdatereport) ;
    	
    	mCheckUpdate.setOnClickListener(this) ;
    	mSendDownloadReport.setOnClickListener(this) ;
    	mSendUpdateReport.setOnClickListener(this) ;
    	mShowUpdateInfo = (TextView)findViewById(R.id.showupdateinfo) ;
    	mShowDownloadReportResult = (TextView)findViewById(R.id.showdownloadreportresult) ;
    	mShowUpdateReportResult = (TextView)findViewById(R.id.showupdatereportresult) ;
    	mSendDownloadReport.setEnabled(false) ;
    	mSendUpdateReport.setEnabled(false) ;
    }

    Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case needupdate :
				//处理升级的界面逻辑
				Bundle mBundle = (Bundle)msg.obj ;
				StringBuffer stringbuffer = new StringBuffer() ;
				stringbuffer.append(mBundle.get(targetVer_key))
				.append("   ")
				.append(mBundle.get(packageSize_key))
				.append("\n")
				.append(mBundle.get(updatemessage_key))
				.append("\n")
				.append(mBundle.get(downloadUrl_key))
				.append("\n")
				.append(mBundle.get(md5_key)) ;
				mShowUpdateInfo.setText(stringbuffer.toString());
			  	mSendDownloadReport.setEnabled(true) ;
		    	mSendUpdateReport.setEnabled(true) ;
				break ;
			case notupdate :
				//处理不需要升级的界面逻辑
				int reason = (Integer)msg.obj ;
				switch(reason){
				case CheckUpdate.VerionIsNew :
					mShowUpdateInfo.setText("当前版本已经是最新，无需升级.");
					break ;
				case CheckUpdate.UpdateInfoDownloadError :
					mShowUpdateInfo.setText("无法得到升级信息导致不能升级.");
					break ;
				}
				break ;
			}
		}
    	
    };
    
	@Override
	public void NeedUpdate(Map<String, String> updateMessage,
			String packageSize, String md5, String downloadUrl, String targetVer) {
		// TODO Auto-generated method stub
		//等到对应当前语言的升级描述
		this.targetVer = targetVer ;
		String localLanguage  = this.getResources().getConfiguration().locale.getLanguage() ;
		String curupdatemessage = updateMessage.get(localLanguage) ;
		//在此步可以考录把得到的升级信息保存起来，如SharedPreferences，方便使用。
		Bundle bundle = new Bundle() ;
		bundle.putString(updatemessage_key, curupdatemessage) ;
		bundle.putString(packageSize_key, packageSize) ;
		bundle.putString(md5_key, md5) ;
		bundle.putString(downloadUrl_key, downloadUrl) ;
		bundle.putString(targetVer_key, targetVer) ;
		Message msg = mHandler.obtainMessage(needupdate, bundle) ;
		mHandler.sendMessage(msg) ;
	}

	@Override
	public void NotUpdate(int reason) {
		// TODO Auto-generated method stub
		Message msg = mHandler.obtainMessage(notupdate, reason) ;
		mHandler.sendMessage(msg) ;
	}

	@Override
	public boolean checkNewestFirmwarePackageVer(String targetVer) {
		// TODO Auto-generated method stub
		//此处需要用目标版本"targetVer"与当前版本"curVer"进行比较，如果targetVer > curVer
		//返回true,则升级；targetVer < curVer 返回false,则不升级
		
		//为了验证功能，这里直接返回true了
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.checkbutton :
			CheckUpdate mCheckUpdate = new CheckUpdate(this,getXmlInfoPath()) ;
//			CheckUpdate mCheckUpdate = new CheckUpdate(this, getFirmwareVersion(), getIMEI(),  getLocale() ,getDeviceModel(), getXmlInfoPath()) ;
			mCheckUpdate.StartCheck(this) ;
			break;
		case R.id.senddownloadreport :
			SendUpdateReport mReportD = new SendUpdateReport(this) ;
			mReportD.DownloadReport("0", "下载成功", getFirmwareVersion(), targetVer, getIMEI(),getDeviceModel()) ;
			break;
		case R.id.senddupdatereport :
			SendUpdateReport mReportU = new SendUpdateReport(this) ;
			mReportU.UpdateReport("0", "升级成功", getFirmwareVersion(), getFirmwareVersion(), targetVer, getIMEI(), getDeviceModel()) ;
			break;
		}
	}
	
	private String getIMEI(){
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String imeiNumber = tm.getDeviceId() ;
		return imeiNumber ;
	}
	
	private String getFirmwareVersion(){
		String firmwareType = android.os.Build.VERSION.INCREMENTAL;
		return firmwareType ;
	}
	
	private String getDeviceModel(){
		String deviceModel  = android.os.Build.MODEL ;
		return deviceModel ;
	}
	
	private String getLocale(){
		String Locale  = getResources().getConfiguration().locale.getLanguage()  ;
		Log.d(Tag, "getLocale="+Locale) ;
		return Locale ;
	}
	
	private String getXmlInfoPath(){
		String filePath = "/sdcard/" ;
		return filePath ;
	}

    
}
