package app.as_service

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.as_service.koin.BaseApplication
import app.as_service.repository.LoginRepo
import org.junit.Rule
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.mock.MockProviderRule
import org.mockito.Mockito

abstract class AbstractKoinTest : KoinTest {
    inline fun <reified T> mock(): T = Mockito.mock(T::class.java)
    private val base = BaseApplication()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        printLogger(Level.DEBUG)
        module { listOf(base.loginModule, base.signUpModule, base.deviceListModule, base.deleteDeviceModule,
            base.getValueDataModule, base.postDeviceModule, base.patchBookMarkModule, LoginRepo())
        }
    }

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz -> Mockito.mock(clazz.java) }

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
}