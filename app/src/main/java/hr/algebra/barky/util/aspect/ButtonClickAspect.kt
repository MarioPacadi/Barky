package hr.algebra.barky.util.aspect

import android.util.Log
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect

@Aspect
class ButtonClickAspect {

    @After("execution(* androidx.compose.foundation.clickable.Clickable.invoke(..)) && args(onClick)")
    fun onButtonClick() {
        val message="Button was Clicked"
        Log.w("LoggingVM", message)
    }

    @Around("@annotation(LogButtonClick)")
    fun logLikeButtonClick(joinPoint: ProceedingJoinPoint) {
        Log.w("LikeButtonClick", "A button has been clicked")
        joinPoint.proceed()
    }

    @Around("@annotation(BottomNavClick)")
    fun logBottomNavClick(joinPoint: ProceedingJoinPoint) {
        Log.w("BottomNavClick", "A button has been clicked")
        joinPoint.proceed()
    }
}