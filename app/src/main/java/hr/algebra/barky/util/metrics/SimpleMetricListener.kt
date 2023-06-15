package hr.algebra.barky.util.metrics

import android.util.Log

class SimpleMetricListener : MetricListener {
    override fun onMetricChanged(metricName: String, value: Any) {
        Log.d("MetricsManager", "Metric: $metricName, Value: $value")
    }
}