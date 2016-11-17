package com.hjm;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.hjm.fragment.TestFragment;
import com.hjm.fragment.VpSimpleFragment;
import com.hjm.widget.LineIndicator;
import com.hjm.widget.TriangleIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private TriangleIndicator mIndicator;


    private ViewPager lineViewPager;
    private LineIndicator lineIndicator;

    private List<String> mTitles = Arrays.asList("第一个","第二个","第三个","disige","diluge","dige","gegs","gegas","gsegrs");
    private List<VpSimpleFragment> mContents  = new ArrayList<>();
    private List<TestFragment> lineContent  = new ArrayList<>();

    private FragmentPagerAdapter mAdapter;
    private FragmentPagerAdapter lineAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initViews();
        initDatas();

        mViewPager.setAdapter(mAdapter);
        lineViewPager.setAdapter(lineAdapter);

        /**
         * params
         * 需要设置绑定的ViewPager
         * 默认现实的ViewPager页面
         * tab数据源
         * 初始化页面tab可见数量
         *
         *      ⚠️注意： ViewPager的滑动监听事件由mIndicator来处理／用户可自由处理逻辑
         *
         *          mIndicator.setOnPagerChangeListener(new ViewPagerIndicator.PagerOnChangeListener() {
                        @Override
                        public void onPagerScrolled(int position, float positionOffset, int positionOffsetPixels) {}
                        @Override
                        public void onPagerSelected(int position) {}
                        @Override
                        public void onPagerScrollStateChanged(int position) {}
                    });
         *
         */
        mIndicator.setViewPager(mViewPager,0,mTitles,4);
        lineIndicator.setViewPager(lineViewPager,2,mTitles,3);

    }

    private void initViews() {
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        mIndicator = (TriangleIndicator) findViewById(R.id.id_indicator);

        lineIndicator = (LineIndicator) findViewById(R.id.line_indicator);
        lineViewPager = (ViewPager) findViewById(R.id.line_viewpager);
    }

    private void initDatas() {
        for (String title:mTitles){
            VpSimpleFragment fragment = VpSimpleFragment.newInstance(title);
            mContents.add(fragment);
        }

        for (String title:mTitles){
            TestFragment testFragment = TestFragment.newInstance(title);
            lineContent.add(testFragment);
        }

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mContents.get(position);
            }

            @Override
            public int getCount() {
                return mContents.size();
            }
        };
        lineAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return lineContent.get(position);
            }

            @Override
            public int getCount() {
                return lineContent.size();
            }
        };
    }
}
