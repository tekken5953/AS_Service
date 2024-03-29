package app.as_service.view.main.fragment

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import app.as_service.R
import app.as_service.adapter.DeviceListAdapter
import app.as_service.dao.AdapterModel
import app.as_service.dao.StaticDataObject
import app.as_service.dao.StaticDataObject.CODE_INVALID_TOKEN
import app.as_service.dao.StaticDataObject.CODE_SERVER_OK
import app.as_service.dao.StaticDataObject.RESPONSE_DEFAULT
import app.as_service.dao.StaticDataObject.TAG_R
import app.as_service.databinding.DashboardFragmentBinding
import app.as_service.util.ConvertDataTypeUtil
import app.as_service.util.RefreshUtils
import app.as_service.util.SharedPreferenceManager
import app.as_service.util.SnackBarUtils
import app.as_service.view.login.LoginActivity
import app.as_service.viewModel.DeleteDeviceViewModel
import app.as_service.viewModel.DeviceListViewModel
import app.as_service.viewModel.TokenRefreshViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class DashboardFragment : Fragment() {
    lateinit var binding: DashboardFragmentBinding

    private lateinit var adapter: DeviceListAdapter
    private val mList = ArrayList<AdapterModel.GetDeviceList>()
    private val searchList = ArrayList<AdapterModel.GetDeviceList>()
    private val deviceListViewModel by viewModel<DeviceListViewModel>()
    private val deleteDeviceViewModel by viewModel<DeleteDeviceViewModel>()
    private val refreshTokenViewModel by viewModel<TokenRefreshViewModel>()
    private val snack = SnackBarUtils()

    override fun onResume() {
        super.onResume()
        adapter.timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                // 뷰모델 호출

                deviceListViewModel.loadDeviceListResult(getAccessToken())
            }
        }, 0, 10 * 1000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cancelTimer(adapter.currentTimer())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate<DashboardFragmentBinding?>(
            inflater,
            R.layout.dashboard_fragment,
            container,
            false
        )
            .apply {
                lifecycleOwner = this@DashboardFragment
                deviceListVM = deviceListViewModel
                deleteDeviceVM = deleteDeviceViewModel
                tokenRefreshVM = refreshTokenViewModel
            }

        if (::binding.isInitialized) {
            adapter = DeviceListAdapter(mList)

            deleteBlinkRecycle(binding.dashboardRecyclerView)   // 화면 깜빡임 제거

            binding.dashboardRecyclerView.adapter = adapter
            //디바이스 리스트 뷰모델 옵저빙
            applyDeviceListInViewModel()
            //디바이스 삭제 뷰모델 옵저빙
            applyDeleteDeviceViewModel()
            //토큰 갱신 뷰모델 옵저빙
            applyTokenRefreshViewModel()

            // 서치뷰 입력시 필터링
            binding.dashboardSearchView.setOnQueryTextListener(object :
                SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    /// 서치뷰의 텍스트가 입력되었을 경우
                    newText?.let { new ->
                        if (new.isNotEmpty()) {
                            searchList.clear()
                            adapter = DeviceListAdapter(searchList) // 어댑터의 데이터모델을 서치리스트로 변경
                            try {
                                mList.forEach { list ->
                                    val upper = new.uppercase()     // 시리얼넘버는 모두 대문자로 변환
                                    if (list.device.contains(upper)
                                        or list.deviceName!!.contains(upper)
                                        or list.businessType!!.contains(upper)
                                    ) {
                                        searchList.add(list)
                                    }
                                }
                            } catch (e: NullPointerException) {
                                e.printStackTrace()
                            }
                        } else {
                            // 서치뷰의 텍스트가 입력되지 않았을 경우
                            adapter = DeviceListAdapter(mList)
                        }
                    }

                    // 어댑터 갱신
                    binding.dashboardRecyclerView.adapter = adapter
                    return true
                }
            })

            // 아이템 롱클릭 시 장치 삭제 다이얼로그
            adapter.setOnItemLongClickListener { _, position ->
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("장치 삭제하기").setMessage("삭제시 복구가 불가능합니다.\n정말 삭제하시겠습니까?")
                    .setPositiveButton(
                        "예"
                    ) { dialog, _ ->
                        mList[position].device.let {
                            deleteDeviceViewModel.loadDeleteDeviceResult(
                                getAccessToken(), it
                            )
                        }
                        dialog.dismiss()
                        // 삭제 뷰모델 호출
                        deviceListViewModel.loadDeviceListResult(getAccessToken())
                    }
                    .setNegativeButton("아니오") { dialog, _ ->
                        dialog.dismiss()
                    }.show()
            }
        }
        return binding.root
    }

    // 뷰모델 호출 후 데이터 반환
    @SuppressLint("NotifyDataSetChanged")
    private fun applyDeviceListInViewModel() {
        deviceListViewModel.getDeviceListResult().observe(viewLifecycleOwner) { listItem ->
            Log.d(TAG_R, "listItem : ${listItem.toString()}")
            if (listItem == null) {
                Log.d(TAG_R, "토큰 만료시간은 ${
                    ConvertDataTypeUtil.longToMillsString(SharedPreferenceManager.getString(context,"exp").toLong(), 
                        "yyyy년 MM월 dd일 HH시 mm분")} 입니다")
                val builder = AlertDialog.Builder(requireContext())
                builder.setMessage("세션이 만료되었습니다.\n다시 로그인 해 주세요")
                    .setPositiveButton(
                        "확인"
                    ) { dialog, _ ->
                        dialog.dismiss()
                        RefreshUtils().logout(requireActivity())
                    }.show()
            } else {
                mList.clear()
                CoroutineScope(Dispatchers.Main).launch {
                    mList.addAll(listItem.filter { it.starred })
                    mList.addAll(listItem.filter { !it.starred })
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    // 장치삭제 뷰모델 호출 후 데이터 반환
    private fun applyDeleteDeviceViewModel() {
        deleteDeviceViewModel.deleteDeviceResult().observe(viewLifecycleOwner) { result ->
            result.let {
                when (it) {
                    CODE_SERVER_OK.toString() -> {
                        snack.makeSnack(this.view, activity, "장치제거에 성공하였습니다")
                    }
                    else -> {
                        snack.makeSnack(this.view, activity, "장치제거에 실패하였습니다")
                    }
                }
            }
        }
    }

    private fun applyTokenRefreshViewModel() {
        refreshTokenViewModel.getResultToken().observe(viewLifecycleOwner) { result ->
            result.let {
                if (it.size == 2) {
                    SharedPreferenceManager.setString(requireContext(), "accessToken", it[0])
                    SharedPreferenceManager.setString(requireContext(), "refreshToken", it[1])
                }
            }
        }
    }

    private fun cancelTimer(timer: Timer?) {
        timer?.let {
            it.cancel()
            it.purge()
        }
    }

    // 리사이클러뷰 데이터가 바뀔 때 깜빡임 제거
    private fun deleteBlinkRecycle(recyclerView: RecyclerView) {
        recyclerView.itemAnimator.apply {
            if (this is SimpleItemAnimator) {
                supportsChangeAnimations = false
            }
        }
    }

    private fun getAccessToken(): String {
        return SharedPreferenceManager.getString(context, "accessToken")
    }

    private fun getRefreshToken(): String {
        return SharedPreferenceManager.getString(context, "refreshToken")
    }
}