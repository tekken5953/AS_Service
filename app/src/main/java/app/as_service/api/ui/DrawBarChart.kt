package app.as_service.api.ui

import android.content.Context
import android.graphics.Color
import androidx.core.content.res.ResourcesCompat
import app.as_service.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.android.gms.location.Priority
import com.orhanobut.logger.Logger
import timber.log.Timber
import kotlin.concurrent.thread

class DrawBarChart(mContext: Context) {
    private val context: Context = mContext

    private val entries1 = ArrayList<BarEntry>()
    private val entries2 = ArrayList<BarEntry>()
    private lateinit var mChart: BarChart

    fun getInstance(chart: BarChart) {
        Logger.d( "그래프 인스턴스 생성")

        mChart = chart
        setChart()
        setAxisX()
        setAxisY()
    }

    fun add(yAxis: List<Float>, yAxis2: List<Float>, label: String,label2: String, color: Int, color2: Int) {
        thread(start = true) {
            for (i: Int in 1 until (yAxis.size)) {
                entries1.add(BarEntry(i.toFloat(), yAxis[i]))
                entries2.add(BarEntry(i.toFloat(), yAxis2[i]))
            }
            setData(label, label2, color, color2)
        }
    }

    fun remove(index: Int) {
        Logger.t("GraphLog").d("remove $index item")
        thread(start = true) {
            removeEntry(index)
        }
    }

    private fun setChart() {
        Logger.t("GraphLog").d("setChart")
        val legend = mChart.legend
        mChart.run {
            description.isEnabled = false   // 차트 설명
            setMaxVisibleValueCount(8)      // 최대로 보이는 엔트리 수
            setPinchZoom(false)             // 핀치줌(손가락 줌인/줌아웃) 설정
            setDrawBarShadow(false)         // 그래프 그림자
            setDrawGridBackground(false)    // 격자구조
            setBackgroundColor(ResourcesCompat.getColor(context.resources, R.color.subDarkColor, null))
            setExtraOffsets(10f, 5f, 10f, 5f); // 차트 Padding 설정
            axisRight.isEnabled = false     // 차트 오른쪽 데이터 비활성화
            setTouchEnabled(false)          // 터치 이벤트
            isClickable = false             // 클릭 금지
            setNoDataText("그래프를 그리는 중입니다")  // 데이터가 없는 경우
            animateY(2000)      // 아래에서 올라오는 애니메이션
        }

        legend.apply {
            isEnabled = true // 범례 비활성화
            form = Legend.LegendForm.SQUARE
            textSize = 13f
            textColor = Color.WHITE
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
            orientation = Legend.LegendOrientation.HORIZONTAL
            this.setDrawInside(false)
        }
    }

    private fun setAxisY() {
        Logger.t("GraphLog").d("setAxisY")
        val axisY = mChart.axisLeft
        axisY.run {
            axisMaximum = 30f                 // 그래프의 맥시멈. 그래프를 그리는 위치에 따라 설정
            axisMinimum = -20f                // 최소값 0
            isGranularityEnabled = false      // y축 간격을 제한하는 세분화 기능
//            granularity = 20f               // 50 단위마다 선을 그림(20f면 총 5개를 그림)
            setDrawLabels(true)               // 값을 표시
            labelCount = 7                    // Y축 라벨 개수
            setDrawGridLines(false)           // 격자라인 활용
            setDrawAxisLine(false)            // 축 그리기
            textColor =                       // 라벨 텍스트 색상
                ResourcesCompat.getColor(context.resources, R.color.white, null)
            textSize = 12f                    // 라벨 텍스트 크기
        }
    }

    private fun setAxisX() {
        Logger.t("GraphLog").d("setAxisX")
        val axisX = mChart.xAxis
        axisX.run {
            position = XAxis.XAxisPosition.BOTTOM       // X축의 위치
            granularity = 1f                            // 엔트리의 간격
            setDrawAxisLine(true)                       // 축 그리기
            setDrawGridLines(false)                     // 축 격자
            textColor =                                 // 라벨 텍스트 색상
                ResourcesCompat.getColor(context.resources, R.color.white, null)
            textSize = 12f                              // 라벨 텍스트 크기
            valueFormatter = MyXAxisFormatter()
            granularity = 1f
        }
    }

    private fun removeEntry(index: Int) {
        Logger.t("GraphLog").d("removeEntry")
        entries1.removeAt(index)
        entries2.removeAt(index)
    }

    private fun setData(label: String, label2: String, color: Int, color2: Int) {
        Logger.t("GraphLog").d("setData")
        val set = BarDataSet(entries1, label)
        val set2 = BarDataSet(entries2, label2)
        set.apply {
            setDrawValues(true)
            valueTextColor = R.color.white
            this.color = ResourcesCompat.getColor(context.resources, color, null)
            valueTextColor =
                ResourcesCompat.getColor(context.resources, R.color.modeTextColor, null)
        }

        set2.apply {
            setDrawValues(true)
            valueTextColor = R.color.white
            this.color = ResourcesCompat.getColor(context.resources, color2, null)
            valueTextColor =
                ResourcesCompat.getColor(context.resources, R.color.modeTextColor, null)
        }

        val dataSet: ArrayList<IBarDataSet> = ArrayList()
        dataSet.add(set)
        dataSet.add(set2)
        val data = BarData(dataSet)
        data.barWidth = 0.3f    // 막대 너비 설정
        mChart.run {
            this.data = data
            setFitBars(true)
            invalidate()
            mChart.groupBars(0.5f,0.4f,0f)
        }
    }

    private class MyXAxisFormatter : ValueFormatter() {
        private val times = arrayOf("00:00", "04:00", "08:00", "12:00", "16:00", "20:00")

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return times.getOrNull(value.toInt() - 1) ?: value.toString()
        }
    }
}