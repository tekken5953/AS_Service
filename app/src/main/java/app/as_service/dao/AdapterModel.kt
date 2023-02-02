package app.as_service.dao

import android.graphics.drawable.Drawable
import androidx.annotation.Nullable

class AdapterModel {
    // 디테일 페이지 공기질데이터 모델
    data class AirCondData(var title: String, var data: String, var sort: String)

    // 장치검색 시 GET할 데이터 모델
    data class GetDeviceList(val user_id: String, val device: String,
                             @Nullable val device_name: String, @Nullable val business_type: String,
                             @Nullable val cai_val: String, @Nullable  val virus_val: String)

    data class GridItem(val img: Drawable, val text: String)
}