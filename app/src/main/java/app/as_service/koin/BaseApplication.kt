package app.as_service.koin

import android.app.Application
import app.as_service.viewModel.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@BaseApplication)
            modules(listOf(loginModule, signUpModule, deviceListModule, getValueDataModule, postDeviceModule ))
        }
    }

    /* single : 싱글톤 빈 정의를 제공. 즉 1번만 객체를 생성한다 */
    /* factory : 호출될 때마다 객체 생성 */
    /* viewModel : 뷰모델 의존성 제거 객체 생성 */
    private val loginModule = module { viewModel { LoginViewModel() } }
    private val signUpModule = module { viewModel { SignUpViewModel() } }
    private val deviceListModule = module { viewModel { DeviceListViewModel() }}
    private val getValueDataModule = module { viewModel { GetValueDataModel()} }
    private val postDeviceModule = module { viewModel { AddDeviceViewModel() }}
}