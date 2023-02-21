package app.as_service.api.weather

import app.as_service.util.ConvertDataTypeUtil.getCurrentTimeMills
import app.as_service.util.ConvertDataTypeUtil.getYesterdayLong
import app.as_service.util.ConvertDataTypeUtil.millsToString

class NearAlgorithm {
    // 근사값 알고리즘
    fun execute(hour: String): String {
        // 단기예보 측정시간
        val array = arrayOf("0210", "0510", "0810", "1110", "1410", "1710", "2010", "2310")
        val dateFormat = "yyyyMMdd"
        val time:String
        val date:String

        for (i: Int in 0 until (array.size)) {
            val diff = try {
                array[i].toInt() - hour.toInt()   // 측정시간 - 현재시간
            } catch (e: NumberFormatException) {
                e.printStackTrace()
                return  "올바르지 않은 형식의 데이터입니다"
            }

            if (diff > 0) {  // 결과가 양수일 경우 = 오늘날짜 이전 측정 결과가 있는 경우
                when (i) {
                    0 -> {
                        time = array.last()
                        date = millsToString(
                            getYesterdayLong(),
                            dateFormat
                        )
                        return "${date}_${time}"
                    }
                    array.lastIndex -> {
                        time = array.first()
                        date = millsToString(
                            getYesterdayLong(),
                            dateFormat
                        )
                        return "${date}_${time}"
                    }
                    else -> {
                        if (array[i-1].toInt() - hour.toInt() < 300) {
                            time = array[i - 1]   // 리턴값을 근삿값으로 지정
                            date = millsToString(
                                getCurrentTimeMills(),
                                dateFormat
                            )
                            return "${date}_${time}"
                        } else {
                            continue
                        }
                    }
                }
            }
        }
       return "null"
    }
}