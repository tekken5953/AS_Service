package app.as_service.view.main.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import app.as_service.R
import app.as_service.databinding.UserFragmentBinding
import app.as_service.util.ConvertDataTypeUtil.setLocaleToEnglish
import app.as_service.util.ConvertDataTypeUtil.setLocaleToKorea
import app.as_service.util.SharedPreferenceManager
import app.as_service.util.ToastUtils
import app.as_service.view.login.LoginActivity

class UserFragment : Fragment() {

    lateinit var binding : UserFragmentBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.user_fragment, container, false)

        binding.userLogout.setOnClickListener {
            AlertDialog.Builder(requireContext()).apply {
                setMessage("로그아웃 하시겠습니까?")
                setPositiveButton("예") { dialog, _ ->
                    dialog.dismiss()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    SharedPreferenceManager.clear(requireContext()) // 저장된 환경 초기화
                    startActivity(intent)
                    requireActivity().finish()
                }
                setNegativeButton("아니오") { dialog, _ ->
                    dialog.dismiss()
                }
                show()
            }
        }

        binding.userKorIv.setOnClickListener {
            ToastUtils(requireActivity()).shortMessage("한국어로 변경")
            setLocaleToKorea(requireContext())
        }
        binding.userEngiv.setOnClickListener {
            ToastUtils(requireActivity()).shortMessage("영어로 변경")
            setLocaleToEnglish(requireContext())
        }
        return binding.root
    }
}