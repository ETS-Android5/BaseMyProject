package cheerly.mybaseproject.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cheerly.mybaseproject.R;
import cheerly.mybaseproject.utils.BaseUtils;
import cheerly.mybaseproject.utils.SmartImageLoader;

public class AutoGalleryBannerView extends RelativeLayout implements LifecycleObserver {
    private ViewPager mViewPager;
    private List<BannerDataItem> mDataList = new ArrayList<>();
    private BannerAdapter mAdapter;
    private Timer mTimer;
    private boolean isFinish = false;
    private boolean isAutoPlay = true;

    public AutoGalleryBannerView(Context context) {
        super(context);
        init();
    }

    public AutoGalleryBannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setDataList(List<BannerDataItem> dataList) {
        mDataList = dataList;
    }

    private void init() {
        AutoGalleryBannerView.this.setClipChildren(false);
        mViewPager = new ViewPager(getContext());
        mViewPager.setPageMargin(-BaseUtils.dip2px(24f));
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setPageTransformer(false, new ScaleTransformer());

        mViewPager.setClipChildren(false);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, -2);
        params.leftMargin = BaseUtils.dip2px(20f);
        params.rightMargin = BaseUtils.dip2px(20f);
        addView(mViewPager, params);

        mAdapter = new BannerAdapter(getContext());
        mViewPager.setAdapter(mAdapter);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    protected void onCreate() {
        isFinish = false;
        startTimer(new Runnable() {
            @Override
            public void run() {
                if (isAutoPlay) {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                }
            }
        });
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        isFinish = true;
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        isAutoPlay = false;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        isAutoPlay = true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_UP
                || action == MotionEvent.ACTION_CANCEL
                || action == MotionEvent.ACTION_OUTSIDE) {
            isAutoPlay = true;
        } else if (action == MotionEvent.ACTION_DOWN) {
            isAutoPlay = false;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void startTimer(final Runnable runnable) {
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (!isFinish) {
                    BaseUtils.getHandler().post(runnable);
                } else {
                    cancel();
                    mTimer.cancel();
                    mTimer = null;
                }
            }
        }, 5 * 1000, 5 * 1000);
    }

    private class BannerAdapter extends PagerAdapter {
        private Context mContext;

        private BannerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.auto_gallery_banner_item, null);
            int newPos = (position - 1) % (mDataList.size());
            ImageView img = view.findViewById(R.id.image);
            ImageView childImg = view.findViewById(R.id.child_img);
            SmartImageLoader.load(img, mDataList.get(newPos).url, -1, -1, 0);
            setImageBitmap(childImg, mDataList.get(newPos).childUrl);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    private void setImageBitmap(final ImageView img, String url) {
        Glide.with(getContext())
                .asBitmap()
                .load(url)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NotNull Bitmap resource, Transition<? super Bitmap> transition) {
                        img.setImageBitmap(resource);
                    }
                });
    }

    public static final class BannerDataItem {
        public String url;
        public String childUrl;
    }


    private final class ScaleTransformer implements ViewPager.PageTransformer {
        private final float MINSCALE = 0.8f;//最小缩放值

        /**
         * position取值特点：
         * 假设页面从0～1，则：
         * 第一个页面position变化为[0,-1]
         * 第二个页面position变化为[1,0]
         *
         * @param view
         * @param v
         */
        @Override
        public void transformPage(@NonNull View view, float v) {
            View childImg = view.findViewById(R.id.child_img);
            float scale;


            if (v > 1 || v < -1) {
                scale = MINSCALE;
            } else if (v < 0) {
                scale = MINSCALE + (1 + v) * (1 - MINSCALE);
            } else {
                scale = MINSCALE + (1 - v) * (1 - MINSCALE);
            }
            view.setScaleY(scale);
            view.setScaleX(scale);
            childImg.setScaleY(scale);
            childImg.setScaleX(scale);
        }
    }
}
