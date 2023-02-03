package app.as_service.view.main.fragment

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
import app.as_service.dao.StaticDataObject.CODE_SERVER_OK
import app.as_service.dao.StaticDataObject.TAG
import app.as_service.databinding.DashboardFragmentBinding
import app.as_service.util.SharedPreferenceManager
import app.as_service.util.SnackBarUtils
import app.as_service.viewModel.DeleteDeviceViewModel
import app.as_service.viewModel.DeviceListViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class DashboardFragment : Fragment() {
    lateinit var binding: DashboardFragmentBinding

    private lateinit var adapter: DeviceListAdapter
    private val mList = ArrayList<AdapterModel.GetDeviceList>()
    private val searchList = ArrayList<AdapterModel.GetDeviceList>()
    private val deviceListViewModel by viewModel<DeviceListViewModel>()
    private val deleteDeviceViewModel by viewModel<DeleteDeviceViewModel>()

    private val accessToken by lazy { SharedPreferenceManager.getString(context, "accessToken") }

    private val snack = SnackBarUtils()

    private var isLoaded = false

    override fun onResume() {
        super.onResume()
        adapter.timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                // 뷰모델 호출
                deviceListViewModel.loadDeviceListResult(accessToken)
            }
        },0,10 * 1000)
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
            }

        if (::binding.isInitialized) {
            adapter = DeviceListAdapter(mList)

            deleteBlinkRecycle(binding.dashboardRecyclerView)   // 화면 깜빡임 제거

            binding.dashboardRecyclerView.adapter = adapter
            //디바이스 리스트 뷰모델 옵저빙
            applyDeviceListInViewModel()
            //디바이스 삭제 뷰모델 옵저빙
            applyDeleteDeviceViewModel()

            // 서치뷰 입력시 필터링
            binding.dashboardSearchView.setOnQueryTextListener(object :
                SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    /// 서치뷰의 텍스트가 입력되었을 겨우
                    if (newText!!.isNotEmpty()) {
                        searchList.clear()
                        adapter = DeviceListAdapter(searchList) // 어댑터의 데이터모델을 서치리스트로 변경
                        mList.forEach {
                            val s = newText.uppercase()     // 시리얼넘버는 모두 대문자로 변환
                            if (it.device.contains(s)
                                or it.deviceName.contains(s)
                                or it.businessType.contains(s)
                            ) {
                                searchList.add(it)
                            }
                        }
                    } else {
                        // 서치뷰의 텍스트가 입력되지 않았을 경우
                        adapter = DeviceListAdapter(mList)
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
                        deleteDeviceViewModel.loadDeleteDeviceResult(
                            accessToken, mList[position].device
                        )
                        dialog.dismiss()
                        // 삭제 뷰모델 호출
                        deviceListViewModel.loadDeviceListResult(accessToken)
                    }
                    .setNegativeButton("아니오") { dialog, _ ->
                        dialog.dismiss()
                    }.show()
            }
        }
        return binding.root
    }

    // 뷰모델 호출 후 데이터 반환
    private fun applyDeviceListInViewModel() {
        deviceListViewModel.getDeviceListResult().observe(viewLifecycleOwner) { listItem ->
            listItem?.let {
                if (!isLoaded) {
                    if (it.size != 0) {
                        mList.clear()
                        mList.addAll(it)
                        adapter.notifyItemRangeInserted(0, it.size)
                    }
                    isLoaded = true
                } else {
                    for (i: Int in 0 until (it.size)) {
                        try {
                            if (mList[i].caiVal != it[i].caiVal)
                                mList[i].caiVal = it[i].caiVal
                            if (mList[i].virusVal != it[i].virusVal)
                                mList[i].virusVal = it[i].virusVal
                            adapter.notifyItemChanged(i)
                        } catch(e: NullPointerException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    // 장치삭제 뷰모델 호출 후 데이터 반환
    private fun applyDeleteDeviceViewModel() {
        deleteDeviceViewModel.deleteDeviceResult().observe(viewLifecycleOwner) { result ->
            result.let {
                if (it == CODE_SERVER_OK.toString()) {
                    snack.makeSnack(this.view, activity, "장치제거에 성공하였습니다")
                } else {
                    snack.makeSnack(this.view, activity, "장치제거에 실패하였습니다")
                }
            }
        }
    }

    private fun deleteBlinkRecycle(recyclerView: RecyclerView) {
        // 리사이클러뷰 데이터가 바뀔 때 깜빡임 제거
        recyclerView.itemAnimator.apply {
            if (this is SimpleItemAnimator) {
                supportsChangeAnimations = false
            }
        }
    }
}