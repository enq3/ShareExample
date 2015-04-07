package ru.enq3dev.shareexample.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import ru.enq3dev.shareexample.R;
import ru.enq3dev.shareexample.core.Constants;

public class DummyPagerFragment extends Fragment {

    int pageNumber;
    private String url;
    private ImageView image;
    private ProgressBar progress;

    public static DummyPagerFragment newInstance(int pageNum, String url) {

        DummyPagerFragment pageFragment = new DummyPagerFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(Constants.ARGUMENT_PAGE_NUMBER, pageNum);
        arguments.putString(Constants.ARGUMENT_IMAGE_URL, url);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);

        image = (ImageView) view.findViewById(R.id.image);
        progress = (ProgressBar) view.findViewById(R.id.progress);

        ImageLoader.getInstance().displayImage(url, image, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                image.setImageDrawable(null);
                progress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                progress.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                progress.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                progress.setVisibility(View.GONE);
            }
        });
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dummy_pager, null);
    }

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(Constants.ARGUMENT_PAGE_NUMBER);
        url = getArguments().getString(Constants.ARGUMENT_IMAGE_URL);

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

}
