package com.cdv.sampling.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.m3.sdk.scannerlib.Barcode;
import com.m3.sdk.scannerlib.BarcodeListener;
import com.m3.sdk.scannerlib.BarcodeManager;

public class ScanActivity extends Activity {

    public static final String EXTRA_SCAN_RESULT = "EXTRA_SCAN_RESULT";

    private Barcode mBarcode = null;
    private BarcodeListener mListener = null;
    private BarcodeManager mManager = null;
    private Barcode.Symbology mSymbology = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBarcode = new Barcode(this);
        mManager = new BarcodeManager(this);
        mSymbology = mBarcode.getSymbologyInstance();
        mBarcode.setScanner(true);
        mListener = new BarcodeListener() {

            @Override
            public void onBarcode(String strBarcode) {
                mBarcode.scanDispose();
                Intent intent = new Intent();
                intent.putExtra(EXTRA_SCAN_RESULT, strBarcode);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onBarcode(String s, String s1) {

            }

            @Override
            public void onGetSymbology(int nSymbol, int nVal) {
            }
        };

        mManager.addListener(mListener);
        mBarcode.scanStart();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 5000);
    }

    @Override
    protected void onDestroy() {
        mManager.removeListener(mListener);
        mManager.dismiss();
        mBarcode.setScanner(false);
        super.onDestroy();
    }
}
