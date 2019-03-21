package com.ark.adkit.polymers.polymer.wiget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.androidquery.AQuery;
import com.ark.adkit.basics.data.ADMetaData;
import com.ark.adkit.polymers.R;
import com.ark.adkit.polymers.polymer.ADTool;
import com.ark.adkit.polymers.ydt.utils.ScreenUtils;

public class NativeAdView extends FrameLayout {

    private TextView adTitleView, adSubTitleView, tvPlatform;
    private ImageView imageView;
    private ImageView logoView;
    private View rlBottom;
    private ADMetaData adMetaData;
    private int mDownX, mDownY, mUpX, mUpY;
    private Context mContext;

    public NativeAdView(Context context) {
        this(context, null);
    }

    public NativeAdView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NativeAdView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NativeAdView(Context context, @Nullable AttributeSet attrs, int defStyleAttr,
                        int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    /**
     * 更新数据
     *
     * @param adMetas ADMetaData
     */
    public void setData(ADMetaData adMetas) {
        this.adMetaData = adMetas;
        if (adMetaData != null) {
            updateUi(adMetaData);
        }
    }

    /**
     * 初始化布局
     *
     * @param context 上下文
     */
    private void initView(Context context) {
        this.mContext = context;
        inflate(context, R.layout.sdk_item_ad_ratio_match_width, this);
        imageView = findViewById(R.id.ad_app_image);
        logoView = findViewById(R.id.ad_app_logo);
        adTitleView = findViewById(R.id.ad_app_title);
        adSubTitleView = findViewById(R.id.ad_app_desc);
        rlBottom = findViewById(R.id.rl_bottom);
        tvPlatform = findViewById(R.id.ad_platform);
        setOnGenericMotionListener(new OnGenericMotionListener() {
            @Override
            public boolean onGenericMotion(View v, MotionEvent event) {
                return false;
            }
        });
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mDownX = (int) event.getX();
                        mDownY = (int) event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        mUpX = (int) event.getX();
                        mUpY = (int) event.getY();
                        v.performClick();
                        if (adMetaData != null) {
                            adMetaData.handleClick(NativeAdView.this);
                            adMetaData.handleClick(NativeAdView.this, imageView, mDownX, mDownY, mUpX, mUpY);
                        }
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 更新布局
     *
     * @param metaData ADMetaData
     */
    private void updateUi(@NonNull ADMetaData metaData) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.width = ScreenUtils.getScreenWidth(mContext);
        layoutParams.height = (int) (ScreenUtils.getScreenWidth(mContext) * 0.5625);
        imageView.setLayoutParams(layoutParams);
        if (!TextUtils.isEmpty(metaData.getImgUrl())) {
            new AQuery(imageView).image(metaData.getImgUrl(), true, true);
        }
        //如果logo不存在就用图片代替
        if (!TextUtils.isEmpty(metaData.getLogoUrl())) {
            new AQuery(logoView).image(metaData.getLogoUrl(), true, true);
        } else if (!TextUtils.isEmpty(metaData.getImgUrl())) {
            new AQuery(logoView).image(metaData.getImgUrl(), true, true);
        }
        String title = metaData.getTitle();
        String subTitle = metaData.getSubTitle();
        //没有标题的话不显示底部
        if (!TextUtils.isEmpty(title)) {
            adTitleView.setText(title);
            rlBottom.setVisibility(VISIBLE);
        }
        if (!TextUtils.isEmpty(subTitle)) {
            adSubTitleView.setText(metaData.getSubTitle());
        }
        if (ADTool.getADTool().isDebugMode()) {
            tvPlatform.setVisibility(VISIBLE);
            tvPlatform.setText(metaData.getPlatform());
        }
        metaData.handleView(this);
    }
}
