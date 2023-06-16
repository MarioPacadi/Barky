package hr.algebra.barky.util.metrics

import android.content.Context
import android.net.TrafficStats
import android.os.Process

class NetworkMetric(private val context: Context, override val listener: MetricListener) : AbstractMetric("Network") {

    override fun computeMetricValue(): Any {
        val uid = Process.myUid()
        val rxBytes = TrafficStats.getUidRxBytes(uid)
        val txBytes = TrafficStats.getUidTxBytes(uid)
        return rxBytes + txBytes
    }
}