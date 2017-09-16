package com.cdv.sampling.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cdv.sampling.R;
import com.cdv.sampling.activity.BaseActivity;
import com.cdv.sampling.activity.ScanActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputLayout extends RelativeLayout {

    private EditText etInput;
    private TextView tvLeft;
    private ImageView ivClear;
    private ImageView ivScan;

    private String validator;
    private boolean scanEnable;
    private String presetText;

    public InputLayout(Context context) {
        super(context);
        init(context, null);
    }

    public InputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public InputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_input_cell, this, true);
        tvLeft = (TextView) findViewById(R.id.tv_left);
        ivClear = (ImageView) findViewById(R.id.iv_clear);
        ivScan = (ImageView) findViewById(R.id.iv_scan);
        etInput = (EditText) findViewById(R.id.et_content);
        etInput.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean isShowClear = findFocus() == etInput && isEnabled() && s.length() > 0;
                ivClear.setVisibility(isShowClear ? VISIBLE : INVISIBLE);
                if (!isShowClear && canShowCan()) {
                    ivScan.setVisibility(VISIBLE);
                } else {
                    ivScan.setVisibility(GONE);
                }
            }
        });
        ivClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                etInput.setText("");
            }
        });
        if (attrs == null) {
            return;
        }
        TypedArray type = context.getTheme().obtainStyledAttributes(attrs, R.styleable.InputLayout, 0, 0);
        int maxLength = type.getInteger(R.styleable.InputLayout_maxLength, 20);
        String hint = type.getString(R.styleable.InputLayout_textHint);
        presetText = type.getString(R.styleable.InputLayout_inputText);
        int inputType = type.getInt(R.styleable.InputLayout_cdv_inputType, -1);
        int maxLines = type.getInteger(R.styleable.InputLayout_inputMaxLine, 1);
        final String leftTitle = type.getString(R.styleable.InputLayout_left_title);
        validator = type.getString(R.styleable.InputLayout_validator);
        scanEnable = type.getBoolean(R.styleable.InputLayout_scanEnable, false);
        etInput.setText(presetText);
        tvLeft.setText(leftTitle);
        etInput.setMaxLines(maxLines);
        etInput.setSingleLine(maxLines <= 1);
        if (inputType > 0) {
            switch (inputType) {
                case 0:
                    etInput.setInputType(InputType.TYPE_CLASS_TEXT);
                    break;
                case 1:
                    etInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;
                case 2:
                    etInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    break;
                case 3:
                    etInput.setInputType(InputType.TYPE_CLASS_PHONE);
                    break;
            }
        }
        ivClear.setVisibility(INVISIBLE);
        if (scanEnable) {
            ivScan.setVisibility(VISIBLE);
        }
        etInput.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                boolean isShowClear = focus && isEnabled() && !TextUtils.isEmpty(etInput.getText().toString());
                ivClear.setVisibility(isShowClear ? VISIBLE : INVISIBLE);
                if (!isShowClear && scanEnable && canShowCan()) {
                    ivScan.setVisibility(VISIBLE);
                } else {
                    ivScan.setVisibility(GONE);
                }
            }
        });
        ivScan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity activity = (BaseActivity) getContext();
                final int finalRequestCode = 1000;
                activity.startActivityForResult(new Intent(activity, ScanActivity.class), finalRequestCode, new PreferenceManager.OnActivityResultListener(){

                    @Override
                    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
                        if (finalRequestCode == requestCode && resultCode == Activity.RESULT_OK){
                            String str = data.getStringExtra(ScanActivity.EXTRA_SCAN_RESULT);
                            if (str != null){
                                str = str.replace("\n", "").trim();
                            }
                            if (tvLeft.getText().toString().equals("批准文号")){
                                String[] param = str.split("，");
                                if (param.length >= 3){
                                    setContent(param[2]);
                                }
                            }else{
                                setContent(str);
                            }
                            return true;
                        }
                        return false;
                    }
                });
            }
        });

        etInput.setHint(hint);
        etInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        etInput.setEnabled(enabled);
        if (!enabled) {
            ivClear.setVisibility(INVISIBLE);
        }
    }

    public boolean validate() {
        if (TextUtils.isEmpty(validator)) {
            return true;
        }
        Pattern pattern = Pattern.compile(validator);
        Matcher isNum = pattern.matcher(etInput.getText().toString());
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public void setContent(String content) {
        if (TextUtils.isEmpty(content)){
            etInput.setText(presetText);
            return;
        }else{
            etInput.setText(content);
        }
        etInput.setSelection(etInput.getText().length());
    }

    public String getContent() {
        if (etInput.getText().toString().equals(presetText)){
            return "";
        }
        return etInput.getText().toString();
    }

    public EditText getEtInput() {
        return etInput;
    }

    public ImageView getIvClear() {
        return ivClear;
    }

    public String getTitle(){
        return tvLeft.getText().toString();
    }

    private boolean canShowCan(){
        if (!scanEnable){
            return false;
        }
        return TextUtils.isEmpty(etInput.getText().toString()) || etInput.getText().toString().equals(presetText);
    }
}
