package app.as_service.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import app.as_service.R
import app.as_service.databinding.UserFragmentBinding
import app.as_service.view.MainActivity

class UserFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    lateinit var binding : UserFragmentBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) mainActivity = context
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.user_fragment, container, false)
        return binding.root
    }
}