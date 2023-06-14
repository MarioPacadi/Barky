package hr.algebra.barky.model.utils

import java.util.concurrent.TimeUnit

data class TimerData(val millis: Long = 0) {
    val hourTens: String
    val hourZero: String
    val minuteTens: String
    val minuteZero: String
    val secondTens: String
    val secondZero: String

    init {
        var millisRemains = millis
        val hours = TimeUnit.MILLISECONDS.toHours(millisRemains)
        hourTens = hours.div(10).coerceIn(0L..9L).toString()
        hourZero = hours.rem(10).toString()
        millisRemains -= TimeUnit.HOURS.toMillis(hours)

        val minute = TimeUnit.MILLISECONDS.toMinutes(millisRemains)
        minuteTens = minute.div(10).toString()
        minuteZero = minute.rem(10).toString()
        millisRemains -= TimeUnit.MINUTES.toMillis(minute)

        val seconds = TimeUnit.MILLISECONDS.toSeconds(millisRemains)
        secondTens = seconds.div(10).toString()
        secondZero = seconds.rem(10).toString()
    }
}