package com.cdv.sampling.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cdv.sampling.R;
import com.cdv.sampling.image.ImageLoaderUtils;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImagePreviewFragment extends Fragment {

    private static final String EXTRA_IMAGE_URL = "EXTRA_IMAGE_URL";

    private String imageUrl;

    public static ImagePreviewFragment newInstance(String imageUrl) {
        ImagePreviewFragment fragment = new ImagePreviewFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_IMAGE_URL, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageUrl = getArguments().getString(EXTRA_IMAGE_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View containerView = inflater.inflate(R.layout.fragment_image_preview, container, false);
        PhotoView photoView = (PhotoView) containerView.findViewById(R.id.photoView);
        if (!imageUrl.toLowerCase().startsWith("http") && !imageUrl.toLowerCase().startsWith("file")){
            imageUrl = ImageDownloader.Scheme.FILE.wrap(imageUrl);
        }
        ImageLoaderUtils.displayImageForIv(photoView, imageUrl);

        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                getActivity().finish();
            }
        });
        return containerView;
    }
}
