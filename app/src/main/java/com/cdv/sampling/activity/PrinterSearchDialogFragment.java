package com.cdv.sampling.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cdv.sampling.R;
import com.epson.isv.eprinterdriver.Common.EpsPrinter;
import com.epson.isv.eprinterdriver.Ctrl.EPrintManager;
import com.epson.isv.eprinterdriver.Ctrl.ISearchPrinterListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PrinterSearchDialogFragment extends DialogFragment implements ISearchPrinterListener {
    final static String ARG_TIMEOUT = "PrinterSearch.Timeout";
    final static String PRINTER = "PrinterSearch.Printer";
    public static final int DEFAULT_TIMEOUT = 69; // UNIT:second

    EPrintManager epManager;
    ArrayAdapter<String> adapter;
    ArrayList<EpsPrinter> printers;
    @BindView(R.id.loading_progress)
    ProgressBar mLoadingProgress;
    @BindView(R.id.printer_list)
    ListView mPrinterList;
    private Unbinder mUnbinder;
    private int mTimeout;
    private OnSelectPrinterListener mListener;

    public PrinterSearchDialogFragment(){

    }

    public static PrinterSearchDialogFragment newInstance(int timeout) {
        PrinterSearchDialogFragment fragment = new PrinterSearchDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TIMEOUT, timeout);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mTimeout = arguments.getInt(ARG_TIMEOUT, DEFAULT_TIMEOUT);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSelectPrinterListener) {
            mListener = (OnSelectPrinterListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSelectPrinterListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Dialog dialog = getDialog();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        View rootView = inflater.inflate(R.layout.print_search, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        epManager = EPrintManager.instance();
        epManager.addSearchListener(this);
        epManager.findPrinter(mTimeout);

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        printers = new ArrayList<>();

        mPrinterList.setAdapter(adapter);
        mPrinterList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                epManager.cancelFindPrinter();
                mListener.onSelectPrinter(printers.get(position));
                dismiss();
            }
        });
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        epManager.cancelFindPrinter();
        mLoadingProgress.setVisibility(View.GONE);
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
        Toast.makeText(getContext(), "検索中...", Toast.LENGTH_SHORT).show();
        mLoadingProgress.setVisibility(View.VISIBLE);
    }

    /**
     * ISearchPrinterListenerインターフェイスのプリンター検索完了通知に対応する処理の実装
     */
    @Override
    public void onSearchFinished(int factor) {
        Context context = getContext();
        if (context != null) {
            Toast.makeText(context, "検索終了", Toast.LENGTH_SHORT).show();
            mLoadingProgress.setVisibility(View.GONE);
        }
    }

    public interface OnSelectPrinterListener {
        void onSelectPrinter(EpsPrinter printer);
    }
}
