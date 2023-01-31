package app.as_service.view.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import app.as_service.R
import app.as_service.adapter.DeviceListAdapter
import app.as_service.dao.AdapterModel
import app.as_service.dao.ApiModel
import app.as_service.databinding.DashboardFragmentBinding
import app.as_service.util.SharedPreferenceManager
import app.as_service.viewModel.DeviceListViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DashboardFragment : Fragment() {
    lateinit var binding : DashboardFragmentBinding

    private lateinit var adapter: DeviceListAdapter
    private val mList = ArrayList<AdapterModel.GetDeviceList>()
    private val deviceListViewModel by viewModel<DeviceListViewModel>()

    private val accessToken by lazy {SharedPreferenceManager.getString(context,"accessToken")}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate<DashboardFragmentBinding?>(inflater, R.layout.dashboard_fragment, container, false)
            .apply {
                lifecycleOwner = this@DashboardFragment
                deviceListVM = deviceListViewModel
            }

        if (::binding.isInitialized) {
            adapter = DeviceListAdapter(mList)
            binding.mainRecyclerView.adapter = adapter
            //디바이스 리스트 뷰모델 옵저빙
            applyDeviceListInViewModel()
            // 뷰모델 호출
            deviceListViewModel.loadDeviceListResult(accessToken)
        }

        return binding.root
    }

    // 뷰모델 호출 후 데이터 반환
    private fun applyDeviceListInViewModel() {
        deviceListViewModel.getDeviceListResult().observe(viewLifecycleOwner) { listItem ->
            listItem?.let {
                if (it.size != 0) {
                    mList.addAll(it)
                    adapter.notifyItemRangeInserted(0,it.size)
                }
            }
        }
    }
}