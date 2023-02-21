package app.as_service

import app.as_service.AbstractKoinTest
import app.as_service.dao.ApiModel
import app.as_service.repository.SignUpRepo
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import com.google.common.truth.Truth.assertThat

/** 참고
 * https://youngest-programming.tistory.com/492
 * https://flowarc.tistory.com/entry/Android-Studio-%EB%8B%A8%EC%9C%84%ED%85%8C%EC%8A%A4%ED%8A%B8Unit-Test-%ED%95%98%EA%B8%B0
 * https://yk-coding-letter.tistory.com/13
 */

class SignUpValidTest : AbstractKoinTest() {
    private val repository = SignUpRepo().httpClient.mMyAPI
    private lateinit var postCorrectEmail: String
    private lateinit var postCorrectPwd: String
    private lateinit var postCorrectRePwd: String
    private lateinit var postCorrectPhone: String
    private lateinit var postWrongEmail: String
    private lateinit var postWrongPwd: String
    private lateinit var postWrongRePwd: String
    private lateinit var postWrongPhone: String

    @Before
    fun setUp() {
        postCorrectEmail = "test@test.com"
        postCorrectPwd = "1234"
        postCorrectRePwd = "1234"
        postCorrectPhone = "01012345678"
        postWrongEmail = "Illegal Email"
        postWrongPwd = "Illegal Password"
        postWrongRePwd = "Diff Password"
        postWrongPhone = "Illegal PhoneNumber"
    }

    @Test
    fun post_correct_all() = runBlocking {
        val response = repository.postSignUp(ApiModel.Member(postCorrectEmail, postCorrectPwd, postWrongPhone)).execute().code()
        assertThat(response).isEqualTo(200)
    }

    @Test
    fun post_correct_emailAndPhone_wrong_password() = runBlocking {
        val response = repository.postSignUp(ApiModel.Member(postCorrectEmail, postWrongPwd, postCorrectPhone)).execute().code()
        assertThat(response).isEqualTo(403)
    }

    @Test
    fun post_correct_email_wrong_passwordAndPhone() = runBlocking {
        val response = repository.postSignUp(ApiModel.Member(postCorrectEmail, postWrongPwd, postCorrectPhone)).execute().code()
        assertThat(response).isEqualTo(403)
    }
}