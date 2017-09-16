package com.epson.isv;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.epson.isv.eprinterdriver.Common.EpsInkInfo;
import com.epson.isv.eprinterdriver.Common.EpsStatus;
import com.epson.isv.eprinterdriver.Ctrl.EPrintManager;

public class ActPrinterStatus extends Activity {
	public final static String OPCODE = "ActPrinterStatus.OPCODE";
	public final static int OP_ZERO              = 0;
	public final static int OP_STATUS            = 1;
	public final static int OP_INK_INFO          = 2;

	EPrintManager epManager;
	TextView statusView;
	Button update;
	int opCode;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.print_status);

		epManager = EPrintManager.instance();
		statusView = (TextView)findViewById(R.id.StatusLabelMessage);
		statusView.setText("更新ボタンを押してください。");
		
		int code;
		code = getIntent().getIntExtra(OPCODE, OP_STATUS);
		if (code == OP_ZERO) return;
		opCode = code;
		
		update = (Button)findViewById(R.id.StatusButtonUpdate);
		update.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				AsyncTask<Void, Void, Void> task = null;
				switch (opCode) {
				case OP_STATUS:
					task = new StatusTask();
					break;
				case OP_INK_INFO:
					task = new InkInfoTask();
					break;
				default:
					break;
				}
				if (task != null) {
					update.setEnabled(false);
					task.execute();
				}
			}
		});	
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK ) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
    }

	void updateStatus(final String s) {
		runOnUiThread(new Runnable() {
			public void run() {
				statusView.setText(s);
				update.setEnabled(true);
			}
		});		
	}
	
	class StatusTask extends AsyncTask<Void, Void, Void> {
		String s = "ステータス取得失敗";
		protected Void doInBackground(Void... params) {
			EpsStatus status = null;
			status = epManager.getStatus();
			if (status != null) {
				s  = "[プリンターステータス]" + "\n";
				s += "Status : " + status.getPrinterStatus() + "\n";
				s += "Error  : " + status.getErrorCode() + "\n";
				s += "Warning: " + status.getWarningCode() + "\n";
				s += "\n";
			}
			updateStatus(s);
			return null;
		}
	}
	
	class InkInfoTask extends AsyncTask<Void, Void, Void> {
		String s = "インク情報取得失敗";
		protected Void doInBackground(Void... params) {
			EpsInkInfo inkInfo = null;
			inkInfo = epManager.getInkInfo();
			if (inkInfo != null) {
				int number = inkInfo.getNumber();
				int[] colors = inkInfo.getColors();
				int[] remaining = inkInfo.getRemaining();
				int[] warning = inkInfo.getWarning();
				
				s = "インク数：" + number + "\n";
				for (int i = 0; i < number; i++) {
					s += colors[i] + "  : ";
					s += remaining[i];
					if (remaining[i] > 0) {
						s += "%";
					}
					s += " WR(" + warning[i] + ")\n";
				}
			}
			updateStatus(s);
			return null;
		}
	
	}

}