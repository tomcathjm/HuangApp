package com.hjm.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hjm.R;

import java.util.List;

/**
 * Created by huang on 2015/10/14.
 */

public class TriangleIndicator extends LinearLayout {


    private Paint mPaint; // 画笔

    private Path mPath;// 构建三角形的 Path

    private int mTriangleWidth; //三角形的宽度

    private int mTriangleHeight; // 三角形的高度

    private static final float RADIO_TRIANGLE_WIDTH = 1 / 6F; // 三角形的宽高的比例

    private int mInitTranslationX; // 初始化时指示器的平移距离

    private int mTranlationX;// 每一次滑动时三角形的平移的距离


    private int mTabVisibleCount; // 可见tab 数量
    private static final int DEFAULT_COUNT_TAB = 4; // 默认的 tab 数量

    private List<String> mTitles; // 用户自定义tab标题数据


    public TriangleIndicator(Context context) {
        this(context, null);
    }

    public TriangleIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 获取自定义 可见tab 数量   visible_tab_count
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TriangleIndicator);
        mTabVisibleCount = a.getInt(R.styleable.TriangleIndicator_visible_tab_count, DEFAULT_COUNT_TAB);
        if (mTabVisibleCount < 0) {
            mTabVisibleCount = DEFAULT_COUNT_TAB;
        }
        a.recycle();

        // 初始化画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#ffffffff"));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setPathEffect(new CornerPathEffect(3));

    }

    /**
     * 当 xml 加载完成之后回调的方法
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        int cCount = getChildCount();
        if (cCount == 0) return;
        for (int i = 0; i < cCount; i++) {
            View view = getChildAt(i);
            LinearLayout.LayoutParams lp = (LayoutParams) view.getLayoutParams();
            lp.weight = 0;
            lp.width = getScreenWidth() / mTabVisibleCount;
            view.setLayoutParams(lp);

        }
        setTabClickEvent();
    }

    // 获取屏幕的宽度
    private int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    // 绘制三角形 onMeasure() onLayout() 方法之后调用 绘制的方法
    @Override
    protected void dispatchDraw(Canvas canvas) {

        canvas.save();

        canvas.translate(mInitTranslationX + mTranlationX, getHeight() + 2);
        canvas.drawPath(mPath, mPaint);

        canvas.restore();

        super.dispatchDraw(canvas);
    }

    /**
     * ViewGroup 中当前控件大小发生改变的时候会回调的方法
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mTriangleWidth = (int) (w / mTabVisibleCount * RADIO_TRIANGLE_WIDTH);
        mInitTranslationX = w / mTabVisibleCount / 2 - mTriangleWidth / 2;

        mTriangleHeight = mTriangleWidth / 2;

        initTriangle();

    }

    /**
     * 初始化三角形指示器
     */
    private void initTriangle() {
        mPath = new Path();
        mPath.moveTo(0, 0); // 从 0 0 点出发
        mPath.lineTo(mTriangleWidth, 0); //向 X轴 移动一个三角形的宽度
        mPath.lineTo(mTriangleHeight / 2, -mTriangleHeight); // Y轴反向移动半个宽度
        mPath.close(); // 闭合
    }

    /**
     * 指示器跟随手指滑动
     *
     * @param position
     * @param offset
     */
    public void scroll(int position, float offset) {

        int tabWidth = getWidth() / mTabVisibleCount;
        mTranlationX = (int) (tabWidth * (offset + position));

        /**
         * 当三角形指示器从可见区域的倒数第二个移动到倒数第一个的时候 并且offset 大于0 并且 tab数量大于可见tab数量
         */
        if (position >= (mTabVisibleCount - 2) && offset > 0 && getChildCount() > mTabVisibleCount) {
            if (mTabVisibleCount != 1) {
                this.scrollTo((position - (mTabVisibleCount - 2)) * tabWidth + (int) (tabWidth * offset), 0);
            } else {
                this.scrollTo((int) (tabWidth * (position + offset)), 0);
            }
        }else{
            if (position == 1 || position == 2){
                this.scrollTo( 0 , 0);
            }
        }

        invalidate(); // 重绘
    }

    /**
     * 对外方法---设置tab
     *
     * @param titles
     */
    private void setTabTitle(List<String> titles) {
        if (titles != null && titles.size() != 0) {
            this.removeAllViews();
            mTitles = titles;
            for (String title : mTitles) {
                addView(createdView(title));
            }
        }
        setTabClickEvent();
    }

    /**
     * 对外方法---设置可见tab数量（其实可以在xml中的自定义控件中自定义属性直接设置）
     * <p>
     * 如果调用了此方法，一定要在方法 setTabTitle(List<String> titles) 之前调用
     *
     * @param visibleTabCount
     */
    private void setVisibleTabCount(int visibleTabCount) {
        mTabVisibleCount = visibleTabCount;
    }

    private ViewPager mViewPager;

    /**
     * 对完公开的方法---绑定ViewPager
     *
     * @param viewPager
     * @param defaultPosition
     */
    public void setViewPager(ViewPager viewPager, int defaultPosition, List<String> titles, int visibleTabCount) {

        setVisibleTabCount(visibleTabCount);
        setTabTitle(titles);

        mViewPager = viewPager;
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (mListener != null) {
                    mListener.onPagerScrolled(position, positionOffset, positionOffsetPixels);
                }
                // 手指移动的时候 positionOffset 从 0 到 1 变化
                // 从第一个tab滑动到第二个的时候 tabWidth * positionOffset
                // 从第二个tab 滑动到第三个的时候 tabWidth * positionOffset + position * tabWidth
                scroll(position, positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                if (mListener != null) {
                    mListener.onPagerSelected(position);
                }
                setTextColor(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (mListener != null) {
                    mListener.onPagerScrollStateChanged(state);
                }
            }
        });
        // 初始化
        viewPager.setCurrentItem(defaultPosition);
        setTextColor(defaultPosition);
    }

    /**
     * 当前ViewPager的滑动监听已被占用 --- 需要对外公开一个方法 可供用户在ViewPager的滑动监听中自定义逻辑
     */
    public interface PagerOnChangeListener {
        void onPagerScrolled(int position, float positionOffset, int positionOffsetPixels);

        void onPagerSelected(int position);

        void onPagerScrollStateChanged(int position);
    }

    public PagerOnChangeListener mListener;

    public void setOnPagerChangeListener(PagerOnChangeListener listener) {
        mListener = listener;
    }

    private static final int COLOR_TEXT_NORMAL = 0x77FFFFFF;
    private static final int COLOR_TEXT_HIGHLIGHT = 0XFFFFFFFF;

    private View createdView(String title) {
        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.width = getScreenWidth() / mTabVisibleCount;
        textView.setText(title);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setTextColor(COLOR_TEXT_NORMAL);
        textView.setLayoutParams(lp);
        for (int j = 0; j < getChildCount(); j++) {
            final int a = j;
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mViewPager.setCurrentItem(a);
                }
            });
        }
        return textView;
    }

    // 高亮文本(高亮文本之前先要把所有的置为未被选中的颜色)
    private void setTextColor(int position) {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof TextView) {
                ((TextView) getChildAt(i)).setTextColor(COLOR_TEXT_NORMAL);
            }
        }
        View childAt = getChildAt(position);
        if (childAt instanceof TextView) {
            ((TextView) childAt).setTextColor(COLOR_TEXT_HIGHLIGHT);
        }
    }

    // 设置 tab 的点击事件
    private void setTabClickEvent() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            final int j = i;
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mViewPager.setCurrentItem(j);
                }
            });

        }
    }

}
