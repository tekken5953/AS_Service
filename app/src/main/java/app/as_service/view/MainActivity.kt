package app.as_service.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import app.as_service.R
import app.as_service.databinding.ActivityMainBinding
import app.as_service.view.fragment.AnalyticsFragment
import app.as_service.view.fragment.ChatFragment
import app.as_service.view.fragment.DashboardFragment
import app.as_service.view.fragment.UserFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val bottomNav: BottomNavigationView = binding.bottomNav
        bottomNav.selectedItemId = R.id.bottom_dashboard
        val viewPager: ViewPager2 = binding.viewPager
        val viewPagerAdapter = ViewPagerAdapter(this)
        viewPager.adapter = viewPagerAdapter
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        viewPager.currentItem = 0
                        bottomNav.selectedItemId = R.id.bottom_dashboard
                    }
                    1 -> {
                        viewPager.currentItem = 1
                        bottomNav.selectedItemId = R.id.bottom_analytics
                    }
                    2 -> {
                        viewPager.currentItem = 2
                        bottomNav.selectedItemId = R.id.bottom_chat
                    }
                    3 -> {
                        viewPager.currentItem = 3
                        bottomNav.selectedItemId = R.id.bottom_user
                    }
                }
                super.onPageSelected(position)
            }
        })

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bottom_dashboard -> {
                    viewPager.currentItem = 0
                    true
                }
                R.id.bottom_analytics -> {
                    viewPager.currentItem = 1
                    true
                }

                R.id.bottom_chat -> {
                    viewPager.currentItem = 2
                    true
                }
                R.id.bottom_user -> {
                    viewPager.currentItem = 3
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    internal class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                1 -> { AnalyticsFragment() }
                2 -> { ChatFragment() }
                3 -> { UserFragment() }
                else -> { DashboardFragment() }
            }
        }

        override fun getItemCount(): Int {
            return 4
        }
    }
}