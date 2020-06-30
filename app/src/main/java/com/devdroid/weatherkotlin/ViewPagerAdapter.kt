package com.devdroid.weatherkotlin

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter

class ViewPagerAdapter (fm : FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {

        return when (position)  {
            0-> {
                CurrentFrag()
            }
            1-> CityFrag()
            2-> ForecastFrag()
            else->{
                return SettingsFrag()
            }
        }

    }

    override fun getCount(): Int {

        return 4

    }


    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun getPageTitle(position: Int): CharSequence? {

        return when (position){
            0-> "Current"
            1-> "City"
            2-> "Forecast"
            else-> {
                 return "Settings"
            }
        }
    }
}