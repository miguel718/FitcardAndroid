package finternet.fitCard.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class AdapterHomePage extends PagerAdapter {
    private final ArrayList<View> imgList;

    public AdapterHomePage(ArrayList<View> imgList) {
        this.imgList = imgList;
    }

    @Override
    public int getCount() {
        return imgList != null && !imgList.isEmpty() ? Integer.MAX_VALUE : 0;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(imgList.get(position % imgList.size()));
        return imgList.get(position % imgList.size());
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(imgList.get(position % imgList.size()));
    }
}
