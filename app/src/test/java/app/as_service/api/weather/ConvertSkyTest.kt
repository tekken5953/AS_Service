package app.as_service.api.weather

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConvertSkyTest {
    private lateinit var getSkyData: GetSkyData

    @Before
    fun setUp() {
        getSkyData = GetSkyData()
    }

    @Test
    fun convert_correctIntStringGiven_returnCorrectWeather() {
        val result = getSkyData.convert("3")
        assertThat(result).isEqualTo("구름많음")
    }

    @Test
    fun convert_wrongIntStringGiven_returnCorrectResult() {
        val result = getSkyData.convert("2")
        assertThat(result).isEqualTo("FAIL")
    }
}