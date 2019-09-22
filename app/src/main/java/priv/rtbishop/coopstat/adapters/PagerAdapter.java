package priv.rtbishop.coopstat.adapters;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import org.jetbrains.annotations.NotNull;

import priv.rtbishop.coopstat.ui.ChartFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private static final int CHARTS_COUNT = 5;
    private int mDaysToShow;

    public PagerAdapter(FragmentManager fm, int daysToShow) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mDaysToShow = daysToShow;
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        ChartFragment fragment = ChartFragment.newInstance();
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putInt("daysToShow", mDaysToShow);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return CHARTS_COUNT;
    }
}
