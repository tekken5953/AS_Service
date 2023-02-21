package app.as_service.api.weather

class GetSkyData {
    // 하늘상태 변환
    fun convert(sky: String): String {
        return when (sky) {
            "1" -> { "맑음" }
            "3" -> { "구름많음" }
            "4" -> { "흐림" }
            else -> { "FAIL" }
        }
    }
}