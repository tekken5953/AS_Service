package app.as_service.view.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import app.as_service.R
import app.as_service.adapter.DeviceListAdapter
import app.as_service.dao.ApiModel
import app.as_service.databinding.DashboardFragmentBinding
import app.as_service.util.SharedPreferenceManager
import app.as_service.view.MainActivity
import app.as_service.viewModel.DeviceListViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DashboardFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding : DashboardFragmentBinding

    private lateinit var adapter: DeviceListAdapter
    private val mList = ArrayList<ApiModel.GetDeviceList>()
    private val deviceListViewModel by viewModel<DeviceListViewModel>()
    private val originToken: String by lazy {
        SharedPreferenceManager.getString(context, "accessToken")    // 로컬 DB 에 저장된 토큰 값 불러오기
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) mainActivity = context
    }


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
            applyDeviceListInViewModel()
            deviceListViewModel.loadDeviceListResult(originToken)
        }

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun applyDeviceListInViewModel() {
        deviceListViewModel.getDeviceListResult().observe(this) { listItem ->
            listItem?.let {
                if (it.size != 0) {
                    mList.addAll(listItem)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }
}