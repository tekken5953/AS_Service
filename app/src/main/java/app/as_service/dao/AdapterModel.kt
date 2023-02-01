package app.as_service.dao

import android.graphics.drawable.Drawable

class AdapterModel {
    // 디테일 페이지 공기질데이터 모델
    data class AirCondData(var title: String, var data: String, var sort: String)

    // 장치검색 시 GET할 데이터 모델
    data class GetDeviceList(val userId: String, val device: String, val deviceName: String, val businessType: String)

    data class GridItem(val img: Drawable, val text: String)
}