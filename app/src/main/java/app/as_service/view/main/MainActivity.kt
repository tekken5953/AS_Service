package app.as_service.view.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import app.as_service.R
import app.as_service.adapter.GridAdapter
import app.as_service.adapter.`interface`.ChangeDialogListener
import app.as_service.api.GetLocation
import app.as_service.dao.StaticDataObject.CODE_INVALID_TOKEN
import app.as_service.dao.StaticDataObject.CODE_SERVER_OK
import app.as_service.databinding.MainActivityBinding
import app.as_service.fcm.SubFCM
import app.as_service.util.RefreshUtils
import app.as_service.util.RequestPermissionsUtil
import app.as_service.util.SharedPreferenceManager
import app.as_service.util.ToastUtils
import app.as_service.view.main.fragment.AnalyticsFragment
import app.as_service.view.main.fragment.DashboardFragment
import app.as_service.view.main.fragment.MapsFragment
import app.as_service.view.main.fragment.UserFragment
import app.as_service.viewModel.AddDeviceViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputLayout
import com.orhanobut.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), ChangeDialogListener {
    private lateinit var binding: MainActivityBinding
    private lateinit var viewSerial: View
    private lateinit var viewBusiness: View
    private lateinit var viewName: View
    private lateinit var builder: AlertDialog.Builder
    private lateinit var bottomNav: BottomNavigationView
    private val postDeviceViewModel by viewModel<AddDeviceViewModel>()
    private var isBackPressed = false
    private var x = "0"
    private var y = "0"
    private var address = "null"

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<MainActivityBinding?>(
            this, R.layout.main_activity
        ).apply {
            lifecycleOwner = this@MainActivity
            postDeviceVM = postDeviceViewModel
        }

        lifecycleScope.launch {
            joinJob()
        }

        RequestPermissionsUtil(this).requestLocation()
//        RequestPermissionsUtil(this).requestNotification()
        // FCM Message
        SubFCM().getToken()
        SubFCM().subTopic("test")

        bottomNav = binding.bottomNav
        bottomNav.selectedItemId = R.id.bottom_dashboard    // 대시보드 화면이 초기화면
        applyPostDeviceViewModel() // 장치 추가 뷰모델 설정
        supportFragmentManager.beginTransaction().replace(R.id.fragmentFrame, DashboardFragment())
            .commit()

        // 바텀메뉴 아이템 변경 시 페이지 이동
        bottomNav.setOnItemSelectedListener {
            val ft = supportFragmentManager.beginTransaction()
            val bundle = Bundle()
            bundle.putString("location", "${x}_${y}_${address}")

            when (it.itemId) {
                R.id.bottom_dashboard -> {
                    val list = DashboardFragment()
                    if (bottomNav.selectedItemId != R.id.bottom_dashboard) {
                        ft.replace(R.id.fragmentFrame, list, "list").commit()
                        true
                    } else false
                }
                R.id.bottom_analytics -> {
                    val analytics = AnalyticsFragment()
                    analytics.arguments = bundle
                    if (bottomNav.selectedItemId != R.id.bottom_analytics) {
                        ft.replace(R.id.fragmentFrame, analytics, "analytics").commit()
                        true
                    } else false
                }
                R.id.bottom_chat -> {
                    val chat = MapsFragment()
                    chat.arguments = bundle
                    if (bottomNav.selectedItemId != R.id.bottom_chat) {
                        ft.replace(R.id.fragmentFrame, chat, "maps").commit()
                        true
                    } else false
                }
                R.id.bottom_user -> {
                    val user = UserFragment()
                    if (bottomNav.selectedItemId != R.id.bottom_user) {
                        ft.replace(R.id.fragmentFrame, user, "user").commit()
                        true
                    } else false
                }
                else -> {
                    false
                }
            }
        }

        var deviceSerial = ""

        // 장치 추가 이벤트리스너
        binding.fab.setOnClickListener {
            builder = AlertDialog.Builder(this, R.style.Dialog)
            viewSerial =       // 시리얼넘버 등록 뷰
                LayoutInflater.from(this).inflate(R.layout.dialog_add_serial, null)
            viewBusiness =     // 비즈니스 타입 등록 뷰
                LayoutInflater.from(this).inflate(R.layout.dialog_add_business, null)
            viewName =         // 별명 등록 뷰
                LayoutInflater.from(this).inflate(R.layout.dialog_add_name, null)

            val serialInput: TextInputLayout = viewSerial.findViewById(R.id.serialEditInput)
            val serialEt: EditText = viewSerial.findViewById(R.id.serialEdit)
            val serialBtn: AppCompatButton = viewSerial.findViewById(R.id.serialBtn)

            val businessTitle: TextView = viewBusiness.findViewById(R.id.businessTitle)
            val businessGrid: GridView = viewBusiness.findViewById(R.id.businessGridView)
            val businessApplyBtn: AppCompatButton = viewBusiness.findViewById(R.id.businessApplyBtn)
            businessApplyBtn.isEnabled = false
            val businessGridAdapter = GridAdapter(this, businessTitle, businessApplyBtn)
            businessGridAdapter.setChangeDialogListener(this)

            val nameApplyBtn: AppCompatButton = viewName.findViewById(R.id.nameBtn)
            val nameEt: EditText = viewName.findViewById(R.id.nameEdit)

            builder.setView(viewSerial)
            serialBtn.isEnabled = false     //디폴트로 disable

            builder.setOnDismissListener { it.dismiss() }

            nameApplyBtn.setOnClickListener {
                (viewName.parent as ViewGroup).removeView(viewName)
                postDeviceViewModel.loadPostDeviceResult(
                    SharedPreferenceManager.getString(this, "accessToken"),
                    deviceSerial,
                    SharedPreferenceManager.getString(this, "jti"),
                    nameEt.text.toString(),
                    businessGridAdapter.resultBusiness
                )
            }

            // 시리얼번호 EditText 처리
            serialEt.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?, start: Int, before: Int, count: Int
                ) {
                    if (serialEt.text.isNotEmpty()
                    ) {
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
                ) {
                    // 다이어로그가 초기생성인지 중복생성인지 구분
                    if (viewBusiness.parent == null) {
                        // 초기생성이면 setView
                        builder.setView(viewBusiness).show()
                    } else {
                        // 중복생성이면 기존 뷰 삭제 후 setView
                        (viewBusiness.parent as ViewGroup).removeView(viewBusiness)
                        builder.setView(viewBusiness).show()
                    }
                    deviceSerial = serialEt.text.toString()
                    businessGrid.adapter = businessGridAdapter
                    businessGridAdapter.drawTypeEntry()
                }
            }
            onChangeToSerial()
        }
    }

    override fun onChangeToSerial() {
        viewSerial.let {
            // 다이어로그가 초기생성인지 중복생성인지 구분
            if (it.parent == null) {
                // 초기생성이면 setView
                builder.setView(it).show()
            } else {
                // 중복생성이면 기존 뷰 삭제 후 setView
                (it.parent as ViewGroup).removeView(it)
                builder.setView(it).show()
            }
        }
    }

    override fun onChangeToBusiness() {
        viewBusiness.let {
            // 다이어로그가 초기생성인지 중복생성인지 구분
            if (it.parent == null) {
                // 초기생성이면 setView
                builder.setView(it).show()
            } else {
                // 중복생성이면 기존 뷰 삭제 후 setView
                (it.parent as ViewGroup).removeView(it)
                builder.setView(it).show()
            }
        }
    }

    override fun onChangeToName() {
        viewName.let {
            // 다이어로그가 초기생성인지 중복생성인지 구분
            if (it.parent == null) {
                // 초기생성이면 setView
                builder.setView(it).show()
            } else {
                // 중복생성이면 기존 뷰 삭제 후 setView
                (it.parent as ViewGroup).removeView(it)
                builder.setView(it).show()
            }
        }
    }

    // 장치등록 뷰모델 호출 후 데이터 반환
    private fun applyPostDeviceViewModel() {
        postDeviceViewModel.postDeviceResult().observe(this) { result ->
            result.let {
                when (it) {
                    CODE_SERVER_OK.toString() -> {
                        Toast.makeText(this, "장치등록에 성공하였습니다", Toast.LENGTH_SHORT).show()
                    }
                    CODE_INVALID_TOKEN.toString() -> {

                    }
                    else -> {
                        Toast.makeText(this, "장치등록에 실패하였습니다", Toast.LENGTH_SHORT).show()
                    }
                }
                RefreshUtils().refreshActivity(this)
            }
        }
    }

    override fun onBackPressed() {
        for (fragment: Fragment in supportFragmentManager.fragments) {
            if (fragment.isVisible) {
                if (fragment is DashboardFragment) {
                    val toast = ToastUtils(this)
                    if (!isBackPressed) {
                        toast.customDurationMessage("버튼을 한번 더 누르면 앱이 종료됩니다", 2)
                        isBackPressed = true
                    } else {
                        finish()
                    }
                    Handler(Looper.getMainLooper()).postDelayed({
                        isBackPressed = false
                    }, 2000)
                } else {
                    bottomNav.selectedItemId = R.id.bottom_dashboard
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getLocation(): Job {
        // 위치정보 불러오기
        return CoroutineScope(Dispatchers.IO).launch {
            val location = GetLocation().execute(this@MainActivity).split("_")
            x = location[0]
            y = location[1]
            address = location[2]
        }
    }

    private suspend fun joinJob() {
        val job = getLocation()
        job.join()
    }
}