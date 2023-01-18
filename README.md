_**<h1>AirSignal 100 Service App</h1>**_


<br/><img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=Android&logoColor=white"> <img src="https://img.shields.io/badge/JAVA-007396?style=for-the-badge&logo=java&logoColor=white"> <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=Kotlin&logoColor=white">


<br/>
<h2>Initial main branch merged at</h2>


``2023-01-18``


<br/>
<h2>Draft by</h2><br/>

``Lee Jae Young``

<br/>

> Github    
> https://github.com/tekken5953

> Mail       
> jy5953@airsignal.kr

<br/>
<h2>Request contents</h2>

* MVVM 아키텍처 패턴을 적용하여 디자인

* OKHttp와 Retrofit을 이용한 서버통신

* 서버 클라이언트 인스턴스 생성을 싱글톤으로 구현

* 내부DB 저장소에 토큰 및 지속정보 저장 (현재는 SharedPreference 사용 중. Room DB로 교체 가능)

* 공기질 정보 등 지속적인 API 호출 데이터는 비동기 처리 (Coroutain 혹은 RxJava 사용 예정)

* JUnit + Mock을 사용하여 Unit Test 코드 작성 예정 (ViewModel Test + Adapter Test + ETC)

<br/>
<h2>Gradle Setting</h2>

``compileSdk : 32``

``minSdk : 26``

``targetSdk : 32``

<br/>
<h2>Implementation Library Info</h2>

__ViewModel__
> implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1"

__LiveData__
> implementation "androidx.lifecycle:lifecycle-livedata-ktx:2.5.1"

__HTTP__
> implementation 'com.squareup.retrofit2:retrofit:2.6.1'

> implementation 'com.squareup.retrofit2:converter-gson:2.6.1'

> implementation 'com.squareup.okhttp3:logging-interceptor:3.12.1'

> implementation 'com.squareup.retrofit2:converter-scalars:2.3.0'  // String 처리시


