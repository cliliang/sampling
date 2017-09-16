package com.cdv.sampling.bean;

import android.view.View;

import java.io.File;

public class FormView implements Comparable<FormView>{

    private View formView;
    private int order;
    private File formImageFile;

    public FormView(View formView, int order) {
        this.formView = formView;
        this.order = order;
    }

    public View getFormView() {
        return formView;
    }

    public void setFormView(View formView) {
        this.formView = formView;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public File getFormImageFile() {
        return formImageFile;
    }

    public void setFormImageFile(File formImageFile) {
        this.formImageFile = formImageFile;
    }

    @Override
    public int compareTo(FormView formView) {
        return order - formView.getOrder();
    }
}
