package hr.algebra.barky.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.firebase.firestore.DocumentSnapshot
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.HashMap

fun String.isValidPassword() =
    Pattern.compile("^" +
            "(?=.*[0-9])" +         //at least 1 digit
            "(?=.*[a-z])" +         //at least 1 lower case letter
            "(?=.*[A-Z])" +         //at least 1 upper case letter
            "(?=.*[a-zA-Z])" +      //any letter
            "(?=.*[@#$%^&+=])" +    //at least 1 special character
            "(?=\\S+$)" +           //no white spaces
            ".{8,}" +               //at least 8 characters
            "$").matcher(this).matches()

fun bitmapDescriptorFromVector(
    context: Context,
    vectorResId: Int
): BitmapDescriptor? {

    // retrieve the actual drawable
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    val bm = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )

    // draw it onto the bitmap
    val canvas = android.graphics.Canvas(bm)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bm)
}

fun getVariableMap(obj: Any): HashMap<String, Any?> {
    val result = HashMap<String, Any?>()
    val fields = obj.javaClass.declaredFields
    for (field in fields) {
        field.isAccessible = true
        if (!field.name.contains("$") && !field.name.equals("Companion"))
            result[field.name] = field.get(obj)
    }
    return result
}

fun initializeClassData(clazz: Class<*>, document: DocumentSnapshot): Any {
    /*
    IMPORTANT NOTES:
        if working with data classes you must create a zero argument constructor
    * */
    try {
        //val constructor = clazz.getDeclaredConstructor()
        val instance = clazz.newInstance()

        clazz.declaredFields.forEach { field ->
            field.isAccessible = true
            val value = document.get(field.name)

            if(value!=null){
                when (field.type) {
                    String::class.java -> field.set(instance, document.getString(field.name))
                    Boolean::class.java -> field.set(instance, document.getBoolean(field.name))
                    Int::class.java -> field.set(instance, document.get(field.name) as Int)
                    Double::class.java -> field.set(instance, document.getDouble(field.name))
                    Long::class.java -> field.set(instance, document.getLong(field.name))
                }
            }
        }

        return instance
    } catch (e: Exception) {
        Log.e("Error",e.toString())
        e.printStackTrace()
    }
    return Any()
}



fun getClassesVariableNames(obj: Any): List<String> {
    val result = mutableListOf<String>()
    val fields = obj.javaClass.declaredFields
    for (field in fields) {
        field.isAccessible = true
        if (!field.name.contains("$") && !field.name.equals("Companion"))
            result.add(field.name)
    }
    return result
}

fun getRandomDate(year_start : Int,year_end : Int) : String{
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.YEAR, (year_start..year_end).random())
    calendar.set(Calendar.MONTH, (0..11).random())
    calendar.set(Calendar.DAY_OF_MONTH, (1..28).random())
    calendar.set(Calendar.HOUR_OF_DAY, (0..23).random())
    calendar.set(Calendar.MINUTE, (0..59).random())
    calendar.set(Calendar.SECOND, (0..59).random())

    return calendar.time.toString()
}

fun randomNameGenerator(): String {
    val firstNames = listOf("John", "Jane", "Michael", "Emily", "William", "Olivia", "Jacob", "Ava")
    val lastNames = listOf("Smith", "Johnson", "Brown", "Jones", "Williams", "Miller", "Davis", "Garcia")

    val random = Random()
    val randomFirstName = firstNames[random.nextInt(firstNames.size)]
    val randomLastName = lastNames[random.nextInt(lastNames.size)]

    return "$randomFirstName $randomLastName"
}

@SuppressLint("SimpleDateFormat")
fun getCurrentDateTime(pattern: String="MMMM d, yyyy 'at' hh:mm:ss a z"): String {
    val dateFormat = SimpleDateFormat(pattern)
    val calendar = Calendar.getInstance()
    return dateFormat.format(calendar.time)
}

fun getCurrentTime(): Long {
    return Calendar.getInstance().timeInMillis
}

fun Long.formatDateTime(pattern: String="yyyy-MM-dd HH:mm:ss"): String {
    val instant = Instant.ofEpochMilli(this)
    val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return dateTime.format(formatter)
}

