package app.as_service.koin.repo

import app.as_service.AbstractKoinTest
import app.as_service.dao.ApiModel
import app.as_service.repository.LoginRepo
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import com.google.common.truth.Truth.assertThat

/** 참고
 * https://youngest-programming.tistory.com/492
 * https://flowarc.tistory.com/entry/Android-Studio-%EB%8B%A8%EC%9C%84%ED%85%8C%EC%8A%A4%ED%8A%B8Unit-Test-%ED%95%98%EA%B8%B0
 * https://yk-coding-letter.tistory.com/13
 */

class LoginRepoTest : AbstractKoinTest() {
    private val repository = LoginRepo().httpClient.mMyAPI
    private lateinit var postCorrectId: String
    private lateinit var postCorrectPwd: String
    private lateinit var postWrongId: String
    private lateinit var postWrongPwd: String

    @Before
    fun setUp() {
        postCorrectId = "test@test.com"
        postCorrectPwd = "1234"
        postWrongId = "Illegal Data"
        postWrongPwd = "Illegal Data"
    }

    @Test
    fun post_correct_id_correct_pwd() = runBlocking {
        val response = repository.postUsers(ApiModel.Login(postCorrectId, postCorrectPwd)).execute().code()
        assertThat(response).isEqualTo(200)
    }

    @Test
    fun post_correct_id_wrong_pwd() = runBlocking {
        val response = repository.postUsers(ApiModel.Login(postCorrectId, postWrongPwd)).execute().code()
        assertThat(response).isEqualTo(403)
    }

    @Test
    fun post_wrong_id_correct_pwd() = runBlocking {
        val response = repository.postUsers(ApiModel.Login(postWrongId, postCorrectPwd)).execute().code()
        assertThat(response).isEqualTo(444)
    }

    @Test
    fun post_wrong_id_wrong_pwd() = runBlocking {
        val response = repository.postUsers(ApiModel.Login(postWrongId, postWrongPwd)).execute().code()
        assertThat(response).isEqualTo(444)
    }
}