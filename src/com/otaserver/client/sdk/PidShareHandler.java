package com.otaserver.client.sdk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Base64;

public class PidShareHandler {

	private static final String KEY_PID = "cm_lnv_lsf_pid_share";

	public static String getPid(Context context) {
		String pidPassword = getPidPassword(context);
		if (pidPassword == null || pidPassword.length() == 0) {
			return getPidOld(context);//getPidOld(context);
		}
		return pidPassword.split(":")[0];
	}

	public static String getPidPassword(Context context) {
		String pidPasswoBase64 = Settings.System.getString(
				context.getContentResolver(), KEY_PID);
		if (pidPasswoBase64 == null || pidPasswoBase64.length() == 0) {
			return null;
		}
		return new String(Base64.decode(pidPasswoBase64, Base64.DEFAULT));
	}

	public static String getPidOld(Context context) {
		BufferedReader bw = null;
		try {
			PackageManager pm = context.getPackageManager();
			String filepath = pm.getApplicationInfo("com.lenovo.lsf", 128).dataDir
					+ "/files/pid.cfg";
			File f = new File(filepath);
			if (!(f.exists())) {
				return "";
			}
			bw = new BufferedReader(new FileReader(f));
			String str1 = bw.readLine();

			return str1;
		} catch (Exception e) {
		} finally {
			try {
				if (bw != null)
					bw.close();
			} catch (Exception e) {
			}
		}
		return "";
	}
}
