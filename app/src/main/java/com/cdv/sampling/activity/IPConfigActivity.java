package com.cdv.sampling.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.cdv.sampling.R;
import com.cdv.sampling.constants.StorageConstants;
import com.cdv.sampling.net.HttpService;
import com.cdv.sampling.storage.SamplingStorage;
import com.cdv.sampling.utils.ToastUtils;

import butterknife.BindView;

public class IPConfigActivity extends BaseActivity {

    @BindView(R.id.input_ip)
    EditText inputIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipconfig);
        inputIp.setText(getIpConfig());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save){
            if (!TextUtils.isEmpty(inputIp.getText()) && !inputIp.getText().equals(getIpConfig())){
                SamplingStorage.getInstance().storeStringValue(StorageConstants.KEY_IP_CONFIG, inputIp.getText().toString());
                ToastUtils.show(this, "设置IP成功！重启后生效");
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getIpConfig(){
        String ipConfig = SamplingStorage.getInstance().getStringValue(StorageConstants.KEY_IP_CONFIG, HttpService.DEFAULT_HOST_URL);
        return ipConfig;
    }
}
