package priv.rtbishop.coopstat.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import java.util.Objects;

import priv.rtbishop.coopstat.R;
import priv.rtbishop.coopstat.adapters.PagerAdapter;

public class ChartPagerFragment extends Fragment {

    private ImageButton mImgPrev, mImgNext;
    private ViewPager mViewPager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chartpager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int days = Objects.requireNonNull(getArguments()).getInt("days");
        PagerAdapter pagerAdapter = new PagerAdapter(getChildFragmentManager(), days);
        mViewPager = view.findViewById(R.id.view_pager_chart);
        mViewPager.setAdapter(pagerAdapter);
        mImgPrev = view.findViewById(R.id.img_prev_chart);
        mImgNext = view.findViewById(R.id.img_next_chart);
        setupButtons();
    }

    private void setupButtons() {
        mImgPrev.setVisibility(View.INVISIBLE);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mImgPrev.setVisibility(View.INVISIBLE);
                } else {
                    mImgPrev.setVisibility(View.VISIBLE);
                }
                if (position == Objects.requireNonNull(mViewPager.getAdapter()).getCount() - 1) {
                    mImgNext.setVisibility(View.INVISIBLE);
                } else {
                    mImgNext.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mImgPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);
            }
        });

        mImgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
            }
        });
    }
}