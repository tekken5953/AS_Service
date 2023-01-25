package app.as_service.view.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import app.as_service.R
import app.as_service.dao.StaticDataObject.CODE_SERVER_DOWN
import app.as_service.dao.StaticDataObject.RESPONSE_DEFAULT
import app.as_service.databinding.ActivityLoginBinding
import app.as_service.util.MakeVibrator
import app.as_service.util.SharedPreferenceManager
import app.as_service.view.MainActivity
import app.as_service.viewModel.LoginViewModel
import app.as_service.viewModel.SignUpViewModel
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel by viewModel<LoginViewModel>()
    private val signUpViewModel by viewModel<SignUpViewModel>()
    private val originToken: String by lazy {
        SharedPreferenceManager.getString(this, "accessToken")    // 로컬 DB 에 저장된 토큰 값 불러오기
    }
    private val context: Context = this@LoginActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializing()
        setLoginTitleText()

        // ViewModel 에게 LiveData 값을 보내라고 명령. 리턴받은 결과 값(토큰)을 비교하여 내부 DB에 저장
        applySignInViewModel()
        applySignUpViewModel()
    }

    // 뷰 생성 시 init 할 데이터
    private fun initializing() {
        binding =
            DataBindingUtil.setContentView<ActivityLoginBinding>(this, R.layout.activity_login)
                .apply {
                    lifecycleOwner = this@LoginActivity
                    signInVM = loginViewModel
                    signUpVM = signUpViewModel
                }

        if (::binding.isInitialized) {
            binding.mainLoginBtn.setOnClickListener {
                binding.username = binding.mainLoginIdEt.text.toString()
                binding.password = binding.mainLoginPwdEt.text.toString()

                loginViewModel.loadSignInResult(
                    requireNotNull(binding.username.toString()),
                    requireNotNull(binding.password.toString())
                )
            }

            binding.mainSignUpBtn.setOnClickListener {
                buildSignUpDialog()
            }
        }
    }

    // 로그인 화면 메인 타이틀 설정
    private fun setLoginTitleText() {
        val span = SpannableStringBuilder(getString(R.string.main_name))
        span.apply {
            setSpan(
                ForegroundColorSpan(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.defaultMainColor,
                        null
                    )
                ),
                0, 2,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        binding.mainLoginTitle.text = span
    }

    private fun nullCheck() {
        if (binding.mainLoginIdEt.text.toString().isBlank()
            || binding.mainLoginPwdEt.text.toString().isBlank()
        )
            Toast.makeText(context, R.string.error_not_input, Toast.LENGTH_SHORT).show()
        else if (!binding.mainLoginIdEt.text.contains("@"))
            Toast.makeText(context, R.string.error_email, Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(context, getString(R.string.error_check_status), Toast.LENGTH_SHORT)
                .show()
    }

    // JWT 토큰의 payload 로 전달된 데이터 추출
    private fun getDecodeStream(accessToken: String?, type: String): String {
        // jti : 이름, iat : 토큰 발행일, exp 토큰 만료일, iss alias, auth : 권한, mobile : 모바일접속여부
        val jwtPayload = String(Base64.getUrlDecoder().decode(accessToken!!.split(".")[1]))
        return JSONObject(jwtPayload).get(type).toString()
    }

    private fun buildSignUpDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogView: View = LayoutInflater.from(context)
            .inflate(R.layout.dialog_sign_up, null, false)
        builder.setView(dialogView)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        val idInput: TextInputLayout = dialogView.findViewById(R.id.sign_up_email_input)
        val idEt: EditText = dialogView.findViewById(R.id.sign_up_email)
        val pwdEt: EditText = dialogView.findViewById(R.id.sign_up_pwd)
        val rePwdEt: EditText = dialogView.findViewById(R.id.sign_up_repwd)
        val rePwdInput: TextInputLayout = dialogView.findViewById(R.id.sign_up_repwd_input)
        val phoneInput: TextInputLayout = dialogView.findViewById(R.id.sign_up_phone_input)
        val phoneEt: EditText = dialogView.findViewById(R.id.sign_up_phone)
        val createBtn: Button = dialogView.findViewById(R.id.sign_up_ok)
        val cancelBtn: Button = dialogView.findViewById(R.id.sign_up_cancel)

        createBtn.setOnClickListener {
            if (idEt.text.isBlank() || phoneEt.text.isBlank() || pwdEt.text.isBlank() || rePwdEt.text.isBlank()) {
                Toast.makeText(context, getString(R.string.error_not_input), Toast.LENGTH_SHORT)
                    .show()
                MakeVibrator(context).run(300)
            } else if (rePwdInput.error != null || phoneInput.error != null) {
                MakeVibrator(context).run(300)
            } else if (!idEt.text.toString().contains("@")) {
                idInput.error = getString(R.string.error_email)
                MakeVibrator(context).run(300)
            } else {
                idInput.error = null

                signUpViewModel.loadSignUpResult(
                    idEt.text.toString(),
                    phoneEt.text.toString(),
                    pwdEt.text.toString()
                )
                alertDialog.dismiss()
            }
        }

        phoneEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (phoneEt.length() > 11)
                    phoneInput.error = getString(R.string.error_phone)
                else
                    phoneInput.error = null
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        rePwdEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (rePwdEt.text.toString() != pwdEt.text.toString())
                    rePwdInput.error = getString(R.string.error_pwd_matching)
                else
                    rePwdInput.error = null
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        cancelBtn.setOnClickListener { alertDialog.dismiss() }
        alertDialog.show()
    }

    private fun applySignInViewModel() {
        loginViewModel.getSignInResult().observe(this@LoginActivity) { newToken ->
            newToken?.let {
                if (it != RESPONSE_DEFAULT) {
                    if (newToken != originToken) {
                        // 엑세스 토큰 저장
                        SharedPreferenceManager.setString(context, "accessToken", newToken)
                        // 유저 이름 저장
                        SharedPreferenceManager.setString(
                            context,
                            "jti",
                            getDecodeStream(newToken, "jti")
                        )
                    }
                    // 토큰이 저장되었으면 메인화면으로 이동
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    MakeVibrator(context).run(300)
                    nullCheck()
                }
            }
        }
    }

    private fun applySignUpViewModel() {
        signUpViewModel.getSignUpCode().observe(this@LoginActivity) { resultCode ->
            resultCode?.let {
                if (it == RESULT_OK.toString()) {
                    Toast.makeText(context, getString(R.string.success_signup), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "예상치 못한 오류 발생 $it", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
