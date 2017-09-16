package com.cdv.sampling.constants;

import com.cdv.sampling.SamplingApplication;
import com.cdv.sampling.utils.ScreenUtils;

public final class Constants {

    public static final String[] YANG_PIN_LEI_XING = {"生产", "批发", "零售", "样品（赠品）"};
    public static final String[] YANG_PIN_MING = {"鸡蛋", "鸡肉", "牛肉", "生鲜乳", "乌鸡肉", "鸭肉", "羊肉", "猪肝", "猪肉", "猪毛", "尿液"};
    public static final String[] JIN_HUO_FANG_SHI = {"生产", "批发", "零售", "样品（赠品）"};

    public static final String FILE_ID_SEPARATOR = ";";

    public static final String SAMPLING_DRUG = "残留抽样";
    public static final String SAMPLING_VERIFICATION = "样品核实采样";
    public static final String SAMPLING_QUALITY = "兽药抽样";


    public static final String TYPE_BEIJIANREN_QIANZI = "0";
    public static final String TYPE_BEIJIANREN_QIANZI_2 = "1";
    public static final String TYPE_CAIYANGREN_QIANZI = "2";
    public static final String TYPE_CAIYANGREN_QIANZI_2 = "3";
    public static final String TYPE_FORM_IMAGE = "4";

    public static final int FORM_IMAGE_WIDTH = ScreenUtils.dpToPxInt(SamplingApplication.getInstance(), 1754);
    public static final int FORM_IMAGE_HEIGHT = ScreenUtils.dpToPxInt(SamplingApplication.getInstance(), 1240);

    public static final int A4_HEIGHT = 794 * 2;
    public static final int A4_WIDTH = 1123 * 2;

}
