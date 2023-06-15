package hr.algebra.barky.util.metrics

interface Metric {
    fun startMonitoring()
    fun stopMonitoring()
}

interface MetricListener {
    fun onMetricChanged(metricName: String, value: Any)
}