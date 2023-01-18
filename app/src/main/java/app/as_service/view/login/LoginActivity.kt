package app.as_service.view.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import app.as_service.R
import app.as_service.dao.StaticDataObject.RESPONSE_DEFAULT
import app.as_service.databinding.ActivityLoginBinding
import app.as_service.util.MakeVibrator
import app.as_service.util.SharedPreferenceManager
import app.as_service.view.MainActivity
import app.as_service.viewModel.LoginViewModel
import org.json.JSONObject
import java.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var originToken: String
    private val context: Context = this@LoginActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializing()

        // ViewModel 에게 LiveData 값을 보내라고 명령. 리턴받은 결과 값(토큰)을 비교하여 내부 DB에 저장
        viewModel.apply {
            getSignInResult().observe(this@LoginActivity) { newToken ->
                if (newToken != RESPONSE_DEFAULT) {
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

    private fun initializing() {
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.lifecycleOwner = this
        binding.signInVM = viewModel

        val span = SpannableStringBuilder(getString(R.string.main_name))
        span.apply {
            setSpan(ForegroundColorSpan(ResourcesCompat.getColor(resources, R.color.defaultMainColor, null)),
                0,2,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        binding.mainLoginTitle.text = span

        originToken =
            SharedPreferenceManager.getString(this, "accessToken")    // 로컬 DB 에 저장된 토큰 값 불러오기

        binding.mainLoginBtn.setOnClickListener {
            binding.username = binding.mainLoginIdEt.text.toString()
            binding.password = binding.mainLoginPwdEt.text.toString()

            viewModel.loadSignInResult(binding.username.toString(), binding.password.toString())
        }
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