package app.as_service.view.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import app.as_service.R
import app.as_service.dao.StaticDataObject.RESPONSE_DEFAULT
import app.as_service.databinding.ActivityLoginBinding
import app.as_service.util.MakeVibrator
import app.as_service.util.SharedPreferenceManager
import app.as_service.view.MainActivity
import app.as_service.viewModel.LoginViewModel
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModel<LoginViewModel>()
    private lateinit var originToken: String
    private val context: Context = this@LoginActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializing()
        setLoginTitleText()

        // ViewModel 에게 LiveData 값을 보내라고 명령. 리턴받은 결과 값(토큰)을 비교하여 내부 DB에 저장
        viewModel.apply {
            getSignInResult().observe(this@LoginActivity) { newToken ->
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
    }

    // 뷰 생성 시 init 할 데이터
    private fun initializing() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.lifecycleOwner = this
        binding.signInVM = viewModel

        originToken =
            SharedPreferenceManager.getString(this, "accessToken")    // 로컬 DB 에 저장된 토큰 값 불러오기

        binding.mainLoginBtn.setOnClickListener {
            binding.username = binding.mainLoginIdEt.text.toString()
            binding.password = binding.mainLoginPwdEt.text.toString()

            viewModel.loadSignInResult(
                requireNotNull(binding.username.toString()),
                requireNotNull(binding.password.toString())
            )
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
            Toast.makeText(context, "아이디와 비밀번호를 모두 입력해주세요", Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(context, "아이디와 비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show()
    }

    // JWT 토큰의 payload 로 전달된 데이터 추출
    private fun getDecodeStream(accessToken: String?, type: String): String {
        // jti : 이름, iat : 토큰 발행일, exp 토큰 만료일, iss alias, auth : 권한, mobile : 모바일접속여부
        val jwtPayload = String(Base64.getUrlDecoder().decode(accessToken!!.split(".")[1]))
        return JSONObject(jwtPayload).get(type).toString()
    }
}
