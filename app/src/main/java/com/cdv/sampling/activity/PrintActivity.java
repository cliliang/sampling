package com.cdv.sampling.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.cdv.sampling.R;
import com.cdv.sampling.utils.PrinterSettingsUtil;
import com.epson.isv.ActPrintListener;
import com.epson.isv.eprinterdriver.Common.EpsPrinter;
import com.epson.isv.eprinterdriver.Ctrl.EPSetting;
import com.epson.isv.eprinterdriver.Ctrl.EPrintManager;
import com.epson.isv.eprinterdriver.Ctrl.PageAttribute;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import butterknife.ButterKnife;

public class PrintActivity extends AppCompatActivity implements PrinterSearchDialogFragment.OnSelectPrinterListener {

    static {
        System.loadLibrary("jpeg-8c");
    }

    private static final String EXTRA_IMAGE = "image";
    private EPrintManager epManager;
    private EpsPrinter epPrinter;

    public static final int OP_PRINT = 1;


    private PrinterSearchDialogFragment mSearchDialog;

    @Override
    public void onSelectPrinter(EpsPrinter printer) {
        if (mSearchDialog != null){
            mSearchDialog.dismiss();
        }
        epPrinter = printer;
        print();
    }


    ArrayList<String> imagePathList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initEpsSdk();
        imagePathList = getIntent().getStringArrayListExtra(EXTRA_IMAGE);
        tryPrint();
    }

    private void tryPrint() {
        if (epPrinter == null) {
            searchPrinter();
        } else {
            print();
        }
    }

    private void searchPrinter() {
        mSearchDialog = PrinterSearchDialogFragment.newInstance(PrinterSearchDialogFragment.DEFAULT_TIMEOUT);
        mSearchDialog.show(getSupportFragmentManager(), mSearchDialog.getClass().getSimpleName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSearchDialog != null) {
            mSearchDialog.dismiss();
            mSearchDialog = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void print() {
        EPSetting.instance().setSelEpsPrinter(epPrinter);
        //印刷Busyかをチェック
        if (EPrintManager.instance().isPrintBusy()) {
            new AlertDialog.Builder(PrintActivity.this)
                    .setTitle(R.string.app_name)
                    .setMessage(R.string.print_setting_printstatus_busy)
                    .setPositiveButton(R.string.print_setting_printstatus_dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .create().show();
            return;
        }

        EPSetting setting = EPSetting.instance();
        PrinterSettingsUtil.PrintSettingParameter parameter = PrinterSettingsUtil.restoreParameter(this);
        int mediaSize = parameter.getPaperSize();
        int mediaType = parameter.getPaperType();
        int printQuality = parameter.getPrintQuality();
        int colorMode = parameter.getColorMode();
        PageAttribute epPageAttri = new PageAttribute(mediaSize, mediaType, printQuality);
        setting.setSelPageAttri(epPageAttri);
        setting.setPrintDirection(parameter.getBidPrint());
        setting.setColorMode(colorMode);
        setting.setPaperSource(parameter.getPaperPath());
        setting.setBorderless(parameter.isBorderless());
        setting.setDuplexPrint(parameter.isDuplexPrint());
        setting.setTotalPages(parameter.getTotalPages());
        setting.setTemporaryImageFilePath(getCacheDir().getAbsolutePath() + "/temp.jpg");

        Intent status = new Intent();
        status.setClass(this, ActPrintListener.class);
        status.putExtra(ActPrintListener.PATH_SELECTED_IMAGE, imagePathList);
        status.putExtra(ActPrintListener.OPCODE, OP_PRINT);
        startActivity(status);
        finish();
    }

    private void initEpsSdk() {
        String libpath = getFilesDir().getParent() + File.separator + "lib";
        String libname = "libeprinterdriver.so";
        epManager = EPrintManager.instance();
        epManager.initEscprLib(getApplicationContext(), libpath, libname);
    }

    /**
     * 调动打印模块开始打印
     *
     * @param context   调用打印模块需要的上下文环境
     * @param imagePathList 被打印的图片的本地路径
     * @throws FileNotFoundException
     */
    public static void start(Context context, ArrayList<String> imagePathList) {
        Intent starter = new Intent(context, PrintActivity.class);
        starter.putStringArrayListExtra(EXTRA_IMAGE, imagePathList);
        context.startActivity(starter);
    }
}
