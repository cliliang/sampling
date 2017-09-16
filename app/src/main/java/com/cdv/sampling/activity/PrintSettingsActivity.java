package com.cdv.sampling.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.cdv.sampling.R;
import com.cdv.sampling.utils.PrinterSettingsUtil;
import com.epson.isv.eprinterdriver.Ctrl.EPSetting;
import com.epson.isv.eprinterdriver.Ctrl.PageAttribute.MediaSizeID;
import com.epson.isv.eprinterdriver.Ctrl.PageAttribute.MediaTypeID;
import com.epson.isv.eprinterdriver.Ctrl.PageAttribute.PrintQuality;
import com.jakewharton.rxbinding.view.RxView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;

import static com.cdv.sampling.utils.PrinterSettingsUtil.PrintSettingParameter.KeyBidPrint;
import static com.cdv.sampling.utils.PrinterSettingsUtil.PrintSettingParameter.KeyBorderless;
import static com.cdv.sampling.utils.PrinterSettingsUtil.PrintSettingParameter.KeyColorMode;
import static com.cdv.sampling.utils.PrinterSettingsUtil.PrintSettingParameter.KeyDuplexPrint;
import static com.cdv.sampling.utils.PrinterSettingsUtil.PrintSettingParameter.KeyPaperPath;
import static com.cdv.sampling.utils.PrinterSettingsUtil.PrintSettingParameter.KeyPaperSize;
import static com.cdv.sampling.utils.PrinterSettingsUtil.PrintSettingParameter.KeyPaperType;
import static com.cdv.sampling.utils.PrinterSettingsUtil.PrintSettingParameter.KeyPrintQuality;
import static com.cdv.sampling.utils.PrinterSettingsUtil.PrintSettingParameter.KeyTotalPages;

public class PrintSettingsActivity extends BaseActivity {

    HashMap<String, OptionValue> optionValueMap = new HashMap<>();
    @BindView(R.id.saveBtn)
    AppCompatButton mSaveBtn;

    abstract class OptionValue {
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
        ListOptionValue(PrinterSettingsUtil.PrintSettingParameter parameter){

        }
        class ListOption {
            public ListOption(String key, Object value) {
                this.key = key;
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

        void bindOptions(int resource, List<ListOption> list, int defaultIndex,  Activity context) {
            options = list;
            spinner = (Spinner) findViewById(resource);
            spinner.setAdapter(getArrayAdapter(this, context));
            spinner.setSelection(defaultIndex);
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
                value = (Boolean) option;
            }
            return value;
        }

        @Override
        public int getIntValue() {
            int value = 0;
            Object option = selectedOption();
            if (option instanceof Integer) {
                value = (Integer) option;
            }
            return value;
        }

        @Override
        public String getStringValue() {
            String value = "";
            Object option = selectedOption();
            if (option instanceof String) {
                value = (String) option;
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
        public BorderlessValue(PrinterSettingsUtil.PrintSettingParameter parameter) {
            super(parameter);
            List<ListOption> array = new ArrayList<ListOption>();
            array.add(new ListOption("不", new Boolean(false)));
            array.add(new ListOption("使", new Boolean(true)));
            int index = 0;
            for (int i = 0; i < array.size(); i++) {
                if (parameter.isBorderless() == ((boolean) array.get(i).value)) {
                    index = i;
                    break;
                }
            }
            bindOptions(R.id.SettingSpinnerBorderless, array, index, PrintSettingsActivity.this);
        }
    }

    class PaperSizeValue extends ListOptionValue {
        public PaperSizeValue(PrinterSettingsUtil.PrintSettingParameter parameter) {
            super(parameter);
            List<ListOption> array = new ArrayList<ListOption>();
            array.add(new ListOption("A4", new Integer(MediaSizeID.EPS_MSID_A4)));
            array.add(new ListOption("A5", new Integer(MediaSizeID.EPS_MSID_A5)));
            array.add(new ListOption("A6", new Integer(MediaSizeID.EPS_MSID_A6)));
            array.add(new ListOption("B5", new Integer(MediaSizeID.EPS_MSID_B5)));
            array.add(new ListOption("六关", new Integer(MediaSizeID.EPS_MSID_8X10)));
            array.add(new ListOption("高清晰度电视", new Integer(MediaSizeID.EPS_MSID_HIVISION)));
            array.add(new ListOption("明信片", new Integer(MediaSizeID.EPS_MSID_POSTCARD)));
            array.add(new ListOption("KG", new Integer(MediaSizeID.EPS_MSID_4X6)));
            array.add(new ListOption("2L尺寸", new Integer(MediaSizeID.EPS_MSID_2L)));
            array.add(new ListOption("L尺寸", new Integer(MediaSizeID.EPS_MSID_L)));
            array.add(new ListOption("卡", new Integer(MediaSizeID.EPS_MSID_CARD_54X86)));
            array.add(new ListOption("密封", new Integer(MediaSizeID.EPS_MSID_POSTCARD)));
            int index = 0;
            for (int i = 0; i < array.size(); i++) {
                if (parameter.getPaperSize() == ((int) array.get(i).value)) {
                    index = i;
                    break;
                }
            }
            bindOptions(R.id.SettingSpinnerPaperSize, array, index, PrintSettingsActivity.this);
        }
    }

    class PaperTypeValue extends ListOptionValue {
        public PaperTypeValue(PrinterSettingsUtil.PrintSettingParameter parameter) {
            super(parameter);
            List<ListOption> array = new ArrayList<ListOption>();
            array.add(new ListOption("普通纸", new Integer(MediaTypeID.EPS_MTID_PLAIN)));
            array.add(new ListOption("照片纸CRISPIA", new Integer(MediaTypeID.EPS_MTID_PLATINA)));
            array.add(new ListOption("照片纸", new Integer(MediaTypeID.EPS_MTID_PGPHOTO)));
            array.add(new ListOption("照片纸入口", new Integer(MediaTypeID.EPS_MTID_GLOSSYPHOTO)));
            array.add(new ListOption("光泽照片纸", new Integer(MediaTypeID.EPS_MTID_GLOSSYCAST)));
            array.add(new ListOption("亚光相纸", new Integer(MediaTypeID.EPS_MTID_MATTE)));
            array.add(new ListOption("超精细纸", new Integer(MediaTypeID.EPS_MTID_PHOTOINKJET)));
            array.add(new ListOption("明信片", new Integer(MediaTypeID.EPS_MTID_HAGAKIRECL)));
            array.add(new ListOption("邮件IJ/明信片", new Integer(MediaTypeID.EPS_MTID_HAGAKIINKJET)));
            array.add(new ListOption("邮件光泽明信片", new Integer(MediaTypeID.EPS_MTID_GLOSSYHAGAKI)));
            int index = 0;
            for (int i = 0; i < array.size(); i++) {
                if (parameter.getPaperType() == ((int) array.get(i).value)) {
                    index = i;
                    break;
                }
            }
            bindOptions(R.id.SettingSpinnerPaperType, array, index, PrintSettingsActivity.this);
        }
    }

    class PrintQualityValue extends ListOptionValue {
        public PrintQualityValue(PrinterSettingsUtil.PrintSettingParameter parameter) {
            super(parameter);
            List<ListOption> array = new ArrayList<ListOption>();
            array.add(new ListOption("快", new Integer(PrintQuality.EPS_MQID_DRAFT)));
            array.add(new ListOption("普通", new Integer(PrintQuality.EPS_MQID_NORMAL)));
            array.add(new ListOption("美丽", new Integer(PrintQuality.EPS_MQID_HIGH)));
            int index = 0;
            for (int i = 0; i < array.size(); i++) {
                if (parameter.getPrintQuality() == ((int) array.get(i).value)) {
                    index = i;
                    break;
                }
            }
            bindOptions(R.id.SettingSpinnerQuality, array,index, PrintSettingsActivity.this);
        }
    }

    class ColorModeValue extends ListOptionValue {
        public ColorModeValue(PrinterSettingsUtil.PrintSettingParameter parameter) {
            super(parameter);
            List<ListOption> array = new ArrayList<ListOption>();
            array.add(new ListOption("颜色", new Integer(EPSetting.COLOR_MODE_COLOR)));
            array.add(new ListOption("单色", new Integer(EPSetting.COLOR_MODE_MONOCHROME)));
            int index = 0;
            for (int i = 0; i < array.size(); i++) {
                if (parameter.getColorMode() == ((int) array.get(i).value)) {
                    index = i;
                    break;
                }
            }
            bindOptions(R.id.SettingSpinnerColorMode, array, index, PrintSettingsActivity.this);
        }
    }

    class PaperPathValue extends ListOptionValue {
        public PaperPathValue(PrinterSettingsUtil.PrintSettingParameter parameter) {
            super(parameter);
            List<ListOption> array = new ArrayList<ListOption>();
            array.add(new ListOption("自动选择", new Integer(EPSetting.PAPER_SOURCE_NOT_SPEC)));
            int index = 0;
            for (int i = 0; i < array.size(); i++) {
                if (parameter.getPaperPath() == ((int) array.get(i).value)) {
                    index = i;
                    break;
                }
            }
            bindOptions(R.id.SettingSpinnerPaperPath, array, index, PrintSettingsActivity.this);
        }
    }

    class DuplexPrintValue extends ListOptionValue {
        public DuplexPrintValue(PrinterSettingsUtil.PrintSettingParameter parameter) {
            super(parameter);
            List<ListOption> array = new ArrayList<ListOption>();
            array.add(new ListOption("不", new Boolean(false)));
            array.add(new ListOption("使", new Boolean(true)));
            int index = 0;
            for (int i = 0; i < array.size(); i++) {
                if (parameter.isDuplexPrint() == ((boolean) array.get(i).value)) {
                    index = i;
                    break;
                }
            }
            bindOptions(R.id.SettingSpinnerDuplexPrint, array, index, PrintSettingsActivity.this);
        }
    }

    class BidPrintValue extends ListOptionValue {
        public BidPrintValue(PrinterSettingsUtil.PrintSettingParameter parameter) {
            super(parameter);
            List<ListOption> array = new ArrayList<ListOption>();
            array.add(new ListOption("使", new Integer(EPSetting.PRINT_DIR_BI)));
            array.add(new ListOption("不", new Integer(EPSetting.PRINT_DIR_UNI)));
            int index = 0;
            for (int i = 0; i < array.size(); i++) {
                if (parameter.getBidPrint() == ((int) array.get(i).value)) {
                    index = i;
                    break;
                }
            }
            bindOptions(R.id.SettingSpinnerBidPrint, array, index, PrintSettingsActivity.this);
        }
    }

    class TotalPagesValue extends NumberOptionValue {
        public TotalPagesValue(PrinterSettingsUtil.PrintSettingParameter parameter) {
            NumberOption option = new NumberOption("份数", parameter.getTotalPages(), 2, 99);
            bindOption(R.id.SettingEditTotalPages, option);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_settings);
        ButterKnife.bind(this);

        optionValueMap.clear();
        PrinterSettingsUtil.PrintSettingParameter parameter = PrinterSettingsUtil.restoreParameter(this);

        optionValueMap.put(KeyPaperSize, new PaperSizeValue(parameter));
        optionValueMap.put(KeyPaperType, new PaperTypeValue(parameter));
        optionValueMap.put(KeyPrintQuality, new PrintQualityValue(parameter));
        optionValueMap.put(KeyColorMode, new ColorModeValue(parameter));
        optionValueMap.put(KeyBorderless, new BorderlessValue(parameter));
        optionValueMap.put(KeyBidPrint, new BidPrintValue(parameter));
        optionValueMap.put(KeyPaperPath, new PaperPathValue(parameter));
        optionValueMap.put(KeyDuplexPrint, new DuplexPrintValue(parameter));
        optionValueMap.put(KeyTotalPages, new TotalPagesValue(parameter));
        RxView.clicks(mSaveBtn)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        saveSettings();
                    }
                });
    }

    private void saveSettings() {
        int mediaSize = getIntValueOf(KeyPaperSize);
        int mediaType = getIntValueOf(KeyPaperType);
        int printQuality = getIntValueOf(KeyPrintQuality);
        int colorMode = getIntValueOf(KeyColorMode);
        int bidPrint = getIntValueOf(KeyBidPrint);
        int paperPath = getIntValueOf(KeyPaperPath);
        boolean borderLess = getBooleanValueOf(KeyBorderless);
        boolean duplexPrint = getBooleanValueOf(KeyDuplexPrint);
        int totalPages = getIntValueOf(KeyTotalPages);


        PrinterSettingsUtil.PrintSettingParameter parameter = new PrinterSettingsUtil.PrintSettingParameter();
        parameter.setPaperSize(mediaSize);
        parameter.setPaperType(mediaType);
        parameter.setPrintQuality(printQuality);
        parameter.setColorMode(colorMode);
        parameter.setBidPrint(bidPrint);
        parameter.setPaperPath(paperPath);
        parameter.setBorderless(borderLess);
        parameter.setDuplexPrint(duplexPrint);
        parameter.setTotalPages(totalPages);
        PrinterSettingsUtil.saveParameter(this, parameter);
        setResult(RESULT_OK);
        finish();
        Toast.makeText(this, R.string.toast_save_settings_success, Toast.LENGTH_SHORT).show();
    }

    /**
     * 启动设置打印机界面
     * @param context
     */
    public static void start(Context context) {
        Intent starter = new Intent(context, PrintSettingsActivity.class);
        context.startActivity(starter);
    }
}
