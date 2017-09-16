package com.cdv.sampling.utils;

import android.text.TextUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinYinUtil {


	private static HanyuPinyinOutputFormat format;


	public static String getPinYin(String input) {
		return ChineseToHanYuPinYin(input).toLowerCase();
	}

	public static String ChineseToHanYuPinYin(String content) {
		char[] charArray = content.toCharArray();

		StringBuffer sb = new StringBuffer();
		for (char c : charArray) {
			if (isChinese(c)) {
				try {
					sb.append(PinyinHelper.toHanyuPinyinStringArray(c, getHanyuPinyinOutputFormatInstance())[0]);
				} catch (Exception e) {
					sb.append(c);
				}
			} else {
				sb.append(c);
			}
		}

		return sb.toString();

	}

	public static String getSearchKeyForPinyin(String chinese){
		if (TextUtils.isEmpty(chinese)){
			return "";
		}
		String jianpin = "";
		String quanpin = "";
		for (int i = 0; i < chinese.length(); i ++){
			String pinyin = ChineseToHanYuPinYin(String.valueOf(chinese.charAt(i)));
			if (!TextUtils.isEmpty(pinyin)){
				quanpin += pinyin;
				jianpin += pinyin.charAt(0);
			}
		}
		return quanpin + "|" + jianpin;
	}

	public static HanyuPinyinOutputFormat getHanyuPinyinOutputFormatInstance() {
		if (format == null) {
			format = new HanyuPinyinOutputFormat();

			format.setCaseType(HanyuPinyinCaseType.LOWERCASE);

			format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

			format.setVCharType(HanyuPinyinVCharType.WITH_V);
		}
		return format;
	}

	public static boolean isChinese(char c) {
		boolean result = false;
		if (c >= 19968 && c <= 171941) {
			result = true;
		}
		return result;
	}

	/**
	 * 转换字符串中汉字为拼音,除汉字外的原样输出,可添加分隔符
	 * @param cnText
	 */
	public static String covertCnTextToPinyin(String cnText, String delimiter) {

		HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
		outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		outputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
		outputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);


		String resultText = "";
		boolean findEnText = false;
		if (cnText != null && cnText.length() > 0) {

			for (int i = 0; i < cnText.length(); i++) {
				String[] pinyinArray = null;
				try {
					char cnTextChar = cnText.charAt(i);
					pinyinArray = PinyinHelper.toHanyuPinyinStringArray(cnTextChar, outputFormat);
					if (pinyinArray == null) {
						pinyinArray = new String[]{String.valueOf(cnTextChar)};
						String outputString = concatPinyinStringArray(pinyinArray);
						resultText = resultText + outputString;
						findEnText = true;
					} else {
						String outputString = concatPinyinStringArray(pinyinArray);
						if (findEnText) {
							resultText = resultText + delimiter + outputString + delimiter;
							findEnText = false;
						} else {
							resultText = resultText + outputString + delimiter;
						}
					}
				} catch (BadHanyuPinyinOutputFormatCombination e1) {
					e1.printStackTrace();
				}

			}


		}

		return "'" + resultText;

	}

	private static String concatPinyinStringArray(String[] pinyinArray)
	{
		StringBuffer pinyinStrBuf = new StringBuffer();

		if ((null != pinyinArray) && (pinyinArray.length > 0))
		{
			for (int i = 0; i < pinyinArray.length; i++)
			{
				pinyinStrBuf.append(pinyinArray[i]);
				break;
			}
		}
		String outputString = pinyinStrBuf.toString();

		return outputString;
	}
}