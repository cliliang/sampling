package com.cdv.sampling.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cdv.sampling.R;
import com.cdv.sampling.fragments.ImagePreviewFragment;
import com.cdv.sampling.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

public class ImageShowActivity extends BaseActivity {

    private static final String EXTRA_IMAGE_URL = "imageUrl";
    private static final String EXTRA_INDEX = "imageIndex";

    private List<String> imageUrlList;
    private ViewPager viewPager;
    private ImageView ivClose;
    private TextView tvTitle;

    private int index;

    public static Intent getStartIntent(Context context, ArrayList<String> imageArrList, int index) {
        Intent intent = new Intent(context, ImageShowActivity.class);
        intent.putStringArrayListExtra(EXTRA_IMAGE_URL, imageArrList);
        intent.putExtra(EXTRA_INDEX, index);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scale_image_show);
        hideToolbar();

        index = getIntent().getIntExtra(EXTRA_INDEX, 0);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        ivClose = (ImageView) findViewById(R.id.iv_close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvTitle = (TextView) findViewById(R.id.tv_image_index);

        imageUrlList = getIntent().getStringArrayListExtra(EXTRA_IMAGE_URL);
        if (ListUtils.isEmpty(imageUrlList)) {
            finish();
            return;
        }

        if (index > imageUrlList.size() || index < 0){
            index = 0;
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tvTitle.setText((position + 1) + "/" + imageUrlList.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment fragment = ImagePreviewFragment.newInstance(imageUrlList.get(position));
                return fragment;
            }

            @Override
            public int getCount() {
                return imageUrlList.size();
            }
        });
    }

}
