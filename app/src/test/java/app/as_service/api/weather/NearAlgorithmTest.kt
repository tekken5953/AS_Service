package app.as_service.api.weather

import android.annotation.SuppressLint
import app.as_service.util.Log
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.text.SimpleDateFormat
import java.util.*

@RunWith(RobolectricTestRunner::class)
class NearAlgorithmTest {
    private lateinit var near: NearAlgorithm
    private lateinit var normalTime: String
    private lateinit var boundaryTime: String
    private lateinit var wrongTime: String

    @Before
    fun setUp() {
        near = NearAlgorithm()
        normalTime = "1120"
        boundaryTime = "0000"
        wrongTime = "null"
    }

    @Test
    fun execute_normalTime() {
        val result = near.execute(normalTime)
        Log.d("NearAlgorithmTest","time : $normalTime result : $result")
        assertThat(result).isEqualTo("${millsToString(System.currentTimeMillis())}_${"1110"}")
    }

    @Test
    fun execute_boundaryTime() {
        val result = near.execute(boundaryTime)
        assertThat(result).isEqualTo("${millsToString(getYesterdayLong())}_${"2310"}")
    }

    @Test
    fun execute_wrongFormatTime() {
        val result = near.execute(wrongTime)
        assertThat(result).isEqualTo("올바르지 않은 형식의 데이터입니다")
    }

    private fun millsToString(mills: Long): String {
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("yyyyMMdd")
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = mills
        return format.format(calendar.time)
    }

    // 어제시간 반환
    private fun getYesterdayLong() : Long {
        val currentTime = System.currentTimeMillis()
        return currentTime - (1000 * 60 * 60 * 24)
    }
}