package hr.algebra.barky.util.metrics

import android.app.ActivityManager
import android.content.Context

class MetricsManager(
    context: Context,
    activityManager: ActivityManager,
    metricListener: MetricListener
) {
    private val cpuUsageMetric = CpuMetric(context, metricListener)
    private val memoryUsageMetric = MemoryMetric(activityManager, metricListener)
    private val networkUsageMetric = NetworkMetric(context, metricListener)
    private val batteryLevelMetric = BatteryLevelMetric(context, metricListener)

    fun startMonitoringAllMetrics() {
        cpuUsageMetric.startMonitoring()
        memoryUsageMetric.startMonitoring()
        networkUsageMetric.startMonitoring()
        batteryLevelMetric.startMonitoring()
    }

    fun stopMonitoringAllMetrics() {
        cpuUsageMetric.stopMonitoring()
        memoryUsageMetric.stopMonitoring()
        networkUsageMetric.stopMonitoring()
        batteryLevelMetric.stopMonitoring()
    }
}