package priv.rtbishop.coopstat.adapters

import android.os.Bundle

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

import priv.rtbishop.coopstat.ui.ChartFragment

class PagerAdapter(fm: FragmentManager, private val mDaysToShow: Int) :
        FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        val fragment = ChartFragment.newInstance()
        val args = Bundle()
        args.putInt("position", position)
        args.putInt("daysToShow", mDaysToShow)
        fragment.arguments = args
        return fragment
    }

    override fun getCount(): Int {
        return CHARTS_COUNT
    }

    companion object {
        private const val CHARTS_COUNT = 5
    }
}
