package com.epson.isv;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.epson.isv.eprinterdriver.Common.EpsPrinter;
import com.epson.isv.eprinterdriver.Ctrl.EPSetting;
import com.epson.isv.eprinterdriver.Ctrl.EPrintManager;

import java.util.HashMap;


/**
 * ファイル選択画面アクティビティ
 *
 */
public class ActMain extends ActPrintSetting {
	final int MENU_ID_SETTING                          = Menu.FIRST + 1;
	
	final int MENU_ID_UTILITY                          = Menu.FIRST + 100;
	final int MENU_ID_NOZZLE_CHECK                     = MENU_ID_UTILITY + 1;
	final int MENU_ID_HEAD_CLEANING                    = MENU_ID_UTILITY + 2;
	final int MENU_ID_PAPER_FEED                       = MENU_ID_UTILITY + 3;

	final int MENU_ID_PRINTER_STATUS                   = Menu.FIRST + 200;
	final int MENU_ID_STATUS                           = MENU_ID_PRINTER_STATUS + 1;
	final int MENU_ID_INK_INFO                         = MENU_ID_PRINTER_STATUS + 2;

	final int ACT_FOR_RESULT_IMAGE                     = 1;
	final int ACT_FOR_RESULT_SEARCH                    = 2;
	
	final String TARGET_PRINTER = "EP-804A";
	
	static {
		System.loadLibrary("jpeg-8c");
	}
	
	class TimeoutValue extends NumberOptionValue {
		public TimeoutValue() {
			NumberOption option = new NumberOption("タイムアウト", 30, 1, 360);
			bindOption(R.id.SettingEditTimeout, option);
		}
	}
	
	HashMap<Integer, Intent> operationMap;	
	TimeoutValue timeoutValue;
	EPrintManager epManager;
	EpsPrinter epPrinter;
	TextView textPrinterName;	
	ImageView imageView;
	String imagePath;
	Bitmap bitmap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_file_select);
		setTitle(R.string.image_file_select_label);
		init();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
    	  if(keyCode == KeyEvent.KEYCODE_BACK ) {		 
    		  epManager.releaseEscprLib();
    		  finish();
    		  System.exit(0);
    		  return true;
    	  }
    	  return super.onKeyDown(keyCode, event);
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	MenuItem menuSetting;
    	menuSetting = menu.add(Menu.NONE, MENU_ID_SETTING, Menu.NONE, "印刷設定");
    	menuSetting.setIcon(android.R.drawable.ic_menu_manage);
    	
    	SubMenu menuUtility;
    	menuUtility = menu.addSubMenu(Menu.NONE, MENU_ID_UTILITY, Menu.NONE, "メンテナンス");
    	menuUtility.setIcon(android.R.drawable.ic_menu_directions);
    	menuUtility.add(Menu.NONE, MENU_ID_NOZZLE_CHECK, Menu.NONE, "ノズルチェック");
    	menuUtility.add(Menu.NONE, MENU_ID_HEAD_CLEANING, Menu.NONE, "ヘッドクリーニング");
    	menuUtility.add(Menu.NONE, MENU_ID_PAPER_FEED, Menu.NONE, "用紙フィード");
    	
    	SubMenu menuPrinterStatus;
    	menuPrinterStatus = menu.addSubMenu(Menu.NONE, MENU_ID_PRINTER_STATUS, Menu.NONE, "ステータス");
    	menuPrinterStatus.setIcon(android.R.drawable.ic_menu_more);
     	menuPrinterStatus.add(Menu.NONE, MENU_ID_STATUS, Menu.NONE, "スターテス");
    	menuPrinterStatus.add(Menu.NONE, MENU_ID_INK_INFO, Menu.NONE, "インク情報");
    	
    	operationMap = new HashMap<Integer, Intent>();
    	operationMap.put(new Integer(MENU_ID_SETTING), opForSetting());
    	operationMap.put(new Integer(MENU_ID_NOZZLE_CHECK), opForPrint(ActPrintListener.OP_NOZZLE_CHECK));
    	operationMap.put(new Integer(MENU_ID_HEAD_CLEANING), opForPrint(ActPrintListener.OP_HEAD_CLEANING));
    	operationMap.put(new Integer(MENU_ID_PAPER_FEED), opForPrint(ActPrintListener.OP_PAPER_FEED));
    	operationMap.put(new Integer(MENU_ID_STATUS), opForStatus(ActPrinterStatus.OP_STATUS));
    	operationMap.put(new Integer(MENU_ID_INK_INFO), opForStatus(ActPrinterStatus.OP_INK_INFO));
        return true;
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent operation = operationMap.get(new Integer(item.getItemId()));
    	if (operation == null) {
    		return true;
    	}
    	
     	if (item.getItemId() == MENU_ID_SETTING) {
        	if (imagePath == null) {
        		show(R.string.print_setting_select_image_print);
        		return true;
        	}
        	
    		operation.putExtra(ActPrintListener.PATH_SELECTED_IMAGE, imagePath);        	
    	}
    	
    	if (epPrinter != null) {
    		startActivity(operation);
    	} else {
    		show(R.string.print_setting_searching_printer_use);
    	}
    	
    	return true;
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ACT_FOR_RESULT_IMAGE) {
			if (resultCode == RESULT_OK && data != null) {
				receiveImage(data);
			}
		} else {
			EpsPrinter printer = null;
			if (resultCode == RESULT_OK && data != null) {
				printer = data.getParcelableExtra(ActPrinterSearch.PRINTER);
			}
			if (printer == null) {
				textPrinterName.setText(R.string.print_setting_msg_search_notfound);
			} else {
				textPrinterName.setText(printer.getModelName());
			}

			epPrinter = printer;
			EPSetting.instance().setSelEpsPrinter(epPrinter);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	

	void init() {
		
		String libpath = "/data/data/com.epson.isv.EPSample/lib";
		String libname = "libeprinterdriver.so";
		epManager = EPrintManager.instance();
		epManager.initEscprLib(getApplicationContext(), libpath, libname);

		timeoutValue    = new TimeoutValue();
		textPrinterName = (TextView)findViewById(R.id.SettingTextPrinterName);
		textPrinterName.setText(R.string.print_setting_msg_search_notfound);
		textPrinterName.setTextSize(20);
		
		imageView       = (ImageView) findViewById(R.id.FileImage);
		imageView.setImageResource(R.drawable.gallery_no_image);
		
		Button search = (Button)findViewById(R.id.SettingButtonSearch);
		search.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				epPrinter = null;
				Intent intent = new Intent(ActMain.this, ActPrinterSearch.class);
				intent.putExtra(ActPrinterSearch.TIMEOUT, timeoutValue.getIntValue());
				startActivityForResult(intent, ACT_FOR_RESULT_SEARCH);
			}
		});	
		
		Button select = (Button) findViewById(R.id.FileButtonSelect);
		select.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Uri uri = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI;
				Intent pick = new Intent(Intent.ACTION_PICK, uri);
				startActivityForResult(pick, ACT_FOR_RESULT_IMAGE);
			}
		});
		
		Button menu = (Button) findViewById(R.id.SettingButtonMenu);
		menu.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				openOptionsMenu(); 
			}
		});		
	}
	
    Intent opForStatus(int opcode) {
    	Intent operation = new Intent();
		operation.setClass(ActMain.this, ActPrinterStatus.class);
		operation.putExtra(ActPrinterStatus.OPCODE, opcode); 
    	return operation;    	
    }
    
    Intent opForPrint(int opcode) {
    	Intent operation = new Intent();
		operation.setClass(ActMain.this, ActPrintListener.class);
		operation.putExtra(ActPrintListener.OPCODE, opcode); 
		return operation;
    }
    
    Intent opForSetting() {
    	Intent operation = new Intent();
		operation.setClass(ActMain.this, ActPrintSetting.class);
		operation.putExtra(ActPrintListener.OPCODE, ActPrintListener.OP_PRINT); 
		operation.putExtra(ActPrintListener.PATH_SELECTED_IMAGE, imagePath);
    	return operation;  	
    }
	
	void show(int message) {
		new AlertDialog.Builder(ActMain.this)
		.setTitle(R.string.app_name)
		.setMessage(message)
		.setPositiveButton(R.string.print_setting_printstatus_dialog_ok, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
			}
		})
		.create().show();    		
	}

	void receiveImage(Intent intent) {
		Uri uri = getUri(intent);
		imagePath = getPathFromUri(uri);

		Options optSize = new Options();
		optSize.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(imagePath, optSize);
		int imgSize = optSize.outWidth * optSize.outHeight;
		if(imgSize > 1500000) {
			Options thOption = new Options();
			thOption.inSampleSize = 4;
			bitmap = BitmapFactory.decodeFile(imagePath, thOption);			
		} else {
			bitmap = BitmapFactory.decodeFile(imagePath);					
		}
		
		imageView.setImageBitmap(bitmap);
	}

	Uri getUri(Intent intent) {
		Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
		if (uri != null) {
			return uri;
		}
		uri = intent.getData();
		return uri;
	}

	String getPathFromUri(Uri uri) {
		String path = "";
		path = uri.getPath();

		try {
			ContentResolver cr = getContentResolver();
			Cursor cursor = cr.query(uri, new String[] { MediaColumns.DATA },
					null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
			cursor.moveToFirst();
			path = cursor.getString(column_index);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return path;
	}

}
