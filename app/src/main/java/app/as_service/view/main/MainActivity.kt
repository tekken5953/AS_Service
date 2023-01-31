package app.as_service.view.main

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import app.as_service.R
import app.as_service.dao.ApiModel
import app.as_service.dao.StaticDataObject.TAG
import app.as_service.databinding.ActivityMainBinding
import app.as_service.view.main.fragment.AnalyticsFragment
import app.as_service.view.main.fragment.ChatFragment
import app.as_service.view.main.fragment.DashboardFragment
import app.as_service.view.main.fragment.UserFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var resultSerial: String   // 장치 추가 시리얼넘버
    lateinit var resultName: String     // 장치 추가 별명
    lateinit var resultBusiness: String // 장치 추가 비즈니스 타입
    lateinit var resultId: String       // 장치 추가 유저 아이디

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val bottomNav: BottomNavigationView = binding.bottomNav
        val viewPager: ViewPager2 = binding.viewPager
        val viewPagerAdapter = ViewPagerAdapter(this)
        bottomNav.selectedItemId = R.id.bottom_dashboard    // 대시보드 화면이 초기화면
        viewPager.adapter = viewPagerAdapter                // 어댑터 바인딩

        // 페이지 이동 시 바텀메뉴 아이템 변경
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> { bottomNav.selectedItemId = R.id.bottom_dashboard }
                    1 -> { bottomNav.selectedItemId = R.id.bottom_analytics }
                    2 -> { bottomNav.selectedItemId = R.id.bottom_chat }
                    3 -> { bottomNav.selectedItemId = R.id.bottom_user }
                }
                viewPager.currentItem = position
                super.onPageSelected(position)
            }
        })

        // 바텀메뉴 아이템 변경 시 페이지 이동
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

        // 장치 추가 이벤트리스너
        binding.fab.setOnClickListener {
            val builder = AlertDialog.Builder(this, R.style.Dialog)
            val viewSerial: View =       // 시리얼넘버 등록 뷰
                LayoutInflater.from(this).inflate(R.layout.dialog_add_serial, null)
            val viewBusiness: View =     // 비즈니스 타입 등록 뷰
                LayoutInflater.from(this).inflate(R.layout.dialog_add_business, null)
            val serialInput: TextInputLayout = viewSerial.findViewById(R.id.serialEditInput)
            val serialEt: EditText = viewSerial.findViewById(R.id.serialEdit)
            val serialBtn: AppCompatButton = viewSerial.findViewById(R.id.serialBtn)
            builder.setView(viewSerial)
            serialBtn.isEnabled = false     //디폴트로 disable

            builder.setOnDismissListener { it.dismiss() }

            //시리얼번호 EditText 처리
            serialEt.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(
                    s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (serialEt.text.isEmpty() ||
                        serialEt.text.length == serialInput.counterMaxLength) {
                        serialInput.error = null    // error
                        serialBtn.isEnabled = true  // enable
                    } else {
                        serialInput.error = getString(R.string.serial_error)    //not error
                        serialBtn.isEnabled = false // disable
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            })

            serialBtn.setOnClickListener {
                if (serialInput.error == null
                    && serialEt.text.length == serialInput.counterMaxLength
                ) {
                    resultSerial = serialEt.text.toString()     //시리얼번호 저장

                    // 다이어로그가 초기생성인지 중복생성인지 구분
                    if (viewBusiness.parent == null) {
                        // 초기생성이면 setView
                        builder.setView(viewBusiness).show()
                    } else {
                        // 중복생성이면 기존 뷰 삭제 후 setView
                        (viewBusiness.parent as ViewGroup).removeView(viewBusiness)
                        builder.setView(viewBusiness).show()
                    }
                }
            }
            builder.setView(viewSerial).show()
        }
    }

    // 프래그먼트 뷰페이저 어댑터 : 총 4개의 프래그먼트
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
        override fun getItemCount(): Int { return 4 }
    }
}