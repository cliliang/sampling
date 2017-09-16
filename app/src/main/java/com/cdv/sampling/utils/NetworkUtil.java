package com.cdv.sampling.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings.System;
import android.telephony.TelephonyManager;

public class NetworkUtil {
    public static final int NETWORK_STATUS_NOT_CONNECTED = 0;
    public static final int NETWORK_STATUS_WIFI = 1;
    public static final int NETWORK_STATUS_2G = 2;
    public static final int NETWORK_STATUS_3G = 3;
    public static NetworkUtil cit = null;
    public Context mContext;

    public NetworkUtil(Context context) {
        this.mContext = context;
    }

    public static NetworkUtil getInstance(Context context) {
        if(cit == null) {
            cit = new NetworkUtil(context.getApplicationContext());
        }

        return cit;
    }

    public boolean isAirplaneModeOn() {
        return System.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) != 0;
    }

    public boolean checkInternet() {
        return isNetworkAvailable(this.mContext);
    }

    public int JudgeCurrentNetState() {
        return getCurrentNetworkStatus(this.mContext);
    }

    public static int getCurrentNetworkStatus(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if(info != null && info.isAvailable()) {
            TelephonyManager mTelephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            int netType = info.getType();
            int netSubtype = info.getSubtype();
            return netType == 1?1:(netType == 0 && netSubtype == 3 && !mTelephony.isNetworkRoaming()?3:2);
        } else {
            return 0;
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager == null) {
            return false;
        } else {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            return info != null && info.isAvailable();
        }
    }

    public static String getNetworkType(Context context) {
        String strNetworkType = "";
        ConnectivityManager conMan = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conMan.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            if(networkInfo.getType() == 1) {
                strNetworkType = "WIFI";
            } else if(networkInfo.getType() == 0) {
                String _strSubTypeName = networkInfo.getSubtypeName();
                int networkType = networkInfo.getSubtype();
                switch(networkType) {
                case 1:
                case 2:
                case 4:
                case 7:
                case 11:
                    strNetworkType = "2G";
                    break;
                case 3:
                case 5:
                case 6:
                case 8:
                case 9:
                case 10:
                case 12:
                case 14:
                case 15:
                    strNetworkType = "3G";
                    break;
                case 13:
                    strNetworkType = "4G";
                    break;
                default:
                    if(!_strSubTypeName.equalsIgnoreCase("TD-SCDMA") && !_strSubTypeName.equalsIgnoreCase("WCDMA") && !_strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                        strNetworkType = _strSubTypeName;
                    } else {
                        strNetworkType = "3G";
                    }
                }
            }
        }

        return strNetworkType;
    }
}