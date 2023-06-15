package hr.algebra.barky.util.metrics

import android.content.Context
import android.os.Process
import java.io.FileReader

class CpuMetric(private val context: Context, override val listener: MetricListener) : AbstractMetric("CPU Usage") {

    private var lastCpuTime = 0L
    private var lastAppTime = 0L

    override fun computeMetricValue(): Any {
        val processCpuFile = "/proc/${Process.myPid()}/stat"
        val statFile = FileReader(processCpuFile)


        val statContents = statFile.readText()
        val statElements = statContents.split(" ")
        val cpuTime = statElements[13].toLong() + statElements[14].toLong()
        val appTime = statElements[15].toLong()

        if (lastCpuTime > 0 && lastAppTime > 0) {
            return ((appTime - lastAppTime) * 100f) / (cpuTime - lastCpuTime)
        }

        lastCpuTime = cpuTime
        lastAppTime = appTime

        return 0f // Placeholder value if CPU information is unavailable
    }
}