package com.epson.isv;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;

import com.epson.isv.eprinterdriver.Common.EpsStatus;
import com.epson.isv.eprinterdriver.Common.ServiceIntent;
import com.epson.isv.eprinterdriver.Ctrl.EPrintManager;
import com.epson.isv.eprinterdriver.Ctrl.EPrintManager.EPRINT_FILETYPE;
import com.epson.isv.eprinterdriver.Ctrl.IPrintListener;

import java.util.List;


/**
 * 印刷中/計測中画面アクティビティ
 *
 */
public class ActPrintListener extends Activity implements IPrintListener {
	public final static String OPCODE = "ActPrintListener.OPCODE";
	public final static int OP_ZERO                             = 0;
	public final static int OP_PRINT                            = 1;
	public final static int OP_NOZZLE_CHECK                     = 2;
	public final static int OP_HEAD_CLEANING                    = 3;
	public final static int OP_PAPER_FEED                       = 4;

	public final static String PATH_SELECTED_IMAGE     = "ActPrintListener.PATH_SELECTED_IMAGE";
	
	EPrintManager epManager;
	TextView tvPntStatus;
	String imagePath;
	int colorMode;
	int opCode;

	private Handler mHandler;
	private List<String> imageList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.print_listener);
		mHandler = new Handler();
		tvPntStatus = (TextView)findViewById(R.id.ListenerLabelMessage);

		epManager = EPrintManager.instance();
		epManager.addPrintListener(this);
		
		Intent intent = getIntent();
		
		int code;
		String path;
		
		code = intent.getIntExtra(OPCODE, OP_ZERO);
		if (code == OP_ZERO) return;
		opCode = code;
		
		switch (opCode) {
		case OP_PRINT:
			imageList = intent.getStringArrayListExtra(PATH_SELECTED_IMAGE);
			if (imageList == null || imageList.size() == 0) {
				finish();
				return;
			}
			startPrint(500);
			break;
		case OP_NOZZLE_CHECK:
			epManager.startNozzleCheck();
			break;
		case OP_HEAD_CLEANING:
			epManager.startHeadCleaning();			
			break;
		case OP_PAPER_FEED:
			epManager.startPaperFeed();			
			break;
		default:
			break;
		}
	}

	private void startPrint(int delay) {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				imagePath = imageList.get(0);
				epManager.startPrint(imagePath, getFiletype(imagePath));
			}
		}, delay);
	}

	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	  if(keyCode == KeyEvent.KEYCODE_BACK ) {
    		  if (epManager.isPrintBusy()) {
    			  if (opCode == OP_PRINT) {
        			  tvPntStatus.setText("キャンセル中...");
        			  epManager.cancelPrint();
    			  } else {
        			  tvPntStatus.setText("印刷実行中はキャンセルできません。");				  
    			  }
    			  return true;
    		  }
    		  release();
    		  finish();
    		  return true;
    	  }
    	  return super.onKeyDown(keyCode, event);
    }
	
	void release(){
		System.gc();
	}
	
	EPRINT_FILETYPE getFiletype(String filename) {
		if (filename.toLowerCase().endsWith("bmp")) {
			return EPRINT_FILETYPE.BMP;
		}
		return EPRINT_FILETYPE.JPEG;
	}
	
	Dialog statusDialog = null;
	Dialog createStatusDialog(EpsStatus status) {
		if (statusDialog != null) {
			dismissStatusDialog();
		}
		
		String message = status.toString();
		final boolean continueable = status.isJobContinue();

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message);
		builder.setCancelable(false);
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				epManager.cancelPrint();
			}
		});
		
		if (continueable) {
			builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					epManager.continuePrint();
				}
			});
		}
		return builder.create();
	}
	
	void disableStatusDialog() {
		((AlertDialog) statusDialog).getButton(Dialog.BUTTON_NEGATIVE).setEnabled(false);
		Button continueable = ((AlertDialog) statusDialog).getButton(Dialog.BUTTON_POSITIVE);
		if (continueable != null) {
			continueable.setEnabled(false);
		}
	}
	
	void dismissStatusDialog() {
		if (statusDialog != null) {
			statusDialog.dismiss();
			statusDialog = null;
		}		
	}

	@Override
	public void onPageFinished(int totalNums, int finishedNum) {
    	System.gc();
	}

	@Override
	public void onPrintBegin() {
		tvPntStatus.setText(R.string.print_status_msg_pntret_printing);
		statusDialog = null;
    	System.gc();
	}

	@Override
	public void onPrintFinished(int factor) {
		System.gc();

		int message;
		switch (factor) {
		case ServiceIntent.StopFactor.PrintSuccess:
			String msg = getString(R.string.print_status_msg_pntret_success);
			imageList.remove(0);
			if (imageList.size() > 0){
				startPrint(500);
				msg += "\n开始打印下一页！";
			}else{
				msg += "\n打印完毕！";
			}
			tvPntStatus.setText(msg);
			return;
		case ServiceIntent.StopFactor.PrinterStopButton:
			message = R.string.print_status_msg_pntret_stopbutton;
			break;
		case ServiceIntent.StopFactor.UserCancel:
			message = R.string.print_status_msg_pntret_usercancel;			
			break;
		case ServiceIntent.StopFactor.PrinterErrorOccur:
			message = R.string.print_status_msg_pntret_printererror;			
			break;
		default:
			message = R.string.print_status_msg_pntret_printererror;			
			break;
		}
		startPrint(2000);
		tvPntStatus.setText(message);
	}
	
	@Override
	public void onPrintPause(int curNum, int pauseFactor, EpsStatus pauseStatus) {
		statusDialog = createStatusDialog(pauseStatus);
		statusDialog.show();
	}

	@Override
	public void onPrintResume() {
		dismissStatusDialog();
	}

	@Override
	public void onPrintAutoContinue() {
		dismissStatusDialog();
	}
	
	@Override
	public void onCleaningTime(int seconds) {
		String s = "onCleaningTimeInformation = " + seconds + " seconds";
		tvPntStatus.setText(s);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		epManager.releaseEscprLib();
	}
}
