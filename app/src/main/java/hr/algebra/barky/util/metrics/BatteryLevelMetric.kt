package hr.algebra.barky.util.metrics

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

class BatteryLevelMetric(private val context: Context, override val listener: MetricListener) : AbstractMetric("BatteryLevel") {
    override fun computeMetricValue(): Any {
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

        if (level != null && scale != null) {
            return (level * 100) / scale
        }

        return 0 // Placeholder value if battery information is unavailable
    }
}