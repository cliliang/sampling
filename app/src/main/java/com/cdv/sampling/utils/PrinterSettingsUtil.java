package com.cdv.sampling.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.epson.isv.eprinterdriver.Ctrl.EPSetting;
import com.epson.isv.eprinterdriver.Ctrl.PageAttribute.MediaSizeID;
import com.epson.isv.eprinterdriver.Ctrl.PageAttribute.MediaTypeID;
import com.epson.isv.eprinterdriver.Ctrl.PageAttribute.PrintQuality;

import static com.cdv.sampling.utils.PrinterSettingsUtil.PrintSettingParameter.DEFAULT_PAGE_NUM;
import static com.cdv.sampling.utils.PrinterSettingsUtil.PrintSettingParameter.KeyBorderless;
import static com.cdv.sampling.utils.PrinterSettingsUtil.PrintSettingParameter.KeyBidPrint;
import static com.cdv.sampling.utils.PrinterSettingsUtil.PrintSettingParameter.KeyColorMode;
import static com.cdv.sampling.utils.PrinterSettingsUtil.PrintSettingParameter.KeyDuplexPrint;
import static com.cdv.sampling.utils.PrinterSettingsUtil.PrintSettingParameter.KeyPaperPath;
import static com.cdv.sampling.utils.PrinterSettingsUtil.PrintSettingParameter.KeyPaperSize;
import static com.cdv.sampling.utils.PrinterSettingsUtil.PrintSettingParameter.KeyPaperType;
import static com.cdv.sampling.utils.PrinterSettingsUtil.PrintSettingParameter.KeyPrintQuality;
import static com.cdv.sampling.utils.PrinterSettingsUtil.PrintSettingParameter.KeyTotalPages;

/**
 * Created by xiao on 1/14/17.
 */

public class PrinterSettingsUtil {

    public static final String PARA_FILE_NAME = PrintSettingParameter.class.getSimpleName();

    public static void saveParameter(Context context, PrintSettingParameter parameter) {
        SharedPreferences preferences = context.getSharedPreferences(PARA_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PrintSettingParameter.KeyPaperSize, parameter.getPaperSize());
        editor.putInt(PrintSettingParameter.KeyPaperType, parameter.getPaperType());
        editor.putInt(PrintSettingParameter.KeyPrintQuality, parameter.getPrintQuality());
        editor.putInt(PrintSettingParameter.KeyColorMode, parameter.getColorMode());
        editor.putBoolean(KeyBorderless, parameter.isBorderless());
        editor.putInt(PrintSettingParameter.KeyPaperPath, parameter.getPaperPath());
        editor.putBoolean(PrintSettingParameter.KeyDuplexPrint, parameter.isDuplexPrint());
        editor.putInt(PrintSettingParameter.KeyBidPrint, parameter.getBidPrint());
        editor.putInt(PrintSettingParameter.KeyTotalPages, parameter.getTotalPages());
        editor.apply();
    }

    public static PrintSettingParameter restoreParameter(Context context) {
        PrintSettingParameter parameter = new PrintSettingParameter();
        SharedPreferences sp = context.getSharedPreferences(PARA_FILE_NAME, Context.MODE_PRIVATE);
        parameter.setBorderless(sp.getBoolean(KeyBorderless, false));
        parameter.setPaperSize(sp.getInt(KeyPaperSize, MediaSizeID.EPS_MSID_A4));
        parameter.setPaperType(sp.getInt(KeyPaperType, MediaTypeID.EPS_MTID_PLAIN));
        parameter.setPrintQuality(sp.getInt(KeyPrintQuality, PrintQuality.EPS_MQID_DRAFT));
        parameter.setColorMode(sp.getInt(KeyColorMode, EPSetting.COLOR_MODE_COLOR));
        parameter.setPaperPath(sp.getInt(KeyPaperPath, EPSetting.PAPER_SOURCE_NOT_SPEC));
        parameter.setDuplexPrint(sp.getBoolean(KeyDuplexPrint, false));
        parameter.setBidPrint(sp.getInt(KeyBidPrint, EPSetting.PRINT_DIR_BI));
        parameter.setTotalPages(sp.getInt(KeyTotalPages, DEFAULT_PAGE_NUM));
        return parameter;
    }

    public static class PrintSettingParameter{
        public static final String KeyPaperSize = "KeyPaperSize";
        public static final String KeyPaperType = "KeyPaperType";
        public static final String KeyPrintQuality = "KeyPrintQuality";
        public static final String KeyColorMode = "KeyColorMode";
        public static final String KeyBorderless = "KeyBorderless";
        public static final String KeyPaperPath = "KeyPaperPath";
        public static final String KeyDuplexPrint = "KeyDuplexPrint";
        public static final String KeyBidPrint = "KeyBidPrint";
        public static final String KeyTotalPages = "KeyTotalPages";

        static final int DEFAULT_PAGE_NUM = 1;

        private int paperSize;
        private int paperType;
        private int printQuality;
        private int colorMode;
        private boolean borderless;
        private int paperPath;
        private boolean duplexPrint;
        private int bidPrint;
        private int totalPages;

        public void setPaperSize(int paperSize) {
            this.paperSize = paperSize;
        }

        public void setPaperType(int paperType) {
            this.paperType = paperType;
        }

        public void setPrintQuality(int printQuality) {
            this.printQuality = printQuality;
        }

        public void setColorMode(int colorMode) {
            this.colorMode = colorMode;
        }

        public void setBorderless(boolean borderless) {
            this.borderless = borderless;
        }

        public void setPaperPath(int paperPath) {
            this.paperPath = paperPath;
        }

        public void setDuplexPrint(boolean duplexPrint) {
            this.duplexPrint = duplexPrint;
        }

        public void setBidPrint(int bidPrint) {
            this.bidPrint = bidPrint;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public int getPaperSize() {
            return paperSize;
        }

        public int getPaperType() {
            return paperType;
        }

        public int getPrintQuality() {
            return printQuality;
        }

        public int getColorMode() {
            return colorMode;
        }

        public boolean isBorderless() {
            return borderless;
        }

        public int getPaperPath() {
            return paperPath;
        }

        public boolean isDuplexPrint() {
            return duplexPrint;
        }

        public int getBidPrint() {
            return bidPrint;
        }

        public int getTotalPages() {
            return totalPages;
        }
    }
}
