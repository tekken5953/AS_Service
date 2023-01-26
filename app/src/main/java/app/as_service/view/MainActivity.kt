package app.as_service.view

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import app.as_service.R
import app.as_service.adapter.DeviceListAdapter
import app.as_service.dao.ApiModel
import app.as_service.databinding.ActivityMainBinding
import app.as_service.util.SharedPreferenceManager
import app.as_service.viewModel.DeviceListViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: DeviceListAdapter
    private val mList = ArrayList<ApiModel.GetDeviceList>()
    private val deviceListViewModel by viewModel<DeviceListViewModel>()
    private val originToken: String by lazy {
        SharedPreferenceManager.getString(this, "accessToken")    // 로컬 DB 에 저장된 토큰 값 불러오기
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityMainBinding?>(this, R.layout.activity_main)
            .apply {
                lifecycleOwner = this@MainActivity
                deviceListVM = deviceListViewModel
            }

        if (::binding.isInitialized) {
            adapter = DeviceListAdapter(mList)
            binding.mainRecyclerView.adapter = adapter
            applyDeviceListInViewModel()
            deviceListViewModel.loadDeviceListResult(originToken)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun applyDeviceListInViewModel() {
        deviceListViewModel.getDeviceListResult().observe(this@MainActivity) { listItem ->
                listItem?.let {
                    if (it.size != 0) {
                        mList.addAll(listItem)
                        adapter.notifyDataSetChanged()
                    }
            }
        }
    }
}