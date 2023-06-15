package hr.algebra.barky.util.aspect

import android.util.Log
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Aspect

@Suppress("unused")
@Aspect
class ExceptionHandlerAspect {
    @AfterThrowing(pointcut = "execution(* hr.algebra.barky.MyApp..*(..))", throwing = "ex")
    fun handleException(ex: Exception) {
        Log.e("ExceptionHandlerAspect", "Exception occurred: ${ex.javaClass.simpleName}")
    }
}
