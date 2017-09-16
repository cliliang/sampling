package com.epson.isv;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.epson.isv.eprinterdriver.Common.EpsCapability;
import com.epson.isv.eprinterdriver.Ctrl.EPSetting;
import com.epson.isv.eprinterdriver.Ctrl.EPrintManager;
import com.epson.isv.eprinterdriver.Ctrl.PageAttribute;
import com.epson.isv.eprinterdriver.Ctrl.PageAttribute.MediaSizeID;
import com.epson.isv.eprinterdriver.Ctrl.PageAttribute.MediaTypeID;
import com.epson.isv.eprinterdriver.Ctrl.PageAttribute.PrintQuality;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



/**
 * 印刷設定画面アクティビティ
 *
 */
public class ActPrintSetting extends Activity {
	public static final String KeyPaperSize             = "KeyPaperSize";
	public static final String KeyPaperType             = "KeyPaperType";
	public static final String KeyPrintQuality          = "KeyPrintQuality";
	public static final String KeyColorMode				= "KeyColorMode";
	public static final String KeyBorderless            = "KeyBorderless";
	public static final String KeyPaperPath             = "KeyPaperPath";
	public static final String KeyDuplexPrint           = "KeyDuplexPrint";
	public static final String KeyBidPrint              = "KeyBidPrint";
	public static final String KeyTotalPages	        = "KeyTotalPages";
	public static final int OP_PRINT                    = 1;


	HashMap<String, OptionValue> optionValueMap = new HashMap<String, OptionValue>();

	abstract class OptionValue extends Object {
		public int getIntValue() {
			return 0;
		}

		public boolean getBooleanValue() {
			return false;
		}

		public String getStringValue() {
			return "";
		}
	}

	int getIntValueOf(String key) {
		OptionValue option = optionValueMap.get(key);
		return option.getIntValue();
	}

	boolean getBooleanValueOf(String key) {
		OptionValue option = optionValueMap.get(key);
		return option.getBooleanValue();
	}

	String getStringValueOf(String key) {
		OptionValue option = optionValueMap.get(key);
		return option.getStringValue();
	}

	class ListOptionValue extends OptionValue {
		class ListOption {
			public ListOption(String key, Object value) {
				this.key   = key;
				this.value = value;
			}
			public String key;
			public Object value;
		}

		List<ListOption> options;
		Spinner spinner;

		ArrayAdapter<String> getArrayAdapter(ListOptionValue option, Activity context) {
			int resource = android.R.layout.simple_spinner_item;
			String[] array = option.getKeyArray();
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, resource, array);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			return adapter;
		}

		void bindOptions(int resource, List<ListOption> list, Activity context) {
			options = list;
			spinner = (Spinner) findViewById(resource);
			spinner.setAdapter(getArrayAdapter(this, context));
		}

		String[] getKeyArray() {
			String[] keys = new String[options.size()];
			for (int i = 0; i < options.size(); i++) {
				keys[i] = options.get(i).key;
			}
			return keys;
		}

		void selectOption(int position) {
			spinner.setSelection(position);
		}

		Object selectedOption() {
			int position = spinner.getSelectedItemPosition();
			ListOption item = options.get(position);
			return item.value;
		}

		@Override
		public boolean getBooleanValue() {
			boolean value = false;
			Object option = selectedOption();
			if (option instanceof Boolean) {
				value = ((Boolean)option).booleanValue();
			}
			return value;
		}

		@Override
		public int getIntValue() {
			int value = 0;
			Object option = selectedOption();
			if (option instanceof Integer) {
				value = ((Integer)option).intValue();
			}
			return value;
		}

		@Override
		public String getStringValue() {
			String value = "";
			Object option = selectedOption();
			if (option instanceof String) {
				value = (String)option;
			}
			return value;
		}
	}

	class NumberOptionValue extends OptionValue {
		class NumberOption {
			public NumberOption(String name, int defaultNumber, int min, int max) {
				this.name = name;
				this.number = defaultNumber;
				this.defaultNumber = defaultNumber;
				this.min = min;
				this.max = max;
			}

			public String name;
			public int number;
			public int defaultNumber;
			public int min;
			public int max;
		}

		NumberOption option;
		EditText editText;

		void bindOption(int resource, NumberOption option) {
			editText = (EditText) findViewById(resource);
			editText.setText(String.valueOf(option.number));
			this.option = option;
		}

		@Override
		public int getIntValue() {
			int number = option.defaultNumber;
			String text = editText.getText().toString();
			try {
				int n = Integer.parseInt(text);
				if (n >= option.min && n <= option.max) {
					number = n;
				}
			} catch (NumberFormatException e) {
			}
			option.number = number;
			return option.number;
		}
	}

	class StringOptionValue extends OptionValue {
		class StringOption {
			public StringOption(String name, String text) {
				this.name = name;
				this.text = text;
				this.defaultText = text;
			}

			public String name;
			public String text;
			public String defaultText;
		}

		StringOption option;
		EditText editText;

		void bindOption(int resource, StringOption option) {
			editText = (EditText) findViewById(resource);
			editText.setText(String.valueOf(option.text));
			this.option = option;
		}

		@Override
		public String getStringValue() {
			String string = editText.getText().toString();
			if (string.equals("")) {
				string = option.defaultText;
			}
			option.text = string;
			return option.text;
		}
	}

	class BorderlessValue extends ListOptionValue {
		public BorderlessValue() {
			List<ListOption> array = new ArrayList<ListOption>();
			array.add(new ListOption("しない", new Boolean(false)));
			array.add(new ListOption("する", new Boolean(true)));
			bindOptions(R.id.SettingSpinnerBorderless, array, ActPrintSetting.this);
		}
	}

	class PaperSizeValue extends ListOptionValue {
		public PaperSizeValue() {
			List<ListOption> array = new ArrayList<ListOption>();
			array.add(new ListOption("A4", new Integer(MediaSizeID.EPS_MSID_A4)));
			array.add(new ListOption("A5", new Integer(MediaSizeID.EPS_MSID_A5)));
			array.add(new ListOption("A6", new Integer(MediaSizeID.EPS_MSID_A6)));
			array.add(new ListOption("B5", new Integer(MediaSizeID.EPS_MSID_B5)));
			array.add(new ListOption("六切", new Integer(MediaSizeID.EPS_MSID_8X10)));
			array.add(new ListOption("ハイビジョン", new Integer(MediaSizeID.EPS_MSID_HIVISION)));
			array.add(new ListOption("ハガキ", new Integer(MediaSizeID.EPS_MSID_POSTCARD)));
			array.add(new ListOption("KG", new Integer(MediaSizeID.EPS_MSID_4X6)));
			array.add(new ListOption("2L判", new Integer(MediaSizeID.EPS_MSID_2L)));
			array.add(new ListOption("L判", new Integer(MediaSizeID.EPS_MSID_L)));
			array.add(new ListOption("カード", new Integer(MediaSizeID.EPS_MSID_CARD_54X86)));
			array.add(new ListOption("シール", new Integer(MediaSizeID.EPS_MSID_POSTCARD)));
			bindOptions(R.id.SettingSpinnerPaperSize, array, ActPrintSetting.this);
		}
	}

	class PaperTypeValue extends ListOptionValue {
		public PaperTypeValue() {
			List<ListOption> array = new ArrayList<ListOption>();
			array.add(new ListOption("普通紙", new Integer(MediaTypeID.EPS_MTID_PLAIN)));
			array.add(new ListOption("写真用紙クリスピア", new Integer(MediaTypeID.EPS_MTID_PLATINA)));
			array.add(new ListOption("写真用紙", new Integer(MediaTypeID.EPS_MTID_PGPHOTO)));
			array.add(new ListOption("写真用紙エントリー", new Integer(MediaTypeID.EPS_MTID_GLOSSYPHOTO)));
			array.add(new ListOption("フォト光沢紙", new Integer(MediaTypeID.EPS_MTID_GLOSSYCAST)));
			array.add(new ListOption("フォトマット紙", new Integer(MediaTypeID.EPS_MTID_MATTE)));
			array.add(new ListOption("スーパーファイン紙", new Integer(MediaTypeID.EPS_MTID_PHOTOINKJET)));
			array.add(new ListOption("官製ハガキ", new Integer(MediaTypeID.EPS_MTID_HAGAKIRECL)));
			array.add(new ListOption("郵便IJ/郵便ハガキ", new Integer(MediaTypeID.EPS_MTID_HAGAKIINKJET)));
			array.add(new ListOption("郵便光沢ハガキ", new Integer(MediaTypeID.EPS_MTID_GLOSSYHAGAKI)));
			bindOptions(R.id.SettingSpinnerPaperType, array, ActPrintSetting.this);
		}
	}

	class PrintQualityValue extends ListOptionValue {
		public PrintQualityValue() {
			List<ListOption> array = new ArrayList<ListOption>();
			array.add(new ListOption("はやい", new Integer(PrintQuality.EPS_MQID_DRAFT)));
			array.add(new ListOption("ふつう", new Integer(PrintQuality.EPS_MQID_NORMAL)));
			array.add(new ListOption("きれい", new Integer(PrintQuality.EPS_MQID_HIGH)));
			bindOptions(R.id.SettingSpinnerQuality, array, ActPrintSetting.this);
			selectOption(1);
		}
	}

	class ColorModeValue extends ListOptionValue {
		public ColorModeValue() {
			List<ListOption> array = new ArrayList<ListOption>();
			array.add(new ListOption("カラー", new Integer(EPSetting.COLOR_MODE_COLOR)));
			array.add(new ListOption("モノクロ", new Integer(EPSetting.COLOR_MODE_MONOCHROME)));
			bindOptions(R.id.SettingSpinnerColorMode, array, ActPrintSetting.this);
		}
	}

	class PaperPathValue extends ListOptionValue {
		public PaperPathValue() {
			List<ListOption> array = new ArrayList<ListOption>();
			array.add(new ListOption("自動選択", new Integer(EPSetting.PAPER_SOURCE_NOT_SPEC)));
			bindOptions(R.id.SettingSpinnerPaperPath, array, ActPrintSetting.this);
		}
	}

	class DuplexPrintValue extends ListOptionValue {
		public DuplexPrintValue() {
			List<ListOption> array = new ArrayList<ListOption>();
			array.add(new ListOption("しない", new Boolean(false)));
			array.add(new ListOption("する", new Boolean(true)));
			bindOptions(R.id.SettingSpinnerDuplexPrint, array, ActPrintSetting.this);
		}
	}

	class BidPrintValue extends ListOptionValue {
		public BidPrintValue() {
			List<ListOption> array = new ArrayList<ListOption>();
			array.add(new ListOption("する", new Integer(EPSetting.PRINT_DIR_BI)));
			array.add(new ListOption("しない", new Integer(EPSetting.PRINT_DIR_UNI)));
			bindOptions(R.id.SettingSpinnerBidPrint, array, ActPrintSetting.this);
		}
	}

	class TotalPagesValue extends NumberOptionValue {
		public TotalPagesValue() {
			NumberOption option = new NumberOption("部数", 1, 1, 99);
			bindOption(R.id.SettingEditTotalPages, option);
		}
	}


	String imagePath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.print_setting);
		String path;

		path = getIntent().getStringExtra(ActPrintListener.PATH_SELECTED_IMAGE);
		if (path != null) {
			imagePath = new String(path);
		}

		Button printPage = (Button) findViewById(R.id.SettingButtonPrint);
		printPage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				print(R.id.SettingButtonPrint);
			}
		});

		optionValueMap.clear();

		optionValueMap.put(KeyPaperSize,             new PaperSizeValue());
		optionValueMap.put(KeyPaperType,             new PaperTypeValue());
		optionValueMap.put(KeyPrintQuality,          new PrintQualityValue());
		optionValueMap.put(KeyColorMode,			 new ColorModeValue());
		optionValueMap.put(KeyBorderless,            new BorderlessValue());
		optionValueMap.put(KeyBidPrint,              new BidPrintValue());
		optionValueMap.put(KeyPaperPath,             new PaperPathValue());
		optionValueMap.put(KeyDuplexPrint,           new DuplexPrintValue());
		optionValueMap.put(KeyTotalPages,                new TotalPagesValue());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK ) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void print(int resid) {
		//印刷Busyかをチェック
		if(EPrintManager.instance().isPrintBusy()) {
			new AlertDialog.Builder(ActPrintSetting.this)
					.setTitle(R.string.app_name)
					.setMessage(R.string.print_setting_printstatus_busy)
					.setPositiveButton(R.string.print_setting_printstatus_dialog_ok, new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					})
					.create().show();
			return;
		}

		EPSetting setting = EPSetting.instance();
		EpsCapability myCapability = new EpsCapability();
		int mediaSize = getIntValueOf(KeyPaperSize);
		int mediaType = getIntValueOf(KeyPaperType);
		int printQuality = getIntValueOf(KeyPrintQuality);
		int colorMode = getIntValueOf(KeyColorMode);
		PageAttribute epPageAttri = new PageAttribute(mediaSize, mediaType, printQuality);
		setting.setSelPageAttri(epPageAttri);
		setting.setPrintDirection(getIntValueOf(KeyBidPrint));
		setting.setColorMode(colorMode);
		setting.setPaperSource(getIntValueOf(KeyPaperPath));
		setting.setBorderless(getBooleanValueOf(KeyBorderless));
		setting.setDuplexPrint(getBooleanValueOf(KeyDuplexPrint));
		setting.setTotalPages(getIntValueOf(KeyTotalPages));
		setting.setTemporaryImageFilePath(getCacheDir().getAbsolutePath() + "/temp.jpg");

		myCapability.setResolution(4);

		Intent status= new Intent();
		status.setClass(ActPrintSetting.this, ActPrintListener.class);
		status.putExtra(ActPrintListener.PATH_SELECTED_IMAGE, imagePath);
		status.putExtra(ActPrintListener.OPCODE, OP_PRINT);
		startActivity(status);
	}
}

