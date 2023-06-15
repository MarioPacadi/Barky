package hr.algebra.barky.util.metrics

import android.app.ActivityManager

class MemoryMetric(private val activityManager: ActivityManager, override val listener: MetricListener) : AbstractMetric("Memory") {

    override fun computeMetricValue(): Any {
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return memoryInfo.totalMem - memoryInfo.availMem
    }
}