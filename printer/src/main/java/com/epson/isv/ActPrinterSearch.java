package com.epson.isv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.epson.isv.eprinterdriver.Common.EpsPrinter;
import com.epson.isv.eprinterdriver.Ctrl.EPrintManager;
import com.epson.isv.eprinterdriver.Ctrl.ISearchPrinterListener;

import java.util.ArrayList;

public class ActPrinterSearch extends Activity implements ISearchPrinterListener {
    public final static String TIMEOUT = "ActPrinterSearch.Timeout";
    public final static String PRINTER = "ActPrinterSearch.Printer";

    EPrintManager epManager;
    ArrayAdapter<String> adapter;
    ArrayList<EpsPrinter> printers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.print_search);

        int timeout = getIntent().getIntExtra(TIMEOUT, 30);
        epManager = EPrintManager.instance();
        epManager.addSearchListener(ActPrinterSearch.this);
        epManager.findPrinter(timeout);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        printers = new ArrayList<EpsPrinter>();

        ListView listView = (ListView) findViewById(R.id.printer_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent data = new Intent();
                data.putExtra(PRINTER, printers.get(position));
                setResult(RESULT_OK, data);
                epManager.cancelFindPrinter();
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_CANCELED, null);
            epManager.cancelFindPrinter();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * ISearchPrinterListenerインターフェイスのプリンター検知通知に対応する処理の実装
     */
    @Override
    public void onFindPrinter(EpsPrinter printer) {
        adapter.add(printer.getModelName() + " [" + printer.getLocation() + "]");
        printers.add(printer);
    }

    /**
     * ISearchPrinterListenerインターフェイスのプリンター検索開始通知に対応する処理の実装
     */
    @Override
    public void onSearchBegin() {
        Toast.makeText(ActPrinterSearch.this, "検索中...", Toast.LENGTH_SHORT).show();
    }

    /**
     * ISearchPrinterListenerインターフェイスのプリンター検索完了通知に対応する処理の実装
     */
    @Override
    public void onSearchFinished(int factor) {
        Toast.makeText(ActPrinterSearch.this, "検索終了", Toast.LENGTH_SHORT).show();
    }
}
