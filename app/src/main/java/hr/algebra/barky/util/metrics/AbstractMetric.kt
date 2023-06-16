package hr.algebra.barky.util.metrics

import android.util.Log

abstract class AbstractMetric(private val metricName: String) : Metric {
    protected abstract val listener: MetricListener
    private var monitoringJob: Thread? = null

    override fun startMonitoring() {
        monitoringJob = Thread {
            while (!Thread.currentThread().isInterrupted) {
                val metricValue = computeMetricValue()
                listener.onMetricChanged(metricName, metricValue)
                //logMetric(metricName, metricValue)

                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
            }
        }
        monitoringJob?.start()
    }

    override fun stopMonitoring() {
        monitoringJob?.interrupt()
        monitoringJob = null
    }

    protected abstract fun computeMetricValue(): Any

//    private fun logMetric(metricName: String, value: Any) {
//        Log.d("MetricsManager", "Metric: $metricName, Value: $value")
//    }
}