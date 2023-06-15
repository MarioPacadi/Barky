package hr.algebra.barky.util.aspect

import android.util.Log
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect

@Suppress("unused")
@Aspect
class ComposableLoggingAspect {

    @Around("@annotation(compose.runtime.Composable)")
    fun logComposableExecution(joinPoint: ProceedingJoinPoint): Any? {
        val methodName = joinPoint.signature.name
        val className = joinPoint.signature.declaringType.simpleName

        val message="Executing Composable function: $className.$methodName"
        Log.w("LoggingVM", message)

        return joinPoint.proceed()
    }

}