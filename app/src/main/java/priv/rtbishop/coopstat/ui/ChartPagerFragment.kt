package priv.rtbishop.coopstat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager

import priv.rtbishop.coopstat.R
import priv.rtbishop.coopstat.adapters.PagerAdapter

class ChartPagerFragment : Fragment() {

    private lateinit var imgPrev: ImageButton
    private lateinit var imgNext: ImageButton
    private lateinit var viewPager: ViewPager

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chartpager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val days = arguments!!.getInt("days")
        val pagerAdapter = PagerAdapter(childFragmentManager, days)
        viewPager = view.findViewById(R.id.view_pager_chart)
        viewPager.adapter = pagerAdapter
        imgPrev = view.findViewById(R.id.img_prev_chart)
        imgNext = view.findViewById(R.id.img_next_chart)
        setupButtons()
    }

    private fun setupButtons() {
        imgPrev.visibility = View.INVISIBLE

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    imgPrev.visibility = View.INVISIBLE
                } else {
                    imgPrev.visibility = View.VISIBLE
                }
                if (position == viewPager.adapter!!.count - 1) {
                    imgNext.visibility = View.INVISIBLE
                } else {
                    imgNext.visibility = View.VISIBLE
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        imgPrev.setOnClickListener { viewPager.setCurrentItem(viewPager.currentItem - 1, true) }

        imgNext.setOnClickListener { viewPager.setCurrentItem(viewPager.currentItem + 1, true) }
    }
}